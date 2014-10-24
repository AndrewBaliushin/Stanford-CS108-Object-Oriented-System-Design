// HW1 2-d array Problems
// CharGrid encapsulates a 2-d grid of chars and supports
// a few operations on the grid.

package assign1;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hamcrest.core.Is;

import assign1.CharGrid.Helper.Around;
import assign1.CharGrid.Helper.Left;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class CharGrid {
	private char[][] grid;

	/**
	 * Constructs a new CharGrid with the given grid.
	 * Does not make a copy.
	 * @param grid
	 */
	public CharGrid(char[][] grid) {
		this.grid = grid;
		
		
	}
	
	/**
	 * Returns the area for the given char in the grid. (see handout).
	 * @param ch char to look for
	 * @return area for given char
	 */
	public int charArea(char ch) {
		/*
		 * This is Dirty version with straightforward approach (which is wrong).
		 * In real life it should use some variation
		 * of convex hull algorithm.
		 */

		int minX = 0;
		int minY = 0;

		int maxX = 0;
		int maxY = 0;

		boolean found = false;

		boolean foundPair = false;
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == ch) {
					if (minX == 0 && minY == 0 && !found) {
						found = true;
						minX = i;
						minY = j;
					} else {
						foundPair = true;

						maxX = i;
						maxY = j;
					}
				}
			}
		}

		if (found && !foundPair) {

			return 1;
		} else if (found) {
			return ((maxX - minX + 1) * (maxY - minY + 1));
		} else {
			return 0;
		}
		

		// return 0; // TODO ADD YOUR CODE HERE
	}
	
	/**
	 * Returns the count of '+' figures in the grid (see handout).
	 * @return number of + in grid
	 */
	public int countPlus() {
		
		for ( int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (countPlusSize(i, j, grid[i][j]) > 0) {
					return countPlusSize(i, j, grid[i][j]);
				}
			}
		}
		
		return 0; // TODO ADD YOUR CODE HERE
	}
	

	private int countPlusSize(int x, int y, char ch) {

		Helper helper = new Helper();

		Set<Helper.Around> directions = new HashSet<CharGrid.Helper.Around>();

		directions.add(helper.new Left());
		directions.add(helper.new Right());
		directions.add(helper.new Up());
		directions.add(helper.new Down());

		// set cords for each object
		for (Around dir : directions) {
			dir.setCoords(x, y, ch);
		}

		boolean everythingOK = true;
		int expandCount = 0;
		
		while (everythingOK) {
			int succesMoves = 0;
			for (Around dir : directions) {
				dir.expand();
				if (dir.checkStatus()) {
					succesMoves++;
				} else {
					break;
				}
			}
			if (succesMoves == 4) {
				expandCount++;
			} else {
				everythingOK = false;
			}
		}

		return (expandCount > 1) ? expandCount : 0;
	}
	
	class Helper {
		abstract class Around {
			protected int x;
			protected int y;
			protected char ch;
			
			public void setCoords(int x, int y, char ch) {
				this.x = x;
				this.y = y;
				this.ch = ch;
			}
			
			public boolean checkStatus() {
				return (checkForBoundries() && checkChar(ch));
			}
			
			protected boolean checkForBoundries() {
				if ( ( x >= 0 && x < grid.length) && 
						( y >= 0 && y < grid[0].length) ) {
					return true;
				} else {
					return false;
				}
			}
			
			protected boolean checkChar (char ch) {
				return (grid[x][y] == ch) ? true : false;
			}

			abstract public void expand();
		}
		
		class Left extends Around {
			@Override
			public void expand() {
				x = x - 1;
			};
		}
		
		class Right extends Around {
			@Override
			public void expand() {
				x = x + 1;
			};
		}
		
		class Down extends Around {
			@Override
			public void expand() {
				y = y - 1;
			};
		}
		
		class Up extends Around {
			@Override
			public void expand() {
				y = y + 1;
			};
		}
	}
	
	
}


