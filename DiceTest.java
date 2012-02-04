

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class DiceTest.
 *
 * @author  (Mike Roam)
 * @version (2012 Feb)
 */
public class DiceTest
{
    /**
     * Default constructor for test class DiceRollTest
     */
    public DiceTest()
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
    public void testConstructors()
    {
        // Default constructor:
        
        Dice d5 = new Dice( );
        assertEquals(Dice.UNROLLED, d5.getDie(1));
        assertEquals(false, d5.getRolled( ));
        assertEquals(Dice.UNROLLED, d5.getDie(1));
        assertNotNull("uh-oh, random generator is null",d5.getRDice( ));
        assertEquals(0, d5.getDoubletMovesCountdown( ));
        assertEquals(false, d5.getUsedDie(1));
        
        // Copy constructor
        Dice d6 = new Dice(d5);
        assertNotSame(d6,d5);
        assertEquals(Dice.UNROLLED, d6.getDie(1));
        assertEquals(false, d6.getRolled( ));
        assertEquals(Dice.UNROLLED, d6.getDie(1));
        assertNotNull("uh-oh, random generator is null",d6.getRDice( ));
        assertEquals(0, d6.getDoubletMovesCountdown( ));
        assertEquals(false, d6.getUsedDie(1));
        
        
        // constructor with all values
        Dice d1 = new Dice(1, 2);
        assertEquals(1, d1.getDie1());
        assertEquals(2, d1.getDie2());
        /* alternative getter */
        assertEquals(1, d1.getDie(1));
        assertEquals(2, d1.getDie(2));

        
        Dice d2 = new Dice(Dice.UNROLLED, 0);
        assertEquals(0, d2.getDie1());
        assertEquals(0, d2.getDie2());
        
        Dice d3 = null;
        try {
            d3 = new Dice(-1,1); /* out of bounds, should result in null! */
        } catch (Exception e) {
            System.out.println(e);
            d3 = null;
        }
        assertNull(d3);
        
        try {
            d3 = new Dice(2,7); /* out of bounds, should result in null! */
        } catch (Exception e) {
            System.out.println(e);
            d3 = null;
        }
        assertNull(d3);
        
        Dice d4 = new Dice(3,4); 
        assertEquals(3, d4.getDie(1));
        try {
            d4 = new Dice(Dice.UNROLLED,5); /* should throw exception for one rolled, one not */
        } catch (Exception e) {
            System.out.println(e);
            d4 = null;
        }
        assertNull(d4);
    } /* test the constructors */
    

    @Test
    public void DR() {
        Dice dr4 = new Dice(4, 5);
        assertEquals(4, dr4.getDie1());
        assertEquals("I set die2 to 5 in constructor",5, dr4.getDie(2 ) );
    }

    @Test
    public void testSetDie() {
        Dice d1 = new Dice();
        d1.setDie(1, 2);
        assertEquals("die1 was UNROLLED and then explicitly set to 2",2, d1.getDie(1));
        assertFalse(d1.getRolled( ));
        d1.setDie(2, 3);
        assertEquals("die2 was UNROLLED and then explicitly set to 3",3, d1.getDie(2));
        assertTrue(d1.getRolled( )); /* now that both dice have values, the dice should think it is rolled */
        Dice d2 = new Dice(d1);
        assertEquals("die2 copied from another dice in which it was explicitly set to 3",3, d2.getDie(2));
        Dice d3 = new Dice(3, 2);
        assertEquals("die2 constructed with 2",2, d3.getDie(2));
        try {
            d3.setDie(2,7); /* should get exception for overflow! */
            d3.setDie(2,-1); /* underflow */
            d3.setDie(3,3); /* no such dice! */
        } catch (Exception e) {
            System.out.println(e);
            d3 = null;
        }
        assertNull(d3);
   }
}

 /* class DiceTest */

