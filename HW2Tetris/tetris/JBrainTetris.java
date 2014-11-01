package tetris;

import java.awt.Dimension;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class JBrainTetris extends JTetris {

	protected JCheckBox brainMode;
	protected JCheckBox brainAutoDropMode;
	protected boolean bestMoveComputed;
	protected JSlider adversary;
	
	protected Random random = new Random();
	
	protected Brain brain;
	protected Brain.Move brainMove;
	
	public JBrainTetris(int pixels) {
		super(pixels);
		
//		brain = new DefaultBrain();
		brain = new AdvancedBrain();		
	}
	
	/**
	 * tick() from Jtetris but with Brain implementation
	 */
	@Override
	public void tick(int verb) {
		if (!gameOn) return;
		
		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}
		
		if (brainMode.isSelected()) {
			useBrainAdjustment();
		}
		
		// Sets the newXXX ivars
		computeNewPosition(verb);
		
		// try out the new position (rolls back if it doesn't work)
		int result = setCurrent(newPiece, newX, newY);
		
		// if row clearing is going to happen, draw the
		// whole board so the green row shows up
		if (result ==  Board.PLACE_ROW_FILLED) {
			repaint();
		}
		

		boolean failed = (result >= Board.PLACE_OUT_BOUNDS);
		
		// if it didn't work, put it back the way it was
		if (failed) {
			if (currentPiece != null) board.place(currentPiece, currentX, currentY);
			repaintPiece(currentPiece, currentX, currentY);
		}
		
		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),	then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		*/
		if (failed && verb==DOWN && !moved) {	// it's landed
		
			bestMoveComputed = false; //reset Brain for next piece
			
			int cleared = board.clearRows();
			if (cleared > 0) {
				// score goes up by 5, 10, 20, 40 for row clearing
				// clearing 4 gets you a beep!
				switch (cleared) {
					case 1: score += 5;	 break;
					case 2: score += 10;  break;
					case 3: score += 20;  break;
					case 4: score += 40; Toolkit.getDefaultToolkit().beep(); break;
					default: score += 50;  // could happen with non-standard pieces
				}
				updateCounters();
				repaint();	// repaint to show the result of the row clearing
			}
			
			
			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				stopGame();
			}
			// Otherwise add a new piece and keep playing
			else {
				addNewPiece();
			}
		}
		
		// Note if the player made a successful non-DOWN move --
		// used to detect if the piece has landed on the next tick()
		moved = (!failed && verb!=DOWN);
	}

	/**
	 * Move piece by 1 cell to left\right or makes 1 rotation.
	 */
	private void useBrainAdjustment() {
		//calculate best move with brain
		if(!bestMoveComputed) {
			brainMove = brain.bestMove(board, currentPiece,
					(board.getHeight() - 4), brainMove);
			bestMoveComputed = true;
		} 
		board.undo();
		
		if (bestMoveComputed && brainMove != null) {
			if (brainMove.piece != currentPiece) {
				currentPiece = currentPiece.fastRotation();
			} else if (brainMove.x > currentX ) {
				currentX++;
			} else if (brainMove.x < currentX){
				currentX--;
			} else if (brainAutoDropMode.isSelected()) {
				computeNewPosition(DROP);
				currentX = newX;
				currentY = newY;
			}
		}
	}
	
	/**
	 Tries to add a new random piece at the top of the board.
	 Ends the game if it's not possible.
	 
	 Modified for 'Adversary' system. 
	 Monitors the adversary slider and throw worst piece if possible.
	 Worst is determined by brain (reverse of best);	 
	*/
	@Override
	public void addNewPiece() {
		count++;
		score++;
		
		if (testMode && count == TEST_LIMIT+1) {
			 stopGame();
			 return;
		}

		// commit things the way they are
		board.commit();
		currentPiece = null;

		//Add worst piece if necessary 
		//adversary slider range is from 0 to 100
		Piece piece;
		int chance = random.nextInt(101);
		if (chance <= adversary.getValue()) {
			System.out.println("*ok*");
			piece = getWorstPiece();
		} else {
			System.out.println("ok");
			piece = pickNextPiece();
		}
		
		// Center it up at the top
		int px = (board.getWidth() - piece.getWidth())/2;
		int py = board.getHeight() - piece.getHeight();
		
		// add the new piece to be in play
		int result = setCurrent(piece, px, py);
		
		// This probably never happens, since
		// the blocks at the top allow space
		// for new pieces to at least be added.
		if (result>Board.PLACE_ROW_FILLED) {
			stopGame();
		}

		updateCounters();
	}
	
	/**
	 * calculate scores for every piece for current board state 
	 * and select highest (worst) 
	 * 
	 * Method is extremely successful. AI can't get through 70 pieces mark (one screen).
	 * I can't either. 
	 * Turns out that worst piece is S or reversed S. On 100% adversary method spams 
	 * with them in 80% of all drops.
	 */
	private Piece getWorstPiece() {
		double heighestScore = 0;
		int heighestScorePieceIndex = 0;
		for (int i = 0; i < pieces.length; i++) {
			//get score for current piece
			double score = (brain.bestMove(board, pieces[i],
					(board.getHeight()), brainMove)).score;
			
			if(score > heighestScore) {
				heighestScore = score;
				heighestScorePieceIndex = i;
			}
		}
		
		return pieces[heighestScorePieceIndex];
		
	}
	
	@Override
	public JComponent createControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// COUNT
		countLabel = new JLabel("0");
		panel.add(countLabel);
		
		// SCORE
		scoreLabel = new JLabel("0");
		panel.add(scoreLabel);
		
		// TIME 
		timeLabel = new JLabel(" ");
		panel.add(timeLabel);

		panel.add(Box.createVerticalStrut(12));
		
		// START button
		startButton = new JButton("Start");
		panel.add(startButton);
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		// STOP button
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		
		enableButtons();
		
		/*
		 * BRAIN
		 * use:
		 * boolean smthing = brainMode.isSelected();
		 */
		panel.add(new JLabel("Brain: "));
		brainMode = new JCheckBox("on/off");
		panel.add(brainMode);
		
		//brain autodrop
		panel.add(new JLabel("Autodrop: "));
		brainAutoDropMode = new JCheckBox("on/off");
		panel.add(brainAutoDropMode);
		
		
		JPanel row = new JPanel();
		
		// SPEED slider
		panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Speed:"));
		speed = new JSlider(0, 200, 75);	// min, max, current
		speed.setPreferredSize(new Dimension(100, 15));
		
		updateTimer();
		row.add(speed);
		
		panel.add(row);
		speed.addChangeListener( new ChangeListener() {
			// when the slider changes, sync the timer to its value
			public void stateChanged(ChangeEvent e) {
				updateTimer();
			}
		});
		
		
		JPanel row2 = new JPanel();
		
		// Adversary slider
//		panel.add(Box.createVerticalStrut(12));
		row2.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0); // min, max, current
		adversary.setPreferredSize(new Dimension(100, 15));
		row2.add(adversary);
		panel.add(row2);
		
		/*
		 * Used in startGame() like this:
		 * testMode = testButton.isSelected();
		 */
		testButton = new JCheckBox("Test sequence");
		panel.add(testButton);
		
		
		return panel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Set GUI Look And Feel Boilerplate.
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JTetris tetris = new JBrainTetris(16);
		JFrame frame = JTetris.createFrame(tetris);
		frame.setVisible(true);

	}

}
