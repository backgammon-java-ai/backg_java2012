/***************************************************************
JBackgammon (http://jbackgammon.sf.net)
 
Copyright (C) 2002
George Vulov <georgevulov@hotmail.com>
Cody Planteen <frostgiant@msn.com>
 
revised by Julien S., Joshua G., Mike Roam, 2011-2


This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
****************************************************************/

/** 
 * File: board.java
 *
 * Description: This file contains the class for the backgammon board, 
 * keeping count how many blots of which color are on each "point",
 * and providing moving and dice rolling. 
 */


import java.util.*;  // provides Collections


public class Board {
     /* if you add more fields here, add them to the copy constructor, also! 
       Beware deep copy if the new fields hold (pointers to) objects! */

    Game myGame = null; /* better be set up in constructor or die! */
    
    int howManyOnPoint[ ]; /* just for board points (1..24), Beware: numbered 1..24, NOT 0..23! */
    int whichColorOnPoint[ ];

    int white_bar = 0; /* how many blots on white's bar */
    int black_bar = 0;
    int white_bear = 0; /* how many blots white has "beared off" the board */
    int black_bear = 0;
    
    Dice myDice = new Dice( );

    // The move possible with each dice
    // Positions: 1 - 24 = points, 1 being on the beginning of the black quarter
    // formerly 0 was black bear off and 25 was white bear off
    private int potDest1, potDest2; // destination of moving from old_point using dice1 & 2 respectively
    private int old_point; // Original position of blot selected for moving

    /* usedDice is now incorporated into myDice ! */


    // point colors (player colors are only white & black)
    /* Beware: Game has a duplicate list of these colors which had better stay identical! */
    public static final int neutral = 0;
    public static final int white = 1;
    public static final int black = 2;
        
    public static final int WHITE_BAR_LOC = 0; /* probably bad that this is same as BLACK_BEAR_OFF_LOC */
    public static final int BLACK_BAR_LOC = 25; /* probably bad that this is same as WHITE_BEAR_OFF_LOC */
    public static final int WHITE_BEAR_OFF_LOC = 25;
    public static final int BLACK_BEAR_OFF_LOC = 0;
    public static final int WHITE_PAST_BEAR_OFF_LOC = 100;
    public static final int BLACK_PAST_BEAR_OFF_LOC = -100;

    public static final int ILLEGAL_MOVE = -97;
    
 //   public static final int bar = 0; /* name of a place one can move to */
  //  public static final int bearoff = -1; /* a move might end on here */
    public static final int howManyPoints = 24; /* points are "spikes"; stored in array 0..24 just using 1..24 */
    public static final int howManyPointsInBearOffZone = 6;
         /* points 19-24 are white's #4 (final) quadrant*/
    public static final int startOfWhiteBearOffZone = howManyPoints - (howManyPointsInBearOffZone - 1); 
    public static final int endOfWhiteBearOffZone = howManyPoints;
    public static final int startOfBlackBearOffZone = 1; /* points 1-6 are black's #4 (final) quadrant */
    public static final int endOfBlackBearOffZone = startOfBlackBearOffZone + (howManyPointsInBearOffZone - 1);
    public static final int howManyBlots = 15;
    public static final int howManyWhiteBlots = howManyBlots; /* some might be on bar or did bear off */
    public static final int howManyBlackBlots = howManyBlots; /* some might be on bar or did bear off */
    public static final int howManyDice = 2; /* with 3 dice could we do triples of rolls if all 3 dice the same? */
    public static final int diceHighNum = 6; /* we could use funky dice! */
    public final static int HOW_MANY_POINTS_IN_QUADRANT = 6;

    
    /**
     * Build a new game board.
     * I was considering having another constructor that takes numeric parameter
     * of which test board you want, but realized it is not necessary: it is 
     * so easy to tell a newly constructed board to merely b.makeBoardWithoutBlots( ) 
     * or b.makeStartingBoard( ) or b.makeAlmostDoneGame( ) or b.make3BlotGame( )!
     * 
     * Can constructors throw exceptions? Shouldn't this be throwing?
     */
    public Board(Game myNewGame) {
        if (myNewGame == null) {
            throw new NullPointerException("Can't give null Game to Board Constructor. Boards HAVE to know their game.");
        } else {
            myGame = myNewGame;
        }
        howManyOnPoint = new int[howManyPoints + 1]; 
        // 0..24?? why 25 points? Think 0th point is the bar?
        // Ah-hah: maybe it is so that we get boxes 0..24 and can say [24] instead of minusing 1
        whichColorOnPoint = new int[howManyPoints + 1];

        try {
            /* regular games start with "makeStartingBoard( ); */ 
            make3BlotGame( );   /* which first calls makeBoardWithoutBlots( ) */
            /* makeEasyHitStartingBoard( ); */
        } catch( BadBoardException e ) {
            System.out.print("ERROR building Board: " + e );
            //throw new BadBoardException("ERROR building Board: " + e );
        }
    } // board constructor
   
    
    
    
    /**
     * Copy constructor so that I can make duplicate boards
     * Usage  Board b2 = new Board( myBoard );
     * ?? Should I also implement clone( ) ???
     * "Game" is here so other Games can copy boards from this board's game
     */
     public Board(Game myNewGame, Board otherBoard ) {
        if (myNewGame == null) {
            throw new NullPointerException("Can't give null Game to Board Constructor. Boards HAVE to know their game.");
        } else {
            myGame = myNewGame;
        }
        howManyOnPoint = new int[howManyPoints + 1]; 
        whichColorOnPoint = new int[howManyPoints + 1];
        for (int i = 0; i < howManyPoints + 1; i++) {
            howManyOnPoint[i] = otherBoard.howManyOnPoint[i];
            whichColorOnPoint[i] = otherBoard.whichColorOnPoint[i];
        }
        white_bar = otherBoard.white_bar;    
        black_bar = otherBoard.black_bar;
        white_bear = otherBoard.white_bear;
        black_bear = otherBoard.black_bear;
        
        myDice = new Dice(otherBoard.myDice); /* deep copy in case I ever have dice keeping track of dice usage */
        /* might be null if dice haven't rolled yet */
        /* note: don't have to use get when talking to myself */

        potDest1 = otherBoard.potDest1;
        potDest2 = otherBoard.potDest2; // destination of moving from old_point using dice1 & 2 respectively
        old_point = otherBoard.old_point; // Original position of blot selected for moving
     } /* copy constructor */


     /**
      * make sure the bars and bears and points are all free of blots.
      * Should this myDice.reset( ); ?? probably
      */
     void makeBoardWithoutBlots( ) {
        for (int i=0; i<=howManyPoints; i++) { /* why start at 0? For prettier pointer numbering 1..24, ignore the 0's? */
            /* could use setPoint(i,0,neutral); ?? */
             howManyOnPoint[i] = 0;
             whichColorOnPoint[i] = neutral;
        }
        white_bar = 0;
        black_bar = 0;
        white_bear = 0;
        black_bear = 0;
        myDice.reset( );
     } /* makeBoardWithoutBlots */
     
    
    /**
    * The regular normal starting position (15 blots of each color)
    */
    public void makeStartingBoard( )  throws BadBoardException {
        makeBoardWithoutBlots( );
        setPoint(1, /* howMany */ 2, white);
        setPoint(6, /* howMany */ 5, black);
        setPoint(8, /* howMany */ 3, black);
        setPoint(12, /* howMany */ 5, white);
        setPoint(13, /* howMany */ 5, black);
        setPoint(17, /* howMany */ 3, white);
        setPoint(19, /* howMany */ 5, white);
        setPoint(24, /* howMany */ 2, black);
        checkForBadNumberOfBlots( white );
        checkForBadNumberOfBlots( black );
    } //  makeStartingBoard( )
    
    
    
