import java.util.*;  // provides Collection ?

/**
 * Write a description of class StartGame here.
 * 
 * @author J & M & J 
 * @version 2012 Mar 11
 */
public class StartGameStrategy extends Strategy
{
    // instance variables - replace the example below with your own
    private int currColor = 0;
    private Board currBoard = null;
    private Dice currDice = null;
    boolean dice1isLow = true;
    int lowDice = 0;
    int highDice = 0;

    /**
     * Constructor for objects of class StartGame
     */
    public StartGameStrategy()
    {

    }

    /**
     * @param  currentBoard is ideally ready for start of move, isn't partially moved already
     * @return     Move is a collection of partial moves
     */
    public Move pickBestMove(Board aBoard, int theColor)
    {
        if (!Board.legitColor(theColor)) {
            throw new IllegalArgumentException("StartGame.pickBestMove( ) got bad color '" + theColor + "'");
        } else {
            currColor = theColor;
        }
        if (aBoard == null) {
            throw new NullPointerException("StartGame.pickBestMove( ) can't analyze null board.");
        } else {
            currBoard = aBoard;
        }

        Move bestMove = null;

        currDice = currBoard.getMyDice( ); // will be clone, so we can't hurt it
        lowDice = currDice.getDie1( );
        highDice = currDice.getDie2( );

        if (lowDice > highDice) {
            dice1isLow = false;
            lowDice = currDice.getDie2( );
            highDice = currDice.getDie1( );
        }

        switch(lowDice) {
            case  1: bestMove = lowDice1(); break;
            case  2: bestMove = lowDice2(); break;
            case  3: bestMove = lowDice3(); break;
            case  4: bestMove = lowDice4(); break;
            case  5: bestMove = lowDice5(); break;
            case  6: bestMove = lowDice6(); break;
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values 1..6!");
        }

        return bestMove;
    }

