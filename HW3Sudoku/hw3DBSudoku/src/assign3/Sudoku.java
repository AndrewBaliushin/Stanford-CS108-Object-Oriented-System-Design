package assign3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGridFork = Sudoku.stringsToGrid(
	"3 0 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");	
	
	
	
	public static final int[][] gridFromAssign = Sudoku.stringsToGrid(
	"035290864",
	"082410703",
	"764380090",
	"218739040",
	"000804230",
	"043052970",
	"406571009",
	"359028417",
	"800900526");
			
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	
	// Refs to all non 0 spots on field;
	private List<Sudoku.Spot> filledSpotsList = new ArrayList<Sudoku.Spot>();

	//list filled with empty spots
	private List<Sudoku.EmptySpot> emptySpotsList = new ArrayList<Sudoku.EmptySpot>();
	
	/*
	 * Lists of Buckets that contain
	 * sets of spots in squares (3x3 area), rows and cols.
	 * Sets won't allow duplication.
	 * 
	 */
	private HashMap<Integer, Set<Spot>> squaresBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	private HashMap<Integer, Set<Spot>> rowsBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	private HashMap<Integer, Set<Spot>> columnsBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	
	/*
	 * grid as is from input.
	 */
	private int[][] originalGrid;
	
	/*
	 * Grid after all spots have been added.
	 */
	private int[][] currentGrid = new int[SIZE][SIZE];
	
	/*
	 * First successfull solve fill this var.
	 */
	private static int[][] solverGrid;
	
	/**
	 * Class for objects of spot. 
	 * Stores coordinates and value for spot;
	 * 
	 * Spots are equal when their values are equal.
	 * 
	 * It's public for the sake of testing.
	 * 
	 * @author Adndrew Baliushin
	 */
	public class Spot {
		
		protected int xCoord;
		protected int yCoord;
		
		private int value;
		
		/**
		 * Ctor of occupied cell;
		 * @param x
		 * @param y
		 * @param value
		 */
		Spot(int y, int x, int value) {
			xCoord = x;
			yCoord = y;
			this.value = value;			
		}
		
		/**
		 * ctor of empty cell. Value = 0;
		 * 
		 * @param x
		 * @param y
		 */
		Spot(int y, int x) {
			this(y, x, 0);
		}
		
		/**
		 * @return int X-coordinate
		 */
		public int getX() {
			return xCoord;
		}
		
		/**
		 * @return int Y-coordinate
		 */
		public int getY() {
			return yCoord;
		}

		/**
		 * @return value
		 */
		public int getValue() {
			return value;
		}
		
		
		//since all we need is equality of values
		//we can just return value
		@Override
		public int hashCode() {
			return value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
		        return true;
		    if ((obj == null) || (obj.getClass() != this.getClass()))
		        return false;
		    // object must be Spot at this point
		    Spot test = (Spot) obj;
		    return (this.getValue() == test.getValue());
		}

		
	} //Spot
	
	
	class EmptySpot extends Spot implements Comparable<EmptySpot> {

		private int[] possibleValues;
		private int numberOfPossibleValues;
		
		EmptySpot(int y, int x, int[] possibleValues) {
			super(y, x, 0);
			this.possibleValues = possibleValues;
			numberOfPossibleValues = possibleValues.length;
		}
		
		private int[] getPossibleValues() {
			return possibleValues;
		}
		
		@Override
		public int compareTo(EmptySpot o) {
			return (this.numberOfPossibleValues - o.numberOfPossibleValues);
		}
		
		@Override
		public int hashCode() {
			return 31*xCoord + 31*yCoord;
		}
	}
	
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		//sudoku = new Sudoku(gridFromAssign);
		//sudoku = new Sudoku(hardGrid);
		sudoku = new Sudoku(hardGridFork); // cool one with 6 solutions
		//sudoku = new Sudoku(mediumGrid); 
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 * 
	 */
	public Sudoku(int[][] ints) {
		/*
		 * Convert 2d array into list of spots. And creates buckets, which
		 * contain sets of spot values in each separate row, col, sqr.
		 * Creates List of occupied spots.
		 * Creates List of empty spots with possible values for them.
		 * Sort that list by number of possible values  
		 */
		
		// YOUR CODE HERE
		
		//save original grid 
		originalGrid = ints;	
		
		//parse 2-d array into list of spots and fill row, cols, squares buckets. 
		for (int y = 0; y < ints.length; y++) { //rows 
			for (int x = 0; x < ints[y].length; x++) { //cols
				createSpotAndPutInBuckets(y, x, ints[y][x]);
			}
		}
		
		//put all empty spots in arraylist and sort
		for (int y = 0; y < ints.length; y++) { //rows 
			for (int x = 0; x < ints[y].length; x++) { //cols
				if (ints[y][x] == 0) {
					emptySpotsList.add(new EmptySpot(y, x, findPossibleValuesForEmptySpot(y, x)));
				}
			}
		}
		Collections.sort(emptySpotsList); 
//		Just checking 
//		for (EmptySpot s : emptySpotsList) {
//			System.out.println(s.numberOfPossibleValues);
//		}
		
	}
	
	public Sudoku(String stringSudoku) {
		this(textToGrid(stringSudoku));
	}
	
	public Sudoku(String... rows) {
		this(stringsToGrid(rows));
	}
	
	
	
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 * Return number of possible solutions to this grid.
	 */
	public int solve() {
		//if emptySpotsList is 0 then we finished
		if (emptySpotsList.size() == 0) {
			solverGrid = new int[currentGrid.length][];
			for (int i = 0; i < currentGrid.length; i++) {
				solverGrid[i] = new int[currentGrid[i].length];
				System.arraycopy(currentGrid[i], 0, solverGrid[i], 0,
						currentGrid[i].length);
			}

			return 1; // return +1 to total count of available solutions
		}
		
		//if emptySpotsList contains spot with zero value options
		//than its over. Grid not solvable. 
		//we can take item at index 0 as smallest since list sorted
		if(emptySpotsList.get(0).numberOfPossibleValues == 0) return 0;
		
		//If there is no single option spots left 
		//we iterate through possible options
		if (emptySpotsList.get(0).numberOfPossibleValues > 1) {
			int solutionsCounter = 0; //count how many grids solved successfully after fork
			EmptySpot emptySpot = emptySpotsList.get(0);
			int[] possibleValues = emptySpot.getPossibleValues();
			for (int i = 0; i < possibleValues.length; i++) {
				int[][] forkedGrid = new int[currentGrid.length][];
				for (int j = 0; j < currentGrid.length; j++) { //copy grid
					forkedGrid[j] = new int[currentGrid[j].length];
					System.arraycopy(currentGrid[j], 0, forkedGrid[j], 0,
							currentGrid[j].length);
				}
				forkedGrid[emptySpot.yCoord][emptySpot.xCoord] = possibleValues[i]; // modify
				try {
					solutionsCounter += new Sudoku(forkedGrid).solve(); // recursion
				} catch (RuntimeException e) {
					// Just ignore it. It means that we tried impossible grid.
				}
				
			}

			return solutionsCounter;
		}
		
		//if we got at least one single option spot
		Iterator<EmptySpot> iter = emptySpotsList.iterator();
		while (iter.hasNext()) {
			EmptySpot emptySpot = iter.next();
			int[] posValues = emptySpot.getPossibleValues();
			if (posValues.length > 1) {
				break;
			}
			createSpotAndPutInBuckets(emptySpot.getY(), emptySpot.getX(), posValues[0]);
			iter.remove();			
		}
		
		return new Sudoku(currentGrid).solve();
		
	}
	
	public String getSolutionText() {
		if (solverGrid != null) {
			return gridToString(solverGrid);
		} else {
			return "Solution not found";
		}
	}
	
	public long getElapsed() {
		return 0; // YOUR CODE HERE
	}
	
	/**
	 * Simply prints grid array.
	 */
	@Override
	public String toString() {
		return gridToString(currentGrid);
	}
	
	public static String gridToString(int[][] grid) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				sb.append(" ");
				sb.append(grid[i][j]);
				if ((j + 1) % PART == 0 && j != (SIZE - 1) ) { //every 3 spots
					sb.append("|");
				}
			}
			
			if ((i + 1) % PART == 0 && i != (SIZE - 1)) { //every 3 rows
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();		
	}
	
	private Spot createSpotAndPutInBuckets(int y, int x, int value) {
		if (value == 0) {
			return null;
		}
		
		Spot spot = new Spot(y, x, value);
		
		//squares bucket
		Set<Spot> sqrBucket = squaresBuckets.get(calculateSquareBucketId(y, x));
		if (sqrBucket == null) {
			sqrBucket = new HashSet<Sudoku.Spot>();
			squaresBuckets.put(calculateSquareBucketId(y, x), sqrBucket);
		}
		putSpotInBucket(spot, sqrBucket);
		
		//row bucket
		Set<Spot> rowBucket = rowsBuckets.get(y);
		if (rowBucket == null) {
			rowBucket = new HashSet<Sudoku.Spot>();
			rowsBuckets.put(y, rowBucket);
		}
		putSpotInBucket(spot, rowBucket);
		
		//column bucket
		Set<Spot> colBucket = columnsBuckets.get(x);
		if (colBucket == null) {
			colBucket = new HashSet<Sudoku.Spot>();
			columnsBuckets.put(x, colBucket);
		}
		putSpotInBucket(spot, colBucket);
		
		//if everything worked without exception than lets add spot to list
		filledSpotsList.add(spot);
		
		//also lets put it in the array of current state.
		currentGrid[y][x] = value;
		
		//return just in case
		return spot;
	}
	
	/*
	 * Helper for putting spots in bucket
	 */
	private void putSpotInBucket(Spot spot, Set<Spot> bucket) {
		if (bucket.contains(spot)) {
			throw new RuntimeException("Spot " + spot.getX() + "," +
						spot.getY() + "="  + spot.getValue() + " is forbiden.");
		} else {
			bucket.add(spot);
		}
	}
	
	/*
	 * Creates Set<Spot> with spots from 0 to SIZE values.
	 * Used for comparation with row, col, sqr buckets.
	 */
	private Set<Spot> createPerfectSpotSet() {
		Set<Spot> perfectSet = new HashSet<Sudoku.Spot>(SIZE);
		for (int i = 1; i <= SIZE; i++) {
			perfectSet.add(new Spot(-1, -1, i));
		}
		return perfectSet;
	}
	
	/**
	 * Return id of bucket of squares (3x3 areas) where
	 * this Spot should lend. 
	 * Exmpl: First bucket in second is bucket #4. etc.
	 * 
	 * @return id of bucket from 0 to SIZE-1
	 */
	private int calculateSquareBucketId(int y, int x) {
		/* How it works:
		 * int devided by anything floors down to closest int
		 * 2/3 = 0, 4/3 = 1;
		 * !!! (y / PART) * PART != y; exmpl: (4/3) * 3 = 3;
		 */
		return (x / PART) + (y / PART) * PART;
	}
	
	
	/*
	 * Finds out what values are possible for empty spot y,x;
	 */
	private int[] findPossibleValuesForEmptySpot(int y, int x) {
		Set<Spot> perfectSetOfSpots = createPerfectSpotSet();
		
		perfectSetOfSpots.removeAll(squaresBuckets.get(calculateSquareBucketId(y, x)));
		perfectSetOfSpots.removeAll(rowsBuckets.get(y));
		perfectSetOfSpots.removeAll(columnsBuckets.get(x));
		
		int[] outputArr = new int[perfectSetOfSpots.size()];
		int i = 0;
		for (Spot s : perfectSetOfSpots) {
			outputArr[i++] = s.getValue();
		}
		
		return outputArr;	
	}

}
