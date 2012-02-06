

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
public class BoardTest
{
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
        Game g2 = new Game(false);
        Board b1 = g2.getMyBoard();
        assertNotNull(b1);
        b1.myDice.roll();
        g2.setCurrentPlayer(Board.black);
        b1.myDice.roll( );
        assertEquals(true, b1.solitaryBlotOnPoint(12, Board.black));
        assertEquals(true, b1.canLandOn(12, Board.black));
        assertEquals(true, b1.canMove(Board.black));
    }
    
    
    @Test
    public void testHandlePoint()
    {
        Game g2 = new Game(false);
        Board b1 = g2.getMyBoard();
        assertNotNull(b1);
        b1.myDice.roll();
        g2.setCurrentPlayer(Board.black);
        b1.myDice.setDie(1,1); /* alternative syntax:b1.myDice.roll(1,2) */
        b1.myDice.setDie(2,2);
        assertEquals(true, b1.solitaryBlotOnPoint(12, Board.black));
        assertEquals(true, b1.canLandOnExact(12,Board.black));
        assertEquals(true, b1.canLandOn(12, Board.black));
        assertEquals(true, b1.canMove(Board.black));
        assertEquals(1, b1.getHowManyBlotsOnPoint(12));
        b1.handlePoint(12, Board.black);
        assertEquals(11, b1.getPotDest(1));
        b1.doPartialMove(12,11,1,Board.black);
        assertEquals(true, b1.solitaryBlotOnPoint(11, Board.black));
    }
    
} /* class BoardTest */

