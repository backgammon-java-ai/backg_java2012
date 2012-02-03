

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
    public void testDice1()
    {
        Dice dr = new Dice(1, 2, /*rolled:*/false);
        assertEquals(1, dr.getDice1());
        assertEquals(2, dr.getDice2());
        
        Dice dr2 = new Dice(0, 0, /*rolled:*/false);
        assertEquals(0, dr2.getDice1());
        assertEquals(0, dr2.getDice2());
        
        Dice dr3;
        try {
            dr3 = new Dice(-1,1, /*rolled:*/false); /* out of bounds, should result in null! */
        } catch (Exception e) {
            System.out.println(e);
            dr3 = null;
        }
        assertEquals(null, dr3);
        
        try {
            dr3 = new Dice(2,7, /*rolled:*/false); /* out of bounds, should result in null! */
        } catch (Exception e) {
            System.out.println(e);
            dr3 = null;
        }
        assertEquals(null, dr3);

    } /* test the constructor */
    

    @Test
    public void DR()
    {
        Dice dr4 = new Dice(4, 5, /*rolled:*/true);
        assertEquals(4, dr4.getDice1());
    }

    @Test
    public void testDice()
    {
        Dice d1 = new Dice();
        Dice d2 = new Dice(d1);
        Dice d3 = new Dice(3, 2, true);
        d1.setDie(1, 2);
    }
}

 /* class DiceTest */

