
/**
 * Abstract class Strategy - 
 * lists the methods that have to be implemented by any Game Strategy we write
 * 
 * @author Mike Roam
 * @version 2012 Mar 6
 */
public abstract class Strategy
{
    

    /**
     * Given a board, will tell us the "best" move.
     */
    abstract Move pickBestMove(Board currentBoard, int theColor);
}
