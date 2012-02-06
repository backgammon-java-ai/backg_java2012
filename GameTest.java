

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class GameTest.
 *
 * @author  Mike Roam
 * @version 2012 Jan
 */
public class GameTest
{
    Game g;
    Board b;
    int ai = Board.black; /* playerColor */
    

    /**
     * Default constructor for test class JBackgammonTest
     */
    public GameTest()
    {
    }

    
    /**
     * Sets up the test fixture.
     *
     * Called before EVERY test case method.
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
    public void testGame() {
        g = new Game(false);
        g.setCurrentPlayer(g.black);
        assertEquals(Board.black, g.getCurrentPlayer( ) );
    }

    
    @Test
    public void testAlmostDoneGame( ) {
        g = new Game();
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.makeAlmostDoneGame( );
            g.setCurrentPlayer(ai);
            b.myDice.roll(3,4);
            assertTrue(b.canBearOff(ai));
            assertFalse(b.onBar(ai));
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }
    }
    

    @Test
    public void test3BlotBoard() {
        g = new Game();
        b = g.getMyBoard();
        try {
            b.make3BlotGame( );/* black on 20 & 12 ends at 0, white on 4 ends past 24 */
            assertEquals(ai, b.getColorOnPoint(12));
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }
    }
    

   

    @Test
    public void testBoardAllMoveable() {
        g = new Game();
        b = g.getMyBoard();
        assertNotNull(b);
        b.myDice.roll( );
        LocList ll = b.allMoveableBlotLocs( Board.white );
        System.out.println(ll.myList);
    }

    

    @Test
    public void testGameBoardDoPartialMove2() {
        g = new Game(false);
        b = g.getMyBoard();
        b.myDice.roll();

        assertNotNull(b.allMoveableBlotLocs(Board.white));
        LocList ll1 = b.allMoveableBlotLocs(Board.white);
        assertNotNull(ll1);
        assertEquals(1, ll1.size());
        g.setCurrentPlayer(ai);
        LocList ll2 = b.allMoveableBlotLocs(Board.black);
        assertNotNull(ll2);
        assertEquals(2, ll2.size());
    }



    @Test
    public void testAI() {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        g.setCurrentPlayer(ai);
        assertEquals(ai,g.getCurrentPlayer( ));
    }
    

    @Test
    public void testBlackMoveDice3n6() {
        g = new Game(false);
        b = g.getMyBoard();
        try {
            b.make3BlotGame( );/* black on 20 & 12 ends at 0, white on 4 ends past 24 */
            assertNotNull(b);
            b.myDice.roll(3, 6);
            g.setCurrentPlayer(ai);
            LocList ll1 = b.allMoveableBlotLocs(ai);
            assertNotNull(ll1);
            assertEquals("[12, 20]", ll1.toString());
            java.util.ArrayList<PartialMove> allpm1 = b.allLegalPartialMoves(ai);
            assertNotNull(allpm1);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }
    }
}

 /* class GameTest */