    /**
     * Starting position in which it is easy for players to hit each other
     * (Handy for testing!)
     */
    public void makeEasyHitStartingBoard( )  throws BadBoardException {
        makeBoardWithoutBlots( );
        setPoint(1, /* howMany */ 1, white); 
        setPoint(6, /* howMany */ 1, black);
        setPoint(8, /* howMany */ 1, black);
        setPoint(12, /* howMany */ 1, white);
        setPoint(13, /* howMany */ 1, black);
        setPoint(17, /* howMany */ 1, white);
        setPoint(19, /* howMany */ 1, white);
        setPoint(24, /* howMany */ 1, black);
        black_bar = 1;
        black_bear = 10;
        white_bar = 0;
        white_bear = 11;
        checkForBadNumberOfBlots( white );
        checkForBadNumberOfBlots( black );
    } //  makeEasyHitStartingBoard( )
    
    
    
    
    /** 
     * black and white each have removed 5 blots already...
     */
    public void makeAlmostDoneGame( ) throws BadBoardException {
        makeBoardWithoutBlots( );
        setPoint(1, /* howMany */ 2, black); 
        setPoint(5, /* howMany */ 5, black);
        setPoint(6, /* howMany */ 3, black);
        black_bear = 5;
        
        setPoint(18, /* howMany */ 3, white);
        setPoint(19, /* howMany */ 5, white);
        setPoint(24, /* howMany */ 2, white);
        white_bear = 5;
        checkForBadNumberOfBlots( white );
        checkForBadNumberOfBlots( black );
    } // makeAlmostDoneGame
    
  
     /** 
     * black and white each have removed 14 blots already...
     * note: white ends past 24, black ends below 1.
     */
    public void make3BlotGame( ) throws BadBoardException {
        makeBoardWithoutBlots( );
        setPoint(20, /* howMany */ 1, black);
        setPoint(12, 1, black);
        black_bear = 13;
        
        setPoint(4, /* howMany */ 1, white);
        white_bear = 14;
        System.out.println("created board with 2 black blots (on points 20,12) and 1 white blot (on point 4)");
        checkForBadNumberOfBlots( white );
        checkForBadNumberOfBlots( black );
    } // make3BlotGame
    
    
    /**
     * Is called by Game.doPartialMove( ), so can't be private.
     * Moves blot from one position to another, modifying the board object.
     * Doesn't check legality of move, doesn't check whether player is on bar,
     * so shouldn't be called willy nilly!
     * 
     * Can move Blots in from bar, so is a partner with "moveToBar( )"
     * Maybe there should be a "moveFromBar( )" that includes the middle of this
     * and is called by this if necessary. Could moveFromBar be called by others?
     * 
     * Apparently old_pos value -1 meant from bar???
     */
    public void moveBlot(int playerColor, int old_pos, int newPointNum) {
        if ( ! legitStartLoc( old_pos, playerColor )) { /* checks also legitPlayerColor */
            throw new IllegalArgumentException("Can't start moving from point '" + old_pos + "'");
        }
        if ( ! legitEndLoc( newPointNum, playerColor )) {
            throw new IllegalArgumentException("Can't move to point '" + newPointNum + "'");
        }
        // If the move is coming from a bar, remove it from the bar
        // and add it to the point
        int howManyBlotsOnDest =  getHowManyBlotsOnPoint(newPointNum); /* cache */
        
        if (old_pos == WHITE_BAR_LOC ) {
            if ((playerColor==white) && (white_bar > 0)) { /* "(white_bar>0)" is equiv of  "onBar(white)" */
                white_bar--;
            } else if (playerColor!=white) {
                throw new IllegalArgumentException("color '" + colorName( playerColor ) + "' isn't white so can't move from WHITE_BAR_LOC.");
            } else if (white_bar <= 0) {
                throw new IllegalArgumentException("player '" + colorName( playerColor ) + "' can't move from WHITE_BAR_LOC since no blots are there!");
            }
            setPoint(newPointNum, howManyBlotsOnDest + 1, playerColor);
        }
        if (old_pos == BLACK_BAR_LOC /*25*/) {
            if ((playerColor==black) && (black_bar > 0)) {
                black_bar--;
            } else if (playerColor!=white) {
                throw new IllegalArgumentException("color '" + colorName( playerColor ) 
                    + "' isn't black so can't move from BLACK_BAR_LOC.");
            } else if (white_bar <= 0) {
                throw new IllegalArgumentException("player '" + colorName( playerColor ) 
                    + "' can't move from BLACK_BAR_LOC since no blots are there!");
            }
            /* omg, why was this next line missing? Explains black not coming in from bar ! */
            setPoint(newPointNum, howManyBlotsOnDest + 1, playerColor);
        } else {
            // Move is coming from another point
            // Decrease the number of blots on the old point
            int howManyOnOldPoint = getHowManyBlotsOnPoint(old_pos);
            if (howManyOnOldPoint > 0) {
                   // next line is equiv to  "takeOneBlotOffPoint(old_pos)" 
                setPoint(old_pos, howManyOnOldPoint - 1, playerColor);
                if (howManyOnOldPoint==0) {
                    setPoint(old_pos, 0, neutral);
                }
            } else {
                throw new IllegalArgumentException("player '" + colorName( playerColor ) + "' can't move from point '" 
                    + old_pos + "' since no blots are there!");
            }
            // Increase the blots on the new point
            setPoint(newPointNum, howManyBlotsOnDest + 1, playerColor);
        }
    } /* moveBlot */

    
   /**
    * Bear off a blot from the current point (aka "old_point").
    * Sure hope that the caller has checked legitimacy of bearing off!
    */
    public void bearOff( int playerColor) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
        if ( 4 != quadrantForPoint(old_point, playerColor )) {
            throw new IllegalArgumentException("can't bear off from point '" + old_point 
               + "' which isn't in final quadrant for player " + colorName(playerColor));
        }
        // Remove a blot from the old point
        takeOneBlotOffPoint(old_point);
        // equiv: setPoint(old_point, getHowManyBlotsOnPoint(old_point,playerColor) - 1, playerColor);
        if (playerColor==white) {
            white_bear++;
        } else {
            black_bear++;
        }

        if (myGame.status.networked) {
            myGame.comm.sendmove(old_point, Board.WHITE_BEAR_OFF_LOC);
        }

        myGame.FButton[myGame.btn_BearOff].setEnabled(false);

        boolean won = false; // did someone win
        if (!myGame.status.networked) {
            won = myGame.checkWin(playerColor);
        }
        if (myGame.status.networked && (myGame.status.observer==false)) {
            won = myGame.checkWin(white);
        }
        if (won) {
            myGame.endPartialMove();// Disable buttons
            return; // Do nothing if there's a winner
        }

        // Remove the dice we used
        if (!myDice.isDoubles( )) {
            // if a previous move has already occurred, we are done
            if ((myDice.getUsedDie(1)) || (myDice.getUsedDie(2))) {
                myGame.endTurn();
            } else {
                // if you can bear off with both, use smaller dice
                if (((potDest1==WHITE_BEAR_OFF_LOC)||(potDest1==BLACK_BEAR_OFF_LOC)) && ((potDest1==WHITE_BEAR_OFF_LOC)||(potDest1==BLACK_BEAR_OFF_LOC))) {
                    if (myDice.getDie(1) > myDice.getDie(2)) {
                        myDice.setUsedDie(2,true );  // I see a Die being marked, but when is move happening??
                    } else {
                        myDice.setUsedDie(1,true );
                    }
                } else if ((potDest1==WHITE_BEAR_OFF_LOC)||(potDest1==BLACK_BEAR_OFF_LOC)) {
                    myDice.setUsedDie( 1,true );
                } else if ((potDest2==WHITE_BEAR_OFF_LOC)||(potDest2==BLACK_BEAR_OFF_LOC)) {
                    myDice.setUsedDie( 2,true );
                }
            }
        } else if (myDice.isDoubles( )) {
            myDice.doubletCountdown();
            if (myDice.getDoubletMovesCountdown( )<=0) {
                myGame.endTurn();
            }
        }

        // Turn off focus on this point
        myGame.endPartialMove();
        myGame.repaint();

