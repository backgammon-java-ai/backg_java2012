import java.awt.*;

/**
 * The playing board has 4 quadrants (upper/lower, left/right)
 * with the bar standing vertically between them.
 * 
 * @author Mike Roam
 * @version 2012 Jan 30
 */
public class BoardPict {
        // Color to be used when drawing a white blot
    static final Color clr_white = new Color(200, 200, 200);
    // Color to be used when drawing a black blot
    static final Color clr_black = new Color(50, 50, 50);

    // Color to be used when drawing a white point
    static final Color color_point_black = new Color(130, 70, 0);
    // Color to be used when drawing a black point
    static final Color color_point_white = new Color(240, 215, 100);


    final static int BOARD_WIDTH = 430; /* should be resizeable someday */
    final static int BOARD_HEIGHT = 360;
    
    final static int BOARD_LEFT_EDGE = 0;
    final static int BOARD_RIGHT_EDGE = BOARD_LEFT_EDGE + BOARD_WIDTH;
    final static int BOARD_TOP_EDGE = 0;
    final static int BOARD_BOTTOM_EDGE = BOARD_TOP_EDGE + BOARD_HEIGHT;
    
    final static int BOARD_MIDPOINT_VERTICAL_PIXELS = 200;
    final static int BOARD_MIDPOINT_HORIZONTAL_PIXELS = 238; /*Is the bar before or after this??*/

    final static int QUADRANT_WIDTH = 190;
    final static int QUADRANT_HEIGHT = 160;
    
    final static int BAR_ZONE_HEIGHT = 40; /* the bar cubes in middle of tall bar */
    final static int BAR_WIDTH = BOARD_WIDTH - (2 * QUADRANT_WIDTH); /* 50? */
       // "gutter" is the height of the horizontal zone between the upper & lower quadrants */
    final static int GUTTER_HEIGHT = BOARD_HEIGHT - (2 * QUADRANT_HEIGHT);
    final static int BAR_MARGIN_TO_BLOT = 11; /* seems small? */
    final static int BAR_BLACK_TOP = 120;
    final static int BAR_WHITE_TOP = 200;
    
    final static int BLOT_WIDTH = 29;
 
    final static int POINT_WIDTH = 30;
    final static int POINT_HEIGHT = QUADRANT_HEIGHT;
    
    Rectangle upperLeftRect;
    Rectangle upperRightRect;
    Rectangle lowerLeftRect;
    Rectangle lowerRightRect;
    final static Rectangle barRect= new Rectangle(BOARD_LEFT_EDGE + QUADRANT_WIDTH, BOARD_TOP_EDGE, 
             BAR_WIDTH, BOARD_BOTTOM_EDGE); 

    /**
     * Constructor for objects of class BoardPict
     */
    public BoardPict() {
        upperLeftRect = new Rectangle(BOARD_LEFT_EDGE, BOARD_TOP_EDGE, QUADRANT_WIDTH, QUADRANT_HEIGHT);
        upperRightRect = new Rectangle(BOARD_LEFT_EDGE + QUADRANT_WIDTH + BAR_WIDTH, BOARD_TOP_EDGE, 
            QUADRANT_WIDTH, QUADRANT_HEIGHT);
        
        lowerLeftRect = new Rectangle(BOARD_LEFT_EDGE, BOARD_TOP_EDGE + QUADRANT_HEIGHT + GUTTER_HEIGHT,
                QUADRANT_WIDTH, QUADRANT_HEIGHT);
        lowerRightRect = new Rectangle(BOARD_LEFT_EDGE + QUADRANT_WIDTH + BAR_WIDTH, 
                BOARD_TOP_EDGE + QUADRANT_HEIGHT + GUTTER_HEIGHT,
                QUADRANT_WIDTH, QUADRANT_HEIGHT);
        //barRect = new Rectangle(BOARD_LEFT_EDGE + QUADRANT_WIDTH, BOARD_TOP_EDGE, 
        //     BAR_WIDTH, BOARD_BOTTOM_EDGE);       
    }

    
} /* class BoardPict */
