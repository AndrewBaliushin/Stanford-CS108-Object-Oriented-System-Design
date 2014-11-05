package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Sudoku solver.
 * Receive puzzle in left input field and show 
 * solution in right field.
 * 
 * @author Andrew Baliushin
 *
 */
 public class SudokuFrame extends JFrame {
	 
	private JTextArea puzzleArea;
	private JTextArea solutionArea;
	private JButton checkButton;
	private JCheckBox checkBox;
	 
	/**
	 * Create window with solver.
	 */
	public SudokuFrame() {
		super("Sudoku Solver");
		
		createLayout();
		addListners();
		setDefaultPuzzle(SudokuTestGridsData.hardGrid);
		
		// Could do this:
		// setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

	private void createLayout() {
		setLayout(new BorderLayout(4,4));
		
		puzzleArea = new JTextArea(15, 20);
		puzzleArea.setBorder(new TitledBorder("puzzle"));
		puzzleArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		add(puzzleArea, BorderLayout.WEST);
		
		solutionArea = new JTextArea(15, 20);
		solutionArea.setBorder(new TitledBorder("solution"));
		add(solutionArea, BorderLayout.EAST);
		
		//controls
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		checkButton = new JButton("Check");		
		panel.add(checkButton);
		
		checkBox = new JCheckBox("Auto Check");
		checkBox.setSelected(true);
		panel.add(checkBox);
		
		add(panel, BorderLayout.SOUTH);
	}
	
	private void addListners() {
		puzzleArea.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (checkBox.isSelected()) {
					solvePuzzle();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				solvePuzzle();
			}
		});
	}
	
	private void setDefaultPuzzle(int[][] puzzle){
		StringBuilder sb = new StringBuilder();
		for (int col = 0; col < puzzle.length; col++) {
			for (int row = 0; row < puzzle[col].length; row++) {
				sb.append(puzzle[col][row]);
			}
			sb.append("\n");
		}
		puzzleArea.setText(sb.toString());
	}
	
	private void solvePuzzle() {
		String puzzleText = deleteNonNumerics(puzzleArea.getText());
		if(puzzleText.length() != 81) return;
		
		Sudoku sudoku;
		try{
			sudoku = new Sudoku(puzzleText);
		} catch (RuntimeException e) {
			solutionArea.setText("Impossible grid");
			return;
		}
		
		int countSolutions = sudoku.solve();
		
		StringBuilder sb = new StringBuilder();
		sb.append(sudoku.getSolutionText());
		sb.append("\n");
		sb.append("solutions: " + countSolutions);
		sb.append("\n");
		sb.append("elapsed: " + sudoku.getElapsed() + "ms");
		
		solutionArea.setText(sb.toString());
	}
	
	private String deleteNonNumerics(String str){
		return str.replaceAll("[^\\d.]", "");
	}
}
