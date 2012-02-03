import java.util.Random;

/**
 * So that we can work with theoretical rolls (if we get roll X then ...)
 *
 * There are 2 dice but how about when we roll doubles we think of them as being 4 dice?
 * Or merely use a partialMovesCountDown? Stored in here?
 * Perhaps a parallel array in here marking which dice have been used so far?
 * 
 * @author (Mike Roam) 
 * @version (2011 Dec 14)
 */
public class Dice
{
    /* if you add more fields here, add them to the copy constructor, also! 
       Beware deep copy if the new fields hold (pointers to) objects! */
    private int[ ] dice = new int[howManyDice];
    private boolean[ ] used = new boolean[maxMovesCount]; /* was called "used_move". */
    /* old code: usedDice == 1 means first dice has been used
       usedDice == 2 means second dice has been used
       usedDice == 0 means no die have been used yet
       doubletMovesCountdown keeps a countdown of total number of moves available, from 4 when doubles.  */

    private boolean rolled = false;
    private Random rdice = null; // random number generator, gets started in constructor.
    private int doubletMovesCountdown = 0;

    static final int howManyDice = 2;
    static final int maxMovesCount = 4; /* Bonus for getting doubles. Would be diff with more dice. */
    static final int UNROLLED = 0; /* for dice in mid-air? */
    static final int minDiceVal = 1;
    static final int maxDiceVal = 6;

    
    /**
     * Default constructor, leaves dice unrolled
     */
    public Dice( ) {
        // dice haven't rolled yet, are all zero!
        rdice = new Random(); // random number generator
        reset( ); //turns off rolled, unrolls all dice, put usedDice & doubletMovesCountdown to 0
    }
    
    
    /**
     * Copy Constructor
     */
    public Dice ( Dice other) {
        for (int i=0; i<howManyDice; ++i) {
            dice[i] = other.dice[i];
        }
        for (int i=0; i<maxMovesCount; ++i) {
            used[i] = other.used[i];
        }
        rolled = other.rolled;
        rdice = other.rdice; /* not copying this, just linking to their generator. We don't need another, do we? */
        doubletMovesCountdown = other.doubletMovesCountdown;
    } /* Copy Constructor */
    
    
    
