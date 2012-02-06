/***************************************************************
JBackgammon (http://jbackgammon.sf.net)
note: "http://jbackgammon.sf.net" no longer exists but jumps to 
"http://jbackgammon.sourceforge.net/" which is just a placeholder: no code, no working links, no info.
??possible alternate "http://djbackgammon.sourceforge.net/" has no source code, might not be java, 
and gives credit to different developer "David le Roux"
 
Copyright (C) 2002
George Vulov <georgevulov@hotmail.com>
Cody Planteen <frostgiant@msn.com>
revised 2011-12 by Joshua G., Julien S., Mike Roam

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
 * File: Game.java (was JBackgammon.java)
 *
 * Description: This file contains the guts of the main program.  
 * All drawing, control, and rule-checking occurs here.
 * A "Game" has a board, and the board has dice. 
 */

 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Random;
import java.util.*;  // provides Collections


public class Game extends JFrame implements ActionListener, CommunicationAdapter {
    static final String VERSION = "1.4";
    public static final long serialVersionUID = 1L; // mjr, version 1
    
    // point colors (player colors are only white & black)
    /* Beware: Board has a duplicate list of these colors which had better stay identical! */
    static final int neutral = 0;
    static final int white = 1;
    static final int black = 2;

    static final int LEFT_MARGIN = 20;
    static final int TOP_MARGIN = 60;

    // Buffers for double buffering
    BufferedImage b_bimage;
    Graphics2D g_buffer;
    BoardPict myBoardPict = new BoardPict( /* could receive board size param someday! */);
    Board myBoard = null; // this gets set up in constructor or die
    AI myAI;
    private int currentPlayer = white;

    // This contains some booleans about the status of the game
    Status status = null;

    
    Communication comm = null;    // performs the network operations
    JTextField msg_input = null;    // for displaying messages (during network game?)
    JTextArea msg_display = null;    // display messages between the players
    JScrollPane msg_scrollpane = null;    // for scrolling messages


    // The buttons the gui uses for various purposes.
    // Bummer: the buttons that show legal available moves on the board
    // are part of this, rather than being part of the Board.
    FixedButton FButton[] = new FixedButton[9]; /* array of buttons 0..8 */

    static final int btn_CancelMove = 0;
    static final int btn_RollDice = 1;
    static final int btn_BearOff = 2;
    static final int btn_AtPotentialMove1 = 3;
    static final int btn_AtPotentialMove2 = 4;
    static final int btn_Connect = 5; /* only if networked */
    static final int btn_SendMessage = 6; /* only if networked */
    static final int btn_NewGame = 7;
    static final int btn_ComputerMove = 8;
   
    // Button labels
    static final String CANCEL = "Cancel Move";
    static final String ROLL_DICE = "Roll Dice";
    static final String BEAR_OFF = "Bear Off";
    static final String MOVE1 = "M1";
    static final String MOVE2 = "M2";
    static final String CONNECT = "Connect";
    static final String SEND_MSG = "Send Message";
    static final String NEW_GAME = "New Game";
    static final String COMPUTER_MOVE = "Computer Move";

    static final int GUI_WIDTH = 202;
    /* GUI fits in the game BOARD_HEIGHT, sitting next to board */
    static final int BOARD_PADDING = 120;
    static final int MESSAGE_HEIGHT = 80; /* only when networked */


    /*=================================================
     * Game-related Methods 
     * ================================================*/


/**
    * Game class constructor
    * assumes you're playing against AI.
    * (Use the Game(boolean) constructor if you want to set up network game)
    */
    public Game() {
        this(false /* networkedTF */);  // merely call fancier constructor
    }


    /**
    * Game class constructor
    * Sets title bar, size, shows the window, and does the GUI
    */
    public Game(boolean networkTF /* networked true/false */) {
        setTitle("JBackgammon");
        setResizable(false); /* someday this can be resizable when all dimensions are relative */
        status = new Status();
        myBoard = new Board(this);
        myAI = new AI( this);
        status.networked = networkTF;

        addMouseListener(new tablaMouseListener(this));

        // Call pack() since otherwise getItsets() does not work until the frame is shown
        pack();

        for (int i=0; i < FButton.length; i++) {
            /* create all the buttons */
            FButton[i] = new FixedButton(getContentPane(), this);
        }

        if (status.networked) {
            setupNetworking( );
            setSize(myBoardPict.BOARD_WIDTH + GUI_WIDTH/*632*/
               , myBoardPict.BOARD_HEIGHT + BOARD_PADDING + MESSAGE_HEIGHT /*560*/);
        } else {
            setSize(myBoardPict.BOARD_WIDTH + GUI_WIDTH/*632*/, myBoardPict.BOARD_HEIGHT + BOARD_PADDING);
        }

        // Set up double buffering
        b_bimage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        g_buffer = b_bimage.createGraphics();

        setupGUI();
        setVisible(true); // was the deprecated "show()";
    } // Game( ) constructor


    /**
      * Called by Game(boolean) constructor if we're networking
      */
      private void setupNetworking( ) {
            comm = new Communication((CommunicationAdapter)this);
            comm.listen();
            // Set up the window for messaging
            getRootPane().setDefaultButton(FButton[btn_SendMessage]);
            msg_input = new JTextField();
            getContentPane().add(msg_input);
            msg_display = new JTextArea();
            msg_scrollpane = new JScrollPane(msg_display);
            msg_scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            getContentPane().add(msg_scrollpane);
        } /* setupNetworking( ) */


     /**
      * Is called by Board, so can't be private.
      */
    public void debug_msg(String dmsg) {
        /*System.out.println("----------------");
        System.out.println("Breakpoint " +  dmsg);
        System.out.println("status.point_selected = " + status.point_selected + "   old_point = " + old_point);
        System.out.println("currentPlayer = " + currentPlayer + "   Dice = " + getMyBoard( ).myDice);
        System.out.println("potDest1 = " + potDest1 + "   potDest2 = " + potDest2);
        System.out.println("doublet_moves = " + doublet_moves + " doublets = " + getMyBoard().myDice.isDoubles( ));
        // System.out.println("networked = " + status.networked + "  observer = " + status.observer);
        /// System.out.println("Number of black = " + myBoard.getBlack());
        // System.out.println("Number of white = " + myBoard.getWhite());
        System.out.println("----------------");
        System.out.println();*/
    } // debug_msg




