

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;  // provides Collection
/**
 * The test class MoveTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class MoveTest
{
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
    public void testMove()
    {
        /* old test, not working anymore, maybe it needs specific board
           probably has issue with the game needing a first roll of dice
           to choose a currentPlayer */
        Game g1 = new Game(false);
        PartialMove pm1 = new PartialMove(3, 2, 5, g1, g1.getCurrentPlayer( ), 1);
        PartialMove pm2 = new PartialMove(5, 3, 8, g1, g1.getCurrentPlayer( ), 2);
        ArrayList<PartialMove> pmlist = new ArrayList<PartialMove>( );
        pmlist.add(pm1);
        pmlist.add(pm2);
        try {
            Move move1 = new Move(pmlist, g1.getCurrentPlayer( ), g1.myBoard);
            System.out.println("move1==" + move1.toString( ) );
            ArrayList<PartialMove> myPartials = move1.getMyPartials( );
            assertEquals(2, myPartials.size( ));
        } catch (Exception e) {
            System.err.println("uh-oh: " + e);
        }
    } /* testMove( ) */
    
} /* Class MoveTest*/