    /**
     * Constructor for objects of class Dice with only 2 dice. 
     * If we get more dice, this has to die.
     * Only accepts rolls of minDiceVal .. maxDiceVal  or Dice.UNROLLED
     * Should it accept 0's so I can pass around unrolled dice? I guess so.
     */
    public Dice(int newDice1, int newDice2, boolean newRolled) throws IllegalArgumentException {
        if (howManyDice > 2) {
            throw new IllegalArgumentException("Can't use the 2 dice constructor because we have " + howManyDice + " dice!");
        }
        if (! legitDiceValue( newDice1 ) ) {
            String myMsg = "dice1 is given bad value '" + newDice1 + "' not 0 and not [" + minDiceVal + ".." + maxDiceVal + "]";
            throw new IllegalArgumentException(myMsg);
        }
        if (! legitDiceValue( newDice2 )) {
            String myMsg = "dice2 is given bad value '" + newDice2 + "' not 0 and not [" + minDiceVal + ".." + maxDiceVal + "]";
            throw new IllegalArgumentException(myMsg);
        }
        if (newRolled && ((newDice1 == UNROLLED) || (newDice2 == UNROLLED))) {
            throw new IllegalArgumentException("Bad dice, claim to be rolled but have values " + newDice1 + "," + newDice2);
        }
        if ( !newRolled && ((newDice1 != UNROLLED) || (newDice2 != UNROLLED))) {
            throw new IllegalArgumentException("Bad dice, claim to be not rolled but have values " + newDice1 + "," + newDice2);
        }
        dice[0] = newDice1;
        dice[1] = newDice2;
        rolled = newRolled;
        rdice = new Random(); // random number generator, gets started in constructor.
        resetUsedDice( );
        resetDoubletMovesCountdown( );
    } /* constructor */

    
    /**
     * allows UNROLLED or [minDiceVal to maxDiceVal]
     */
    static boolean legitDiceValue(int diceVal) {
        return (((minDiceVal<= diceVal) && (diceVal <= maxDiceVal)) || (diceVal == UNROLLED));
    } /* hasLegitDiceValue( ) */
    
    
    /**
     * for specifying which DIE we're talking about, not the value on a face of a die!
     */
    static boolean legitDieNum(int dieNum) {
        return ((1 <= dieNum) && (dieNum <= howManyDice));
    }
    
    
    /**
     * convenience method.
     * Note: there is no dice0
     */   
    public int getDice1() {
        return dice[0];
    } 

    
    /**
     * convenience method
     * Note: there is a dice2, and there is no dice0
     */   
    public int getDice2() {
        return dice[1];
    } 
    
    
    /**
     * Dice are named "1", "2" ... up to howManyDice (there is no dice 0!)
     * corresponding to hidden array dice[0], dice[1], respectively
     */
    public int getDice( int whichDie) {
        if (! legitDieNum(whichDie)) {
            throw new IllegalArgumentException("bad die number '" + whichDie + "'");
        }
        return dice[whichDie - 1]; 
    } /* getDice(int ) */
    
    
    /**
     * This for setting a specified individual die.
     * Can't use this to set first two dice at once! Use "roll(5,6)" to do that.
     * This could really screw up the usedDice statistics...
     */
    public void setDie( int whichDie, int newRoll ) {
        if (! legitDieNum(whichDie)) {
            throw new IllegalArgumentException("Can't talk to dice '" + whichDie + "', we only have dice 1.." + howManyDice);
        }
        if (! legitDiceValue(newRoll)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll + "', our dice only can roll " + minDiceVal + ".." + maxDiceVal);
        }
        if (dice[whichDie] != newRoll) {
            dice[whichDie] = newRoll;
            used[whichDie] = false;
        }
        if (allDiceHaveValues()) {
            rolled = true;
        }
        resetDoubletMovesCountdown( );
    }
    
    
    /**
     * I don't want to have a setRolled( ) because I think this is better: rolls all dice and sets rolled to true.
     * To invalidate the dice, use "reset( )"
     * Unfortunately, if receiving a networked roll, we need to set rolled to true, I guess.
     * Note: there is a convenience version of this for setting two dice roll(int,int)
     */
    public void roll( ) {
        for (int i=0; i<howManyDice; ++i) {
            dice[i] = rdice.nextInt(maxDiceVal) + minDiceVal;
        }
        rolled = true;
        resetDoubletMovesCountdown( );
        resetUsedDice( );
        //System.out.println("I just rolled the dice and got " + this.toString( ) + "!");
    }

    
    /**
     * changes the first two dice to specified values, is convenience
     */
    public void roll(int newRoll1, int newRoll2 ) {
        if (! legitDiceValue(newRoll1)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll1 + "', our dice only can roll " + minDiceVal + ".." + maxDiceVal);
        }
        if (! legitDiceValue(newRoll2)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll1 + "', our dice only can roll " + minDiceVal + ".." + maxDiceVal);
        }
        roll( );
        dice[0] = newRoll1;
        dice[1] = newRoll2;
    }
    
    
    
    /**
     * If we want to manually set the dice values, set them all before setting "rolled" to true
     * because I will squawk if some of the dice don't have values (are UNROLLED).
     * It's probably easier to just roll( ) the dice and then change some of their values.
     */
    public void setRolled(boolean newRolled) {
        if (newRolled == false) {
            rolled = false;
            reset( );
        } else if (allDiceHaveValues()) {
            rolled = true;
            resetDoubletMovesCountdown( );
            resetUsedDice( );
        } else {
            throw new IllegalArgumentException("Uh-oh: dice think they are rolled but some don't have values.");
        }
    }
    
    
    /**
     * checks for no dice are UNROLLED value
     */
    public boolean allDiceHaveValues( ) {
        boolean allDiceAreRolled = true;
        for (int i=0; i<howManyDice; ++i) {
            if (dice[i] == UNROLLED) {
                allDiceAreRolled = false;
            };
        }
        return allDiceAreRolled;
    }
    

    /**
     * Supposedly, when rolled is true, all dice have values from minDiceVal..maxDiceVal.
     * Use "roll( )" to roll again, use "reset( )" to blank them all out and set rolled to false.
     */
    public boolean getRolled( ) {
        if (rolled) {
            if (allDiceHaveValues( )) {
                return true;
            } else {
                throw new IllegalArgumentException("Uh-oh: dice think they are rolled but some don't have values.");
            }
        }
        return rolled;
    }
    
    
    /**
     * note: if I get doubles, I'm keeping track of 4 usable dice!
     */
    public boolean getUsedDie( int newUsedDie ) {
        /* starting to replace this with an array, I'm just hard-coding the
           temporary code's limits. */
           if ( ! ((0<= newUsedDie) && (newUsedDie <= maxMovesCount)) ) {
            throw new IllegalArgumentException("bad newUsedDie '" + newUsedDie + "', should be 0, 1, or 2");
        }
        return used[newUsedDie];
         //  throw new IllegalArgumentException("getUsedDice has a new style: specify which dice you're curious about");
    }
    
    
    /**
     * 
     */
    public void setUsedDie(int newUsedDie, boolean newUsedTF) {
        /* since I'm hoping to replace this with an array soon, I'm just hard-coding the
           temporary code's limits. */
        if ( ! ((0<= newUsedDie) && (newUsedDie <= maxMovesCount)) ) {
            throw new IllegalArgumentException("bad newUsedDie '" + newUsedDie + "', should be 0, 1, or 2");
        }
        used[newUsedDie] = newUsedTF;
    }
    

    /**
     * Puts the dice into unrolled state without dice values, ready for new roll.
     */
     public void reset( ) {
         for (int i=0; i<howManyDice; ++i) {
            dice[i] = UNROLLED;
        }
        rolled = false;
        resetDoubletMovesCountdown( );
        resetUsedDice( );
    }
    
    
    public void resetUsedDice( ) {
        for (int i=0; i<maxMovesCount; ++i) {
            used[i] = false;
        }
    }

    

    public boolean isDoubles( ) {
        return dice[0] == dice[1];
    } /* isDoubles */
    
    
    
    /**
     * restarts the countdown, so don't use this willy nilly!
     */
    private void resetDoubletMovesCountdown( ) {
        if (! rolled) {
            doubletMovesCountdown = 0;
        } else if (isDoubles( ) ) /* or rolledBonus( ) */ {
            doubletMovesCountdown = maxMovesCount;
        } else {
            doubletMovesCountdown = howManyDice;
        }
    }

    
    public void doubletCountdown( ) {
        doubletMovesCountdown--;
        if ( doubletMovesCountdown < 0 ) {
            throw new IllegalArgumentException("uh-oh: doubletMovesCountdown went negative!");
        }
    }
    
    
    /**
     * might be nice to have a simpler "doubletCountdown( )" that subtracts one
     */
    public int getDoubletMovesCountdown() {
        return doubletMovesCountdown;
    }
    
    
    public void setDoubletMovesCountdown(int newCountDown) {
        if (! (( 0 <= newCountDown) && (newCountDown <= maxMovesCount))) {
            throw new IllegalArgumentException("bad doubletMovesCountdown '" + newCountDown + "', can only be 0.." + maxMovesCount);
        }
        doubletMovesCountdown = newCountDown;
    }
    
    
    public String toString( ) {
        // StringBuffer temp = new StringBuffer("[");
        // for (int i=0; i<(howManyDice-1); ++i) { temp.append(dice[i] + ","; }
        // temp.append(dice[howManyDice-1] + "]");
        // return temp.toString( );
        return "[" + dice[0] + "," + dice[1] + "]";
    }
    
} /* class Dice */
