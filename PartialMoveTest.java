

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class PartialMoveTest.
 *
 * @author  (Mike Roam)
 * @version (2012)
 */
public class PartialMoveTest
{
    /**
     * Default constructor for test class PartialMoveTest
     */
    public PartialMoveTest()
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
    public void testEquals() /* hey, I had to put "test" on manually and see (at)Test, wassup */
    {
        Game g1 = new Game( false /* networked true/false */ );
        // PartialMove(int newStart, int newRoll, int newEnd, Game newGame, int newColor, int newWhichDie)
        PartialMove partialM1 = new PartialMove(3, 2, 5, g1, g1.getCurrentPlayer( ), 1);
        PartialMove partialM2 = new PartialMove(3, 2, 5, g1, g1.getCurrentPlayer( ), 1);
        System.out.println("partialM1==" + partialM1.toString( ) );
        assertEquals(true, partialM1.equals(partialM2));
    }
} /* class PartialMoveTest */

