import java.util.*;   // for collections

/**
 * class AI tries to think of good moves.
 * 
 * @author (Mike Roam) 
 * @version (a version number or a date)
 * Gavin suggestion: that AI be an interface that can be implemented in various ways.
 * 
 */
public class AI
{
    private int myColor = Game.black;
    private Game myGame = null; /* gets set in constructor. Perhaps can be changed 
        if there are multiple boards ? */

        
    /**
     * Constructor for objects of class AI
     */
    public AI(Game myNewGame) {
        myGame = myNewGame;        
    } /* Constructor */
    

    /**
     * AI's main method
     * Should this return a "Move" and somebody else handles moving?? -gy suggestion 2011
     */
    public void thinkAndPlay() throws BadBoardException, BadMoveException, BadPartialMoveException {
        System.out.println("computer is thinking...");
        if (myGame.getCurrentPlayer( ) != myColor) {
            throw new BadBoardException("AI can't move now, it's not AI's turn!");
        }
        if ( ! myGame.myBoard.myDice.getRolled( ) ) {
            myGame.getMyBoard().myDice.roll( );
        }
        ArrayList<PartialMove> myMoves = myGame.myBoard.allLegalPartialMoves( myColor/* , myGame*/);
//      ArrayList<Move> myMoves = myGame.myBoard.allLegalMoves( myColor/* , myGame*/);
        /* might not have any moves! */
        //System.out.println(myMoves);
        
        PartialMove gonnaMove = bestPartialMoveOf( myMoves ); /* might be null */
        /* Move gonnaMove = bestMoveOf( myMoves ); /* might be null */
        System.out.println(gonnaMove);
        
        /* Dang: superMove wants to know which dice was used */
//        for (PartialMove p : gonnaMove.getMyPartials()) {
//            myGame.myBoard.handlePoint( p.getStart( )   );
//            myGame.doPartialMove(p );
        myGame.getMyBoard( ).doPartialMove( gonnaMove );
            /* to make the move actually happen, check out Game's methods:
             superMove( ), forfeit( ). Note: superMove( ) calls endTurn( )
             */
//        }
    } // thinkAndPlay()
    
    
    
    /**
     * for now just picks first PartialMove. This needs some major work to figure out good (combo-of-partials) Move
     * Might want to compare boards using their getHowManyUnprotected( ) or  getHowManyProtected( ) 
     * or the new    getProtectedPointScore( )!! And should also compare pip counts since the opponent
     * might have got sent back!
     */
    public PartialMove bestPartialMoveOf( ArrayList<PartialMove> possibleMoves ) /*throws BadMoveException*/ {
        System.out.println("AI's doBestPartialMove( ) for now just picks first move. Fix!!");
        if ( possibleMoves == null ) {
            throw new NullPointerException("no possible Moves! Maybe just skip a turn?");
        }
        if ( possibleMoves.isEmpty( ) ) {
            return null;
        }
        PartialMove bestMove = possibleMoves.get(0); /* counting from 0, just like arrays! */
        System.out.println( "My AI is dumb and is just choosing first possible move. Will move to " + bestMove );
        return bestMove;
    } /* bestPartialMoveOf */
    
    
    /**
     * This needs some major work to figure out good (combo-of-partials) Move
     * Might want to compare boards using their getHowManyUnprotected( ) or  getHowManyProtected( ) 
     * or the new    getProtectedPointScore( )!! And should also compare pip counts since the opponent
     * might have got sent back!
     */
    public Move bestMoveOf( ArrayList<Move> possibleMoves ) /*throws BadMoveException*/ {
        System.out.println("AI's doBestMove( ) for now just picks first move. Fix!!");
        if ( possibleMoves == null ) {
            throw new NullPointerException("no possible Moves! Maybe just skip a turn?");
        }
        if ( possibleMoves.isEmpty( ) ) {
            return null;
        }
        Move bestMove = possibleMoves.get(0); /* counting from 0, just like arrays! */
        System.out.println( "My AI is dumb and is just choosing first possible move. Will move to " + bestMove );
        return bestMove;
    } /* bestMoveOf */
    
} /* class AI */