        if ( ! canMove(playerColor) ) {
            myGame.forfeit();
        }
    } // bearOff
    
    
    /**
     * Handle someone being on the bar.
     * Mark possible escapes and forfeit if there are none.
     * This is automatically called by Game.doRoll( )   if   Board.onBar(Game.current_player)
     * and doRoll otherwise checks whether a player canMove( ) and forfeits for them if they can't move! 
     */
    public void handleBar(int playerColor) {
        int escape1;
        int escape2;

        if (playerColor==white) {
            escape1 = myDice.getDie(1);
            escape2 = myDice.getDie(2);
        } else {
            escape1 = (howManyPoints + 1) - myDice.getDie(1);
            escape2 = (howManyPoints + 1) - myDice.getDie(2);
        }

        // Can they escape?
        if ( (! myDice.getUsedDie( 1 )) && canLandOn(escape1,playerColor) ) {
            myGame.FButton[myGame.btn_AtPotentialMove1].drawOnPoint(escape1); // potential move 1
            myGame.FButton[myGame.btn_AtPotentialMove1].setVisible(true); // show this as possible move
            potDest1 = escape1;
            if (playerColor==white) {
                old_point = WHITE_BAR_LOC;
            } else {
                old_point = BLACK_BAR_LOC;
            }
            myGame.status.point_selected = true;
        }
        if ( (!myDice.getUsedDie(2)) && canLandOn(escape2,playerColor) ) {
            myGame.FButton[myGame.btn_AtPotentialMove2].drawOnPoint(escape2); // potential move 2
            myGame.FButton[myGame.btn_AtPotentialMove2].setVisible(true);
            potDest2 = escape2;
            if (playerColor==white) {
                old_point = WHITE_BAR_LOC;
            } else {
                old_point = BLACK_BAR_LOC;
            }
            myGame.status.point_selected = true;
        }

        // Nope? Then they forfeit
        if (myDice.getUsedDiceHowMany( ) == 0) {
            if ( (!canLandOn(escape1, playerColor)) && (!canLandOn(escape2, playerColor)) ) {
                myGame.forfeit();
            }
        } else if (myDice.getUsedDie(1)) {
            if (!canLandOn(escape2, playerColor)) {
                myGame.forfeit();
            }
        } else if (myDice.getUsedDie(2)) {
            if (!canLandOn(escape1, playerColor)) {
                myGame.forfeit();
            }
        }
    } // handleBar


    /**
     * While a blot is moving, this remembers its original (starting) position.
     * I'm trying to not use it much.
     */
    public int getOldPoint( ) {
        return old_point;
    }


    /**
     * While a blot is moving, this remembers its original (starting) position
     */
    public void setOldPoint(int newOldPointNum ) {
        if ( ! legitStartLoc( newOldPointNum, myGame.getCurrentPlayer() )) {
            throw new IllegalArgumentException("Can't start moving from point '" + newOldPointNum + "'");
        }
        old_point = newOldPointNum;
    } 


    /* set/getUsedDie was called "getUsedMove( )",  is now in myDice */
    /* set/getDoubletMovesCountdown( ) is now in myDice */


    /**
     * Of the 2 potential destinations for the selected point (old_point), tell us the Point that is reached by dest 'n'
     * This should be part of myDice??
     */
    public int getPotDest(int whichDest) {
        if ((whichDest < 0) || (whichDest > howManyDice)) {
            throw new IllegalArgumentException("It's no good trying to talk to potential dest# '" + whichDest + "', can only use 1..4");
        }
        if (whichDest == 1) {
            return potDest1;
        } else if (whichDest == 2) {
            return potDest2;
        } else if ((whichDest > 2) && (! myDice.isDoubles( ))) {
            throw new IllegalArgumentException("It's no good trying to talk to potential move '" + whichDest + "', can only use 1..4");
        } else {
            throw new IllegalArgumentException("getPotDest( ) doesn't know how to return potential destination '" + whichDest + "', can only use 1..2");
        }
    } /* getPotDest( ) */


    /**
     * how many blots of this color are on the bar, waiting to come back into the game.
     */
    public int getBar( int playerColor ) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        if (playerColor == white) {
            return white_bar;
        } else /* if (playerColor == black)*/ {
            return black_bar;
        } /* else ... bad color, alt color? */
    } /* getBar( ) */


    /**
     * Tells how many blots (pieces) of a particular color are here (including on the bar, the bear, or the board).
     * There are supposed to always be 15 blots of each color in traditional backgammon. 
     * Used by "checkForBadNumberOfBlots( ) to check possible corruption of board.
     */
    public int howManyBlots(int playerColor) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        int blotCount= getBlotCountOnBoard( playerColor );
        if (playerColor == white) {
            blotCount += white_bar + white_bear; /* bar and bear should be arrays */
        } else {
            blotCount += black_bar + black_bear;
        }
        return blotCount;
    } /* howManyBlots( ) */


    /**
     * Checks for legal number of white and black blots.
     */
    public void checkForBadNumberOfBlots(int playerColor) throws BadBoardException {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        if ( playerColor == white ) {
            int howManyWhitesNow = howManyBlots(white);
            if (howManyWhitesNow != howManyWhiteBlots) {
                throw new BadBoardException("There are " + howManyWhitesNow + " white blots but legal board should have " + howManyWhiteBlots);
            }
        } else {
            int howManyBlacksNow = howManyBlots(black);
            if (howManyBlacksNow != howManyBlackBlots) {
                throw new BadBoardException("There are " + howManyBlacksNow + " black blots but legal board should have " + howManyBlackBlots);
            }
        }
    } /* checkForBadNumberOfBlots */


     /**
     * This will mostly be used for partial moves?
     * The biggest possible partial move is 6 (diceHighNum).
     * The smallest possible partial move is 1 (or 0? if forfeit? not really a "move")
     * The biggest possible (full, non partial) move in standard backgammon is 24: doubles of 6s.
     */
    public static boolean legitStepsNum( int steps ) {
        return ( (1 <= steps) && (steps <= /* diceHighNum */ (Dice.maxMovesCount * howManyDice * diceHighNum)) );
    } // legitStepsNum( )


    /**
     * This only accepts pointNumbers [1..(howManyPoints==24)], 
     * NOT BEAR, NOT BAR!!
     * If I make Classes for PointNum, StartLoc, EndLoc
     */
    public static boolean legitPointNum( int pointNum ) {
       return ( (1 <= pointNum) && (pointNum <= howManyPoints) );
    }


    /**
     * For deciding legality of starting place for a blot move.
     * More picky than legitEndLoc( ) since not allowing BEAR and PAST_BEAR.
     * Good for error prevention of move math.
     * Checks the legitColor( playerColor ) so callers don't have to if they don't want to.
     * Picky about not allowing white on BLACK_BAR, etc.
     * A blot might start on the bar or on a legit point.
     * Not sure whether point 0 is used in this implementation...
     * (See the Game.java file for rules of moves...)
     * Static so that other classes can use it without an instance.
     */
    public static boolean legitStartLoc( int pointNum, int playerColor ) {
        if ( ! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
        if ( (1 <= pointNum) && (pointNum <= howManyPoints) ) {
            return true;
        } else if (((playerColor == white ) && (pointNum == WHITE_BAR_LOC)) 
              || ((playerColor == black ) && (pointNum == BLACK_BAR_LOC))) {
            return true;
        } else {
            return false;
        }
    } // legitStartLoc( )


    /**
     * For deciding legality of end place for a blot move.
     * Picky about color since black can't be on WHITE_BAR, etc.
     * but moves can otherwise end anywhere: on points, bar, bear, past_bear,
     * so this isn't as picky as legitStartLoc( ) which doesn't allow the BEAR and PAST_BEAR.
     */
    public static boolean legitEndLoc( int pointNum, int playerColor) {
        if ( ! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
        if (legitPointNum(pointNum)) {
            return true;
        }
        if ( ((playerColor == white ) && ((pointNum == WHITE_BEAR_OFF_LOC) || (pointNum == WHITE_PAST_BEAR_OFF_LOC)))
              || ((playerColor == black) && ((pointNum == BLACK_BEAR_OFF_LOC ) || (pointNum == BLACK_BEAR_OFF_LOC )) )) {
            return true;
        } else if (((playerColor == white ) && (pointNum == WHITE_BAR_LOC)) 
              || ((playerColor == black ) && (pointNum == BLACK_BAR_LOC))) {
            return true;
        } else {
            return false;
        }
    } // legitEndLoc( )


    /**
     * This does the math of moving: given a starting point & a roll distance (one die), tells the end of the move.
     * For black, use subtraction!?? I'll throw exception for negative steps!
     * For white, simple math in the middle of the board (endpoint = start + steps)
     * but trickier at the end since after final point is the bar.
     * This just calculates but doesn't actually try to move any blots.
     * Is handy for creating partialMoves (which have start, roll, end ).
     *
     * Note: The endpoint might not be legally landable upon (if protected by enemy). This doesn't check.
     * 
     * Maybe this could decide whether to return BEAR_OFF_LOC even when it is going PAST_BEAR_OFF_LOC, but
     * for now I'll return BEAR_OFF_LOC for exact bear off and PAST_BEAR_OFF_LOC if overshooting...
     */
    public static int endPointMovingFrom( int startPoint, int steps, int playerColor, Board board) /*throws BadBoardException*/ {
        if (board == null) {   /* hmmm, non static call, okay with "this"? */
            throw new NullPointerException("board can't be null");
        }
        if ( ! legitStartLoc( startPoint, playerColor )) {  // also checks legitColor( )
            throw new IllegalArgumentException/*BadBoardException*/("Can't start moving from point '" + startPoint + "'");
        }
        if ( ! legitStepsNum( steps )) {
            throw new IllegalArgumentException/*BadBoardException*/("Can't move bad number of steps '" + startPoint + "'");
        }
        
        int endPoint = startPoint; /* temp value in case something goes wrong */
        if (playerColor == white) {
            endPoint = startPoint + steps;
            if ( endPoint > howManyPoints ) {
 //               if (board.canBearOff( white )) {
 //               check this for redundant or recursive logic
 //                   if (needsInexactRolls(playerColor)) {
                    if (endPoint == howManyPoints + 1) {
                        endPoint = WHITE_BEAR_OFF_LOC;
                    } else {
                        endPoint = WHITE_PAST_BEAR_OFF_LOC;
                    }
 //               } else {
//                    endPoint = startPoint; /* can't move?? */
//                    System.out.println("blot at startpoint:" + startPoint + " can't move " + steps +" steps (not allowed to bear off yet)");
//                }
            }
        } else if (playerColor == black) {
            endPoint = startPoint - steps; /* MINUS */
            if ( endPoint < 1 ) {
//                if (board.canBearOff( black )) {
//                check this for redundant logic, see "Board.canMove( )" and canBearOff( )
//                    if (needsInexactRolls(playerColor)) {
                    if (endPoint == 0) {
                        endPoint = BLACK_BEAR_OFF_LOC;
                    } else {
                        endPoint = BLACK_PAST_BEAR_OFF_LOC;
                    }
//                } else {
//                    endPoint = startPoint; /* can't move?? */
//                    System.out.println("blot at startpoint:" + startPoint + " can't move " + steps +" steps (not allowed to bear off yet)");
 //               }
            }
        }
        return endPoint;
    } /* end Point Moving From */








    /**
     * Tells us if a color is legal (black, white, neutral)
     * Use "legitPlayerColor( )" if you want to check for only black and white!
     */
    public static boolean legitColor( int color ) {
        return ( (color == neutral) || (color == black) || (color==white) );
    } // legitColor( )


    /**
     * Tells us if a Player's color is legal (only black and white currently allowed).
     * See legitColor if you're checking points, which can also be "neutral" color.
     */
    public static boolean legitPlayerColor(int playerColor ) {
        return ( (playerColor == black) || (playerColor==white) );
    } // legitPlayerColor( )



    /**
     * Utility for toString of our 3 colors (black,white,neutral)
     */
    public static String colorName( int color ) {
        if ( ! legitColor( color) ) {
           throw new IllegalArgumentException("bad color '" + color + "'");
        }
        if (color == neutral) {
            return "neutral (no color)";
        } else if (color == black) {
            return "black";
        } else if (color==white) {
            return "white";
        }
        return "unknown color '" + color + "'";
    } /* colorName */


    /**
     * Tells us which color is on the specified point (black, white, or neutral).
     * This can't be used for BAR and BEAR_OFF zones!
     * Maybe it should be allowed to do BAR and BEAR ... but would have to be given a color
     * then so that we knew which BAR/BEAR to look at. Yuck.
     */
    public int getColorOnPoint(int pointNum) {
        if ( ! legitPointNum(pointNum) ) {
            throw new IllegalArgumentException("Bad pointNum '" + pointNum + "'");
        }
        return whichColorOnPoint[pointNum];
    } // getColorOnPoint


    /**
     * might return 0
     * This is more specific alternative to getHowManyBlotsOnPoint(int pointNum)
     * I suppose this is okay reporting back about BAR and BEAR??
     */
    public int getHowManyBlotsOnPoint(int pointNum, int playerColor) {
         // is it a point 1..24 or bar/bear?
         if ( ! legitEndLoc(pointNum,playerColor)) { /* also checks the color for legitimacy */
             throw new IllegalArgumentException("Bad pointNum '" + pointNum + "'");
        }
        if ((playerColor==white) && (pointNum == WHITE_BAR_LOC)) {
            return white_bar;
        } else if ((playerColor==black) && (pointNum == BLACK_BAR_LOC)) {
            return black_bar;
        } else if ((playerColor==white) && ((pointNum == WHITE_BEAR_OFF_LOC) || (pointNum == WHITE_PAST_BEAR_OFF_LOC))) {
            return white_bear;
        } else if ((playerColor==black) && (pointNum == BLACK_BAR_LOC)) {
            return black_bar;
        } else if ((playerColor==black) && ((pointNum == BLACK_BEAR_OFF_LOC) || (pointNum == BLACK_PAST_BEAR_OFF_LOC))) {
            return black_bear;
        }
        if (getColorOnPoint(pointNum) == playerColor) {            
            return howManyOnPoint[pointNum];
        } else {
            return 0;
        }
    } // getHowManyBlotsOnPoint( )


    /**
     * Tells us how many blots without specifying their color.
     * See alternative getHowManyBlotsOnPoint(int pointNum, int playerColor)
     */
    public int getHowManyBlotsOnPoint(int pointNum) {
        if ( ! legitPointNum(pointNum)) {
             throw new IllegalArgumentException("Bad pointNum '" + pointNum + "'");
        }
        return howManyOnPoint[pointNum];
    } // getHowManyBlotsOnPoint( )


    /**
     * Convenience method
     * What to do if there are no blots on the specified point? Uh-oh.
     * This is only for points on the board (1..24), and maybe bar?
     */
    public void takeOneBlotOffPoint( int startPointNum) {
        int playerColor;
        if (startPointNum == WHITE_BAR_LOC) {
            playerColor = white;
        } else if (startPointNum == BLACK_BAR_LOC) {
            playerColor = black;
        } else if (! legitPointNum(startPointNum)) {
             throw new IllegalArgumentException("Bad PointNum '" + startPointNum + "'");
        } else {
            playerColor = getColorOnPoint(startPointNum);
        }
        int howMany = getHowManyBlotsOnPoint(startPointNum);
        if (howMany < 1) {
            throw new IllegalArgumentException("Can't remove a blot from point '" 
               + startPointNum + "' which has no blots!");
        }
        setPoint( startPointNum, howMany - 1, playerColor);
    } /* takeOneBlotOffPoint( ) */


    /**
     * This seems partly misnamed: shouldn't it be "setNumOfBlotsOnPoint( )"??
     * Specified point will end up holding "howMany" blots of specified color. 
     * (Might be 0 blots: cleared off, neutral). In that case any color is okay (black, white, neutral).
     * Might the board be temporarily having a bad number of blots while one is moving??
     * Checking for legit END-point location, but have to treat bar special??
     */
    public void setPoint(int destPointNum, int howMany, int color/* not merely playerColor*/) {
        if ( ! legitColor( color ) ) { /* neutral is allowed by "legitColor( )", unlike "legitPlayerColor( )" */
            throw new IllegalArgumentException("Bad color '" + color + "' in setPoint( )");
        }
        if (color == neutral) {
            if (howMany != 0) {
                throw new IllegalArgumentException("Bad color: Can't put " + howMany 
                    + " 'neutral' blots anywhere!");
            } else {
                howManyOnPoint[destPointNum] = 0;
                whichColorOnPoint[destPointNum] = neutral;
            }
        } else if ( ! legitEndLoc(destPointNum, color )) { /* also checks legitPlayerColor */
            throw new IllegalArgumentException("Bad destPointNum '" + destPointNum + "' in setPoint( )");
        } else if ( (howMany < 0) || (howMany > howManyBlots) ) {
            /* ******* Hey, should we check that there are legit num of blots on board?? No,
            because we use this to populate starting boards, when there aren't legal number 
            of blots yet.
            But there's never any excuse for putting a negative number of blots onto a point,
            and no excuse for putting more than "howManyBlots" (aka 15) blots onto a point. */
            throw new IllegalArgumentException("Bad number '" + howMany 
                + "' of blots in setPoint( ), should be in range [0.." + howManyBlots + "]");
        } else if ((howMany > 0) && ( ! legitPlayerColor(color) )) {
            throw new IllegalArgumentException("Bad color '" + color + "' for blots");
        } else {
            /* finally getting down to work, having dealt with neutral and legitimacy issues */
            /* What if we're dealing with a BAR? Could such a thing happen??
                there is a moveToBar( ) method for hitting a blot. How do blots get off bar? */
            /* Could we be dealing with BEAR_OFF or PAST_BEAR_OFF also?? */
            if ((destPointNum == WHITE_BAR_LOC) && (color == white)) {
                System.out.println("hmmm, weird, doing 'setPoint(" + destPointNum + ",/*howMany:*/" + howMany + ",/*player:*/" 
                    + colorName(color) + ") for WHITE_BAR but I'll give it a try.");
                white_bar = howMany;
            } else if ((destPointNum == BLACK_BAR_LOC) && (color == black)) {
                System.out.println("hmmm, weird, doing 'setPoint(" + destPointNum + ",/*howMany:*/" + howMany + ",/*player:*/" 
                    + colorName(color) + ") for BLACK_BAR but I'll give it a try.");
                black_bar = howMany;
            } else {
                howManyOnPoint[destPointNum] = howMany;
                if (howMany==0) {
                    whichColorOnPoint[destPointNum] = neutral;
                } else {
                    whichColorOnPoint[destPointNum] = color;
                }
            }
        }
    } // setPoint( )


    /* rollDice(), getDice1, getDice2, getDice(int),resetDice are all now in myDice */



    /**
    * Moving to "bar" from the specified point. (Checks that can't be coming from BEAR nor BAR!)
    * The point then getting set to color neutral because only a single can get sent to the bar.
    * What if more blots are there: bogus setup.
    * There doesn't seem to be an equivalent "moveFromBar(destPoint, playerColor )" method
    * but "moveBlot( from, to, color)" works when the fromLoc is a bar.
    */
    public void moveToBar(int pointNum, int bounceeColor) {
        if ( ! legitStartLoc(pointNum,bounceeColor)) { // also checks if legitPlayerColor
             throw new IllegalArgumentException("Bad pointNum '" + pointNum + "'in moveToBar!");
        }
        if (getColorOnPoint(pointNum)!=bounceeColor) {
            throw new IllegalArgumentException("There's no " + colorName(bounceeColor) 
                + " blot on point '" + pointNum + "' to move to bar!");
        }
        if (getColorOnPoint(pointNum)==white) {
            white_bar++;
        } else {
            black_bar++; 
        }
        int howManyBlotsOnThisPoint = getHowManyBlotsOnPoint( pointNum );
        if ( howManyBlotsOnThisPoint == 1 ) {
            //System.out.println("When does point " + pointNum + " lose the blot that is going to the bar?");
            setPoint(pointNum, /* howMany */ 0, neutral);   
            /* doesn't seem redundant from setPoint( ) which calls moveToBar(), but I wonder ?? */
        } else {
            throw new IllegalArgumentException("Error, can only send single blot from point " 
                + pointNum + " to bar, but it has " + howManyBlotsOnThisPoint + " blots!");
        }
    } // moveToBar



    /**
    * Says how how many moves left before black blots are all "beared off".
    * 
    * E.G. suppose there is one black blot on point 1: answer is 1
    * (But white counts the other way: final move on board for white is point 24.)
    * This is equivalent to getBlackPipCount( ) and getWhitePipCount( ) but probably
    * better since multi-purpose.
    */
    public int getPipCount( int playerColor ) {
        if (! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
        int pipcount = 0;

        for (int i=1; i<=howManyPoints; i++) {
            if (playerColor == black) {
                pipcount += getHowManyBlotsOnPoint(i, playerColor) * i;
            } else if (playerColor == white) {
                pipcount += getHowManyBlotsOnPoint(i, white) * (25 - i);
            }           
        }
        /* blots on the bar are 25 moves away from bearing off */
        if (playerColor == black) {
            pipcount += black_bar * 25;
        } else if (playerColor == white) {
            pipcount += white_bar * 25;
        }

        System.out.println(colorName(playerColor) + "'s pip count is " + pipcount);
        return pipcount;
    } // getPipCount


    /**
    * Says how how many moves left before black blots are all "beared off".
    * This is equivalent to getPipCount(black), which is probably the better thing to use.
    * 
    * E.G. suppose there is one black blot on point 1: answer is 1
    * (But white counts the other way: final move on board for white is point 24.)
    */
    public int getBlackPipCount( ) {
        int pipcount = 0;

        for (int i=1; i<=howManyPoints; i++)  {
            pipcount += getHowManyBlotsOnPoint(i, black) * i;
        }
        /* blots on the bar are 25 moves away from bearing off */
        pipcount += black_bar * 25; 

        System.out.println("Black pip count is " + pipcount);
        return pipcount;
    } // getBlackPipCount



    /**
    * Says how how many moves left before black blots are all "beared off".
    * This is equivalent to getPipCount(white), which is probably the better thing to use.
    *
    * At the start of the game we think this should be 162.
    * 
    * E.G. suppose there is one white blot on point 1: answer is 24.
    * (But black counts the other way: final move for black is point 1.)
    */
    public int getWhitePipCount( ) {
        int pipcount = 0;

        for (int i=1; i<=howManyPoints; i++) {
            pipcount += getHowManyBlotsOnPoint(i, white) * (25 - i);
        }
                /* blots on the bar are 25 moves away from bearing off */
        pipcount += white_bar * 25; 

        System.out.println("White pip count is " + pipcount);
        return pipcount;
    } // getWhitePipCount


    /**
     * For comparing boards, thinking that protected points on certain parts of the board
     * are more useful than on other parts of the board, and unprotected blots are in more
     * danger in some places than in others.
     * Writing this separately for white and black since they count up and down differently, unfortunately.
     *    not sure if we're going to use this.
     */
    public double protectionScoreWhite(  ) {
        System.out.println("protectionScoreWhite is totally FAKE, fix!");
        System.err.println("protectionScoreWhite is totally FAKE, fix!");
        return 0.5;
    } /* protectionScoreWhite */


    /**
    * Looks at all points to see if there are any loner blots who aren't protected.
    * Note: they might not be in danger if no enemies are nearby.
    * Tells us how many of a color are unprotected.
    * 
    * Might be useful for comparing boards.  or might not matter....
    */
    public int getHowManyUnprotected(int playerColor ) {
        if (! legitColor(playerColor)) {
            throw new IllegalArgumentException("bad player color '" + playerColor + "'");
        }
        int howManyUnprotected = 0;

        for (int i=1; i<=howManyPoints; i++) {
            if (solitaryBlotOnPoint( i, playerColor)) {
            /* if ((getColorOnPoint(i)==color)  && (getHowManyBlotsOnPoint(i) == 1)) { */
                howManyUnprotected++;
                    /* where they are ought to matter also */
            }
        }
        
        System.out.println("There are " + howManyUnprotected + " unprotected " 
             + colorName( playerColor ) + " points on the board.");
        return howManyUnprotected;
    } // getHowManyUnprotected


    /**
    * Looks at all points to see if there are any points who are protected.
    * Tells us how many of a color are protected.
    * Ought to care about where they are but doesn't.
    * 
    * Might be useful for comparing boards.
    */
    public int getHowManyProtected(int playerColor ) {
         if (! legitPlayerColor(playerColor)) {
             throw new IllegalArgumentException("bad color '" + playerColor + "'");
         }

        int howManyProtected = 0;

        for (int i=1; i<=howManyPoints; i++)  {
            if (getHowManyBlotsOnPoint(i,playerColor) > 1) {
                howManyProtected++; /* where they are ought to matter also */
            }
        }
        
        System.out.println("There are " + howManyProtected + " protected " 
            + colorName( playerColor ) + " points on the board.");
        return howManyProtected;
    } // getHowManyUnprotected




     /**
      * Adds up the scores (values) of the importance of the (protected) points.
      * Useful for comparing boards.
      * ?? Should this give more value to highly protected points??
      */
     public double getAllPointScore(int playerColor, double playerCautious ) {
         if (! legitPlayerColor(playerColor)) {
             throw new IllegalArgumentException("bad color '" + playerColor + "'");
         }
         double score = 0;
 
         for (int i=1; i<=howManyPoints; i++) {
             /* if ((getColorOnPoint(i)==color)  && (getHowManyBlotsOnPoint(i) > 1)) { */
             int howManyMyBlots = howMuchProtected( i, playerColor);
             if (howManyMyBlots > 1) {
                 score+= (howImportantIsThisPoint( i, playerColor, playerCautious) * howManyMyBlots);
            }
         }
         
         System.out.println("There are " + score + " worth of protected " 
             + colorName( playerColor ) + " points on the board.");
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
     * [ ]Should we include the bear-off zone? Or include number of beared off in superMegaHappyScore( )?
     */
     public double howImportantIsThisPoint(final int pointNum, final int playerColor, double cautious ) {
        if ( ! legitStartLoc(pointNum,playerColor)) { // checks legitPlayerColor( )
            throw new IllegalArgumentException("Bad pointNum '" + pointNum + "', you cheater!");
        }
        int ourPointNum = pointNum; /* for black, this is the opposite (25 - p) so we can use white's value switch */
        if (playerColor == black) {
            ourPointNum = (howManyPoints + 1) - pointNum;
        }
        int theOtherColor = theReversePlayerColor(playerColor);
        
        double value = 0;
        if  (pointNum == WHITE_BAR_LOC) {
            /* How to calculate the importance of the bar?? Varies in course of game. 
                Is a long pip count from bearing out.   */
            if ( playerColor == white) {
                value = howManyOnBar(playerColor) * -1.5; /* players are unhappy when they're on the bar */
            } else {
                value = howManyOnBar(playerColor) * 1.5; /* players like when the other guy is on the bar */
            }
            return value;
        }
        if  (pointNum == BLACK_BAR_LOC) {
            /* How to calculate the importance of the bar?? Varies in course of game. 
                Is a long pip count from bearing out.   */
            if ( playerColor == black) {
                value = howManyOnBar(playerColor) * -1.5; /* players are unhappy when they're on the bar */
            } else {
                value = howManyOnBar(playerColor) * 1.5; /* players like when the other guy is on the bar */
            }
            return value;
        }
        
        /* Use 'ourPointNum' (switched to white point of view). 
        Don't use "pointNum" if calculating black's board value!! */
        switch(ourPointNum) {
            case  6: value = 24.0; break;
            case 23: value = 23.0; break;
            
            case 20: value = 22.0; /* Says how much we love this point. Eg. pt 20 is our fave. */ break;
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
        
        int howManyOfTheColorAreOnThePoint = howMuchProtected( pointNum, playerColor);
        int howManyOfTheOtherColorAreOnThePoint = howMuchProtected( pointNum, theOtherColor);
       
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
 
 
     /**
      * For comparing boards so we can decide what move to use.
      * Should take into account if we're in a risk-averse mood or not.
      * Cautious is a number from 0 (carefree) .. 1 (cautious)
      *
      * Answer will be in what range???
      * High Score is good!
      */
     public double superMegaHappyScore( double cautious, int playerColor ) {
         double theScore = 0;
         /* zero-sum game: we get happier from making the opponent unhappy! */
         /* but what "cautious" mood should we use when calculating opponent's board? */
         theScore = getAllPointScore( playerColor, cautious )  
             - getAllPointScore( theReversePlayerColor( playerColor), cautious);
         /* change in opponent's pipcount? Not only that, but how important
            the point was to them that they lost: all contained in their AllPointScore */;         
         return theScore;
     } /* superMegaHappyScore */



   /**
    * If given white, this returns black, and vice-versa.
    * If we ever get a game with more than 2 colors, this will die, and
    * so will its caller 'howImportantIsThisPoint( )'
    */
     public int theReversePlayerColor( int playerColor ) {
        if (! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        if (playerColor == white) {
            return black;
        } else if (playerColor == black) {
            return white;
        } else {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
    } /* theReversePlayerColor( ) */


    /**
     * Calculate white's total danger score, by figuring
     * for every exposed white blots are there
     * And how far away are black blots that could hit them
     * AND how far are those white blots from the end?? At least by quadrant?
     * And do we care how far the exposed white blots are from eventual safety?
     * 
     * Note: white starts on 1 and ends on 25 (bear)
     */
    public double getWhiteBeHitProbability( ) {
        double whiteBeHit = 0.0;
        /* finding the exposed white blots */
        for (int pointNum=1; pointNum<=howManyPoints; pointNum++) {
            if ( solitaryBlotOnPoint( pointNum , /* color:*/ white) ) {
                double thisWhiteBeHitProb = blackCanHitPointProb( pointNum );
                int distanceFromStart = pointNum; /* bar is starting point */
                int distanceToBearOff = (howManyPoints + 1) - pointNum;
                int quadrantNumber = quadrantForPoint( pointNum, white );
                double thisProbScore = distanceFromStart * thisWhiteBeHitProb; /* ?? /* should this be linear?? */
                whiteBeHit += thisProbScore;
            }
        } 
        return whiteBeHit;
    } // getWhiteBeHitProbability( )


    /**
     * Points 1..6 are quadrant "1", 7..12 = q"2", for white, etc.
     * This works for white and black (reverse the pointNum itself before calculating black).
     * What about bar and bear? Bar is quadrant 1?? or 0?? Or don't matter for bar?
     * Well, bar is definitely not 4, since all pieces have to be in 4 to permit bearing off.
     * [ ]This would be better dividing by howManyPoints and taking floor?
     */
    public int quadrantForPoint(final int pointNum, final int playerColor ) {
        if ( ! legitStartLoc(pointNum,playerColor)) { /* also checks legitPlayerColor( ) */
                throw new IllegalArgumentException("Bad pointNum '" + pointNum + "'");
        }
        /* so now we know we've got bar or point-on-board (1..24) */
        int pointForMe = pointNum;
        if (playerColor == black) {
            pointForMe = (howManyPoints + 1) - pointNum;
        }
        if ((1<=pointForMe) && (pointForMe <= 6)) {
            return 1;
        } else if ((7<=pointForMe) && (pointForMe <= 12)) {
            return 2;
        } else if ((13<=pointForMe) && (pointForMe <= 18)) {
            return 3;
        } else if ((19<=pointForMe) && (pointForMe <= 24)) {
            return 4;
        } else {
            return 1; //or is bar quadrant "0"?
        }
    } // quadrantForPoint( )


    /**
     * for a particular point, what are the odds black can land on it.
     * Unwritten, not in use yet.
     */
    public double blackCanHitPointProb( int point ) {
        System.out.println("blackCanHitPointProb needs lots of work because not all rolls");
        System.out.println("work to bring dangerous blots onto us: if there is a protected");
        System.out.println("point in between then enemy can't use it as a step toward hitting us!");
        System.out.println("Uh-oh: don't forget that black blots on bar can hit us!");
        return 0.5; // ?? obviously a fake answer
       // ??
    } // 



    /**
     * For a specific point, is there a solitary blot of color 'color' on it? (Unprotected, exposed!)
     */
    public boolean solitaryBlotOnPoint( int pointNum ,int playerColor ) {
        if (! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        return (getHowManyBlotsOnPoint(pointNum, playerColor) == 1);
    } // solitaryBlotOnPoint( )


     /** 
      * For a specific point, are there two or more blots of color 'color' on it? (Protected!)
      * Another way to ask this is "if ( howMuchProtected(p, color) > 1) {..."
      */
     public boolean isProtected( int pointNum ,int playerColor ) {
         if (! legitPlayerColor(playerColor)) {
             throw new IllegalArgumentException("bad color '" + playerColor + "'");
         }
         return (getHowManyBlotsOnPoint(pointNum, playerColor) > 1);
     } // isProtected( )


     /**
      * For a specific point, how many blots of color 'color' are on it? (Protected!)
      * Might return 0.
      * Equivalent to getHowManyBlotsOnPoint(pointNum, playerColor);
      */
     public int howMuchProtected( int pointNum ,int playerColor ) {
         if (! legitPlayerColor(playerColor)) {
             throw new IllegalArgumentException("bad color '" + playerColor + "'");
         }
         return getHowManyBlotsOnPoint(pointNum, playerColor);
     } // howMuchProtected( )



    /**
     * Says how many blots of specified color are still on the board.
     * Doesn't seem to count any on the bar??
     * was called "getBlackOnBoard" and "getWhiteOnBoard"
     */
    public int getBlotCountOnBoard(int playerColor ) {
        if (! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("bad player color '" + playerColor + "'");
        }
        int sum = 0;
        
        for (int i=1; i<=howManyPoints; i++) {
            sum += getHowManyBlotsOnPoint(i, playerColor);
        }
        
        System.out.println("There are currently " + sum + " " + colorName( playerColor) + " blots on the board");
        return sum;
    } // getBlotCountOnBoard



    /**
     * blots can't bear off until all 15 are on final 6 points (final quadrant).
     */
    public boolean canBearOff(final int playerColor) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad player color '" + playerColor + "'");
        }
        
        int sum = 0;
        
        if (playerColor==white) {   
            // add up the white blots on last 6 points + those that already did bear off.
            // If all the blots are here, we can bear off!
            for (int i=startOfWhiteBearOffZone; i<=endOfWhiteBearOffZone; i++) {
                sum += getHowManyBlotsOnPoint(i, white);
            }
            sum = sum + white_bear;
        } else if (playerColor==black) {
            // add up black blots on first 6 points
            for (int i=startOfBlackBearOffZone; i<=endOfBlackBearOffZone; i++) {
                sum += getHowManyBlotsOnPoint(i, black);
            }
            sum = sum + black_bear;
        } else {
            /* why didn't legitPlayerColor( ) find this, above??? */
           throw new IllegalArgumentException("bad player color param '"+ playerColor + "'");
        }
        
        if (sum==howManyBlots) {
            return true;         //There are 15 blots (pieces) in backgammon
        }
        return false;
    } // canBearOff


    /**
     * True if specified color has any blots on the bar
     */
    public boolean onBar(int playerColor) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad player color '" + playerColor + "'");
        }
        if (playerColor==white) {
            if (white_bar > 0)  {
                return true;
            } else {
                return false;
            }
        } else if (playerColor==black) {
            if (black_bar>0) {
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    } // onBar



    /**
     * How many blots of specified color are on its bar
     */
    public int howManyOnBar(int playerColor) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
        if (playerColor==white) {
            return white_bar;
        } else if (playerColor==black) {
            return black_bar;
        } else {
            throw new IllegalArgumentException("bad playerColor '" + playerColor + "'");
        }
    } // howManyOnBar



    /**
     * Selects a point and shows the possible moves.
     *
     * This happens when current player clicks one of her own blots: this calculates the potential 
     * moves (dest1 & dest2) 
     * and displays the potential move buttons on the points that we can move this blot to.
     * Memorizes the clicked upon point as "old_point".
     * ?? Does this ensure that everybody is in from the bar?? 
     *   No, "handleBar" does that and is called by Game.superMove
     */
    public void handlePoint(int pointNum, int playerColor) {
        //int potDest1, potDest2; Don't declare, using the class fields. (Bad idea?)
        // The player cannot move the other's blots
        if ((getColorOnPoint(pointNum)==playerColor) && (!myGame.status.point_selected)) {
            // Get the possible destinations when starting from that point
               potDest1 = endPointMovingFrom(pointNum, myDice.getDie(1 ), playerColor, this);
               potDest2 = endPointMovingFrom(pointNum, myDice.getDie(2 ), playerColor, this);
//             if (playerColor==white) { potDest1 = pointNum + getDie(1); potDest2 = pointNum + getDie(2);
//                 // If the player can make no other moves, allow him
//                 // to bear off with rolls larger than what is needed to bear off
//                 if (needsInexactRolls(playerColor) ) {
//                     if (potDest1 > 25) { /* white is trying to bear off and going too far! */
//                         potDest1 = 25;
//                     }
//                     if (potDest2 > 25)  { /* white is trying to bear off and going too far! */
//                         potDest2 = 25;
//                     }
//                 }
//             } else if (playerColor==black) {
//                 potDest1 = pointNum - getDie(1); potDest2 = pointNum - getDie(2);
//                 // If the player can make no other moves, allow him
//                 // to bear off with rolls larger than what is needed to bear off
//                 if (needsInexactRolls(playerColor)) {
//                     if (potDest1 < 0) { /* black is trying to bear off and going too far! */
//                         potDest1 = 0;
//                     }
//                     if (potDest2 < 0) { /* black is trying to bear off and going too far! */
//                         potDest2 = 0;
//                     }
//                 }
//             } // player white/black

            // If a move is valid, enable the button to move to it. Perhaps this stuff should be in Game
            if ( (canLandOn(potDest1,playerColor)) && (! myDice.getUsedDie( 1))) {
                if ((1 <= potDest1) && (potDest1 <= howManyPoints)) {
                    myGame.FButton[myGame.btn_CancelMove].setEnabled(true);
                    myGame.FButton[myGame.btn_AtPotentialMove1].drawOnPoint(potDest1);
                    myGame.status.point_selected = true;
                } else {
                    // The possible move leads to bearing off
                    myGame.FButton[myGame.btn_CancelMove].setEnabled(true); 
                    myGame.FButton[myGame.btn_BearOff].setEnabled(true);
                    myGame.status.point_selected = true;
                }
            } else {
                potDest1 = ILLEGAL_MOVE;
            }// end if move1 is valid
            
            if ( (canLandOn(potDest2,playerColor)) && (! myDice.getUsedDie( 2 ))) {
                if ((1<= potDest2) && (potDest2 <= howManyPoints)) {
                    myGame.FButton[myGame.btn_CancelMove].setEnabled(true);
                    myGame.FButton[myGame.btn_AtPotentialMove2].drawOnPoint(potDest2);
                    myGame.status.point_selected = true;
                } else {
                    // The possible move leads to bearing off
                    // if (potDest2 == WHITE_BEAR_OFF_LOC   or  BLACK_BEAR_OFF_LOC
                    myGame.FButton[myGame.btn_CancelMove].setEnabled(true);
                    myGame.FButton[myGame.btn_BearOff].setEnabled(true);
                    myGame.status.point_selected = true;
                }
            } else {
                potDest2 = ILLEGAL_MOVE;
            } // if move2 is valid
            old_point = pointNum;
        }
        myGame.debug_msg("handlePoint() is ending");
    } // handlePoint( )



    /**
     * Returns whether the current player can't move anywhere else
     * and needs to be able to bear off with an inexact roll.
     *
     * Note: still can't use small roll to bear off when higher blots exist!
     * temporarily unprivate so I can test it...
     */
    /*private*/ boolean needsInexactRolls(int playerColor) {
        
        boolean canmove = false;
        int move1, move2;
        // Cycle through all the points
        
        if ((! canMoveExact(playerColor) )  && (canBearOff(playerColor))) {
            // ?? and no blots are on higher pointNum than the blot that wants to move/?
            return true;
        } else {
            return false;
        }
    } // needsInexactRolls( )


    /**
     * Uses method   boolean canMove(int color)
     * Should it use boolean canLandOn(int newPointNum)??
     */
    ArrayList<PartialMove> allLegalPartialMoves( int playerColor /*, Game myGame*/) 
    /*throws BadMoveException, BadPartialMoveException, BadBoardException*/ {        
        /* using handlePoint( ) to find all legal moves. Might also want to check canMove( ) */
        if ( ! canMove( playerColor ) ) {
            System.out.println(Board.colorName( playerColor ) + " cannot move.");
            return new ArrayList<PartialMove>( ); /*  I will return an ArrayList that has no elements ! */
        }
        LocList myPoints = allMoveableBlotLocs(playerColor/*, myGame*/); /* might be empty */
        if (myPoints.myList.isEmpty( )) {
            System.out.println(Board.colorName( playerColor ) + " has no moveable blots.");
            return new ArrayList<PartialMove>( ); /*  I will return an ArrayList that has no elements ! */
        }
        System.out.println("in board.allLegalPartialMoves( ), the moveableBlotLocs==" + myPoints.toString( ));
        
        ArrayList<PartialMove> bunchOfPartialMoves = new ArrayList<PartialMove>( );   
             /* for storing & returning a collection of "PartialMove"s */
        /* Work through every point in myPoints */
        for (int myPoint : myPoints.myList ) {
            bunchOfPartialMoves.addAll( legalPartialMovesFromPoint(myPoint, playerColor) );
        }
        System.out.println("in board.allLegalPartialMoves( ), the bunchOfPartialMoves=='" + bunchOfPartialMoves.toString( ) + "'");
        return bunchOfPartialMoves;
    } /* allLegalPartialMoves( ) */


    /**
     * Saving a list of the partial moves that can be made from a particular point.
     * Might be empty list. 
     * Called by allLegalPartialMoves( )
     * Only gets called if there are blots of myColor on myPoint, supposedly.
     * This is counting on the move math calculator (endPointMovingFrom:) to handle bar and bear properly!
     */
    ArrayList<PartialMove> legalPartialMovesFromPoint(int myPoint, int playerColor) {
        handlePoint( myPoint, playerColor ); 
             /* will discover the potential points we can move to: potDest1, potDest2.*/
        /* Since this is worried about "Partial" (one-step) moves, dice doubles aren't an issue. There's no potDest3,4. */
        int endPointA = potDest1; // better check not zero!
        int endPointB = potDest2;
        int dice1 = myDice.getDie(1);
        int dice2 = myDice.getDie(2);
        
        ArrayList<PartialMove> bunchOfPartialMoves = new ArrayList<PartialMove>( );   
                /* for storing & returning a collection of "PartialMove"s */
        if (! myDice.getUsedDie(1) ) {
            int endPoint1 = /*Board.*/endPointMovingFrom(myPoint, dice1, playerColor, this);
            System.out.println("for starting at " + myPoint + " with roll:" + dice1 + " estimated move to:" + endPoint1);
            PartialMove fakePartialMove1 = new PartialMove( myPoint, dice1, endPoint1, myGame, playerColor, /* whichDie:*/ 1 );
            bunchOfPartialMoves.add(fakePartialMove1);
        }
        
        if (! myDice.getUsedDie(2)) {
            /* building move2, should check whether it is legal */
            int endPoint2 = endPointMovingFrom(myPoint, dice2, playerColor, this /*board*/);
            PartialMove fakePartialMove2 
               = new PartialMove( myPoint, dice2, endPoint2, myGame, playerColor,/*whichDie:*/2 );           
            bunchOfPartialMoves.add(fakePartialMove2);
        }
        System.out.println("in legalPartialMovesFromPoint(" + myPoint + "..), the bunchOfPartials=='" + bunchOfPartialMoves.toString( ) + "'");
        return bunchOfPartialMoves;
    } /* legalPartialMovesFromPoint( ) */


    /**
     * Calculate just one legal move. This is just an attempt to sneak up on designing "allLegalMoves( )".
     * Not for real use!
     * Note: this is a "full" move, using all the dice. This is not just a partial move (which use one die).
     */
    Move aLegalMove( int playerColor /*, Game myGame*/) throws BadBoardException, BadMoveException {
        ArrayList<PartialMove> partials1 = allLegalPartialMoves(playerColor );
        PartialMove myPartial1 = partials1.get(0);
        Board partialBoard = new Board(myGame, this); /* a copy of this including dice values */
        partialBoard.doPartialMove(myPartial1); 
        /*maybe doPartialMove could refrain from switching playerColor and therefore be in Board class...*/
        ArrayList<PartialMove> partials2 =  partialBoard.allLegalPartialMoves(playerColor);
        PartialMove myPartial2 = partials2.get(0);
        
        ArrayList<PartialMove> allMyPartials = new ArrayList<PartialMove>( );
        allMyPartials.add( myPartial1 );
        allMyPartials.add( myPartial2 );
        if (myDice.isDoubles( )) { // or while (partialBoard.hasmoves(playerColor))
            // get more moves
            throw new IllegalArgumentException("not done calculating doubles moves");
        }
        return new Move( allMyPartials, playerColor, myGame);
    } /* aLegalMove( ) */




    /**
     * Uses method boolean canMove(int color)
     * Should it be using boolean canLandOn(int newPointNum, playerColor)??? 
     * Beware: with doubles (4 partial moves) I think that 15 blots provides up to 15^4 possible moves = 50,625.
     * (Without doubles, there are up to 450 possible moves (2*15^2) for each pair-of-dice roll.
     */
    ArrayList<Move> allLegalMoves( int playerColor /*, Game myGame*/) 
    /*throws BadMoveException, BadPartialMoveException, BadBoardException*/ {
        System.out.println("allLegalMoves is totally fake, FIX!!");
        ArrayList<PartialMove> currentPartials = allLegalPartialMoves(playerColor );
        
        throw new NullPointerException( "allLegalMoves isn't built yet.");
//         /* do something with every partial move: perhaps build a new board, 
//           * and then if we still have more moves left,
//          * ask that new board what its legal partial moves are and combine them with this currPartial. */ 
//         /* Each "move" is a collection of partial moves */
//         int endPoint1 = Board.endPointMovingFrom(newPoint1Start, dice1, playerColor, this);
//         System.out.println("for starting at " + newPoint1Start + " with roll:" 
//                 + dice1 + " estimated move to:" + endPoint1);
//         PartialMove fakePartialMove1 
//           = new PartialMove( newPoint1Start, dice1, endPoint1, myGame, playerColor, /*whichDie:*/1 );
//         
//         int endPoint2 = Board.endPointMovingFrom(newPoint2Start, dice2, playerColor, this);
//         System.out.println("for starting at " + newPoint2Start + " with roll:" 
//                  + dice2 + " estimated move to:" + endPoint2);
//         PartialMove fakePartialMove2 
//              = new PartialMove( newPoint2Start, dice2, endPoint2, myGame, playerColor, /*whichDie:*/2 );
//         
//         ArrayList<PartialMove> temp = new ArrayList<PartialMove>();
//         temp.add(fakePartialMove1);
//         temp.add(fakePartialMove2);
//         Move fakeMove1 = new Move(temp, playerColor, myGame);
//         
//         /* building move2 */
//         int endPoint3 = Board.endPointMovingFrom(newPoint3Start, dice1, playerColor, myGame.getMyBoard( ));
//         PartialMove fakePartialMove3 
//              = new PartialMove( newPoint3Start, dice1, endPoint3, myGame, playerColor,/*whichDie:*/1 );
//         int endPoint4 = Board.endPointMovingFrom(newPoint4Start, dice2, playerColor, myGame.getMyBoard( ));
//         PartialMove fakePartialMove4 
//              = new PartialMove( newPoint4Start, dice2, endPoint4, myGame, playerColor,/*whichDie:*/2  );
//        
//         temp.clear( );
//         temp.add(fakePartialMove3);
//         temp.add(fakePartialMove4);
//         Move fakeMove2 = new Move(temp, playerColor, myGame);
//         
//         /* planning to return a collection of "Move"s */
//         ArrayList<Move> bunchOfMoves = new ArrayList<Move>( );
//         bunchOfMoves.add(fakeMove1);
//         bunchOfMoves.add(fakeMove2);
//         System.out.println("in game.allLegalMoves( ), the bunchOfMoves==" + bunchOfMoves.toString( ));
//         return bunchOfMoves;
    } /* allLegalMoves( ) */


    /**
     * Alternate calling overload: breaks apart fields and passes them to old doPartialMove, to
     * spare us from having to manufacture temporary PartialMoves in the button listeners.
     * But that might not be so bad??
     */
    void doPartialMove(PartialMove pm) {
        if (pm == null) {
            /* barf or just don't do empty move? The latter is friendlier... */
            throw new NullPointerException("c'mon, give me a real PartialMove to do!");
        }
        doPartialMove(pm.getStart( ), pm.getEnd( ), pm.getWhichDie( ), pm.getColor( ));
        /* ignoring roll */
    } /* doPartialMove */


    /** 
     * Handle moving a blot to a point. (Was originally named 'superMove'.)
     * "fromPoint" - where the blot is starting (might be 0 (white bar) or 25 (black bar))
     * "toPoint" - the new position (point number) to move to.
     * "whichDie" - which dice is being used, the first one or the second one.
     * 
     * calls "Board.moveBlot( )" method
     * There is a "myDice.getUsedDice( )" which says which partial moves 
     * (corresponding to which dice) have been used. 
     * myDice.isDoubles( ); is true when doubles have been rolled, 
     * and myDice.getDoubletMovesCountdown( ) keeps track of 4 moves countdown.
     * 
     * Note: if this is the final partial move, this switches players by calling Game.endTurn( )!
     */
    /*private*/ void doPartialMove(int fromPoint, int toPoint, int whichDie, int playerColor) {
        /* In networked mode: 25 = to bar, 26 = bear off */
        if ( ! Board.legitStartLoc( fromPoint, playerColor )) { /* also checks legitPlayerColor */
            throw new IllegalArgumentException("Can't start moving from point '" + fromPoint + "'");
        }
        if ( ! Dice.legitDieNum( whichDie )) {
            throw new IllegalArgumentException("Can't use Die number '" + whichDie + "'");
        }
        if ( ! Board.legitEndLoc( toPoint, playerColor )) {
            throw new IllegalArgumentException("Can't legally move to point '" + toPoint + "'");
        }

        boolean switchedplayers = true;

        // If the new space is empty, make the move
        // Else send the opponent on the bar first
        int blotColor = getColorOnPoint(toPoint);
        if ((blotColor==playerColor ) || (blotColor==neutral)) {
            /* formerly used "old_point" field which said where the blot in motion was started from */
            moveBlot(playerColor, fromPoint /* was myBoard.getOldPoint( )*/, toPoint);
            if ( myGame.status.networked && (!myGame.status.observer) ) {
                myGame.comm.sendmove(fromPoint /* was myBoard.getOldPoint()*/, toPoint);
            }
        } else { 
            // send the opponent on the bar first
            moveToBar(toPoint, blotColor);
            moveBlot(playerColor, fromPoint /*was myBoard.getOldPoint()*/, toPoint);
            if (myGame.status.networked) {
                myGame.comm.sendonbar(toPoint);
                myGame.comm.sendmove(fromPoint /* was myBoard.getOldPoint()*/, toPoint);
            }
        } // end if move-to is legit (our color or neutral)
        
        if (!myDice.isDoubles( )) {
            // If a move has been made previously,
            // this is the second move, end the player's turn
            if ((myDice.getUsedDie( 1)) || (myDice.getUsedDie(2 ))) {
                myGame.endTurn();
            } else {
                switchedplayers = false;
                myDice.setUsedDie( whichDie,true );
            }
        } else if (myDice.isDoubles( )) {
            myDice.doubletCountdown( );
            if (myDice.getDoubletMovesCountdown( )==0) {
                myGame.endTurn();
            } else {
                switchedplayers = false;
            }
        }
        
        // Turn off focus on this point
        myGame.endPartialMove();
        myGame.repaint();
        
        // If this wasn't the player's last move,
        // check if he is still on the bar or if he can make more moves
        if ( ! switchedplayers ) {
            if (onBar(playerColor )) {
                handleBar(playerColor);
            }
            if (!canMove(playerColor )) {
//                forfeit(); ?? 
            }
        }
    } // doPartialMove( )


    /** 
     * Return whether the current player can place a blot at a certain position.
     * This checks whether moves can bear off with inexact dice.
     * See "canLandOnExact( )" which only accepts precise moves matching dice roll.
     * 
     * Is this checking that nobody is still on the bar waiting to come in??
     * Hmmm, this might be actually checking whether the blot on the bar can come in here,
     * so don't be too negative about there being somebody on the bar!
     * was called "checkFair"
     */
    public boolean canLandOn(int pointNum, int playerColor) {
        if (! legitEndLoc(pointNum, playerColor)) { /* checks legitPlayerColor */
            // Or?? return false; 
            throw new IllegalArgumentException("bad pointNum '" + pointNum + "'");
        }
        
        if (canLandOnExact(pointNum, playerColor)) { // allows BEAR_OFF_LOC, not PAST_BEAR_OFF
            return true;
        } else if (((pointNum == WHITE_PAST_BEAR_OFF_LOC) && (playerColor == white)) 
            || ((pointNum == BLACK_PAST_BEAR_OFF_LOC) && (playerColor == black))) {
            if ( canBearOff(playerColor) && needsInexactRolls(playerColor)) {
                return true;
            }
        }
        return false;
    } // canLandOn


    /** 
     * Return whether the current player can place a blot at a certain position.
     * See "canLandOn( )" which is like this but allows for bearing off with inexact dice rolls.
     *
     * Is this checking that nobody is still on the bar waiting to come in??
     * Hmmm, this might be actually checking whether the blot on the bar can come in here,
     * so don't be too negative about there being somebody on the bar!
     * was called checkFair
     */
    public boolean canLandOnExact(int pointNum, int playerColor) {
        if (! legitEndLoc(pointNum,playerColor)) { /* checks legitPlayerColor */
            // Or?? return false; 
            throw new IllegalArgumentException("bad pointNum '" + pointNum + "'");
        }
        
        if ((pointNum == WHITE_PAST_BEAR_OFF_LOC) 
             || (pointNum == BLACK_PAST_BEAR_OFF_LOC)) {
            return false;
        }

        if (((pointNum == WHITE_BEAR_OFF_LOC) && (playerColor == white)) 
            || ((pointNum == BLACK_BEAR_OFF_LOC) && (playerColor == black))) {
            if ( canBearOff(playerColor)) {
                return true;
            } // else returns false below
        } else if (1 == getHowManyBlotsOnPoint(pointNum)) {
               // If there is only one blot of either color, the move is legal
               // so don't use the color specifying form of getHowManyBlotsOnPoint( )!
            return true;
        } else {
            // If the target point is empty or has the user's own blots, the move is legal
            int pointColor = getColorOnPoint(pointNum);
            if ((pointColor==neutral) || (pointColor==playerColor )) {
                return true;
            }
        }
        return false;
    } // canLandOnExact



    /**
     * With the current rolls, can the user move anywhere?
     * Beware: this calls "needsInexactRolls()" which can either call 
     * this canMove() or does its equivalent?? looping through all points
     * This will call canMoveExact( ), and if stuck then try canBearOff and needsInexactRolls
     */ 
    public boolean canMove( int playerColor) {
        if (! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("Bad color '" + playerColor + "'");
        }
        if ((myDice.isDoubles( )) && (myDice.getDoubletMovesCountdown( ) < 1)) {
            return false;
        }
        // ?? need total movesCountdown so we can use up PartialMoves !!
        int move1, move2;
        // Cycle through all the points
        for (int point = 1; point <= howManyPoints; point++) {
            // Only check points which contain the player's blots
            if (getColorOnPoint(point) == playerColor ) {
                move1 = endPointMovingFrom(point, myDice.getDie(1 ), playerColor, this); 
                       // might return BLACK_PAST_BEAR_OFF_LOC
                move2 = endPointMovingFrom(point, myDice.getDie(2 ), playerColor, this);
                    //if (playerColor==white) { move1 = point + getDie(1); move2 = point + getDie(2);
                    //} else { move1 = point - getDie(1); move2 = point - getDie(2); }
                if ( (canLandOn(move1, playerColor) && (!myDice.getUsedDie( 1))) 
                   || (canLandOn(move2, playerColor) && (!myDice.getUsedDie(2)))) {
                    return true;
                }
                // canLandOn() only allows bearing off with exact rolls.
                // If the player has no other option, moving with a roll greater than needed to bear off is legal.
                
                //} else if ( (needsInexactRolls(playerColor)) 
                //   && (move1 > 25 || move1 < 0 || move2 > 25 || move2 < 0)) { 
                     // White's bearOff move is 25, Black's is 0 
                if (playerColor == white ) {
                    if ((move1 == WHITE_BEAR_OFF_LOC) || (move2 == WHITE_BEAR_OFF_LOC)) { 
                        return true; 
                    }
                    /* are the parentheses right in the following?? */
                    if ((needsInexactRolls(playerColor)) 
                       && ((move1==WHITE_PAST_BEAR_OFF_LOC) || (move1==WHITE_PAST_BEAR_OFF_LOC))) {
                        return true;
                    }
                }
                if (playerColor == black ) {
                    if ((move1 == BLACK_BEAR_OFF_LOC) || (move2 == BLACK_BEAR_OFF_LOC)) { 
                        return true; 
                    }
                    /* are the parentheses right in the following?? */
                    if ((needsInexactRolls(playerColor))
                      && ((move1==WHITE_PAST_BEAR_OFF_LOC) || (move1==WHITE_PAST_BEAR_OFF_LOC))) {
                        return true;
                    }
                }
            }
        }
        return false;
    } // canMove( )



    /**
     * With the current rolls, can the user move anywhere with exact dice rolls? 
     * (Later we'll worry about whether inexact rolls are allowed.)
     * Beware: the broader "canMove" calls this and then calls "needsInexactRolls()" if necessary.
     * Watch out for either of them calling this canMoveExact() or does its equivalent?? looping through all points.
     *
     * This used to have simple overflow/underflow move math:
     *  if (playerColor==white) { move1 = point + getDie(1); move2 = point + getDie(2);
     *  } else { move1 = point - getDie(1); move2 = point - getDie(2); }
     */ 
    public boolean canMoveExact( int playerColor) {
        if (! legitPlayerColor(playerColor)) {
            throw new IllegalArgumentException("Bad color '" + playerColor + "'");
        }
        if ((myDice.isDoubles( )) && (myDice.getDoubletMovesCountdown( ) < 1)) {
            return false;
        }
        // ?? need total movesCountdown so we can use up PartialMoves !!
        int move1, move2;
        // Cycle through all the points
        for (int point = 1; point <= howManyPoints; point++) {
            // Only check points which contain the player's blots
            if (getColorOnPoint(point) == playerColor ) {
                move1 = endPointMovingFrom(point, myDice.getDie(1 ), playerColor, this); // might return BLACK_PAST_BEAR_OFF_LOC
                move2 = endPointMovingFrom(point, myDice.getDie(2 ), playerColor, this);
                if ( ((canLandOnExact(move1, playerColor)) && (!myDice.getUsedDie( 1))) 
                   || ((canLandOnExact(move2, playerColor)) && (!myDice.getUsedDie(2 ))) ) {
                    return true;
                }
                // canLandOnExact() only allows bearing off with exact rolls.
                // If the player has no other option, moving with a roll greater than needed to bear off is legal
            }
        }
        return false;
    } // canMoveExact( )


    /** 
     * Gives locations of moveable blots. Doesn't say how many are at each loc.
     * If I have blots on the bar, they are the only moveables!
     * Beware: black moves in negative direction
     * and old version of game coded 0 as black wanna-bear-off and 25 as white wanna-bear-off.
     * Shouldn't this encode HOW MANY moveable blots are at that location?
     * 
     * Beware?? This marks as moveable ANY blot, even if it has a wall of barricades
     */
    public LocList allMoveableBlotLocs( int playerColor/*, Game myGame *//*, int moveDist*/ ) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        LocList myMovers = new LocList( );
        if ( /*myBoard.*/getBar( playerColor ) > 1) {
            //System.out.println("I don't know how to calculate moves for blots waiting to come in from bar." 
            //    + "Sorry. Fix me!");
            if (playerColor == white) {
                myMovers.myList.add(new Integer( Board.WHITE_BAR_LOC ));
            } else {
               myMovers.myList.add(new Integer( Board.BLACK_BAR_LOC ));
            }
            return myMovers;
            //if (canOnlyMoveFromBar( playerColor )) {
                /* only thing we can do is move in from bar, so how to show that in a list: 
                with a code number? Or a "PointLoc" class? */
            //}
        }
        int move1, move2;
        int dice1 = myDice.getDie(1);
        int dice2 = myDice.getDie(2);
        // Cycle through all the points
        for (int point = 1; point <=howManyPoints; point++) {
            // Only check points which contain the player's blots
            if (getColorOnPoint(point) == playerColor) {
                myMovers.myList.add(new Integer(point));
                /* if (playerColor==white) { move1 = point + dice1(); move2 = point + dice2();
                } else { move1 = point - dice1(); move2 = point - dice2();
                }*/
                /*
                if ( ((canLandOn(move1,playerColor)) && (! myDice.getUsedDice(1))) 
                 || ((canLandOn(move2,playerColor)) && (! myDice.getUsedDice(2))) )
                { return true; }
                */
                // canLandOn() only allows bearing off with exact rolls.
                // If the player has no other option, moving with a roll 
                // greater than needed to bear off is legal
                // White's bearOff move was 25, Black's is 0 
                /* else if (needsInexactRolls() && (move1 > 25 || move1 < 0 || move2 > 25 || move2 < 0))
                { return true; } */
            }
        }
        return myMovers;
    } // allMoveableBlotLocs( )


    /**
    * With doubles we can possibly move 3 blots in from bar and still have a 4th blot to move,
    * and without doubles then we can move 1 blot in from bar and still have a move left.
    * This says whether we're stuck moving ONLY blots from the bar.
    * used by "allMoveableBlotLocs( )"
    */
    private boolean canOnlyMoveFromBar( int playerColor ) {
        if ( ! legitPlayerColor(playerColor) ) {
            throw new IllegalArgumentException("bad color '" + playerColor + "'");
        }
        
        if (myDice.getDie(1) == myDice.getDie(2)) { /* doubles! */
            return (/*myBoard.*/getBar( playerColor ) > 3);
        } else {
            return (/*myBoard.*/getBar( playerColor ) > 1);
        }
    } /* canOnlyMoveFromBar( ) */
    
    
} // class Board
