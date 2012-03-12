import java.util.*;  // provides Collection ?

/**
 * Write a description of class StartGame here.
 * 
 * @author J & M & J 
 * @version 2012 Mar 11
 */
public class StartGame extends Strategy
{
    // instance variables - replace the example below with your own
    private int currColor = 0;
    private Board currBoard = null;

    /**
     * Constructor for objects of class StartGame
     */
    public StartGame()
    {

    }

    /**
     * 
     * 
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
        if (currBoard == null) {
            throw new NullPointerException("StartGame.pickBestMove( ) can't analyze null board.");
        } else {
            currBoard = aBoard;
        }

        Move bestMove = null;

        Dice theDice = currBoard.getMyDice( ); // will be clone, so we can't hurt it
        int lowDice = theDice.getDie1( );
        int highDice = theDice.getDie2( );
        boolean dice1isLow = true;
        if (lowDice > highDice) {
            dice1isLow = false;
            lowDice = theDice.getDie2( );
            highDice = theDice.getDie1( );
        }

        switch(lowDice) {
            case  1: bestMove = lowDice1(lowDice, highDice, dice1isLow ); break;
//             case  2: bestMove = lowDice2(lowDice, highDice ); break;
//             case  3: bestMove = lowDice3(lowDice, highDice ); break;
//             case  4: bestMove = lowDice4(lowDice, highDice ); break;
//             case  5: bestMove = lowDice5(lowDice, highDice ); break;
//             case  6: bestMove = lowDice6(lowDice, highDice ); break;
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values 1..6!");
        }

        return bestMove;
    }

    /**
     * e
     * 
     */
    public Move lowDice1(int lowDice, int highDice, boolean dice1isLow  ) {
        int lowPoint = 0;
        int highPoint = 0;
        /* what about doubles?? Maybe have special method?? */
        // suppose the best move for 1,2 is point5 moves 1, point6 moves 2
        // briefly store the point that will make the low roll move and the point that will
        // make the higher roll move
        switch(highDice) {
            case  1: 

            break;
            case  2: //suppose the best move for 1,2 is point5 moves 1, point6 moves 2
            lowPoint = 5;
            highPoint = 6;
            // these are fake!
            break;
            case  3: 
            lowPoint = 5;
            highPoint = 6;
            // these are fake!
            break;
            case  4: 
            lowPoint = 5;
            highPoint = 6;
            // these are fake!
            break;
            case  5: 
            lowPoint = 5;
            highPoint = 6;
            // these are fake!
            break;
            case  6: 
            lowPoint = 5;
            highPoint = 6;
            // these are fake!
            break;
            default: 
            throw new IllegalArgumentException("StartGame.pickBestMove( ) only knows dice values"
                + " 1..6! not '" + highDice + "'");
        }

        Move bestMove = null;        
        PartialMove pm0 = null;
        PartialMove pm1 = null;
        if (dice1isLow) { // 1,2
            int newEnd = currBoard.endPointMovingFrom(lowPoint, lowDice, currColor);
            // endPointMovingFrom( int startPoint, int steps, int playerColor/*, Board board*/) 
            pm0 = new PartialMove( lowPoint, lowDice, newEnd, currBoard.myGame, currColor, 1);
            //PartialMove(int newStart, int newRoll, int newEnd, Game newGame, int newColor, int newWhichDie)
            newEnd = currBoard.endPointMovingFrom(highPoint, highDice, currColor);
            pm1 = new PartialMove( highPoint, highDice, newEnd, currBoard.myGame, currColor, 2);
        } else { // 2,1
           int newEnd = currBoard.endPointMovingFrom(lowPoint, lowDice, currColor);
            // endPointMovingFrom( int startPoint, int steps, int playerColor/*, Board board*/) 
            pm0 = new PartialMove( lowPoint, lowDice, newEnd, currBoard.myGame, currColor, 2); //<--2!!
            //PartialMove(int newStart, int newRoll, int newEnd, Game newGame, int newColor, int newWhichDie)
            newEnd = currBoard.endPointMovingFrom(highPoint, highDice, currColor);
            pm1 = new PartialMove( highPoint, highDice, newEnd, currBoard.myGame, currColor, 1);
        }
        ArrayList<PartialMove> theNewPartials = new ArrayList<PartialMove>( );
        theNewPartials.add(pm0);
        theNewPartials.add(pm1);
        try {
        bestMove = new Move(theNewPartials, currColor,currBoard );
        //Move(ArrayList<PartialMove> theNewPartials, int myNewColor, /*Game myNewGame*/ Board myNewStarterBoard /*Final?*/)
    } catch(Exception e) {
        throw new IllegalArgumentException(e);
    }
        return bestMove;
    }

    // StartGame
}
