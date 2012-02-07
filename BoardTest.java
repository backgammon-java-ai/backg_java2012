

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class BoardTest.
 *
 * @author  (Mike Roam)
 * @version (2012 Jan, Feb)
 */
public class BoardTest {

    Game g;
    Board b;
    int ai = Board.black; /* playerColor */

    
    /**
     * Default constructor for test class BoardTest
     */
    public BoardTest()
    {
    }

    
    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    
    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }

    
    @Test
    public void testCanMove()
    {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.make3BlotGame( ); /* black on 20 & 12 ends at 0, white on 4 ends past 24 */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(ai);
        b.myDice.roll( );
        assertEquals(true, b.solitaryBlotOnPoint(12, ai));
        assertEquals(true, b.canLandOn(12, ai));
        assertEquals(true, b.canMove(ai));
    }
    
    
    @Test
    /**
     * testing ai points in mid board
     */
    public void testHandlePoint1()  {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.make3BlotGame( );/* black on 20 & 12 ends at 0, white on 4 ends past 24 */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(ai);
        b.myDice.setDie(1,1); /* alternative syntax:b1.myDice.roll(1,2) */
        b.myDice.setDie(2,6);
        assertEquals(true, b.solitaryBlotOnPoint(12, ai));
        assertEquals(true, b.canLandOnExact(12, ai));
        assertEquals(true, b.canLandOn(12, ai));
        assertEquals(true, b.canMove(ai));
        assertEquals(1, b.getHowManyBlotsOnPoint(12));
        b.handlePoint(12, ai);
        assertEquals(11, b.getPotDest(1));
        assertEquals(6, b.getPotDest(2));
        b.doPartialMove(12,11,/*whichDie:*/1,ai);
        assertEquals(true, b.solitaryBlotOnPoint(11, ai));
        b.handlePoint(11, ai);
        b.doPartialMove(20,14,/*whichDie:*/2,ai);
        assertEquals(true, b.solitaryBlotOnPoint(11, ai));
        assertEquals(true, b.solitaryBlotOnPoint(14, ai));
    }
    
    
    @Test
    /**
     * testing ai points in end game
     */
    public void testHandlePoint2()  {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.makeAlmostDoneGame( );
            /* black just has singles in quadrant 4, on points 1,4,6 (bearing off to 0) */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(ai);
        b.myDice.roll(5,6); /* alternative syntax:b1.myDice.setDie(1,5) setDie(2,6)*/
        assertEquals(true, b.solitaryBlotOnPoint(6, ai));
        assertEquals(true, b.canLandOnExact(6, ai));
        assertEquals(true, b.canLandOn(6, ai));
        assertEquals(true, b.canMove(ai));
        assertEquals(1, b.getHowManyBlotsOnPoint(6));
        b.handlePoint(6, ai);
        assertEquals(1, b.getPotDest(1));
        assertEquals(Board.BLACK_BEAR_OFF_LOC, b.getPotDest(2));
        b.doPartialMove(6,6,/*whichDie:*/2,ai);
        assertEquals(false, b.solitaryBlotOnPoint(6, ai));
        assertTrue(b.needsInexactRolls(ai));
        b.handlePoint(4, ai);
        b.doPartialMove(4,5,/*whichDie:*/1,ai);
        assertEquals(4, b.getHowManyBlotsOnPoint(4));
        assertEquals(14, b.black_bear);
    }
    
    
    
    @Test
    public void testSuperMegaHappyScore1() {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.make3BlotGame( );/* black on 20 & 12 ends at 0, white on 4 ends past 24 */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(ai);
        assertEquals(5.25, b.howImportantIsThisPoint(20, ai, g.myAI.getCautious( )), 
            /*how close?*/0.01);
        assertEquals(13006.75, b.getAllPointScore(ai, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(4.25, b.howImportantIsThisPoint(4, Board.white, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(14004.25, b.getAllPointScore(Board.white, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(-997.5, b.superMegaHappyScore(/*cautious:*/g.myAI.getCautious( ), ai ), 
            /*how close?*/0.01);
        System.out.println(b.superMegaHappyScore(/*cautious:*/g.myAI.getCautious( ), ai ));
        /* let's test a new score for another board layout...*/
    }
    
    
    @Test
    public void testLegitEndLoc( ) {
        assertTrue(Board.legitEndLoc(15,ai));
        assertFalse(Board.legitEndLoc(Board.ILLEGAL_MOVE,ai));
        assertFalse(Board.legitEndLoc(Board.howManyPoints + 3,ai));
        assertFalse(Board.legitEndLoc(-3,ai));
    }
} /* class BoardTest */