    /**
     * This will calculate the starting moves for dice 6 & anything.
     */
    public Move lowDice6() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  6: 
            return dealWithDoubles(lowDice);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will calculate the starting moves for dice 5 & anything.
     */
    public Move lowDice5() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  5: 
            return dealWithDoubles(lowDice);
            case  6: 
            // hmmm, the low move should be the 5, but we care about the ORDER of the moves!!
            return buildMove(/*startLow*/18,/*endLow*/13,  /*startHigh*/24,/*endHigh*/18, /*makeTheHighMoveFirst */true);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will calculate the starting moves for dice 4 & anything.
     */
    public Move lowDice4() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  4: 
            // these are fake!
            return dealWithDoubles(lowDice);
            case  5: 
            return buildMove(/*startLow*/13,/*endLow*/9,  /*startHigh*/13,/*endHigh*/8);
            case  6: 
            return buildMove(/*startLow*/24,/*endLow*/18,  /*startHigh*/18,/*endHigh*/14, /*makeTheHighMoveFirst */true);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will calculate the starting moves for dice 3 & anything.
     */
    public Move lowDice3() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  3: 
            return dealWithDoubles(lowDice);
            case  4: 
            return buildMove(/*startLow*/13,/*endLow*/10,  /*startHigh*/13,/*endHigh*/9);
            case  5: 
            return buildMove(/*startLow*/6,/*endLow*/3,  /*startHigh*/8,/*endHigh*/3);
            case  6: 
            return buildMove(/*startLow*/13,/*endLow*/10,  /*startHigh*/24,/*endHigh*/18);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will calculate the starting moves for dice 2 & anything.
     */
    public Move lowDice2() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  2:  
            return dealWithDoubles(lowDice);
            case  3: 
            // these are fake!
            return buildMove(/*startLow*/13,/*endLow*/11,  /*startHigh*/13,/*endHigh*/10);
            case  4: 
            // these are fake!
            return buildMove(/*startLow*/19,/*endLow*/21,  /*startHigh*/6,/*endHigh*/2);
            case  5: 
            // these are fake!
            return buildMove(/*startLow*/13,/*endLow*/11,  /*startHigh*/13,/*endHigh*/8);
            case  6: 
            // these are fake!
            return buildMove(/*startLow*/13,/*endLow*/11,  /*startHigh*/24,/*endHigh*/18);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will calculate the starting moves for dice 1 & anything.
     */
    public Move lowDice1() {
        int lowStartPoint = 0;
        int highStartPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  1: 
            return dealWithDoubles(lowDice);
            case  2: //suppose the best move for 1,2 is point5 moves 1 to 4, point6 moves 2 to 4
            // these are fake!
            return buildMove(/*startLow*/6,/*endLow*/5,  /*startHigh*/13,/*endHigh*/11);
            case  3: 
            // these are fake!
            return buildMove(/*startLow*/6,/*endLow*/5,  /*startHigh*/8,/*endHigh*/5);
            case  4: 
            // these are fake!
            return buildMove(/*startLow*/6,/*endLow*/5,  /*startHigh*/13,/*endHigh*/9);
            case  5: 
            // these are fake!
            return buildMove(/*startLow*/24,/*endLow*/23,  /*startHigh*/23,/*endHigh*/18);
            case  6: 
            // these are fake!
            return buildMove(/*startLow*/8,/*endLow*/7,  /*startHigh*/13,/*endHigh*/7);
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }
    }

    /**
     * This will happen if order of small die move and then large die move.
     * It it HAS to move in the reverse order, call this with boolean makeTheHighMoveFirst = true.
     */
    public Move buildMove(int startLow, int endLow, int startHigh, int endHigh) {
        return buildMove(startLow, endLow, startHigh, endHigh,/*makeTheHighMoveFirst */ false);
    }

    /**
     * The order of these moves might matter!
     */
    public Move buildMove(int startLow, int endLow, int startHigh, int endHigh, boolean makeTheHighMoveFirst ) {
        Move bestMove = null;        
        PartialMove pm0 = null;
        PartialMove pm1 = null;

        if (dice1isLow) { // 1,2
            pm0 = new PartialMove( startLow, lowDice, endLow, currBoard, currColor, 1);
            //PartialMove(int newStart, int newRoll, int newEnd, Board newBoard, int newColor, int newWhichDie)
            pm1 = new PartialMove( startHigh, highDice, endHigh, currBoard, currColor, 2);
        } else { // 2,1
            pm0 = new PartialMove( startLow, lowDice, endLow, currBoard, currColor, 2); //<--2!!
            //PartialMove(int newStart, int newRoll, int newEnd, Board newBoard, int newColor, int newWhichDie)
            pm1 = new PartialMove( startHigh, highDice, endHigh, currBoard, currColor, 1);
        }

        ArrayList<PartialMove> theNewPartials = new ArrayList<PartialMove>( );
        if (makeTheHighMoveFirst) {
            theNewPartials.add(pm1);
            theNewPartials.add(pm0);
        } else {
            theNewPartials.add(pm0);
            theNewPartials.add(pm1);
        }
        try {
            bestMove = new Move(theNewPartials, currColor,currBoard );
            //Move(ArrayList<PartialMove> theNewPartials, int myNewColor, Board myNewStarterBoard /*Final?*/)
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
        return bestMove;
    }

    /**
     * Only six possible doubles to deal with.
     * Note: starting board has whites on point 1 (2 of 'em), 12 (5), 17 (3), 19 (5)
     * blacks on points 6 (5), 8 (3), 13 (5), 24 (2)
     * 
     * This is rigged to only deal with black.
     */
    Move dealWithDoubles(int theRoll) {
        switch(theRoll) {
            case  1: 
            // gonna move 2 blots from (black's) 8 to 7, & 2 blots from 6 to 5
            //  note: the way our board is numbered, 
            // for white this will be 2 from 17 to 18 and 2 from 19 to 20
            return move2And2(/*start1*/8,/*end1*/7, /*start2*/6,/*end2*/5);

            case 2:  // bug? was coded with both go 1->3 and then from 3->5
            return move2And2(/*start1*/24,/*end1*/22, /*start2*/22,/*end2*/20);

            case 3:
            return move2And2(/*start1*/13,/*end1*/10, /*start2*/10,/*end2*/7);

            case 4: 
            return move2And2(/*start1*/13,/*end1*/9, /*start2*/24,/*end2*/20);

            case 5:
            return move2And2(/*start1*/13,/*end1*/8, /*start2*/8,/*end2*/3);

            case 6:
            return move2And2(/*start1*/13,/*end1*/7, /*start2*/24,/*end2*/18);

            default:
            throw new IllegalArgumentException("Bad dice value '" + theRoll + "', should be 1..6!!!!");
        }
    }

    /**
     * This was briefly called "dealWithDoublesOf1" but now is
     * more general purpose for dealing with all doubles. Assumes
     * you're moving two blots from (point) start1 to end1, and moving 
     * another 2 blots from start2 to end2.
     */
    Move move2And2(int start1, int end1, int start2, int end2) {
        Move bestMove = null;        
        PartialMove pm0 = new PartialMove( start1, 1, end1, currBoard, currColor, 1);
        PartialMove pm1 = new PartialMove( start1, 1, end1, currBoard, currColor, 1);
        PartialMove pm2 = new PartialMove( start2, 1, end2, currBoard, currColor, 2);
        PartialMove pm3 = new PartialMove( start2, 1, end2, currBoard, currColor, 2);

        ArrayList<PartialMove> theNewPartials = new ArrayList<PartialMove>( );
        theNewPartials.add(pm0);
        theNewPartials.add(pm1);
        theNewPartials.add(pm2);
        theNewPartials.add(pm3);
        try {
            bestMove = new Move(theNewPartials, currColor, currBoard );
            //Move(ArrayList<PartialMove> theNewPartials, int myNewColor, Board myNewStarterBoard /*Final?*/)
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
        return bestMove;
    } /* move2And2( ) */
} /* class StartGameStrategy */
