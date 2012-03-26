

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;  // provides Collection
/**
 * The test class MoveTest.
 *
 * @author  Mike Roam
 * @version 2012 Mar 25
 */
public class MoveTest {


    Game g;
    Board b;
    int aiColor = Board.black; /* playerColor */

    /**
     * Default constructor for test class MoveTest
     */
    public MoveTest()
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
    public void testMove() {
        /* old test, not working anymore, maybe it needs specific board
           probably has issue with the game needing a first roll of dice
           to choose a currentPlayer */
        g = new Game(false);
        b = g.getMyBoard( );
        assertNotNull(b);
        try {
            b.makeStartingBoard( );/* regular game */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(aiColor);
        b.myDice.roll(1,1); /* alternative syntax:b1.myDice.roll(1,2) */
        assertEquals(4, b.myDice.getDoubletMovesCountdown( ) );

        StartGameStrategy sg = new StartGameStrategy( );
        Move best = sg.pickBestMove(b,aiColor);
        System.out.println(best);
        assertTrue( best.isPossible( ) );
        ArrayList<PartialMove> partials = best.getMyPartials();
        assertEquals(4, partials.size( ) );

        best.doMove( );
        assertEquals(0, b.myDice.getDoubletMovesCountdown( ) );
    } /* testMove( ) */
    
} /* Class MoveTest*/