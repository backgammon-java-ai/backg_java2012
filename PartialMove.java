/**
 * Immutable! (Only has constructor and private fields and getters.)
 * Holds a "single" part of a move.
 * (one blot moves from point or bar to point or bear off)
 * 
 * @author (Mike Roam) 
 * @version (2012 Feb 11)
 */
public class PartialMove implements Comparable<PartialMove>
{
    
    private final int roll;  /* keeping this for reference, but creator decides and 
    specifies end location, since bearing off sometimes allows higher roll if you
    there are no pieces that can use that full higher roll */
    private final int whichDie;  /* Game.supermove( ) wants to know which die you're using */

    /* note: start and end can be 1..Board.howManyPoints(==24) or
    use Board static final ints W/B_BAR_LOC, 
    W/B_BEAR_OFF and W/B_PAST_BEAR_OFF */
    private  int start = Board.ILLEGAL_MOVE ;
    private  int end  = Board.ILLEGAL_MOVE; 
    private  int color = Board.neutral;
    /* private Game myGame = null; /* is this necessary? */
    private Board myBoard = null; /*For comparing the quality score of moves*/


    // public static final int bar = 0;
    // public static final int bear = -1; /* just a symbol: is it bad to mismatch board?? */

    /**
     * Constructor for objects of class MoveOne
     * Note: start + roll might not equal end when bearing off
     * but creator of PartialMove should use 
     * int Board.endPointMovingFrom(int start, int steps, int color)
     * to calculate end.
     * and no higher blots can use a too-high roll.
     */
    public PartialMove(int newStart, int newRoll, int newEnd, /*Game newGame*/ Board newBoard, int newColor, int newWhichDie) 
    /*throws BadPartialMoveException, BadBoardException*/ {
        if (newBoard == null) {
            /* throw new BadBoardException*/
            throw new NullPointerException("Moves must know the board they belong to, can't be null board");
        }
        //myGame = newGame;
        myBoard = newBoard;
        if (Board.legitStartLoc( newStart,newColor ) && Board.legitEndLoc( newEnd, newColor ) && 
        (Dice.legitDiceValue( newRoll )) && Board.legitColor( newColor )) {
            start = newStart;
            roll = newRoll;
            end = newEnd;
            color = newColor;
            whichDie = newWhichDie;
        } else {
            String msg = "[bad PartialMove start:" + newStart + " roll:" + newRoll 
                + " end:" + newEnd + " color:" + newColor + " whichDie:" + newWhichDie + "]";
            throw new IllegalArgumentException/*BadPartialMoveException*/(msg);
        }
    } // constructor with values

    /**
     * This could be done with the click (?? "handlePoint" ?? ) maybe? but then have to unclick?
     * 
     * This does NOT verify that any of the dice match the "roll" (distance moved), because we
     * might be constructing all kinds of hypothetical moves (if they do that, then we could do
     * this) without changing the dice for each move we consider.
     */
    public boolean isPossible( /*Board theBoard*/ ) {
        if ( ! Board.legitStartLoc( start, color ) ) {
            return false;
        }
        if (myBoard.getHowManyBlotsOnPoint(start, color) < 1 ) {
            return false;
        }
        /* start might be on bar, so using the fancy getColorOnPoint( ) */
        if (myBoard.getColorOnPoint( start,color ) != color) {
            return false;
        }
        int calculatedEnd = myBoard.endPointMovingFrom( start, roll, color);
        if (end != calculatedEnd) {
            return false;
        }
        // does legitEndLoc check color compatibility? probably just checks that it is not out of bounds
        if (! myBoard.canLandOn(end, color)) {
            return false;
        }

        return true;
    }

    public int getStart( ) {
        return start;
    }

    public int getEnd( ) {
        return end;
    }

    public int getColor( ) {
        return color;
    }

    public int getRoll( ) {
        return roll;
    }

    public int getWhichDie( ) {
        return whichDie;
    }

    public String toString( ) {
        return ("[start:" + start +  ", end:" + end + ", roll:" + roll + ", color:" + color+"]");
    }

    /**
     * has to check values inside PartialMoves
     */
    public boolean equals(Object other) throws ClassCastException  {
        // boolean identical = false;
        // do they have same values?
        if (!(other instanceof PartialMove)) {
            return false;
        }
        PartialMove otherPM = (PartialMove) other;
        return ((this.start==otherPM.start) && (this.roll==otherPM.roll) && (this.end==otherPM.end) && (this.color==otherPM.color) && (this.whichDie==otherPM.whichDie)); 
    } // equals( )

    public int hashCode() {
        return start + (roll*26) + (end * 6 * 26) + (whichDie * 2 * 6 * 26) + (color * 2 * 2 * 6 * 26);
    }

    /*
     * required function for implementing "Comparable"
     * returns negative integer, 0, or a positive integer depending on whether the 
     * "this" is less than, equal to, or greater than the other object.
     * 
     * ?? Does it matter if these are using the same dice "whichDie"????
     */
    public int compareTo(PartialMove other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.color < other.color) {
            return -1;
        } else if (this.color > other.color) {
            return 1;
        }
        if (this.start < other.start) {
            return -1;
        } else if (this.start > other.start) {
            return 1;
        }
        /* okay, starts are equal */
        if (this.roll < other.roll) {
            return -1;
        } else if (this.roll > other.roll) {
            return 1;
        }
        /* starts and rolls are equal */
        if (this.end < other.end) {
            return -1;
        } else if (this.end > other.end) {
            return 1;
        } else {
            /* shouldn't get here since this implies start,roll & end are tied which
            the first ".equals( )" should have stopped! */
            String myMsg = "PartialMove '"+ this.toString( )+ "' isn't comparable to PartialMove '" + other.toString( ) + "'";
            throw new ClassCastException(myMsg);
        }
    } // CompareTo
    // class PartialMove
}
