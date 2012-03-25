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
    /* beware! Outside world talks about getUsed(1) which refers to used[0], getUsed(2) means used[1], etc */
    private boolean[ ] used = new boolean[maxMovesCount]; /* was called "used_move". Too small for collection? */
    private int doubletMovesCountdown = 0; /* should be called howManyPartialMovesAvailable */

    /* old code for used: usedDice == 1 means first dice has been used
    usedDice == 2 means second dice has been used
    usedDice == 0 means no die have been used yet
    doubletMovesCountdown keeps a countdown of total number of moves available, from 4 when doubles.  */

    private boolean rolled = false;
    private Random rdice = null; // random number generator, gets started in constructor.
    private int usedDiceHowMany = 0;

    static final int howManyDice = 2;
    static final int maxMovesCount = 4; /* Bonus for getting doubles. Would be diff with more dice. */
    static final int NO_SUCH_DIE = -9; /* if someone asks unrolled dice which one is highest roll */

    static final int UNROLLED = 0; /* for dice in mid-air? */
    static final int minDieVal = 1;
    static final int maxDieVal = 6;

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
        doubletMovesCountdown = other.doubletMovesCountdown;
        rolled = other.rolled;
        rdice = other.rdice; /* not copying this, just linking to their generator. We don't need another, do we? */
        usedDiceHowMany = other.usedDiceHowMany;
    } /* Copy Constructor */

    
    /**
     * Constructor for objects of class Dice with only 2 dice. 
     * If we get more dice, this has to die.
     * Only accepts rolls of minDiceVal .. maxDiceVal  or Dice.UNROLLED
     * Should it accept 0's so I can pass around unrolled dice? I guess so.
     * Figures out rolled status based on the values it gets for the Dice:
     * Either both dice have values in the "rolled" range or both must be UNROLLED:
     * This will throw exception if one diceValue is UNROLLED while other is in rolled range!
     */
    public Dice(int newDie1, int newDie2/*, boolean newRolled*/) 
    /* throws IllegalArgumentException, ArrayIndexOutOfBoundsException */
    {
        if (! (howManyDice == 2)) {
            throw new IllegalArgumentException("Can't use the 2 dice constructor because we have " + howManyDice + " dice!");
        }
        if (! legitDiceValue( newDie1 ) ) {
            throw new IllegalArgumentException("dice1 is given bad value '" + newDie1 
                + "' not 0 and not [" + minDieVal + ".." + maxDieVal + "]");
        }
        if (! legitDiceValue( newDie2 )) {
            throw new IllegalArgumentException("dice2 is given bad value '" + newDie2 
                + "' not 0 and not [" + minDieVal + ".." + maxDieVal + "]");
        }
        if ((newDie1 != newDie2) && ((newDie1 == UNROLLED) || (newDie2 == UNROLLED))) {
            throw new IllegalArgumentException("Bad dice pair '" + newDie1 + "," + newDie2 
                +"', must both be UNROLLED or both be" + minDieVal + ".." + maxDieVal);
        }
        /* so either dice are both unrolled, or both rolled, and it makes no difference which one tells us */ 
        rolled = (newDie1 != UNROLLED);
        dice[0] = newDie1;
        dice[1] = newDie2;
        rdice = new Random(); // random number generator, gets started in constructor.
        resetUsedDice( ); /* calls        resetDoubletMovesCountdown( ); */
    } /* constructor */

    /**
     * allows UNROLLED or [minDiceVal to maxDiceVal]
     */
    static boolean legitDiceValue(int dieVal) {
        return (((minDieVal<= dieVal) && (dieVal <= maxDieVal)) || (dieVal == UNROLLED));
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
    public int getDie1() {
        return dice[0];
    } 

    /**
     * convenience method
     * Note: there is a dice2, and there is no dice0
     */   
    public int getDie2() {
        return dice[1];
    } 

    /**
     * Dice are named "1", "2" ... up to howManyDice (there is no dice 0!)
     * corresponding to hidden array dice[0], dice[1], respectively
     */
    public int getDie( int whichDie) {
        if (! legitDieNum(whichDie)) {
            throw new IllegalArgumentException("bad die number '" + whichDie + "', should be 1.." + howManyDice);
        }
        return dice[whichDie - 1]; 
    } /* getDice(int ) */

    /**
     * returns the value of the highest die.
     * will be 0 (??) if dice unrolled?
     * See whichUnusedDieIsHighest( ) to find out which Die holds this value.
     */
    public int lowestUnusedRoll( ) {
        if ((!rolled) || (usedDiceHowMany==howManyDice)) {
            return UNROLLED;
        } else {
            int lowestRoll = maxDieVal;
            for (int i=0; i<howManyDice; ++i) {
                if ((!used[i]) && (dice[i] < lowestRoll)) {
                    lowestRoll = dice[i];
                };
            }
            return lowestRoll;
        }
    } /* highestRoll( ) */

    /**
     * returns the value of the highest die.
     * will be 0 (??) if dice unrolled?
     * See whichUnusedDieIsHighest( ) to find out which Die holds this value.
     */
    public int highestUnusedRoll( ) {
        if ((!rolled) || (usedDiceHowMany==howManyDice)) {
            return UNROLLED;
        } else {
            int highestRoll = 0;
            for (int i=0; i<howManyDice; ++i) {
                if ((!used[i]) && (dice[i] > highestRoll)) {
                    highestRoll = dice[i];
                };
            }
            return highestRoll;
        }
    } /* highestRoll( ) */

    
    /**
     * unlike highestRoll, this tells us WHICH Unused die has the highest value.
     */
    public int whichUnusedDieIsLowest( ) {
        if ((!rolled) || (usedDiceHowMany==howManyDice)) {
            return NO_SUCH_DIE;
        } else {
            int whereLowest = 0;
            int lowestRoll = maxDieVal;
            for (int i=0; i<howManyDice; ++i) {
                if ((!used[i]) && (dice[i] < lowestRoll)) {
                    whereLowest = i;
                    lowestRoll = dice[i];
                };
            }
            return whereLowest+1; /* beware OBOB! user expects these dice to be named 1 & 2 */
        }
    } /* whichDieIsHighest( ) */

    /**
     * unlike highestRoll, this tells us WHICH Unused die has the highest value.
     */
    public int whichUnusedDieIsHighest( ) {
        if ((!rolled) || (usedDiceHowMany==howManyDice)) {
            return NO_SUCH_DIE;
        } else {
            int whereHighest = 0;
            int highestRoll = 0;
            for (int i=0; i<howManyDice; ++i) {
                if ((!used[i]) && (dice[i] > highestRoll)) {
                    whereHighest = i;
                    highestRoll = dice[i];
                };
            }
            return whereHighest+1; /* beware OBOB! user expects these dice to be named 1 & 2 */
        }
    } /* whichDieIsHighest( ) */

    /**
     * This for setting a specified individual die.
     * Can't use this to set first two dice at once! Use "roll(5,6)" to do that.
     * This expects dice numbers 1 or 2 (not array indices 0,1!!)
     * A changed die is marked as unused, and the DoubletCountdown starts over from the top!
     */
    public void setDie( int whichDie, int newRoll ) {
        if (! legitDieNum(whichDie)) {
            throw new IllegalArgumentException("Can't set value of dice#'" 
                + whichDie + "', we only have dice#1.." + howManyDice);
        }
        if (! legitDiceValue(newRoll)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll 
                + "', our dice only can roll values " + minDieVal + ".." + maxDieVal);
        }
        if (dice[whichDie - 1] != newRoll) { /* okay, changing a die */
            dice[whichDie - 1] = newRoll;
            used[whichDie - 1] = false;    
            resetDoubletMovesCountdown( );  // in case we've acquired doubles
            if ((! rolled) && (allDiceHaveValues())) { //changing to "rolled" status!
                rolled = true;
                resetUsedDice( );  // calls resetDoubletMovesCountdown( );
            } // changed rolled status
        } // changed a die
    } /* setDie( ) */

    /**
     * I don't want to have a setRolled( ) because I think this is better: 
     * this rolls all dice and sets rolled to true.
     * To invalidate the dice, use "reset( )"
     * Unfortunately, if receiving a networked roll, we need to set rolled to true, I guess.
     * Note: there is a version of this for setting two specific dice values "roll(int,int)" 
     * which is called by this.
     */
    public void roll( ) {
        int newDie1 = rdice.nextInt(maxDieVal) + minDieVal;
        int newDie2 = rdice.nextInt(maxDieVal) + minDieVal;
        roll(newDie1,newDie2); // this would have to pass array if I don't have 2 dice??
        if (howManyDice > 2) {
            throw new IllegalArgumentException("I don't yet know how to handle more than 2 dice.");
//             for (int i=0; i<howManyDice; ++i) {
//                 dice[i] = rdice.nextInt(maxDieVal) + minDieVal;
//             }
        }
        //        System.out.println("I just rolled the dice and got " + this.toString( ) + "!");
    }

    /**
     * changes the (first two) dice to specified values
     */
    public void roll(int newRoll1, int newRoll2 ) {
        if (howManyDice > 2) {
            throw new IllegalArgumentException("I don't yet know how to handle more than 2 dice.");
        }
        if (! legitDiceValue(newRoll1)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll1 
                + "', our dice only can roll " + minDieVal + ".." + maxDieVal);
        }
        if (! legitDiceValue(newRoll2)) {
            throw new IllegalArgumentException("Bad dice value '" + newRoll1 
                + "', our dice only can roll " + minDieVal + ".." + maxDieVal);
        }
        
        dice[0] = newRoll1;
        dice[1] = newRoll2;
        rolled = true;
        resetUsedDice( ); // calls resetDoubletMovesCountdown( );

        System.out.println("I changed the rolled dice to " + this.toString( ) + "!");
    } 

    /* roll( )

    /**
     * If we want to manually set the dice values, set them all before setting "rolled" to true
     * because I will squawk if some of the dice don't have values (are UNROLLED).
     * It's probably easiest to just call roll(newDie1val, newDie2val)
     * (or do what roll(int,int) does: call roll( ) and then change some of their values.
     * 
     * Hmmm, if the dice are already rolled, this has no effect, (doesn't reset the used and doubletcountdown)
     */
    public void setRolled(boolean newRolled) {
        if (newRolled == false) {
            rolled = false;
            reset( );
        } else if (allDiceHaveValues()) {
            if (!rolled) { /* if this is a change ... */
                rolled = true;
                resetUsedDice( ); /* calls             resetDoubletMovesCountdown( ); */
            }
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
     * gets the private "rdice" random number generator.
     * Why should anybody need it? I don't know, but I'm revealing it
     * for unit testing. Might be null!
     */
    public Random getRDice( ) {
        return rdice;
    }

    /**
     * note: if I get doubles, I'm keeping track of 4 usable dice!
     * Users are speaking in terms of die#1 and die#2 which use our private used[0] and used[1] respectively.
     * And I'm sneaking up on the idea of coding doubles as 4 (identical) dice 1..4, so allowing up to 4 here.
     */
    public boolean getUsedDie( int newUsedDie ) {
        if ( ! ((1<= newUsedDie) && (newUsedDie <= maxMovesCount)) ) {
            throw new IllegalArgumentException("bad newUsedDie '" + newUsedDie + "', should be 1.." + maxMovesCount);
        }
        return used[newUsedDie-1];
    }

    /**
     * Says how many of the dice have been 'used' so far.
     */
    public int getUsedDiceHowMany(  ) {
        //int howMany = 0;
        //for (int i=0; i<maxMovesCount; ++i) { if (used[i]) { howMany++; } }
        return usedDiceHowMany;
    } /* getUsedDiceHowMany( ) */

    /**
     * 
     */
    public void setUsedDie(int newUsedDie, boolean newUsedTF) {
        /* Beware! Outside users talk about die1 and die2 while in here we say used[0] & used[1] */
        if ( ! ((1<= newUsedDie) && (newUsedDie <= maxMovesCount)) ) {
            throw new IllegalArgumentException("bad newUsedDie '" + newUsedDie + "', should be 0, 1, or 2");
        }
        if (used[newUsedDie-1] != newUsedTF) { // change!
            used[newUsedDie-1] = newUsedTF;
            if (newUsedTF) {
                usedDiceHowMany++;
            } else {
                usedDiceHowMany--;
            }
        }
    } /* setUsedDie( ) */

    /**
     * Puts the dice into unrolled state without dice values, ready for new roll.
     */
    public void reset( ) {
        for (int i=0; i<howManyDice; ++i) {
            dice[i] = UNROLLED;
        }
        rolled = false;
        resetUsedDice( ); /* calls   resetDoubletMovesCountdown( ); */
    }

    /**
     * Tells all the dice that they are unused.
     * This also resetDoubletMovesCountdown( ) so don't make it in turn call us!
     */
    public void resetUsedDice( ) {
        for (int i=0; i<maxMovesCount; ++i) {
            used[i] = false;
        }
        usedDiceHowMany = 0;
        resetDoubletMovesCountdown( );
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
        } else if ( isDoubles( ) ) /* or rolledBonus( ) */ {
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
        System.err.println("hi, who is doing setDoubletMovesCountdown( )");
        Thread.dumpStack( ); // sends it to standard error

        if (! (( 0 <= newCountDown) && (newCountDown <= maxMovesCount))) {
            throw new IllegalArgumentException("bad doubletMovesCountdown '" 
                + newCountDown + "', can only be 0.." + maxMovesCount);
        }
        doubletMovesCountdown = newCountDown;
    }

    public String toString( ) {
        // StringBuffer temp = new StringBuffer("[");
        // for (int i=0; i<(howManyDice-1); ++i) { temp.append(dice[i] + ","; }
        // temp.append(dice[howManyDice-1] + "]");
        // return temp.toString( );
        StringBuffer usedSB = new StringBuffer("[used:");
        for (int i=0; i<maxMovesCount; ++i) {
            if (used[i]) {
                usedSB.append( i + " ");
            }
        }
        usedSB.append("]");
        String doubletString = "[" + doubletMovesCountdown + " doubletCountdown]";
        return "[" + dice[0] + "," + dice[1] + "]" + usedSB.toString( ) + doubletString;
    }

} /* class Dice */
