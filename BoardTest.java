

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
    public void testHandlePoint()
    {
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
    public void testSuperMegaHappyScore()
    {
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
        assertEquals(4.25, b.howImportantIsThisPoint(4, Board.white, /*cautious*/0.5), /*how close?*/0.01);
        assertEquals(4.25, b.getAllPointScore(Board.white, /*cautious*/0.5), /*how close?*/0.01);
        assertEquals(5.25, b.howImportantIsThisPoint(20, ai, g.myAI.getCautious( )), /*how close?*/0.01);
        assertEquals(6.75, b.getAllPointScore(ai, /*cautious*/0.5), /*how close?*/0.01);
        assertEquals(2.5, b.superMegaHappyScore(/*cautious:*/g.myAI.getCautious( ), ai ), /*how close?*/0.01);
        System.out.println(b.superMegaHappyScore(/*cautious:*/g.myAI.getCautious( ), ai ));
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
        /* let's test a new score now that we're on positions 11 & 14...*/
    }
} /* class BoardTest */