   /** 
     * calls "Board.doPartialMove( )" method
     * There is a "getUsedDie( )" (in myBoard().myDice) which says which which dice have been used.
     * (getMyBoard().myDice.isDoubles( ) is true when doubles have been rolled, 
     * and Board.myDice.getDoubletMovesCountdown( ) keeps track of 4 moves countdown)
     * 
     * Note: this switches players by calling game.endTurn( )!
     */
    /*not private for testing */ void doMove(Move myMove) {
        debug_msg("doMove()");
        int howManyPartialsDone = 0;
        ArrayList<PartialMove> myPartials = myMove.getMyPartials( );
        for (PartialMove myPartial : myPartials ) {
            myBoard.doPartialMove( myPartial );
            howManyPartialsDone++;
            if (getMyBoard().myDice.isDoubles( )) {
                myBoard.myDice.doubletCountdown( ); /* -1 */
            }
            myBoard.myDice.setUsedDie( myPartial.getWhichDie( ), true );
            // Turn off focus on this point
            endPartialMove();
            repaint();
            // this better use up all the dice rolls, but not try to do too many!
        }

        //boolean switchedplayers = true;

        if (!getMyBoard().myDice.isDoubles( )) {
            // If a move has been made previously, this is 2d move, end the player's turn
            //if ((myBoard.myDice.getUsedDie(1 )) || (myBoard.getUsedDie( 2)) {
            //if ( howManyPartialsDone > 1?0? might only be able to make one move... 
                endTurn();
            //} else {
            //    /*myGame.*/switchedplayers = false;
            //    myBoard.setUsedDie( whichDie );
            // }
        } else if (getMyBoard().myDice.isDoubles( )) {
            //myBoard.myDice.doubletCountdown(  );
            //if (myBoard.getDoubletMovesCountdown( )==0) {
                endTurn();
            //} else {
            //    /*myGame.*/switchedplayers = false;
           // }
        }

        // Turn off focus on this point
        //endPartialMove();
        //repaint();
        
        // If this wasn't the player's last move,
        // check if he is still on the bar or if he can make more moves
        //if ( ! switchedplayers ) {
        //    if (myBoard.onBar(playerColor )) {
        //        myBoard.handleBar(playerColor);
        //    }
        //    if (!myBoard.canMove(playerColor )) {
        //        forfeit();
        //    }
        //}
    } // doMove( )




    /**
    * Forfeit the current player’s turn.
    * Is called by Board, so can’t be private.
    */
    public void forfeit() {
        String msg = "You are stuck, you forfeit your turn.";
        JOptionPane.showMessageDialog(this, msg);
        endTurn();
        repaint();
    } // forfeit( )


    /**
    * Checks if there is a winner
    * If there is one, displays appropriate message.
    * Return true if there was a winner, false otherwise
    */
    public boolean checkWin(int color)
    {
        String msg;

        if ( (color==white) && (!status.networked) ) {
            msg = "White wins";
        } else if ((color==black)&&(!status.networked)) {
            msg = "Black wins";
        } else {
            msg = "You win!";
        }

        if (color==white) {
            if (myBoard.white_bear==Board.howManyBlots) {
                if (status.networked) {
                    comm.sendlose();
                }
                repaint();
                JOptionPane.showMessageDialog(this, msg);
                return true;
            }
        }

        if (color==black) {
            if (myBoard.black_bear==15) {
                if (status.networked) {
                    comm.sendlose();
                }
                repaint();
                JOptionPane.showMessageDialog(this, msg);
                return true;
            }
        }
        return false;
    } // checkWin( )



    /** 
     * Roll the dice for the current player.
     * If current player is on the bar then this calls "myBoard.handleBar( )"
     * If the current player can't move, this calls "myBoard.forfeit( )"
     */
    public void doRoll() {
        myBoard.myDice.roll(); /* sets a doublet countdown (4 or 2), has method isDoubles( ) which knows the truth */
        
        if (status.networked) {
            comm.sendroll(myBoard.myDice.getDie(1), myBoard.myDice.getDie(2));
        }

        // Turn off roll dice button
        FButton[btn_RollDice].setEnabled(false); // roll dice

        repaint();

        // Check if the player is on the bar and deal with that right away before player tries to move.
        if (myBoard.onBar(currentPlayer)) {
            myBoard.handleBar(currentPlayer);
        } else if ( ! myBoard.canMove(currentPlayer) ) {
            forfeit();
        }
    } // doRoll( )


    /**
     * This could handle more than 2 players with slight modification...
     */
    public void changePlayer() {
        if (currentPlayer == white) {
            currentPlayer = black;
        } else {
            currentPlayer = white;
        }
    } /* changePlayer */



    /**
     * End the current player's turn and start the turn
     * of the other player.
     * Is called by Board, so it can't be private.
     */
    public void endTurn() {
        String msg;
        changePlayer( );

        // Reset vars, turn off new game button (why??)
        myBoard.myDice.reset();  /* calls resetUsedDice(  ),  sets rolled to false and countdown to 0 */
        FButton[btn_NewGame].setEnabled(false);
        
        repaint();

        if (!status.networked) {
            msg = "Your turn is now over.  Please switch players.";
        } else {
            msg = "Your turn is now over.";
        }

        if (status.networked) {
            comm.sendendturn();
            status.observer = true;
        }

        JOptionPane.showMessageDialog(this, msg);

        if (!status.networked) {
            startTurn();
        }
        repaint();
    } // endTurn


