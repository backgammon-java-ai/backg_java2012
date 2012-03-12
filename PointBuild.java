
/**
 * PointBuild Strategy - tries to build protection on favorite points
 * 
 * @author J^2+M
 * @version 2012 Mar 6
 */
public class PointBuild extends Strategy
{
    // instance variables 
 //   Board currBoard = null;
        /* scoring, */
    public final static double valueOfBearedOffPiece = 1000.0; /* for convenience of math & testing */
    public final static double penaltyOfBarPiece = -100.0;/* for convenience of math & testing */



    /**
     * Constructor for objects of class PointBuild
     */
    public PointBuild()
    {
        
    }

    /**
     * Given a board, will tell us the "best" move.
     */
    Move pickBestMove(Board currentBoard, int theColor) {
        Move newMove = null;
        //currBoard = currentBoard; // might be null
        throw new IllegalArgumentException("The 'PointBuild' Strategy doesn't work yet.");
        // return newMove;
    }
    
   /**
      * For comparing boards so we can decide what move to use.
      * Should take into account if we're in a risk-averse mood or not.
      * Cautious is a number from 0 (carefree) .. 1 (cautious)
      *
      * Answer will be in what range???
      * High Score is good!
      * In the 3BlotGame, with cautious 0.5 (not doing anything), I'm expecting smhscore == 20.5
      * 
      * Should pipcount figure in this? 
      * (Is it implicitly hiding in allPointScore? Not really except
      * pieces on bar get penalized)
      */
     public double superMegaHappyScore(Board currentBoard, double cautious, int playerColor ) {
         int opponentColor = Board.theReversePlayerColor( playerColor);
         double theScore = 0;
         /* zero-sum game: we get happier from making the opponent unhappy! */
         /* but what "cautious" mood should we use when calculating opponent's board? */
         theScore = getAllPointScore(currentBoard, playerColor, cautious)  
             - getAllPointScore(currentBoard, opponentColor, cautious);
             /*     + pipCount(playerColor) - pipCount(opponentColor); */
             /* not including pipcount for now since it is visible on board. */
         /* Should we score any CHANGE in opponent's pipcount? 
          * Not only that, but how important the point was to them that they lost 
          * though that latter idea contained in their AllPointScore */;         
         return theScore;
     } /* superMegaHappyScore */
     
     
     
