

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class AIPartialMoveTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class AIPartialMoveTest {
    private Game g1;
    private Board b1;
    private LocList ll1;
    private AI aI1;
    private java.util.ArrayList<PartialMove> pm;
    private PartialMove pmove;
    
    
    

    /**
     * Default constructor for test class AIPartialMoveTest
     */
    public AIPartialMoveTest()
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
        g1 = new Game(false);
        b1 = g1.getMyBoard();
        g1.setCurrentPlayer(Board.black);
        b1.myDice.roll();
        ll1 = b1.allMoveableBlotLocs(Board.black);
        assertNotNull(ll1);
        aI1 = new AI(g1);
        //try {
            pm = b1.allLegalPartialMoves(Board.black);
            assertNotNull(pm);
            PartialMove pmove = aI1.bestPartialMoveOf(pm);
            assertNotNull(pmove);
            g1.getMyBoard( ).doPartialMove(pmove);
        //} catch(Exception e) {
        //    System.err.println(e);
        //}
    } /* setUp( ) */

    
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
    public void testDoPartial()
    {
        java.lang.Integer Int1 = ll1.get(0);
        assertNotNull(Int1);
        assertEquals(12, Int1.intValue());
    }


    @Test
    public void AIPartial2()
    {
        //try {
            java.util.ArrayList<PartialMove> pm2 = b1.allLegalPartialMoves(Board.black);
            assertNotNull(pm2);
            PartialMove pmove2 = aI1.bestPartialMoveOf(pm2);
            assertNotNull(pmove2);
            g1.getMyBoard( ).doPartialMove(pmove2);
        //} catch(Exception e) {
        //    System.err.println(e);
        //}
    }
    
    
}
 /* class AIPartialMoveTest */