    /**
     *  Begins a player's turn
     */
    private void startTurn() {
        // Enable roll dice and new game buttons
        FButton[btn_RollDice].setEnabled(true);
        FButton[btn_NewGame].setEnabled(true);
        if (status.networked && !status.observer) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "It is now your turn");
        }
    } // startTurn( )


    /** 
     * This is for Partial Move
     * Remove focus from a sertain point which has been selected
     * This allows the player to select a new point.
     * called by Board, so can't be private.
     * Why does this disable the CancelMove button? No undo?
     */
    public void endPartialMove() {
        status.point_selected = false;
        // Disable potential move buttons, which ought to be part of board someday
        /*myGame.*/FButton[btn_AtPotentialMove1].setVisible(false); // potential move 1
        /*myGame.*/FButton[btn_AtPotentialMove2].setVisible(false); // potential move 2
        // Disable "Cancel Move" button
        /*myGame.*/FButton[btn_CancelMove].setEnabled(false); // cancel move
    } // endPartialMove( )

    
    public Board getMyBoard( ) {
        return myBoard;
    }
    
    
    /** 
     * returns int white = 1; black = 2; (shouldn't ever have neutral = 0;)
     */
    public int /*PlayerColor*/ getCurrentPlayer( ) {
        return currentPlayer;
    }
    
    
    /**
     * note: beware overlapping duties hardcoded into startTurn( ), endTurn( ), ??
     * Should this acknowledge a change somehow? Shouldn't roll dice, I guess.
     */
    public void setCurrentPlayer(int newPlayerColor ) {
        /* only change if currentPlayer will become different from before */
        if ((Board.legitPlayerColor( newPlayerColor )) && (currentPlayer != newPlayerColor)) {
            currentPlayer = newPlayerColor;
        }
    } 




    /**
     *  Initialize the GUI
     *   Sets up all the buttons
     */
    public void setupGUI() {
        int left = GUI_Dim.BTN_LEFT_EDGE; /* 475 when board is 430 wide */
        int width = GUI_Dim.BTN_WIDTH; /* 135 */
        int height = GUI_Dim.BTN_HEIGHT; /* 25 */
        FButton[btn_CancelMove].setBounds(left, 355, width, height);
        FButton[btn_CancelMove].setVisible(true);
        FButton[btn_CancelMove].setText(CANCEL);
        FButton[btn_CancelMove].addActionListener(this);
        FButton[btn_CancelMove].setEnabled(false);

        FButton[btn_RollDice].setBounds(left, 320, width, height);
        FButton[btn_RollDice].setVisible(true);
        FButton[btn_RollDice].setText(ROLL_DICE);
        FButton[btn_RollDice].addActionListener(this);
        FButton[btn_RollDice].setEnabled(true);

        FButton[btn_BearOff].setBounds(left, 285, width, height);
        FButton[btn_BearOff].setVisible(true);
        FButton[btn_BearOff].setText(BEAR_OFF);
        FButton[btn_BearOff].addActionListener(this);
        FButton[btn_BearOff].setEnabled(false);

 // potential move 1. Are these coords so it is hiding off the right side?
        FButton[btn_AtPotentialMove1].setBounds(650, 490, 9, 10);
        FButton[btn_AtPotentialMove1].setVisible(true);
        FButton[btn_AtPotentialMove1].setText(MOVE1);
        FButton[btn_AtPotentialMove1].addActionListener(this);
        FButton[btn_AtPotentialMove1].setEnabled(true);

 // potential move 2. Are these coords so it is hiding off the right side?
        FButton[btn_AtPotentialMove2].setBounds(750, 490, 9, 10); 
        FButton[btn_AtPotentialMove2].setVisible(true);
        FButton[btn_AtPotentialMove2].setText(MOVE2);
        FButton[btn_AtPotentialMove2].addActionListener(this);
        FButton[btn_AtPotentialMove2].setEnabled(true);

        FButton[btn_NewGame].setBounds(left, 250, width, height);
        FButton[btn_NewGame].setVisible(true);
        FButton[btn_NewGame].setText(NEW_GAME);
        FButton[btn_NewGame].addActionListener(this);
        FButton[btn_NewGame].setEnabled(true);
        
        FButton[btn_ComputerMove].setBounds(left, 380, width, height);
        FButton[btn_ComputerMove].setVisible(true);
        FButton[btn_ComputerMove].setText(COMPUTER_MOVE);
        FButton[btn_ComputerMove].addActionListener(this);
        FButton[btn_ComputerMove].setEnabled(true);

        if (status.networked) {
            FButton[btn_Connect].setBounds(left, 225, width, height);
            FButton[btn_Connect].setVisible(true);
            FButton[btn_Connect].setText(CONNECT);
            FButton[btn_Connect].addActionListener(this);
            FButton[btn_Connect].setEnabled(true);

            FButton[btn_SendMessage].setBounds(left, TOP_MARGIN + getInsets().top + 412, width, height);
            FButton[btn_SendMessage].setVisible(true);
            FButton[btn_SendMessage].setText(SEND_MSG);
            FButton[btn_SendMessage].addActionListener(this);
            FButton[btn_SendMessage].setEnabled(false);

            FButton[btn_RollDice].setEnabled(false);
            FButton[btn_NewGame].setEnabled(false);

            msg_input.setBounds(LEFT_MARGIN - getInsets().left, TOP_MARGIN + getInsets().top + 412, 450, height);

            msg_scrollpane.setBounds(LEFT_MARGIN - getInsets().left, TOP_MARGIN + getInsets().top + 327, 593, 80);
            msg_display.setEditable(false);
            msg_display.setLineWrap(true);
            msg_display.setWrapStyleWord(true);
        }
    } // setupGUI


    /**
     *  Connect to another Game for network play
     */
    public void connect() {
        String input_ip;
        input_ip = JOptionPane.showInputDialog("Enter computer name or IP address");
        FButton[btn_Connect].setEnabled(false); // connect
        if (input_ip != null) {
            if ( (comm.portBound == 1 ) 
                && ( (input_ip.equalsIgnoreCase("localhost")) || (input_ip.equals("127.0.0.1")) ) )
            {
                JOptionPane.showMessageDialog(this, "Game cannot connect to the same instance of itself");
                FButton[btn_Connect].setEnabled(true);
            } else {
                status.clicker = true;
                comm.connect(input_ip);
            }
        } else { // The user canceled, re-enable the connect button
            FButton[btn_Connect].setEnabled(true);
        }
    } // connect( )


    /**
     *  Method to send a message through a JOptionPane to the other user
     */
    public void sendMessage() {
        String message = msg_input.getText();
        if (message.length() > 0) {
            comm.sendmessage(message);
            msg_display.append("White player: " + message + '\n');
            // Scroll the text area to the bottom
            msg_display.scrollRectToVisible(new Rectangle(0, msg_display.getHeight(), 1, 1));
        }
        msg_input.setText("");
    } // sendMessage( )


    /*=================================================
     * Network Methods 
     * ================================================*/

    /**
     * The network player has won
     */
    public void receiveLose() {
        FButton[btn_NewGame].setEnabled(true);
        JOptionPane.showMessageDialog(this, "You lose!");
    } // receiveLose( )


    /**
     *  Connection lost, reset the board for a new game
     */
    public void disconnected() {
        JOptionPane.showMessageDialog(this, "Network connection lost!");
        // Allow the person to connect to someone else
        FButton[btn_Connect].setEnabled(true);
        // Reset the order of connecting
        status.clicker = false;
        // Start listening for connections again
        comm.listen();
        resetGame();
    } // disconnected( )


    /**
     * Implementing the "connectionRefused( )" method of interface CommunicationAdapter
     * Which says what to do if we could not connect to an ip
     */
    public void connectionRefused() {
        JOptionPane.showMessageDialog(this, "Connection refused.\n\nMake sure the " 
        +   "computer name/IP is correct\n" 
           + "and that the destination is running Game in networked mode.");
        status.clicker = false;
        FButton[btn_Connect].setEnabled(true);
    } // connectionRefused( )


    /**
     *  The network player has rolled the dice, display them
     */
    public void receiveRolls(int i, int j) {
        currentPlayer = black;
        myBoard.myDice.setDie(1,i);
        myBoard.myDice.setDie(2, j);
        myBoard.myDice.setRolled(true);
        repaint();
    } // receiverolls( )


    /**
     *  The non-network player got sent to the bar, update the board
     * Apparently point is a number from the point of view of the opponent,
     * So we're doing the 25 - point thing..
     */
    public void receiveBar(int point) {
          /* int destPointNum, int howMany, int color/* not merely playerColor*/
        myBoard.setPoint((Board.howManyPoints+1) - point, /*howMany:*/0, /*color:*/neutral);
        myBoard.white_bar++;
        repaint();
    } // receivebar


    /**
     * The network player requested a new game, get a response
     */ 
    public void receiveResetReq() {
        int reset = JOptionPane.showConfirmDialog(this, 
            "The network player has requested a new game.\nDo you want to accept?",
            "New Game Request",JOptionPane.YES_NO_OPTION);
        comm.sendResetResp(reset);
        if ( reset == JOptionPane.YES_OPTION ) {
            resetGame();
        }
    } // receiveResetReq


    /**
     * The network player responded to a new game request, process the results
     */ 
    public void receiveResetResp( int resp ) {
        if (resp == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(this, "Request for new game denied.");
        } else {
            JOptionPane.showMessageDialog(this, "Request for new game accepted.");
            resetGame();
            Random r = new Random();
            boolean goesfirst = r.nextBoolean();
            if (goesfirst) {
                status.observer = false;
                startTurn();
            } else {
                status.observer = true;
                comm.sendendturn();
            }
        }
    } // receiveResetResp( )


    /**
    * The network player has moved, update the board.
    * Apparently remote network player is always black?? (which explains why her moves are getting (25 - X)).
    * Apparently oldpos value -1 used to mean coming in from bar, and 26 meant bearing off?
    * Are the constants being handed in properly for BAR_ and BEAR?  Probably not.
    */
    public void receiveMove(int playerColor, int oldpos, int newpos) {
        /* throw New IllegalArgumentException("This network moving needs complete overhaul"); */
        if ( ! Board.legitStartLoc( oldpos, playerColor )) { // also checks the playerColor
            throw new IllegalArgumentException("point '" + oldpos + "' isn't a legal starting place");
        }
        if ( ! Board.legitEndLoc( newpos, playerColor )) {
            throw new IllegalArgumentException("Can't legally move to point '" + newpos + "'");
        }
        if (playerColor == white) {
            if ( (1 <= oldpos) && (oldpos<= Board.howManyPoints) && (1<=newpos) && (newpos<=Board.howManyPoints) ) {
                myBoard.moveBlot(playerColor /* white*/, oldpos, newpos);
                repaint();
            } else if (newpos==Board.WHITE_BEAR_OFF_LOC ) {
                myBoard.white_bear++;
                myBoard.setPoint(/*point*/oldpos, /*howMany:*/myBoard.getHowManyBlotsOnPoint(oldpos) - 1, playerColor/*white*/);
                repaint();
            } else if (oldpos== Board.WHITE_BAR_LOC ) {
                myBoard.white_bar--;
                myBoard.setPoint(/*point*/newpos, /*howMany:*/myBoard.getHowManyBlotsOnPoint(newpos) + 1, playerColor/*white*/);
                repaint();
            }
        } else {
            int oldPosForBlack = (Board.howManyPoints + 1) - oldpos;
            int newPosForBlack = (Board.howManyPoints + 1) - newpos;
            if ( (1<=oldPosForBlack) && (oldPosForBlack<=Board.howManyPoints) 
                && (1<=newPosForBlack) && (newPosForBlack<=Board.howManyPoints) ) {
                myBoard.moveBlot(playerColor /* black*/, oldPosForBlack, newPosForBlack);
                repaint();
            } else if (newpos == Board.BLACK_BEAR_OFF_LOC /* was 26*/) {
                myBoard.black_bear++;
                myBoard.setPoint(/*pointNum:*/newPosForBlack
                    , /*howMany:*/myBoard.getHowManyBlotsOnPoint(newPosForBlack) - 1, black);
                repaint();
            } else if (oldpos == Board.BLACK_BAR_LOC /*was -1*/) {
                myBoard.black_bar--;
                myBoard.setPoint(/*pointNum:*/newPosForBlack
                    , /*howMany:*/myBoard.getHowManyBlotsOnPoint(newPosForBlack) + 1, black);
                repaint();
            }
        }
    } // receiveMove( )


    /**
     * The network player has sent an instant message. Display it
     */
    public void receiveMessage(String message) {
        msg_display.append("Black player: " + message + '\n');
        // Scroll the text area to the bottom
        msg_display.scrollRectToVisible(new Rectangle(0, msg_display.getHeight(), 1, 1));
    } // receiveMessage( )


    /** 
     * Connection with an instance of Game successfully established
     * Start the game
     */
    public void connected() {
        FButton[btn_Connect].setEnabled(false);
        FButton[btn_SendMessage].setEnabled(true);

        // The client initiating the connection
        // decides who goes first
        if (status.clicker) {
            Random r = new Random();
            boolean goesfirst = r.nextBoolean();
            if (goesfirst) {
                status.observer = false;
                startTurn();
            } else {
                status.observer = true;
                comm.sendendturn();
            }
        } else {
            status.observer = true;
        }
        repaint();
    } // connected( )


    /** The network player has finished his turn.
     * Start the local player's turn
     * "local" is always white??
     */
    public void turnFinished() {
        status.observer = false;
        currentPlayer = white;

        myBoard.myDice.reset(); /* sets rolled = false; */
        startTurn();
    } // turnFinished( )


    /*=================================================
     * Overridden Methods 
     * (constructor wants boolean re networked? true/false)
     * ================================================*/



    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ROLL_DICE)) {
            doRoll();
        } else if (e.getActionCommand().equals(CANCEL)) {
            status.point_selected = false;
            FButton[btn_CancelMove].setEnabled(false);
            FButton[btn_BearOff].setEnabled(false);
            FButton[btn_AtPotentialMove1].setVisible(false);
            FButton[btn_AtPotentialMove2].setVisible(false);
            repaint();
        } else if (e.getActionCommand().equals(BEAR_OFF)) {
            myBoard.bearOff(currentPlayer);
        } else if (e.getActionCommand().equals(MOVE1)) {
            myBoard.doPartialMove(myBoard.getOldPoint(), /*toPoint:*/myBoard.getPotDest(1), /*whichDie:*/1, currentPlayer);
        } else if (e.getActionCommand().equals(MOVE2)) {
            myBoard.doPartialMove(myBoard.getOldPoint(), /*toPoint:*/myBoard.getPotDest(2), /*whichDie:*/2, currentPlayer);
        } else if (e.getActionCommand().equals(COMPUTER_MOVE)) {
            try {
                myAI.thinkAndPlay();
            } catch(Exception ex) {
                System.out.println("AI error: " + ex);
            }
        } else if (e.getActionCommand().equals(SEND_MSG)) {
            sendMessage();
        } else if (e.getActionCommand().equals(CONNECT)) {
            connect();
        } else if (e.getActionCommand().equals(NEW_GAME)) {
            if ( status.networked ) {
                int conf = JOptionPane.showConfirmDialog(this, "Send new game request?", "New Game", JOptionPane.YES_NO_OPTION);
                if ( conf == JOptionPane.YES_OPTION ) {
                    // FIXME: should check for network connection(?)
                    comm.sendResetReq();
                }
            } else {
                int conf = JOptionPane.showConfirmDialog(this, "Start a new game?", "New Game", JOptionPane.YES_NO_OPTION);
                if ( conf == JOptionPane.YES_OPTION ) {
                    resetGame();
                }
            } // if t/f networked
        } // if newgame
    } // actionPerformed( )


    public void paint(Graphics g) {
        // Cast the Graphics to a Graphics2D so actual drawing methods
        // are available
        Graphics2D screen = (Graphics2D) g;
        g_buffer.clearRect(0, 0, getWidth( ), getHeight( ));
        drawBoard( );
        drawBar( );
        drawBlots( );
        drawBearStats( );
        drawPipStats( );

        if (myBoard.myDice.getRolled()) { // 
            drawCurrentDice( );
        }

        if ( (status.networked) && (! comm.isConnected() ) ) {
            putString("Waiting for connection...", /*X:*/15, /*Y:*/50, Color.RED, /*fontsize:*/15);
        }
        
        // Blit the buffer onto the screen
        screen.drawImage(b_bimage, null, 0, 0);

        FButton[btn_CancelMove].repaint();
        FButton[btn_RollDice].repaint();
        FButton[btn_BearOff].repaint();
        FButton[btn_AtPotentialMove1].repaint();
        FButton[btn_AtPotentialMove2].repaint();
        FButton[btn_NewGame].repaint();
        FButton[btn_ComputerMove].repaint();

        if (status.networked) {
            FButton[btn_Connect].repaint();
            FButton[btn_SendMessage].repaint();
            msg_input.repaint();
            msg_scrollpane.repaint();
        }
    } // paint( )


    public static void main(String args[]) {
        JFrame f = new JFrame("Main Menu");
        f.setResizable(false);

        f.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder( /* TLRB */ 20, 20, 20, 20) );
        /* TLRB means "top, left, bot, right" */
        pane.setLayout(new GridLayout(/* rows (height) */ 0, /* cols (width) */ 1));
        JLabel l1 = new JLabel("JBackgammon v" + VERSION);
        JLabel l2 = new JLabel("started by Cody Planteen and George Vulov");
        /* JLabel l3 = new JLabel("http:// jbackgammon.sf.net"); Dead link*/
        pane.add(l1);
        pane.add(l2);
     /*   pane.add(l3); */

        JButton ButtonA = new JButton("1P vs. 2P (same computer)");
        ButtonA.addActionListener(new MainMenuListener(f, /* networked */ false));
        JButton ButtonB = new JButton("1P vs. 2P (network)");
        ButtonB.addActionListener(new MainMenuListener(f, /* networked */ true));
        pane.add(ButtonA);
        pane.add(ButtonB);
        f.getContentPane().add(pane);
        
        f.pack();
        f.setVisible(true); // was the deprecated "f.show();"
    } // main( )


    /*=================================================
     * Drawing Methods 
     * ================================================*/

     /** 
      * Gets the X coordinate of the specified point (aka "column" or "spike")
      */
    public int findX(int point) {
        if (point <= 6) { /* quadrant one is 1..6 (for white, right?) */
            return LEFT_MARGIN + 401 - (32*(point - 1));
        }
        if (point <= 12)  { /* quadrant two is 7..12 ? */
            return LEFT_MARGIN + 161 - (32*(point - 7));
        }
        if (point <= 18) { /* quadrant three is 13..18 ? */
            return LEFT_MARGIN + 1 + (32*(point - 13));
        }
        if (point <= 24) { /* quadrant four is 19..24 ? */
            return LEFT_MARGIN + 241 + (32*(point - 19));
        }
        return -1; // WTF??
    } // findX( )


    /** 
      * Gets the Y coordinate of the specified point (aka "column" or "spike")
      */
     public int findY(int point) {
        if (point <= 12) { /* points 1..12 are in top half of board */
            return TOP_MARGIN;
        }
        if (point <= 24) { /* points 13..24 are in lower half of board */
            return TOP_MARGIN + 361;
        }
        return -1; // wtf??
    } // findY( )



    public void drawPipStats() {
        String m1, m2;
        m1 = "White Pip count: " + myBoard.getPipCount( white );
        m2 = "Black Pip count: " + myBoard.getPipCount( black );

        g_buffer.setColor(Color.DARK_GRAY);
        g_buffer.fill(new Rectangle2D.Double(/*left*/455, /*top*/168, /*width*/160, /*height*/30));

        putString(m1, /*X:*/furtherleft, /*Y:*/180, Color.WHITE, /*fontsize:*/12);
        putString(m2, /*X:*/furtherleft, /*Y:*/195, Color.WHITE, /*fontsize:*/12);
    } // drawPipStats( )


       /* shouldn't be final if board is resizable */
        final static int left = GUI_Dim.BTN_LEFT_EDGE; /* 475 */
        final static int furtherleft = GUI_Dim.LEFT_EDGE; /* 455 */

    /**
      * Announce how many pieces each player beared off so far
      */
    public void drawBearStats() {
        String m1, m2;
        m1 = "White Pieces Beared Off: " + myBoard.white_bear;
        m2 = "Black Pieces Beared Off: " + myBoard.black_bear;
        

        g_buffer.setColor(Color.BLACK);
        g_buffer.fill(new Rectangle2D.Double(left, 130, 150, 30));

        putString(m1, /*X:*/furtherleft, /*Y:*/150, Color.WHITE, /*fontsize:*/12);
        putString(m2, /*X:*/furtherleft, /*Y:*/165, Color.WHITE, /*fontsize:*/12);
    } // drawBearStats( )


    private void putString(String message, int x, int y, Color c, int fontsize) {
        g_buffer.setFont(new Font("Arial", Font.BOLD, fontsize));
        g_buffer.setColor(c);
        g_buffer.drawString(message, x, y);
    } // putString( )


     /**
      * Driver, organizes data and color before calling general purpose "drawDice"
      */
    private void drawCurrentDice( ) {
        int dice1x = GUI_Dim.DICE1_LEFT;
        int dice2x = GUI_Dim.DICE2_LEFT;
        int diceTop = GUI_Dim.DICE_TOP;
        Color diceColor, dotColor;
        if (currentPlayer==black) {
            diceColor = myBoardPict.clr_black;
            dotColor = myBoardPict.clr_white;
        } else {
            diceColor = myBoardPict.clr_white;
            dotColor = myBoardPict.clr_black;
        }
        drawDice(myBoard.myDice.getDie(1), dice1x, diceTop,  diceColor, dotColor);
        drawDice(myBoard.myDice.getDie(2), dice2x, diceTop,   diceColor, dotColor);
    } /* drawCurrentDice( ) */


    /**
     * Called by "drawCurrentDice( )"
     */
    private void drawDice(int roll, int x, int y, Color dicecolor, Color dotcolor) {
         int diceSize = GUI_Dim.DICE_SIZE; /* 25 */
        int dotSize = GUI_Dim.DOT_SIZE; /* 4 */
        int leftX = GUI_Dim.DICE_MARGIN; /* 2 */
        int topY = leftX;   /* 2 */
        int midX = (diceSize / 2) - GUI_Dim.DICE_MARGIN; /* 11 */
        int midY = midX;
        int rightX = 2 + (2 * (midX - leftX)); /* trying to evenly space. was 19, 20 ugly, trying 22 */
        int lowY = rightX;
        
        g_buffer.setColor(dicecolor);
        g_buffer.fill(new Rectangle2D.Double(x, y, diceSize, diceSize ));
        g_buffer.setColor(dotcolor);

        switch(roll) {
        case 1:
            g_buffer.fill(new Rectangle2D.Double(x+midX, y+midY, dotSize, dotSize));
            break;
        case 2:
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+lowY, dotSize, dotSize));
            break;
        case 3:
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+midX, y+midY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+lowY, dotSize, dotSize));
            break;
        case 4:
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+lowY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+lowY, dotSize, dotSize));
            break;
        case 5:
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+lowY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+lowY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+midX, y+midY, dotSize, dotSize));
            break;
        case 6:
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+lowY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+topY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+lowY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+leftX, y+midY, dotSize, dotSize));
            g_buffer.fill(new Rectangle2D.Double(x+rightX, y+midY, dotSize, dotSize));
            break;
        }
    } // drawDice( )


    /**
    * drawTriangle: Draws a triangle with the point facing downward, 
    * x,y gives left corner coordinates and a number for color.
    * Hooks: status, g_buffer, old_point 
    */
    private void drawTriangle(int x, int y, int point_color) {
        if (point_color==1) {
            g_buffer.setColor(myBoardPict.color_point_white);
        } else {
            g_buffer.setColor(myBoardPict.color_point_black);
        }
        
        int [ ] myXs = new int[3]; /* re-written in attempt to fix bluej's "cannot parse" but didn't help? */
        myXs[0] = x; myXs[1] = x + myBoardPict.POINT_WIDTH/2; myXs[2] = x + myBoardPict.POINT_WIDTH;
        int [ ] myYs = new int[] { y, y + myBoardPict.POINT_HEIGHT, y};

        Polygon tri = new Polygon(myXs, myYs, 3);
        
        g_buffer.fillPolygon(tri);
        if (status.point_selected) {
            debug_data("TRI: Calling getPointNum",0);
            if (myBoard.getOldPoint( ) == getPointNum(x,y)) {
                g_buffer.setColor(Color.RED);
                debug_data("TRI: old_point = ",myBoard.getOldPoint( ) );
            }
        }
        g_buffer.drawPolygon(tri);
    } // drawTriangle( )


    /**
    * drawTriangleRev: Draws a triangle with the point facing upward,
    * x,y gives left corner coordinates and a number for color.
    * Hooks: status, g_buffer, old_point 
    */
    private void drawTriangleRev(int x, int y, int point_color) {
        if (point_color==neutral) {
            g_buffer.setColor(myBoardPict.color_point_white);
        } else {
            g_buffer.setColor(myBoardPict.color_point_black);
        }

        int [ ] myXs = new int[] { x, x + myBoardPict.POINT_WIDTH/2, x + myBoardPict.POINT_WIDTH};
        int [ ] myYs = new int[] { y, y - myBoardPict.POINT_HEIGHT, y};

        Polygon tri = new Polygon(myXs, myYs, 3);
        g_buffer.fillPolygon(tri);
        if (status.point_selected) {
            debug_data("DEBUG: drawTriangleRev: Calling getPointNum",0);
            if (myBoard.getOldPoint( ) == getPointNum(x,y)) {
                g_buffer.setColor(Color.RED);
                debug_data("drawTriangleRev: old_point = ", myBoard.getOldPoint( ) );
            }
        }
        g_buffer.drawPolygon(tri);
    } // drawTriangleRev


    /**
     * Draws the Game board onto the buffer
     */
    private void drawBoard() {
        // Set the green color
        g_buffer.setColor(new Color(0 , 150, 0));

        // Draw the two (left & right) halves of the board
        Rectangle2D.Double halfBoardA 
          = new Rectangle2D.Double(LEFT_MARGIN, TOP_MARGIN, BoardPict.QUADRANT_WIDTH + 2/*192*/, BoardPict.BOARD_HEIGHT/*360*/);
        Rectangle2D.Double halfBoardB 
          = new Rectangle2D.Double(LEFT_MARGIN+BoardPict.BOARD_MIDPOINT_HORIZONTAL_PIXELS/*238*/, TOP_MARGIN, BoardPict.QUADRANT_WIDTH + 2/*192*/, BoardPict.BOARD_HEIGHT/*360*/);

        g_buffer.draw(halfBoardA);
        g_buffer.fill(halfBoardA);
        g_buffer.draw(halfBoardB);
        g_buffer.fill(halfBoardB);

        // Draw the bar
        g_buffer.setColor(new Color(128,64,0)); /* brown? */
        Rectangle2D.Double bar = new Rectangle2D.Double(LEFT_MARGIN+BoardPict.QUADRANT_WIDTH + 2/*192*/, TOP_MARGIN, 
           BoardPict.BAR_WIDTH-4/*46*/, BoardPict.BOARD_HEIGHT/*360*/);
        g_buffer.draw(bar);
        g_buffer.fill(bar);

        g_buffer.setColor(Color.WHITE);
        int point_color = white;

        // Draw the points
        for (int i=0; i<=BoardPict.QUADRANT_WIDTH - 10/*180*/; i+=(BoardPict.POINT_WIDTH+2)/*32*/) {
            if (point_color == neutral) {
                point_color = white;
            } else {
                point_color = neutral;
            }

            drawTriangle(LEFT_MARGIN+i, TOP_MARGIN, point_color);
            drawTriangleRev(LEFT_MARGIN+i, TOP_MARGIN+BoardPict.BOARD_HEIGHT/*360*/, point_color);

            drawTriangle(LEFT_MARGIN+240+i, TOP_MARGIN, point_color);
            drawTriangleRev(LEFT_MARGIN+240+i, TOP_MARGIN+BoardPict.BOARD_HEIGHT/*360*/, point_color);
        }
        debug_data("FINISHED THE SPIKES ",0);
    } // drawBoard( )


    private void drawBar() {
        g_buffer.setColor(new Color(100, 50, 0)); /* dark-brown? */
        int left = LEFT_MARGIN + BoardPict.barRect.x + 2 /*192*/;
        int topBlack = TOP_MARGIN + BoardPict.BAR_BLACK_TOP; /* topmarg + 120 */
        int topWhite = TOP_MARGIN + BoardPict.BAR_WHITE_TOP; /* topmarg + 200 */
        g_buffer.drawRect(left,topBlack/*?*/,BoardPict.BAR_WIDTH - 4,BoardPict.BAR_ZONE_HEIGHT);
        g_buffer.fill(new Rectangle2D.Double(left, topBlack, BoardPict.BAR_WIDTH - 4, BoardPict.BAR_ZONE_HEIGHT));
        g_buffer.fill(new Rectangle2D.Double(left, topWhite, BoardPict.BAR_WIDTH - 4, BoardPict.BAR_ZONE_HEIGHT));

        g_buffer.setColor(Color.WHITE);
        g_buffer.fill(new Rectangle2D.Double(left, (topBlack+topWhite)/2, BoardPict.BAR_WIDTH - 3, BoardPict.BAR_ZONE_HEIGHT));
        left = LEFT_MARGIN + BoardPict.barRect.x + BoardPict.BAR_MARGIN_TO_BLOT; /* 201 */
        int blotSize = BoardPict.BLOT_WIDTH; /* 29 */

        if (myBoard.onBar(black)) {
            g_buffer.setColor(myBoardPict.clr_black);
            g_buffer.fill(new Ellipse2D.Double(left, topBlack + 5, blotSize, blotSize));
            if (myBoard.black_bar > 1) {
                putString(String.valueOf(myBoard.black_bar), /*X:*/left+21, /*Y:*/topBlack + 85, Color.RED, /*fontsize:*/15);
            }
        }
        
        if (myBoard.onBar(white)) {
            g_buffer.setColor(myBoardPict.clr_white);
            g_buffer.fill(new Ellipse2D.Double(left, topWhite + 5, blotSize, blotSize));
            if (myBoard.white_bar > 1) {
                putString(String.valueOf(myBoard.white_bar), /*X:*/left+21, /*Y:*/topWhite + 85, Color.RED, /*fontsize:*/15);
            }
        }

    } // drawBar( )


    private void drawBlots() {
        debug_msg("drawBlots()");
         int blotSize = BoardPict.BLOT_WIDTH; /* 29 */

        for (int point=1; point<=12; point++) {
           int howManyBlots = myBoard.getHowManyBlotsOnPoint(point);
            if ( (0<howManyBlots) && (howManyBlots<=5) ) {
                for (int i=0; i<howManyBlots; i++) {
                    if (myBoard.getColorOnPoint(point)==white) {
                        g_buffer.setColor(myBoardPict.clr_white);
                    } else {
                        g_buffer.setColor(myBoardPict.clr_black);
                    }
                    g_buffer.fill(new Ellipse2D.Double(findX(point), findY(point) + i*30, blotSize, blotSize));
                }
            }
            if (howManyBlots>5) {
                for (int i=0; i<5; i++) {
                    if (myBoard.getColorOnPoint(point)==white) {
                        g_buffer.setColor(myBoardPict.clr_white);
                    } else {
                        g_buffer.setColor(myBoardPict.clr_black);
                    }
                    g_buffer.fill(new Ellipse2D.Double(findX(point), findY(point) + i*30, blotSize, blotSize));
                }
                putString(String.valueOf(howManyBlots)
                   , /*X:*/findX(point)+10, /*Y:*/235, Color.RED, /*fontsize:*/15);
            }
        } // for point 1..12

        for (int point=13; point<=24; point++) {
            int howManyBlots = myBoard.getHowManyBlotsOnPoint(point);
            if ((0<howManyBlots) && (howManyBlots<=5)) {
                for (int i=0; i<howManyBlots; i++) {
                    if (myBoard.getColorOnPoint(point)==white) {
                        g_buffer.setColor(myBoardPict.clr_white);
                    } else {
                        g_buffer.setColor(myBoardPict.clr_black);
                    }
                    g_buffer.fill(new Ellipse2D.Double(findX(point), findY(point) - 30 - i*30, blotSize, blotSize));
                }
            }
            if (howManyBlots>5) {
                for (int i=0; i<5; i++) {
                    if (myBoard.getColorOnPoint(point)==white) {
                        g_buffer.setColor(myBoardPict.clr_white);
                    } else {
                        g_buffer.setColor(myBoardPict.clr_black);
                    }
                    g_buffer.fill(new Ellipse2D.Double(findX(point), findY(point) - 30 - i*30, blotSize, blotSize));
                }
                /* note: findX can return -1 if it doesn't know the point */
                putString(String.valueOf(howManyBlots)
                   , /*X:*/findX(point)+10, /*Y:*/255, Color.RED, /*fontsize:*/15);
            }
        } // for point 13..24
    } // drawBlots( )
    
    
    /**
     * This probably tells which point is touched by the x,y int coordinates
     */
    public int getPointNum(int point_x, int point_y) {
        boolean leftHalf=true;
        boolean topHalf=true;
        int i=1;

        debug_data("point_x = ",point_x);
        debug_data("point_y = ",point_y);
        // Find which portion of the board the click occurred in
        if (point_y >= BoardPict.BOARD_MIDPOINT_VERTICAL_PIXELS) {
            topHalf = false;
        }

        if (point_x >= BoardPict.BOARD_MIDPOINT_HORIZONTAL_PIXELS) {
            point_x -=BoardPict.BOARD_MIDPOINT_HORIZONTAL_PIXELS;
            debug_data("point_x changed to ", point_x);
            leftHalf = false;
        }
        /* debug_data("half = ", half);
        debug_data("quad = ", quad); */
        // Find how many times we can subtract 32 from the position
        // while remaining positive
        for ( i=1; point_x >= 32; point_x -= 32) {
            i++;
        }

        // Compensate for top/bottom and left/right
        if (topHalf) {
            if (leftHalf) {
                i = (6-i) + 7;
            } else {
                i = (6-i) + 1;
            }
        } else { /* bottom half */
            if (leftHalf)  {
                i += 12;
            } else {
                i += 18;
            }
        }
        // Useful debug statements
        debug_data("getPointNum returns ",i);
        return i;
    } // getPointNum( )


    public void debug_data( String msg, int data) {
        /*
            System.out.print("DEBUG: ");
            System.out.print(msg);
            System.out.println(data);
        */
    } // debug_data( )


    /**
     * Set up a new game
     */
    public void resetGame() {
        // System.out.println("GAME RESET WAS HIT");
        // Reset Game data /
        myBoard.myDice.reset( ); /* puts to unrolled, unused, countdown=0 */ 
        myBoard.setOldPoint( 0 );
        currentPlayer = white;
    
        // Reset buttons
        FButton[btn_CancelMove].setEnabled(false);
        FButton[btn_RollDice].setEnabled(true); /* was false, why? Was this the reason new game didn't work? */
        FButton[btn_BearOff].setEnabled(false);
        FButton[btn_NewGame].setEnabled(false);
        FButton[btn_AtPotentialMove1].setVisible(false);
        FButton[btn_AtPotentialMove2].setVisible(false);

        // Re-create the board
        myBoard = new Board(this);

        // Have the Status object reset game values, keep network value
        status.newGame();

        repaint();
    } // resetGame( )

} // class Game

