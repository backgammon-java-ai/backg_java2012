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

/** 
 * Public Class - Status
 * Status is for keeping track of various variables throughout the program and
 * “keeping the state of the game nice and clean.” 
 */
public class Status
{
    //If the player rolled doublets
    // public boolean doublets; // now held in Dice
    //If the game is in network mode (playing over the net)
    public boolean networked;
    //If the player is observing(only in networked mode)
    public boolean observer;
    public boolean clicker;
    //Whether the current player has selected a spike and the
    //possible move positions are showing.
    public boolean point_selected;

    
    public Status() {
        // doublets = false;
        networked = false;
        observer = false;
        clicker = false;
        point_selected = false;
    } // Status constructor
    
    
    public void newGame() {
        //doublets = false;
        observer = false;
        clicker = false;
        point_selected = false;
    } // newGame
        
} // class Status
