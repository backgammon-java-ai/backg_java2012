------------------------------------------------------------------------
This is the JBackgammon README file.
------------------------------------------------------------------------

PROJECT TITLE: Backgammon with AI

PURPOSE OF PROJECT: build backgammon a.i.

VERSION or DATE:
2012 Feb 9 [x]Shows whose turn it is, shows board score. AI still dies
when a blot is near the end but can't bear off yet.

HOW TO START THIS PROJECT: 
  If using Java IDE "BlueJ" (from https://bluej.org) 
you can instantiate "JBackgammon(false)" (boolean says whether networked or not)
or use Safari or Firefox to browse JBackgammonApplet.html (Chrome doesn't work)
or in terminal say "java JBackgammon" in this directory. 
Handy compile option:   javac -Xlint:unchecked JBackgammon.java
There is a jar that works on mac if double-clicked.
(The html page gets security exceptions re bluej core .jars.)


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
[ ] StartGameStrategy's Chatty test isn't picking the good moves?? Or at least isn't using them?? 
[ ] Should AI not only add up happiness about his own blots but unhappiness about where the opponent's
blots are (which is NOT the same thing as opponent's happiness about where his blots are, which
might also be included, and even include opponent's unhappiness of where our blots are?)
[ ] AI doesn't know to come in from bar: maybe "allLegalPartialMoves" is only looking at points
instead of looking at bar. {Workaround: can bring AI blots in manually!}
Bugs below here might be gone, now!
[ ] testHandlePoint2a() shows that AI doesn't know to bear off, AND chooses a move that can't
be used because AI is not taking blot on point 6 off first with that 6 bearoff, and is instead
trying to do a move (taking blot on point 1 way off) that is illegal because can't use inexact rolls yet.
Run testHandlePoint2a() and press computer move and see the terminal!
AI thinkAndPlay had exception: java.lang.IllegalArgumentException: Bad pointNum '-100'
[ ] Black is in final quadrant, should be able to bear off, but even exact moves (on point 1, roll=1)
don't make the "Bear Off" button turn on. What should be turning it on?
[ ] White is on bar, the rolled dice could let her onto the board where the potential moves are
showing, but clicking the potMove buttons doesn't cause anything to happen: white stays on the bar! 
[ ] Black is on points 20 & 3, so 3 is close to bear off but 20 is far away. I rolled 2,4 and clicking 
on blot on 3 gets point 1 to properly light up and point20 gets red triangle highlight, wassup!!??
Clicking on the blot on point 20 gets 18 and 16 to properly light up as well as red triangle on point 20.
Whoa: clicking on blot on 3 and moving to 1 works but also causes the blot on _20_ to jump to point 3!
Why is the wrong blot moving as well as the correct one?
[ ] Bear Off button doesn't light up when black gets all her pieces into quadrant 4! (White's bear btn lights)
I wonder if point '5' counts as quad 4 for black ai??
also, clicking a black blot that would perfectly go to bear off (not past bear off) gets:
bad pointNum '-100'
	at Board.canLandOn(Board.java:1796)
	at Board.handlePoint(Board.java:1479)
[ ] ComputerMove button should be disabled during white's turn!
[ ] Manually moving a black piece (in final quadrant, if that matters) gets
java.lang.IllegalArgumentException: bad pointNum '-100'
	at Board.canLandOn(Board.java:1796)
	at Board.canMove(Board.java:1876)
	at Board.doPartialMove(Board.java:1776)
[ ] "New Game" button doesn't enable the "Roll Dice" button so white can't make her first move.
IllegalArgumentException: Can't start moving from point '0'
	at Board.setOldPoint(Board.java:474)
	at Game.resetGame(Game.java:1303)
[ ] There might be big trouble in Game.doMove( ) which I've been messing with. Does anybody call it?
Does anybody ever endTurn( )?
[ ] Black can't come in from bar: AI error: java.lang.IllegalArgumentException: bad pointNum '-100'
[ ] Black can't come in from bar: blot disappears
[ ] White can't bear off past exact roll, infinite loop: "java.lang.StackOverflowError
...	at Board.canLandOn(Board.java:1768)
	at Board.needsInexactRolls(Board.java:1499)
	at Board.canLandOn(Board.java:1772)
	at Board.needsInexactRolls(Board.java:1499)
	at Board.canLandOn(Board.java:1772)
[ ] Computer is getting itself stuck trying to bear off (but can't) when other blots are lagging behind. 
Maybe white can't bear off either, probably bug in handlePoint calling canLandOn calling canLandOnExact
calling getHowManyBlotsOnBoard with pointNum '100' (WHITE_PAST_BEAR_OFF_LOC)
AI error: java.lang.IllegalArgumentException: bad pointNum '-100'
$
java.lang.IllegalArgumentException: bad pointNum '-100'
	at Board.canLandOn(Board.java:1760)
	at Board.canMove(Board.java:1840)
	at Board.doPartialMove(Board.java:1740)
[ ] AI can't come in from bar ("java.lang.IllegalArgumentException: Can't start moving from point '25'
	at Board.doPartialMove(Board.java:1681)
	at Game.actionPerformed(Game.java:764)"
[ ] When AI moves itself, it is bearing off before it is legal! Wassup? Wait, now it tries to bear off 
before lower blots have caught up, and with 2 dice rolls, it took the higher:
in board.allLegalPartialMoves( ), the moveableBlotLocs==[3, 20]
for starting at 3 with roll:2 estimated move to:1
AI error: java.lang.IllegalArgumentException: [bad PartialMove start:3 roll:6 end:-100 color:2 whichDie:2]

[ ] Rather than merely summing up the values of the blots that are on points, also sum up how
much risk there is of exposed blots (of either color) ACTUALLY getting hit.
[x] Why is "allLegalPartials" printing ALL of them twice? Is its arraylist double booked? 
They're in the same order, but could it be pt1moves5+pt1moves2, pt1moves2+pt1moves5 ??
Nevermind, another method was printing.
[x] White was able to use die1 twice, even though not doubles, so I suspect "usedDice" isn't working right. 
[x] White knocked black onto bar and the move didn't count, nor did its (how many?) successors, so white 
got subsequent infinite (or 4?) free moves!
Fixed: obob in setUsed( ).


...High priority
[ ] Our startGameStrategy doesn't check that it is actually choosing possible moves: to fix this
note that legitStartLoc doesn't check whether we have a blot on the point in question, so any
attempt to say "if such&such move is possible" has to check LegitStartLoc AND check that we have
a blot on the point in question.
[ ] Fix ai's switchStrategy , which now only chooses PointBuildStrategy.
[ ] See how long it takes to create and score and rank 100 (non-default) Boards!
[ ] The value of a board should include pip count factored in some how! Oh, it does implicitly 
since there will be fewer protection points with a blot bumped out to the bar.
[ ] Get AI move-picker to know about coming in from bar.
[ ] Get AI to do a better job of choosing a move. This will involve finding combinations of partial 
moves, perhaps trying using one roll first and then the other roll first, especially if trying to
come in from the bar.
[x] Got AI to actually make its move. Problem was that "doPartialMove( )" wanted to know whether
to use the first or second dice, so our partialMoves are now remembering which dice
is which.


...Things to think about and decide
[ ] When computing the value of points on the board, how much more is a point with 3 blots on it 
compared to a point with 2 blots on it??
[ ] Backgammon.java uses unchecked or unsafe operations. recompile with   -Xlint:unchecked for details.
[ ] Suppose there are 2 exposed white blots (b1 and b2) that are respectively s1 and s2 steps from
the start. (Distance to bearing off and escaping is 25-s.) The blots have individual probability 
(d1 and d2) of being hit by a black blot. How do we decide which to move? 
We could give them scores for comparing...the one that has gone farther has more to lose (higher s).
So maybe score = (s * d)?   or s + d? 
[ ] Does the random number generator have a seed and follow the same series of rolls every time??
[ ] Should AI be an interface?
[ ] Should AI.thinkAndPlay( ) return a "Move" and somebody else handles moving?? -gy suggestion 2011


...Future Plans:
[ ] For iPad interface: drag pieces with zoom-in for aiming at the target
[ ] Mark the dice that have been used.
[ ] Do formal start procedure: Each player rolls one dice (one white, one black): high number goes
first using those two rolls. (For tie, roll again.)
[ ] Have some buttons on the screen to choose whether AI is white or black.
[ ] supermegahappyscore cares that blots are on the bar, should care even more depending on where
the blot got sent back from. (Did he lose a valuable point?)
[ ] More than 2 players, square board (Julien idea).
[ ] Bayard suggests physics so dice and blots can bounce and move and click.
[ ] GUI says whose turn, Xs out the dice that have been used, has undo, 
[ ] Computer should be able to play itself (to test moves and strategies, for example). To make
this happen we have to let the computer press the "next move" button for itself.
[ ] Need more "scoring functions" (danger, 
[ ] At start up ask whether network game or two humans or human vs ai.
[ ] Learn how a save bunch of moves so that we can compare them and choose the best.
[ ] Realize that dice rolls get totally used up except when you're bearing off.
[ ] Make the computer do (legal) moves.
[ ] Build "points" (by protecting them) (and [ ]keep track of the risk)
[ ] GUI offers choice of which starting board. For now Board is hard-wired.
[ ] Change "drawMen" to "drawBlots".
[ ] FButtons array should use static int's when talking about buttons instead of hardcoded numbers.
(For horrible example of hardcoded undocumented numbers see in JBackgammon's "HandlePoint( )" method.)
(Maybe the silly buttons should just have their own names and listeners like a grown up program!)
[ ] Add a button to call Board's "makeAlmostDoneGame" method for testing end game.
[ ] Add a third die for fun.
[ ] Make legal random moves. Note: the JBackgammon.java file has a "canLandOn( )" method that we should
perhaps study.
[ ] Show (on the gui) how many blots are on the board. There is already something showing how many
blots have beared off, and we could extend it.
Perhaps this could also show how many steps left before finishing.
[ ] Study the move legality checker.
[ ] Make games that begin almost completed so we can test bearing off. 
     Note: the "Board" constructor method could do this.
[ ] Add English comments to more of the code.
[ ] Perhaps for convenience be able to jump the value of combined dice, if the intermediate step is legal.


...History
2012 Mar 25: doubletMoveCountdown wasn't getting adjusted when dice were
being given explicit values, so countdown was 2 for doubles. Is fixed!

2012 Feb 9: Board UI shows whose move it is! Human can't come in from bar,
and AI has to be brought in from bar manually.

2012 Feb 1: compiles, computer makes its own partial moves (user has to press 
"Computer Move" button twice (or 4 times for doubles), dumb AI is still taking 
first possible move. If the first possible move is attempt to bear off (and other
blots aren't yet in final quadrant) the AI gets stuck.

2012 Jan 4: compiles, and AI is working but dumb: can't handle coming in from bar
and chooses first possible move. Human has to actually make the moves for the AI
(which is playing black).
started 2011 Oct 18

2011 nov 8
[x] PipCount now counts blots on the bar (that have to come back in: count as 25).

2011 oct 25. 
Added pip count method to board, and displays the result on GUI.
Added "Computer Move" button to JBackgammon (see setupGUI) but it doesn't do anything yet.
Added "AI" class which might have the brains inside. (Has to know about board so it can strategize.)

2011 oct 18
Changed most all methods to speak of "points" (instead of columns or spikes) and "blots" (pieces).
Set up a method in JBackgammon to start the board with other than default setup.
[x] Added functions to check legit point numbers and colors.



Notes:
as of Feb2012: AI calls thinkAndPlay which asks board for allLegalPartialMoves and does the "best."
