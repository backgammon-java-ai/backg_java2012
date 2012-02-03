

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
    private Game g1;
    private Board b1;

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
    public void testGame()
    {
        Game g = new Game(false);
        g.setCurrentPlayer(g.black);
        assertEquals(Board.black, g.getCurrentPlayer( ) );
        
    }

    
    @Test
    public void test2pieceBoard()
    {
        Game g = new Game(false);
        Board myb = g.getMyBoard();
        assertEquals(Board.black, myb.getColorOnPoint(12));
    }
    

   

    @Test
    public void testBoardAllMoveable()
    {
        Game g1 = new Game(false);
        Board b1 = g1.getMyBoard();
        assertNotNull(b1);
        b1.myDice.roll( );
        LocList ll = b1.allMoveableBlotLocs( Board.white );
        System.out.println(ll.myList);
    }

    

    @Test
    public void testGameBoardDoPartialMove2()
    {
        g1 = new Game(false);
        b1 = g1.getMyBoard();
        b1.myDice.roll();

        assertNotNull(b1.allMoveableBlotLocs(Board.white));
        LocList ll1 = b1.allMoveableBlotLocs(Board.white);
        assertNotNull(ll1);
        assertEquals(1, ll1.size());
        g1.setCurrentPlayer(Board.black);
        LocList ll2 = b1.allMoveableBlotLocs(Board.black);
        assertNotNull(ll2);
        assertEquals(2, ll2.size());
    }



    @Test
    public void testAI()
    {
        Game g2 = new Game(false);
        Board b1 = g2.getMyBoard();
        assertNotNull(b1);
        g2.setCurrentPlayer(Board.black);
    }

    @Test
    public void testBlackMoveDice3n6()
    {
        Game g1 = new Game(false);
        g1.doRoll();
        Board b1 = g1.getMyBoard();
        assertNotNull(b1);
        b1.setDice(3, 6);
        g1.setCurrentPlayer(Board.black);
        LocList ll1 = b1.allMoveableBlotLocs(Board.black);
        assertNotNull(ll1);
        assertEquals("[12, 20]", ll1.toString());
        java.util.ArrayList<PartialMove> allpm1 = b1.allLegalPartialMoves(Board.black);
        assertNotNull(allpm1);
    }
}

 /* class GameTest */



