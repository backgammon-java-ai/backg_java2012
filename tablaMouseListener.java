

/***************************************************************
JBackgammon (http://jbackgammon.sf.net)
 
Copyright (C) 2002
George Vulov <georgevulov@hotmail.com>
Cody Planteen <frostgiant@msn.com>
 
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

/* File: tablaMouseListener.java
 *
 * Description: This file contains the mouse listener class and identifies
 * which spike the mouse click occured on. */

 

import java.awt.event.*;

public class tablaMouseListener extends MouseAdapter
{
    Game parent;

    public tablaMouseListener(Game newParent) {
        //Find where the main Game class is so we can use methods from it
        parent = newParent;
    } // tablaMouseListener( ) constructor


    public void mouseReleased(MouseEvent e) {
        //Adjust values as if the board was set in the top left corner at (0,0)
        int mx = e.getX() - Game.LEFT_MARGIN;
        int my = e.getY() - Game.TOP_MARGIN;
        
        //We only want to check clicks within the bounds of the playing part of the board
        //   (0 <= x <= 190) OR (240 <= x <= 430) AND
        //   (0 <= y <= 160) OR (200 <= y <= 360)
       
        boolean hitInPlayableArea = false;
        if (parent.myBoardPict.upperLeftRect.contains(mx,my)) { hitInPlayableArea = true; }
        if (parent.myBoardPict.upperRightRect.contains(mx,my)) { hitInPlayableArea = true; }
        if (parent.myBoardPict.lowerLeftRect.contains(mx,my)) { hitInPlayableArea = true; }
        if (parent.myBoardPict.lowerRightRect.contains(mx,my)) { hitInPlayableArea = true; }

        /*
        if( ( ((Game.BOARD_LEFT_EDGE <= mx) && (mx<=190)) || ((mx>=240) && (mx<=Game.BOARD_RIGHT_EDGE)) ) &&
                ( ((Game.BOARD_TOP_EDGE<=my) && (my<=160)) || ((my>=200) && (my<=Game.BOARD_BOTTOM_EDGE))  ) && */
        if (hitInPlayableArea && parent.myBoard.myDice.getRolled( ) && !parent.status.observer ) {
            parent.myBoard.handlePoint( parent.getPointNum(mx,my), parent.getCurrentPlayer( ) );
        }
    } // mouseReleased( )

} // class tablaMouseListener
