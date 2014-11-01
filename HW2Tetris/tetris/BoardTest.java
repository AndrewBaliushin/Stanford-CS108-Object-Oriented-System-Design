package tetris;

import static org.junit.Assert.*;

import org.junit.*;

public class BoardTest {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	
	/*
	 * Board class checklist:
	 * Board(int, int)  -- CHECK
	 * getWidth() -- CHECK
	 * getHeight() -- CHECK
	 * getMaxHeight() -- CHECK
	 * sanityCheck() -- CHECK (if DEBUG == true)
	 * dropHeight(Piece, int) 
	 * getColumnHeight(int) -- CHECK
	 * getRowWidth(int) -- CHECK
	 * getGrid(int, int) -- CHECK
	 * place(Piece, int, int) -- CHECK
	 * clearRows() -- CHECK
	 * undo() -- CHECK
	 */

	// This shows how to build things in setUp() to re-use
	// across tests.
	
	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	@Before
	public void setUp() throws Exception {
		b = new Board(3, 6);
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
		
		b.place(pyr1, 0, 0);
	}
	
	// Check the basic width/height/max after the one placement
	@Test
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	
	// Place sRotated into the board, then check some measures
	@Test
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
		
		/* --> my tests*/
		
		//test clear rows
		b.clearRows();
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(3, b.getColumnHeight(1));
		assertEquals(2, b.getColumnHeight(2));
		assertEquals(3, b.getMaxHeight());
		
		//undo to the state of last commit
		b.undo(); 
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		
		
		
		
	}
	
	// Make  more tests, by putting together longer series of 
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.
	
	@Test
	public void testCase3() {
		Board b2 = new Board(5, 5); 
		
		//System.out.println(b2.toString());
		
		//test new board		
		assertEquals(0, b2.getColumnHeight(0));
		assertEquals(0, b2.getColumnHeight(1));
		assertEquals(0, b2.getMaxHeight());
		assertEquals(0, b2.getRowWidth(0));
		assertEquals(0, b2.getRowWidth(1));
		assertEquals(0, b2.getRowWidth(2));
		
		b2.place(s, 0, 0);
		b2.commit();
		//System.out.println(b2.toString());
		
		/*
		 * current state
		    |     |
			|     |
			|     |
			| ++  |
			|++   |
			-------
		 */
		

		//check that undo doesn't do anything
		b2.undo();
		assertEquals(1, b2.getColumnHeight(0));
		assertEquals(2, b2.getColumnHeight(1));
		assertEquals(2, b2.getRowWidth(0));
		assertEquals(2, b2.getRowWidth(1));
		
	}
	
	@Test
	public void testDropHeight() {
		Board b3 = new Board(3, 6); 
		
		b3.place(pyr1, 0, 0);
		b3.commit();
		
		/*
		System.out.println(b3);
		|   |
		|   |
		|   |
		|   |
		| + |
		|+++|
		-----
		*/
		
		assertEquals(2, b3.getColumnHeight(1)); //just checking

		assertEquals(2, b3.dropHeight(pyr3, 0)); // pyr3 is upside down pyr1
		
		assertEquals(2, b3.dropHeight(s, 0)); //s -- [(0,0), (1,0), (1,1), (2,1)]
		
		assertEquals(1, b3.dropHeight(sRotated, 1)); //[(1,0), (1,1), (0,1), (0,2)]

	}
	
	/*
	 * dropHeight() should throw e if placed not in bounds
	 */
	@Test(expected=RuntimeException.class)
	public void testDropHeightException() {
		Board b3 = new Board(3, 6); 
		
		b3.place(pyr1, 0, 0);
		b3.commit();
		
		/*
		System.out.println(b3);
		|   |
		|   |
		|   |
		|   |
		| + |
		|+++|
		-----
		*/
		

		assertEquals(2, b3.dropHeight(pyr3, 1)); // pyr3 is upside down pyr1
	}
	
	@Test
	public void testClearRows() {
		Board b3 = new Board(3, 6);
		
		b3.place(pyr1, 0, 0);
		b3.commit();
		
		/*
		System.out.println(b3);
		|   |
		|   |
		|   |
		|   |
		| + |
		|+++|
		-----
		*/
		
		
		b3.clearRows();
		b3.commit();
		
		/* now
		 	|   |
			|   |
			|   |
			|   |
			|   |
			| + |
			-----
		 */
		assertEquals(0, b3.getColumnHeight(0));
		assertEquals(1, b3.getColumnHeight(1));
		assertEquals(1, b3.getRowWidth(0));
		assertEquals(0, b3.getRowWidth(1));
		
		b3.place(pyr4, 0, 0);
		b3.commit();
		
		/*System.out.println(b3);
		 	|   |
			|   |
			|   |
			|+  |
			|++ |
			|++ |
			-----
		 */
		
		//check how it works when there is no full rows
		b3.clearRows(); 
		
		assertEquals(3, b3.getColumnHeight(0));
		assertEquals(2, b3.getColumnHeight(1));
		assertEquals(0, b3.getColumnHeight(2));
		assertEquals(2, b3.getRowWidth(0));
		assertEquals(1, b3.getRowWidth(2));
		
		b3.commit();
		
		Piece stick = new Piece(Piece.STICK_STR);
		b3.place(stick, 2, 0);
		
		/* System.out.println(b3);
			|   |
			|   |
			|  +|
			|+ +|
			|+++|
			|+++|
			-----
		*/
		
		b3.clearRows();
		assertEquals(1, b3.getColumnHeight(0));
		assertEquals(0, b3.getColumnHeight(1));
		assertEquals(2, b3.getColumnHeight(2));
		assertEquals(2, b3.getRowWidth(0));
		assertEquals(0, b3.getRowWidth(3));
		
		System.out.println(b3);
		
		
	}
}
