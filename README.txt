------------------------------------------------------------------------
This is the JBackgammon README file.
------------------------------------------------------------------------

PROJECT TITLE: Backgammon AI and strategy

PURPOSE OF PROJECT: build backgammon a.i.

VERSION or DATE:
2012 Feb 1: compiles, computer makes its own partial moves (user has to press 
"Computer Move" button twice (or 4 times for doubles), dumb AI is still taking 
first possible move. If the first possible move is attempt to bear off (and other
blots aren't yet in final quadrant) the AI gets stuck.

2012 Jan 4: compiles, and AI is working but dumb: can't handle coming in from bar
and chooses first possible move. Human has to actually make the moves for the AI
(which is playing black).
started 2011 Oct 18

HOW TO START THIS PROJECT: 
  In BlueJ you can instantiate "JBackgammon(false)" (boolean says whether networked or not)
or use Safari or Firefox to browse JBackgammonApplet.html (Chrome doesn't work)
or in terminal say "java JBackgammon" in this directory. 
(handy compile option:   javac -Xlint:unchecked JBackgammon.java

AUTHORS: 
Original code from JBackgammon (http://jbackgammon.sf.net) [website nonexistent in 2012], 
Copyright (C) 2002
by George Vulov <georgevulov@hotmail.com> & Cody Planteen <frostgiant@msn.com>.
Revised by Josh G. & Julien S. & Mike Roam starting Sep 2011.


USER INSTRUCTIONS:
Roll the dice, then click a column that you want to move from. The possible legal moves
will get little buttons beneath them and you can press them to choose which your move.


Backgammon Rules:
You have to clear all your blots off the bar before you may do anything else, so 
is there a way to have both black and white blots on the bar? Yes, if coming back 
onto the board from the bar lands on the other color.
If you roll doubles you get 4 moves.


...How This Code works
White is trying to end on points 19-24 and Black is ending on points 1-6.
The "Board" class keeps track of what color is on a particular "point" ("column", "spike") and 
how many blots are on that point.
If you specify a number of number of blots on a point, they are black by default if you don't say.
Still not sure if point 25 is being used for anything. Program dies if it doesn't create 25 points. 
(There are separate white_bar, white_bear, black_bar, black_bear variables.)


...Bugs
[ ]"New Game" button doesn't enable the "Roll Dice" button so white can't make her first move.
[ ]White knocked black onto bar and the move didn't count, nor did its (how many?) successors, so white 
got subsequent infinite (or 4?) free moves!
[ ]White was able to use die1 twice, even though not doubles, so I suspect "usedDice" isn't working right.
[ ]Black can't come in from bar: blot disappears
[ ]White can't bear off past exact roll, infinite loop: "java.lang.StackOverflowError
...	at Board.canLandOn(Board.java:1768)
	at Board.needsInexactRolls(Board.java:1499)
	at Board.canLandOn(Board.java:1772)
	at Board.needsInexactRolls(Board.java:1499)
	at Board.canLandOn(Board.java:1772)
[ ]Computer is getting itself stuck trying to bear off (but can't) when other blots are lagging behind. 
Maybe white can't bear off either, probably bug in handlePoint calling canLandOn calling canLandOnExact
calling getHowManyBlotsOnBoard with pointNum '100' (WHITE_PAST_BEAR_OFF_LOC)
[ ]AI can't come in from bar ("java.lang.IllegalArgumentException: Can't start moving from point '25'
	at Board.doPartialMove(Board.java:1681)
	at Game.actionPerformed(Game.java:764)"
[ ]When AI moves itself, it is bearing off before it is legal! Wassup?
[ ]Rather than merely summing up the values of the blots that are on points, also sum up how
much risk there is of exposed blots (of either color) ACTUALLY getting hit.
[x]Why is "allLegalPartials" printing ALL of them twice? Is its arraylist double booked? 
They're in the same order, but could it be pt1moves5+pt1moves2, pt1moves2+pt1moves5 ??
Nevermind, another method was printing.


...High priority
[ ]See how long it takes to create and score and rank 1000 (non-default) Boards!
[ ]The value of a board should include pip count factored in some how! Oh, it does implicitly 
since there will be fewer protection points with a blot bumped out to the bar.
[ ]Get AI move-picker to know about coming in from bar.
[ ]Get AI to do a better job of choosing a move. This will involve finding combinations of partial 
moves, perhaps trying using one roll first and then the other roll first, especially if trying to
come in from the bar.
[x]Get AI to actually make its move. Problem is that "superMove" wants to know whether
to use the first or second dice, and our partialMoves haven't been remembering which dice
is which.

...Things to think about and decide
[ ]When computing the value of points on the board, how much more is a point with 3 blots on it 
compared to a point with 2 blots on it??
[ ]"Backgammon.java uses unchecked or unsafe operations. recompile with   -Xlint:unchecked for details.
[ ]Suppose there are 2 exposed white blots (b1 and b2) that are respectively s1 and s2 steps from
the start. (Distance to bearing off and escaping is 25-s.) The blots have individual probability 
(d1 and d2) of being hit by a black blot. How do we decide which to move? 
We could give them scores for comparing...the one that has gone farther has more to lose (higher s).
So maybe score = (s * d)?   or s + d? 
[ ]Does the random number generator have a seed and follow the same series of rolls every time??
[ ]Should AI be an interface?
[ ]Should 
[ ]Should AI.thinkAndPlay( ) return a "Move" and somebody else handles moving?? -gy suggestion 2011


...Future Plans:
[ ]More than 2 players, square board (Julien idea).
[ ]Bayard suggests physics so dice and blots can bounce and move and click.
[ ]GUI says whose turn, Xs out the dice that have been used, has undo, 
[ ]Computer should be able to play itself (to test moves and strategies, for example). To make
this happen we have to let the computer press the "next move" button for itself.
[ ]Need more "scoring functions" (danger, 
[ ]At start up ask whether network game or two humans or human vs ai.
[ ]Learn how a save bunch of moves so that we can compare them and choose the best.
[ ]Realize that dice rolls get totally used up except when you're bearing off.
[ ]Make the computer do (legal) moves.
[ ]Build "points" (by protecting them) (and [ ]keep track of the risk)
[ ]GUI offers choice of which starting board. For now Board is hard-wired.
[ ]Change "drawMen" to "drawBlots".
[ ]FButtons array should use static int's when talking about buttons instead of hardcoded numbers.
(For horrible example of hardcoded undocumented numbers see in JBackgammon's "HandlePoint( )" method.)
(Maybe the silly buttons should just have their own names and listeners like a grown up program!)
[ ]Add a button to call Board's "makeAlmostDoneGame" method for testing end game.
[ ]Add a third die for fun.
[ ]Make legal random moves. Note: the JBackgammon.java file has a "canLandOn( )" method that we should
perhaps study.
[ ]Show (on the gui) how many blots are on the board. There is already something showing how many
blots have beared off, and we could extend it.
Perhaps this could also show how many steps left before finishing.
[ ]Study the move legality checker.
[ ]Make games that begin almost completed so we can test bearing off. 
     Note: the "Board" constructor method could do this.
[ ]Add English comments to more of the code.
[ ]Perhaps for convenience be able to jump the value of combined dice, if the intermediate step is legal.


...History
2011 nov 8
[x]PipCount now counts blots on the bar (that have to come back in: count as 25).

2011 oct 25. 
Added pip count method to board, and displays the result on GUI.
Added "Computer Move" button to JBackgammon (see setupGUI) but it doesn't do anything yet.
Added "AI" class which might have the brains inside. (Has to know about board so it can strategize.)

2011 oct 18
Changed most all methods to speak of "points" (instead of columns or spikes) and "blots" (pieces).
Set up a method in JBackgammon to start the board with other than default setup.
[x]Added functions to check legit point numbers and colors. 
