package sudoku;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing (!!!-> Moved to SudokuTestGridsData)
	// The instance variable strategy is up to you.

	public static final int SIZE = 9; // size of the whole 9x9 puzzle
	public static final int PART = 3; // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	private static long endTime;

	private long startTime;

	// list of non 0 spots
	private List<Sudoku.FilledSpot> filledSpotsList = new ArrayList<Sudoku.FilledSpot>();

	// list of empty spots
	private List<Sudoku.EmptySpot> emptySpotsList = new ArrayList<Sudoku.EmptySpot>();

	// buckets for separate rows, cols, squares (area 3x3)
	private HashMap<Integer, Set<FilledSpot>> squaresBuckets = new HashMap<Integer, Set<FilledSpot>>(
			SIZE);
	private HashMap<Integer, Set<FilledSpot>> rowsBuckets = new HashMap<Integer, Set<FilledSpot>>(
			SIZE);
	private HashMap<Integer, Set<FilledSpot>> columnsBuckets = new HashMap<Integer, Set<FilledSpot>>(
			SIZE);

	private int[][] currentGrid = new int[SIZE][SIZE];

	// solve() put solution here (if any)
	private static int[][] solvedGrid;

	private static abstract class Spot {

		protected int column;
		protected int row;

		private int value;

		public static FilledSpot createFilledSpot(int row, int col, int value) {
			return new FilledSpot(row, col, value);
		}

		/**
		 * @param row
		 * @param col
		 * @param possibleValues allowed by Sudoku rules values for this spot
		 * @return
		 */
		public static EmptySpot createEmptySpot(int row, int col,
				int[] possibleValues) {
			return new EmptySpot(row, col, possibleValues);
		}

		protected Spot(int row, int col, int value) {
			this.column = col;
			this.row = row;
			this.value = value;
		}

		public int getRow() {
			return row;
		}
		
		public int getCol() {
			return column;
		}

		public int getValue() {
			return value;
		}
	}

	private static class FilledSpot extends Spot {

		protected FilledSpot(int row, int col, int value) {
			super(row, col, value);
		}

		// hashs are equal when values equal
		@Override
		public int hashCode() {
			return this.getValue();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if ((obj == null) || (obj.getClass() != this.getClass()))
				return false;
			// object must be Spot at this point
			FilledSpot test = (FilledSpot) obj;
			return (this.getValue() == test.getValue());
		}
	}

	private static class EmptySpot extends FilledSpot {

		private int[] possibleValues;
		private int numberOfPossibleValues;

		protected EmptySpot(int y, int x, int[] possibleValues) {
			super(y, x, 0);
			this.possibleValues = possibleValues;
			numberOfPossibleValues = possibleValues.length;
		}

		public int[] getPossibleValues() {
			return possibleValues;
		}

		public int getNumberOfPossibleValues() {
			return numberOfPossibleValues;
		}

		@Override
		public int hashCode() {
			return SIZE * getCol() + getRow();
		}

		static class PossibleValuesComparator implements Comparator<EmptySpot> {
			@Override
			public int compare(EmptySpot o1, EmptySpot o2) {
				return (o1.getNumberOfPossibleValues() - o2
						.getNumberOfPossibleValues());
			}

		}
	}

	// Provided various static utility methods to
	// convert data formats to int[][] grid.

	/**
	 * Returns a 2-d grid parsed from strings, one string per row. The "..." is
	 * a Java 5 feature that essentially makes "rows" a String[] array.
	 * (provided utility)
	 * 
	 * @param rows
	 *            array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row < rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}

	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid. Skips
	 * all the non-numbers in the text. (provided utility)
	 * 
	 * @param text
	 *            string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE * SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:"
					+ nums.length);
		}

		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}

	/**
	 * Given a string containing digits, like "1 23 4", returns an int[] of
	 * those digits {1 2 3 4}. (provided utility)
	 * 
	 * @param string
	 *            string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i = 0; i < string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i + 1));
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
		// sudoku = new Sudoku(SudokuTestGridsData.gridFromAssign);
		// sudoku = new Sudoku(SudokuTestGridsData.hardGrid);
		// sudoku = new Sudoku(SudokuTestGridsData.mediumGrid);
		sudoku = new Sudoku(SudokuTestGridsData.hardGridFork); // cool one with 6 solutions

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
		collectFilledSpots(ints);
		collectEmptySpots(ints);

		// sort by number of possible values (min -> max)
		Collections.sort(emptySpotsList, new EmptySpot.PossibleValuesComparator());
	}

	public Sudoku(String stringSudoku) {
		this(textToGrid(stringSudoku));
	}

	public Sudoku(String... rows) {
		this(stringsToGrid(rows));
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search. Return
	 * number of possible solutions to this grid.
	 */
	public int solve() {
		startTime = System.currentTimeMillis();

		// no empty spots
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

		// unsolvable
		if (emptySpotsList.get(0).numberOfPossibleValues == 0)
			return 0;

		// only spots with multiple options left
		if (emptySpotsList.get(0).numberOfPossibleValues > 1) {
			int solutionsCounter = 0;
			EmptySpot emptySpot = emptySpotsList.get(0);
			int[] possibleValues = emptySpot.getPossibleValues();
			for (int i = 0; i < possibleValues.length; i++) {
				int[][] forkedGrid = new int[currentGrid.length][];
				for (int j = 0; j < currentGrid.length; j++) { // copy grid
					forkedGrid[j] = new int[currentGrid[j].length];
					System.arraycopy(currentGrid[j], 0, forkedGrid[j], 0,
							currentGrid[j].length);
				}
				// fork
				forkedGrid[emptySpot.row][emptySpot.column] = possibleValues[i];
				try {
					solutionsCounter += new Sudoku(forkedGrid).solve();
				} catch (RuntimeException e) {
					// paradox grid occurred.
				}
			}
			return solutionsCounter;
		}

		// fill all single-option spots
		Iterator<EmptySpot> iter = emptySpotsList.iterator();
		while (iter.hasNext()) {
			EmptySpot emptySpot = iter.next();
			int[] posValues = emptySpot.getPossibleValues();
			if (posValues.length > 1) {
				break;
			}
			createSpotAndPutInBuckets(emptySpot.getRow(), emptySpot.getCol(),
					posValues[0]);
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

	// time between initialization of Sudoku and completion of solve()
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
				if ((j + 1) % PART == 0 && j != (SIZE - 1)) { // every 3 spots
					sb.append("|");
				}
			}
			if ((i + 1) % PART == 0 && i != (SIZE - 1)) { // every 3 rows
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private void collectFilledSpots(int[][] input) {
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++) {
				if (input[row][col] != 0) {
					createSpotAndPutInBuckets(row, col, input[row][col]);
				}
			}
		}
	}

	private void collectEmptySpots(int[][] input) {
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++) {
				if (input[row][col] == 0) {
					emptySpotsList.add(Spot.createEmptySpot(row, col,
							findPossibleValuesForEmptySpot(row, col)));
				}
			}
		}
	}

	private FilledSpot createSpotAndPutInBuckets(int y, int x, int value) {
		if (value == 0) {
			return null;
		}

		FilledSpot spot = Spot.createFilledSpot(y, x, value);

		Set<FilledSpot> sqrBucket = squaresBuckets.get(calculateSquareBucketId(y, x));
		if (sqrBucket == null) {
			sqrBucket = new HashSet<Sudoku.FilledSpot>();
			squaresBuckets.put(calculateSquareBucketId(y, x), sqrBucket);
		}
		putSpotInBucket(spot, sqrBucket);

		Set<FilledSpot> rowBucket = rowsBuckets.get(y);
		if (rowBucket == null) {
			rowBucket = new HashSet<Sudoku.FilledSpot>();
			rowsBuckets.put(y, rowBucket);
		}
		putSpotInBucket(spot, rowBucket);

		Set<FilledSpot> colBucket = columnsBuckets.get(x);
		if (colBucket == null) {
			colBucket = new HashSet<Sudoku.FilledSpot>();
			columnsBuckets.put(x, colBucket);
		}
		putSpotInBucket(spot, colBucket);

		filledSpotsList.add(spot);

		currentGrid[y][x] = value;

		return spot;
	}

	private void putSpotInBucket(FilledSpot spot, Set<FilledSpot> bucket) {
		if (bucket.contains(spot)) {
			throw new RuntimeException("Spot " + spot.getCol() + ","
					+ spot.getRow() + "=" + spot.getValue() + " is forbiden.");
		} else {
			bucket.add(spot);
		}
	}

	// set of spots with values from 0 to (SIZE - 1)
	private Set<FilledSpot> createPerfectSpotSet() {
		Set<FilledSpot> perfectSet = new HashSet<Sudoku.FilledSpot>(SIZE);
		for (int i = 1; i <= SIZE; i++) {
			perfectSet.add(Spot.createFilledSpot(-1, -1, i));
		}
		return perfectSet;
	}

	// divide grid on PART x PART size square buckets and return proper bucketID.
	private int calculateSquareBucketId(int y, int x) {
		return (x / PART) + (y / PART) * PART;
	}

	private int[] findPossibleValuesForEmptySpot(int y, int x) {
		Set<FilledSpot> perfectSetOfSpots = createPerfectSpotSet();

		perfectSetOfSpots.removeAll(squaresBuckets.get(calculateSquareBucketId(
				y, x)));
		perfectSetOfSpots.removeAll(rowsBuckets.get(y));
		perfectSetOfSpots.removeAll(columnsBuckets.get(x));

		int[] outputArr = new int[perfectSetOfSpots.size()];
		int i = 0;
		for (FilledSpot s : perfectSetOfSpots) {
			outputArr[i++] = s.getValue();
		}

		return outputArr;
	}

}
