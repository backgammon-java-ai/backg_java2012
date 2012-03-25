import java.util.*;   // for collections

/**
 * class AI tries to think of good moves.
 * 
 * @author (Mike Roam) 
 * @version (2012 Feb 6)
 * Gavin suggestion: that AI be an interface that can be implemented in various ways.
 */
public class AI {
    private int myColor = Game.black;
    private Game myGame = null; /* gets set in constructor. Perhaps can be changed 
    if there are multiple boards ? */

    private Strategy myStrategy = new StartGameStrategy( );
    private double cautious = 0.5; /* our mood can move from 0.0 to 1.0. 
    Doesn't make any difference yet, but is a (useless) argument to board.getSuperMegaHappyScore( ) */

    final static double cautiousMinimum = 0.0;
    final static double cautiousMaximum = 1.0;

    /**
     * Constructor for objects of class AI
     */
    public AI(Game myNewGame) {
        myGame = myNewGame;
    } /* Constructor */

    /**
     * our cautiousness mood can move from 0.0 (brave) to 1.0 (timid). 
     */
    public double getCautious( ) {
        return cautious;
    }

    public int getColor( ) {
        return myColor;
    }

    /**
     * our cautiousness mood can move from 0.0 (brave) to 1.0 (timid).
     */
    public void setCautious(double newCautious) {

        if (! (cautiousMinimum<= newCautious) && (newCautious <= 1.0)) {
            throw new IllegalArgumentException("bad cautious-ness setting '" + newCautious 
                + "' for AI, can only be " + cautiousMinimum + ".." + cautiousMaximum);
        } else {
            cautious = newCautious;
        }
    } /* setCautious */

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
            myGame.getMyBoard().myDice.roll( ); // if onBar then calls handleBar, if can'tMove then calls forfeit
        }

        Move aiMove = myStrategy.pickBestMove(myGame.getMyBoard( ), myColor);

        /* might not have any moves! */
        if (aiMove == null) {
            myStrategy = switchStrategy( );
            aiMove = myStrategy.pickBestMove(myGame.getMyBoard( ), myColor);
            /* what if this is null?? */
            if (aiMove == null) {
                throw new NullPointerException("Damn, I don't know where to move.");
            }
        }
        //System.out.println(myMoves);

        System.out.println("AI will move to '" + aiMove + "'");

        for (PartialMove p : aiMove.getMyPartials()) {
            //            myGame.myBoard.handlePoint( p.getStart( )   );
            //            myGame.doPartialMove(p );
            System.err.println("DoubletCountdown is '" + myGame.getMyBoard( ).getMyDice().toString( ) + "'");
            myGame.getMyBoard( ).doPartialMove( p );
            /* to make the move actually happen, check out Game's methods:
            superMove( ), forfeit( ). Note: superMove( ) calls endTurn( )
             */
            //        }
        }
    } // thinkAndPlay()

    /**
     * AI's main method
     * Should this return a "Move" and somebody else handles moving?? -gy suggestion 2011
     */
    public void oldThinkAndPlay() throws BadBoardException, BadMoveException, BadPartialMoveException {
        System.out.println("computer is thinking...");
        if (myGame.getCurrentPlayer( ) != myColor) {
            throw new BadBoardException("AI can't move now, it's not AI's turn!");
        }
        if ( ! myGame.myBoard.myDice.getRolled( ) ) {
            myGame.getMyBoard().myDice.roll( ); // if onBar then calls handleBar, if can'tMove then calls forfeit
        }
        // if ( ! myGame.myBoard.onBar(myColor)) { myGame.myBoard.
        ArrayList<PartialMove> myMoves = myGame.myBoard.allLegalPartialMoves( myColor/* , myGame*/);
        // how about trying aLegalMove( ) instead of allLegalPartialMoves( )????
        //      ArrayList<Move> myMoves = myGame.myBoard.allLegalMoves( myColor/* , myGame*/);
        /* might not have any moves! */
        //System.out.println(myMoves);

        PartialMove gonnaMove = bestPartialMoveOf( myMoves ); /* might be null */
        /* Move gonnaMove = bestMoveOf( myMoves ); /* might be null */
        System.out.println("AI will move to '" + gonnaMove + "'");

        //        for (PartialMove p : gonnaMove.getMyPartials()) {
        //            myGame.myBoard.handlePoint( p.getStart( )   );
        //            myGame.doPartialMove(p );
        myGame.getMyBoard( ).doPartialMove( gonnaMove );
        /* to make the move actually happen, check out Game's methods:
        superMove( ), forfeit( ). Note: superMove( ) calls endTurn( )
         */
        //        }
    } // oldThinkAndPlay()

    /**
     * hmmm, we might get here if startGameStrategy is stuck,
     * so maybe we should switch to PointBuildStrategy.
     * 
     * hmmm, when do we switch from what to what?
     */
    public Strategy switchStrategy( ) {
        /* if it is ai's first move, return StartGameStrategy */
        /* if it is time for a race... */
        /* if we are 25% ahead in the pip count 
        OR if we have 5 points out of any group of 6 
        AND they are between the opponent and its bear out zone */
        // if (myStrategy.instanceOf(StartGameStrategy)) {
        return new PointBuildStrategy( );
        // }
    }

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
        if (cautious > 0.5) {
            // be more timid
        }
        Move bestMove = possibleMoves.get(0); /* counting from 0, just like arrays! */
        System.out.println( "My AI is dumb and is just choosing first possible move. Will move to " + bestMove );
        return bestMove;
    } /* bestMoveOf */
    /* class AI */ 
}