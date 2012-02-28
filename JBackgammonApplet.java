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


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JBackgammonApplet extends JApplet
{
    public static final long serialVersionUID = 1L; // for serializing, mike version 1

    public void init() {
        getContentPane().add(new JLabel("Applet!"));
        Game app = new Game(false);
    } // init( )

    /**
     * A main() for the application:
     * from Eckel, "Thinking in Java, 3rd Ed."
     */ 
    public static void main(String[] args) {
        JApplet applet = new JBackgammonApplet();
        JFrame frame = new JFrame("Backgammon");
        // To close the application:
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(applet);
        frame.setSize(60,50);
        applet.init();
        applet.start();
        frame.setVisible(true);
    }
} // JBackgammonApplet