     /**
      * Adds up the scores (values) of the importance of the points with blots.
      * Useful for comparing boards.
      * howImportantIsThisPoint() gives more value to protected points. 
      * Should we give even higher value to highly protected points, say by 
      * multiplying like in the comments??
      * 
      * For testing, good to know that the 3Blot game has black on 20 & 12 (absolute locations) 
      * and white on 4.
      * So from black's point of view, she is on 5 & 13, worth 21 and 6 respectively. 
      * Since single blots we divide by 4.
      * Total black score should be 21/4 (5.25) & 6/4 (1.5) == 27/4 == 6.75
      * White's 4 is worth 17, which gets divided by 4 for howImportant=4.25 
      * and total white score = 4.25
      */
     public double getAllPointScore(Board currentBoard, int playerColor, double playerCautious ) {
         if (! Board.legitPlayerColor(playerColor)) {
             throw new IllegalArgumentException("bad color '" + playerColor + "'");
         }
         double score = 0;
 
         for (int i=1; i<=Board.howManyPoints; i++) {
             /* if ((getColorOnPoint(i)==color)  && (getHowManyBlotsOnPoint(i) > 1)) { */
             int howManyMyBlots = currentBoard.howMuchProtected( i, playerColor);
             if (howManyMyBlots > 0) {
                 score+= howImportantIsThisPoint( currentBoard, i, playerColor, playerCautious);
             //    score+= (howImportantIsThisPoint( i, playerColor, playerCautious) * howManyMyBlots);
             }
         }
         
           /*  check the bar */
         if (currentBoard.onBar(playerColor)) { /* we HATE to be on the bar! */
             if (playerColor == Board.white) {
                 score += howImportantIsThisPoint( currentBoard, Board.WHITE_BAR_LOC, playerColor, playerCautious);
            } else /*if (playerColor == black) */{
                 score += howImportantIsThisPoint( currentBoard, Board.BLACK_BAR_LOC, playerColor, playerCautious);
            }
         }
           /* check the beared off tally */
         if (playerColor == Board.white) {
              score += valueOfBearedOffPiece * currentBoard.white_bear;
         } else /*if (playerColor == Board.black) */{
             score += valueOfBearedOffPiece * currentBoard.black_bear;
         }
         if (currentBoard.canBearOff(playerColor)) {
             /* is it time for a whole different scoring/playing regime? */
             
         }
         System.out.println("There are " + score + " worth of " + Board.colorName( playerColor ) 
            + " points on the board.");
         return score;
     } /* getAllPointScore( ) */

     
      /**
      * Returns a score (0..24) based on j&j idea of which points are more important
      * for starting to build on this particular point.
      * 
      * This should vary based upon context and risk-averse attitude. 
      * Receives "cautious" but doesn't use it yet.
      * ?? Shouldn't the bear off zone be included, be most precious? We love it when blots are in there!
      * Important for comparing boards.
      * 
      * Have unused "cautious" param for my risk adversion. 
      * Or should driver program calls various functions similar to this depending on mood?
      * This is called by "getAllPointScore( )" for every point on the board (including bar but not bear)
      * which also calculates value of beared-off blots.
      */
     public double howImportantIsThisPoint(Board currentBoard, final int pointNum, final int playerColor, double cautious ) {
        if ( ! Board.legitStartLoc(pointNum,playerColor)) { // checks legitPlayerColor( )
            throw new IllegalArgumentException("Bad pointNum '" + pointNum + "', you cheater!");
        }
        int ourPointNum = pointNum; 
        /* for black, this is the opposite (25 - p) so we can use white's value switch */
        if (playerColor == Board.black) {
            ourPointNum = (Board.howManyPoints + 1) - pointNum;
        }
        int theOtherColor = Board.theReversePlayerColor(playerColor);
        
        double value = 0;
        if (pointNum == Board.WHITE_BAR_LOC) {
            /* How to calculate the importance of the bar?? Varies in course of game. 
                Is a long pip count from bearing out.   */
            if ( playerColor == Board.white) {
                value = currentBoard.howManyOnBar(playerColor) * -1 * penaltyOfBarPiece; 
                /* players are unhappy when they're on the bar */
            } else {
                value = currentBoard.howManyOnBar(playerColor) * penaltyOfBarPiece; 
                /* players like when the other guy is on the bar */
            }
            return value;
        }
        if (pointNum == Board.BLACK_BAR_LOC) {
            /* How to calculate the importance of the bar?? Varies in course of game. 
                Is a long pip count from bearing out.   */
            if ( playerColor == Board.black) {
                value = currentBoard.howManyOnBar(playerColor) * -1 * penaltyOfBarPiece; 
                /* players are unhappy when they're on the bar */
            } else {
                value = currentBoard.howManyOnBar(playerColor) * penaltyOfBarPiece; 
                /* players like when the other guy is on the bar */
            }
            return value;
        }
        
        /* Use 'ourPointNum' (switched to white point of view). 
        Don't use "pointNum" if calculating black's board value!! */
        switch(ourPointNum) {
            /* Says how much we love this point. Eg. pt 6 is our fave. */
            case  6: value = 24.0; break;
            case 23: value = 23.0; break;
            
            case 20: value = 22.0;  break;
            case  5: value = 21.0; break;
            case 18: value = 20.0; break;
            case  7: value = 19.0; break;
            case 21: value = 18.0; break;
            case  4: value = 17.0; break;
            case 22: value = 16.0; break;
            case  3: value = 15.0; break;
            case 17: value = 14.0; break;
            case  8: value = 13.0; break;
            case 16: value = 12.0; break;
            case  9: value = 11.0; break;
            case 19: value = 10.0; break;
            
            case  2: value = 9.0; break;
            case 15: value = 8.0; break;
            case 10: value = 7.0; break;
            case 13: value = 6.0; break;
            case 12: value = 5.0; break;
            case 14: value = 4.0; break;
            case 11: value = 3.0; break;
            case 24: value = 2.0; break;
            case  1: value = 1.0; break;
         default:  
            value = 0;
            throw new IllegalArgumentException( "bad ourPointNum '" + ourPointNum + "'" );
        } /* switch */
        
        int howManyOfTheColorAreOnThePoint = currentBoard.howMuchProtected( pointNum, playerColor);
        int howManyOfTheOtherColorAreOnThePoint = currentBoard.howMuchProtected( pointNum, theOtherColor);
       
        if ((howManyOfTheColorAreOnThePoint == 0) && (howManyOfTheOtherColorAreOnThePoint == 0)) {
           return 0.0;
        } else if (howManyOfTheColorAreOnThePoint == 1) {
           return value / 4.0;
        } else if (howManyOfTheColorAreOnThePoint == 2) {
           return value;
        } else if (howManyOfTheColorAreOnThePoint > 2) {
           return value * 1.2; /* howabout: return value * (0.6 * howManyOfTheColorAreOnThePoint);  */
        } else if (howManyOfTheOtherColorAreOnThePoint == 1) {
            return value / -4.0; /* ?? */
        } else if (howManyOfTheOtherColorAreOnThePoint == 2) {
            return value * -1; /* ?? */
        } else if (howManyOfTheOtherColorAreOnThePoint > 2) {
            return value * -1.2; /* howabout: return value * (-0.6 * howManyOfTheColorAreOnThePoint);  */
        }
       return value;
     }/* howImportantIsThisPoint( ) */
 
 
     
     
}
