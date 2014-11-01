// Board.java
package tetris;

import java.util.Arrays;


/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	
	/* my variables */
	
	/* stores how tall columns are. 0 if no blocks in column
	 * 2 if 2 blocks. etc
	 * starts from x=0; So {0,1,2,0} is
	 * flat-1block-2block-flat 
	 */
	private int heightsOfCols[];
	
	/*
	 * Stores how many blocks are in each row starting from bottom
	 * {5,3,2} means 5 in first, 3 in second and 2 in last row
	 */
	private int widthsOfRows[];
	
	/*
	 * We can call clearRows() after place() even if committed == false;
	 * place() turn this to true; commit() turns to false;
	 */
	private boolean placeMethodUsed;

	/* backups for undo*/
	private boolean[][] buGrid;
	private int buHeightsOfCols[];
	private int buWidthsOfRows[];
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		
		// YOUR CODE HERE
		heightsOfCols = new int[width];
		widthsOfRows = new int[height];
		
		//init backup storage
		buGrid = new boolean[width][height];
		buHeightsOfCols = new int[width];
		buWidthsOfRows = new int[height];
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	/**
	 * Fills heightsOfCols[] and widthOfRows[]
	 */
	private void makeHeightAndWidthArrays() {
		//clear arrays
		Arrays.fill(heightsOfCols, 0);
		Arrays.fill(widthsOfRows, 0);
		
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				if (grid[x][y]) { //empty cell
					heightsOfCols[x] = y + 1; //cause 0 means flat. 
					widthsOfRows[y]++;
				}
			}
		}
	}
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		int output = 0;
		for (int i = 0; i < heightsOfCols.length; i++) {
			if (heightsOfCols[i] > output) output = heightsOfCols[i];
		}
		
		return output;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	 
	 Checks that widthsOfRows, heightsOfCols and maxHeight are correct.
	*/
	public void sanityCheck() {
		if (!DEBUG) {
			return;
		}
		
		int[] checkWidths = new int[height];
		int[] checkHeights = new int[width];
		int checkMaxHeight = 0;
		
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				if (grid[x][y]) { // empty cell
					checkHeights[x] = y + 1; // cause 0 means flat.
					checkWidths[y]++;
					if ((y + 1) > checkMaxHeight) {
						checkMaxHeight = y + 1;
					}
				} 
			}
		}
		
		if (!Arrays.equals(checkHeights, heightsOfCols) || 
				!Arrays.equals(checkWidths, widthsOfRows) ||
				checkMaxHeight != getMaxHeight()) {
			throw new RuntimeException("sanityCheck() failed");
		}

	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt(); //exmpl: for S-piece it's {1,0} 
		
		if (x < 0 || (x + skirt.length) > width) {
			throw new RuntimeException("droping out of bounds");
		}
		
		int finalRestHeight = 0;
		for (int i = x; i < x + skirt.length; i++) {
			int currentHeight = getColumnHeight(i) - skirt[(i - x)]; //i(0)-x=0; i(1)-x=1...
			if (currentHeight > finalRestHeight) {
				finalRestHeight = currentHeight;
			}
		}
		
		return finalRestHeight;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heightsOfCols[x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widthsOfRows[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			return true;
		} else if (grid[x][y] == true) {
			return true;
		} else {
			return false;
		}
		
		//return false; // YOUR CODE HERE
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) {
			throw new RuntimeException("place commit problem");
		}

		if (x < 0 || y < 0) {
			return PLACE_OUT_BOUNDS;
		}

		TPoint[] bodyOfPiece = piece.getBody();

		// check if all spots are free and in bounds
		for (int i = 0; i < bodyOfPiece.length; i++) {

			int xSpot = x + bodyOfPiece[i].x;
			int ySpot = y + bodyOfPiece[i].y;

			if (xSpot >= width || ySpot >= height) {
				return PLACE_OUT_BOUNDS;
			}

			if (getGrid(xSpot, ySpot)) { // true = no empty space
				return PLACE_BAD;
			}
		} //for
		
		makeBackup();
		committed = false;
		placeMethodUsed = true;
		
		//ok, all spots are free. Lets place piece.
		for (int i = 0; i < bodyOfPiece.length; i++) {
			
			int xSpot = x + bodyOfPiece[i].x;
			int ySpot = y + bodyOfPiece[i].y;
			
			grid[xSpot][ySpot] = true;
		}
		
		
		makeHeightAndWidthArrays(); // refresh data
		sanityCheck();
		
		/* check rows occupied by new piece for full row*/
		for (int i = y; i < (y + piece.getHeight()); i++) {
			if (widthsOfRows[i] == width) {
				return PLACE_ROW_FILLED;
			}
		}
		
		return PLACE_OK;
		
	}
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if (!committed && !placeMethodUsed) {
			throw new RuntimeException("place commit problem");
		}
		placeMethodUsed = false; //sets marker to false, so can't be call again
		committed = false;
		
		makeHeightAndWidthArrays(); //just in case
		
		int rowsCleared = 0;
		int from = 0;
		int to = 0;
		while (from < height) {
			
			if (widthsOfRows[from] == width) {//if row is full
				from++; //skip this line and take from next
				rowsCleared++; 
				continue;
			}
			
			if (from == to) {//if no copy required
				from++;
				to++;
				continue;
			}
			
			copyRow(from, to); //move row down
			makeHeightAndWidthArrays(); //update required
			
		
			from++;
			to++;			
		}
		
		if (rowsCleared > 0) { //refresh widths and heights
			makeHeightAndWidthArrays();
		}
		
		sanityCheck();
		return rowsCleared;
	}

	/**
	 * Copy row of grid
	 * @param fromNofRow  number of row to copy from
	 * @param toNofRow	  destination
	 */
	private void copyRow(int fromNofRow, int toNofRow) {
		for (int x = 0; x < grid.length; x++) {
			grid[x][toNofRow] = grid[x][fromNofRow]; 
		}
	}


	private void makeBackup() {		
		//make deep copy
		for (int i = 0; i < grid.length; i++) {
			 System.arraycopy(grid[i], 0, buGrid[i], 0, grid[i].length); 
		}
		
		System.arraycopy(widthsOfRows, 0, buWidthsOfRows, 0, widthsOfRows.length);
		System.arraycopy(heightsOfCols, 0, buHeightsOfCols, 0, heightsOfCols.length);
	}
	
	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		
		if (committed == true) {
			return;
		}
		
		if (buGrid == null) {
			throw new RuntimeException("No source for backup");
		}
		
		committed = true;

		//make deep copy
		for (int i = 0; i < buGrid.length; i++) {
			 System.arraycopy(buGrid[i], 0, grid[i], 0, buGrid[i].length); 
		}
		
		System.arraycopy(buWidthsOfRows, 0, widthsOfRows, 0, buWidthsOfRows.length);
		System.arraycopy(buHeightsOfCols, 0, heightsOfCols, 0, buHeightsOfCols.length);
		
		sanityCheck();
		
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		placeMethodUsed = false;
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


