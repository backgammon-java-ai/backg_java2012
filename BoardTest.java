

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
    int aiColor = Board.black; /* playerColor */

    
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
    public void testCanMove(){
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

        g.setCurrentPlayer(aiColor);
        b.myDice.roll( );
        assertEquals(true, b.solitaryBlotOnPoint(12, aiColor));
        assertEquals(true, b.canLandOn(12, aiColor));
        assertEquals(true, b.canMove(aiColor));
    }
    
    
    @Test
    /**
     * testing ai points in mid board
     */
    public void testHandlePoint1()  {
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

        g.setCurrentPlayer(aiColor);
        b.myDice.setDie(1,1); /* alternative syntax:b1.myDice.roll(1,2) */
        b.myDice.setDie(2,6);
        assertEquals(true, b.solitaryBlotOnPoint(12, aiColor));
        assertEquals(true, b.canLandOnExact(12, aiColor));
        assertEquals(true, b.canLandOn(12, aiColor));
        assertEquals(true, b.canMove(aiColor));
        assertEquals(1, b.getHowManyBlotsOnPoint(12));
        b.handlePoint(12, aiColor);
        assertEquals(11, b.getPotDest(1));
        assertEquals(6, b.getPotDest(2));
        b.doPartialMove(12,11,/*whichDie:*/1,aiColor);
        assertEquals(true, b.solitaryBlotOnPoint(11, aiColor));
        b.handlePoint(11, aiColor);
        b.doPartialMove(20,14,/*whichDie:*/2,aiColor);
        assertEquals(true, b.solitaryBlotOnPoint(11, aiColor));
        assertEquals(true, b.solitaryBlotOnPoint(14, aiColor));
    }
    
    
    @Test
    /**
     * testing ai points in end game
     */
    public void testHandlePoint2()  {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.makeAlmostDoneGame( );
            /* black just has singles in quadrant 4, on points 1,4,6 (bearing off to 0) */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(aiColor);
        b.myDice.roll(5,6); /* alternative syntax:b1.myDice.setDie(1,5) setDie(2,6)*/
        assertEquals(true, b.solitaryBlotOnPoint(6, aiColor));
        assertEquals(true, b.canLandOnExact(6, aiColor));
        assertEquals(true, b.canLandOn(6, aiColor));
        assertEquals(true, b.canMove(aiColor));
        assertEquals(1, b.getHowManyBlotsOnPoint(6));
        b.handlePoint(6, aiColor);
        assertEquals(1, b.getPotDest(1));
        assertEquals(Board.BLACK_BEAR_OFF_LOC, b.getPotDest(2));
        b.doPartialMove(6,6,/*whichDie:*/2,aiColor);
        assertEquals(false, b.solitaryBlotOnPoint(6, aiColor));
        assertTrue(b.needsInexactRolls(aiColor));
        b.handlePoint(4, aiColor);
        b.doPartialMove(4,5,/*whichDie:*/1,aiColor);
        assertEquals(4, b.getHowManyBlotsOnPoint(4));
        assertEquals(14, b.black_bear);
    }

    
        @Test
    /**
     * testing ai points in end game, doesn't do as much as testHandlePoint2
     */
    public void testHandlePoint2a() {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.makeAlmostDoneGame( );
            /* black just has singles in quadrant 4, on points 1,4,6 (bearing off to 0) */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(aiColor);
        b.myDice.roll(5,6); /* alternative syntax:b1.myDice.setDie(1,5) setDie(2,6)*/
        assertEquals(true, b.solitaryBlotOnPoint(6, aiColor));
        assertEquals(true, b.canLandOnExact(6, aiColor));
        assertEquals(true, b.canLandOn(6, aiColor));
        assertEquals(true, b.canMove(aiColor));
        assertEquals(1, b.getHowManyBlotsOnPoint(6));
        b.handlePoint(6, aiColor);
        assertEquals(1, b.getPotDest(1));
        assertEquals(Board.BLACK_BEAR_OFF_LOC, b.getPotDest(2));
    }
    
    
    @Test
    public void testTakeOneBlotOffPoint( ) {
        g = new Game(false);
        b = g.getMyBoard();
        assertNotNull(b);
        try {
            b.makeAlmostDoneGame( );
            /* black just has singles in quadrant 4, on points 1,4,6 (bearing off to 0) */
            assertNotNull(b);
        } catch(Exception e) {
            /* isn't there a way to test without catching exceptions? */
            fail(e.toString( ));
        }

        g.setCurrentPlayer(aiColor);
        b.myDice.roll(5,6); /* alternative syntax:b1.myDice.setDie(1,5) setDie(2,6)*/
        assertEquals(true, b.solitaryBlotOnPoint(6, aiColor));
        b.takeOneBlotOffPoint(6);
        assertEquals(0, b.getHowManyBlotsOnPoint(6));
    }
    
    
    @Test
    public void testSuperMegaHappyScore1() {
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

        g.setCurrentPlayer(aiColor);
        PointBuildStrategy pb = new PointBuildStrategy( );
        
        assertEquals(5.25, pb.howImportantIsThisPoint(b,20, aiColor, g.myAI.getCautious( )), 
            /*how close?*/0.01);
        assertEquals(13006.75, pb.getAllPointScore(b,aiColor, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(4.25, pb.howImportantIsThisPoint(b,4, Board.white, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(14004.25, pb.getAllPointScore(b,Board.white, /*cautious*/0.5), 
            /*how close?*/0.01);
        assertEquals(-997.5, pb.superMegaHappyScore(b,/*cautious:*/g.myAI.getCautious( ), aiColor ), 
            /*how close?*/0.01);
        System.out.println(pb.superMegaHappyScore(b,/*cautious:*/g.myAI.getCautious( ), aiColor ));
        /* let's test a new score for another board layout...*/
    }
    
    
    @Test
    public void testLegitEndLoc( ) {
        assertTrue(Board.legitEndLoc(15,aiColor));
        assertFalse(Board.legitEndLoc(Board.ILLEGAL_MOVE,aiColor));
        assertFalse(Board.legitEndLoc(Board.howManyPoints + 3,aiColor));
        assertFalse(Board.legitEndLoc(-3,aiColor));
    }
} /* class BoardTest */

