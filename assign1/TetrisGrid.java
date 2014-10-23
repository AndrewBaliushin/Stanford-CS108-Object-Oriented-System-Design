//
// TetrisGrid encapsulates a tetris board and has
// a clearRows() capability.
package assign1;

import java.util.ArrayList;
import java.util.Arrays;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class TetrisGrid {
	
	private boolean[][] grid;
	
	/**
	 * Constructs a new instance with the given grid.
	 * Does not make a copy.
	 * @param grid
	 */
	public TetrisGrid(boolean[][] grid) {
		this.grid = grid;
	}
	
	
	/**
	 * Does row-clearing on the grid (see handout).
	 */
	public void clearRows() {
		/*
		 * input example:
		 * {true, true, false, },
		 * {false, true, true, }
		 */
		
		boolean[][] rotatedGrid = new boolean[grid[0].length][grid.length];
		
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				rotatedGrid[j][i] = grid[i][j];
			}
		}
		
//		System.out.println(Arrays.deepToString(rotatedGrid));
		
		// now 
		// [true, false]
		// [true, true]
		
		for (int i = 0; i < rotatedGrid.length; i++) {
			if (isRowFull(rotatedGrid[i])) {
				rotatedGrid = removeRowAndAddEmptyOnTop(rotatedGrid, i);
			}
		}
		
//		System.out.println(Arrays.deepToString(rotatedGrid));
		
		for (int i = 0; i < rotatedGrid.length; i++) {
			for (int j = 0; j < rotatedGrid[0].length; j++) {
				grid[j][i] = rotatedGrid[i][j];
			}
		}
		
//		System.out.println(Arrays.deepToString(rotatedGrid));
//		System.out.println(Arrays.deepToString(grid));
		
	} //end of clearRows();
	
	
	private boolean isRowFull(boolean[] row) {
		for (int i = 0; i < row.length; i++) {
			if (!row[i]) {
				return false;
			}
		}		
		return true;
	}
	
	private boolean[][] removeRowAndAddEmptyOnTop(boolean[][] arr, int noOfRow) {
		boolean[][] tmpAr = new boolean[arr.length][arr[0].length];
		
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
			
				if (i < noOfRow) {
					tmpAr[i][j] = arr[i][j]; 
				} else if (( i < (tmpAr.length - 1)) && (j < (tmpAr[0].length))) {
					tmpAr[i][j] = arr[i+1][j];
				}
			}
		}
		
		//last line -- Doesn't needed. Array filled with False by default.
//		for (int j = 0; j < tmpAr[0].length; j++) {
//			tmpAr[tmpAr.length - 1][j] = false; 
//		}
		
		return tmpAr;
		
	}
	
	/**
	 * Returns the internal 2d grid array.
	 * @return 2d grid array
	 */
	boolean[][] getGrid() {
		return grid; // TODO YOUR CODE HERE
	}
}
