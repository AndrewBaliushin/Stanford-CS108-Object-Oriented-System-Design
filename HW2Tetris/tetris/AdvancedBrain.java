package tetris;

public class AdvancedBrain extends DefaultBrain {

	/*
	 * @see tetris.DefaultBrain#rateBoard(tetris.Board)
	 * 
	 * Lower scores are better.
	 * 
	 * Implementation which rate board by number of holes and hanging cliffs.
	 * Higher scores given when piece is attached only by horizontal side.
	 * 'Flat' suffice without tall peaks scores less. 
	 * 
	 */
	@Override
	public double rateBoard(Board board) {
		
		final int width = board.getWidth();
        //final int maxHeight = board.getMaxHeight();
        
        int sumHeight = 0;
        int holes = 0;
        int cliffs = 0;
        int peaks = 0; //score added for each peak above or below alloweDiffOfHeight
        
        //how many block below and above avg height is ok
        final int allowedDiffOfHeight = 2;
        
        
        double avgHeight;
        
        // Get avg height
        for (int x=0; x<width; x++) {
        	final int colHeight = board.getColumnHeight(x);
            sumHeight += colHeight;
        }
        avgHeight = ((double)sumHeight)/width;
        
        
        // Count the holes, and sum up the heights
        for (int x=0; x<width; x++) {
            final int colHeight = board.getColumnHeight(x);
            
            //peaks
            if (colHeight > (avgHeight + allowedDiffOfHeight) || 
            		colHeight < (avgHeight - allowedDiffOfHeight)) {
				peaks++;
			}
            
            int y = colHeight - 2;    // addr of first possible hole
            
            while (y>=0) {
                if  (board.getGrid(x,y)) {
                	y--;
                	continue;
                }
                
                holes++;
                
                /*
                 * Cliffs counter with XOR.
                 * If cell above is occupied
                 */
                if (x != (width - 1) || x != 0) { //not near wall
                	if (board.getGrid(x,(y + 1))) {//above is occupied
                		//if from either left or right is empty space
                    	if (!board.getGrid((x + 1), y) && !board.getGrid((x + 1), (y + 1)) ) {
                    		cliffs++;
    					}
                    	if (!board.getGrid((x - 1), y) && !board.getGrid((x - 1), (y + 1)) ) {
                    		cliffs++;
    					}                    	
    				}
				} //cliffs for loop
                
                y--;
            }
            
            
        }
        
        // Add up the counts to make an overall score
        // variants: 
        // (1*holes + 1*cliffs + 1*peaks);   -- is pretty good. beats test sequence
        // (1*avgHeight + 1*holes + 1*cliffs + 1*peaks) -- also very good. 
        // (1*avgHeight + 1*holes + 2*cliffs + 1*peaks); -- best so far
        
        return (1*avgHeight + 1*holes + 2*cliffs + 1*peaks);  
	}

}
