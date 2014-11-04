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
	
	// 6 solutions
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
	
	private static final long startTime = System.currentTimeMillis(); 
	
	private static long endTime;
	
	// Refs to all non 0 spots on field;
	private List<Sudoku.Spot> filledSpotsList = new ArrayList<Sudoku.Spot>();

	//list filled with empty spots
	private List<Sudoku.EmptySpot> emptySpotsList = new ArrayList<Sudoku.EmptySpot>();
	
	//buckets for separate rows, cols, squares (area 3x3)
	private HashMap<Integer, Set<Spot>> squaresBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	private HashMap<Integer, Set<Spot>> rowsBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	private HashMap<Integer, Set<Spot>> columnsBuckets = new HashMap<Integer, Set<Spot>>(SIZE);
	
	private int[][] currentGrid = new int[SIZE][SIZE];
	
	//solve() put solution here (if any) 
	private static int[][] solvedGrid;
	
	
	/**
	 * Spots are equal when their values are equal.
	 * 
	 * @author Adndrew Baliushin
	 */
	private class Spot {
		
		protected int xCoord;
		protected int yCoord;
		
		private int value;
		
		/**
		 * Ctor for occupied cell;
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
		 * Ctor for empty cell. Value = 0;
		 * @param x
		 * @param y
		 */
		Spot(int y, int x) {
			this(y, x, 0);
		}
		
		public int getX() {
			return xCoord;
		}
		
		public int getY() {
			return yCoord;
		}

		public int getValue() {
			return value;
		}
		
		//hashs equals when values equals
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
	
	/**
	 * Contains coords and array of possible values
	 */
	private class EmptySpot extends Spot implements Comparable<EmptySpot> {

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
		//sudoku = new Sudoku(mediumGrid); 
		sudoku = new Sudoku(hardGridFork); // cool one with 6 solutions
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		//fill Spots lists and buckets.
		convertToSpots(ints);
		
		//sort by number of possible values (min -> max)
		Collections.sort(emptySpotsList);		
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
		//no empty spots
		if (emptySpotsList.size() == 0) {
			solvedGrid = new int[currentGrid.length][];
			for (int i = 0; i < currentGrid.length; i++) {
				solvedGrid[i] = new int[currentGrid[i].length];
				System.arraycopy(currentGrid[i], 0, solvedGrid[i], 0,
						currentGrid[i].length);
			}
			endTime = System.currentTimeMillis();
			return 1;
		}
		
		//unsolvable 
		if(emptySpotsList.get(0).numberOfPossibleValues == 0) return 0;
		
		//only spots with multiple options left
		if (emptySpotsList.get(0).numberOfPossibleValues > 1) {
			int solutionsCounter = 0;
			EmptySpot emptySpot = emptySpotsList.get(0);
			int[] possibleValues = emptySpot.getPossibleValues();
			for (int i = 0; i < possibleValues.length; i++) {
				int[][] forkedGrid = new int[currentGrid.length][];
				for (int j = 0; j < currentGrid.length; j++) { //copy grid
					forkedGrid[j] = new int[currentGrid[j].length];
					System.arraycopy(currentGrid[j], 0, forkedGrid[j], 0,
							currentGrid[j].length);
				}
				//fork
				forkedGrid[emptySpot.yCoord][emptySpot.xCoord] = possibleValues[i];
				try {
					solutionsCounter += new Sudoku(forkedGrid).solve();
				} catch (RuntimeException e) {
					//paradox grid occurred. 
				}
			}
			return solutionsCounter;
		}
		
		//fill all single-option spots
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
		if (solvedGrid != null) {
			return gridToString(solvedGrid);
		} else {
			return "Solution not found";
		}
	}
	
	//time between initialization of Sudoku and completion of solve()
	public long getElapsed() {
		return endTime - startTime;
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
	
	private void convertToSpots(int[][] input) {
		//create non empty spots and put them in buckets of rows, cols, sqrs
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++) {
				if (input[row][col] != 0) {
					createSpotAndPutInBuckets(row, col, input[row][col]);
				}
			}
		}

		//populate emptySpotsList
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++) {
				if (input[row][col] == 0) {
					emptySpotsList.add(new EmptySpot(row, col,
							findPossibleValuesForEmptySpot(row, col)));
				}
			}
		}
	}
	
	private Spot createSpotAndPutInBuckets(int y, int x, int value) {
		if (value == 0) {
			return null;
		}
		
		Spot spot = new Spot(y, x, value);
		
		Set<Spot> sqrBucket = squaresBuckets.get(calculateSquareBucketId(y, x));
		if (sqrBucket == null) {
			sqrBucket = new HashSet<Sudoku.Spot>();
			squaresBuckets.put(calculateSquareBucketId(y, x), sqrBucket);
		}
		putSpotInBucket(spot, sqrBucket);
		
		Set<Spot> rowBucket = rowsBuckets.get(y);
		if (rowBucket == null) {
			rowBucket = new HashSet<Sudoku.Spot>();
			rowsBuckets.put(y, rowBucket);
		}
		putSpotInBucket(spot, rowBucket);
		
		Set<Spot> colBucket = columnsBuckets.get(x);
		if (colBucket == null) {
			colBucket = new HashSet<Sudoku.Spot>();
			columnsBuckets.put(x, colBucket);
		}
		putSpotInBucket(spot, colBucket);
		
		filledSpotsList.add(spot);
		
		currentGrid[y][x] = value;
		
		return spot;
	}
	
	private void putSpotInBucket(Spot spot, Set<Spot> bucket) {
		if (bucket.contains(spot)) {
			throw new RuntimeException("Spot " + spot.getX() + "," +
						spot.getY() + "="  + spot.getValue() + " is forbiden.");
		} else {
			bucket.add(spot);
		}
	}
	
	//set of spots with values from 0 to (SIZE - 1)
	private Set<Spot> createPerfectSpotSet() {
		Set<Spot> perfectSet = new HashSet<Sudoku.Spot>(SIZE);
		for (int i = 1; i <= SIZE; i++) {
			perfectSet.add(new Spot(-1, -1, i));
		}
		return perfectSet;
	}
	
	//Divide grid on buckets PART x PART size
	private int calculateSquareBucketId(int y, int x) {
		return (x / PART) + (y / PART) * PART;
	}
	
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
