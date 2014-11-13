package counter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class JCount extends JPanel{
	
	private static final int NUM_THREADS = 4;
	private static final int COUNT_TO = 1000000000;
	private static final int REFRESH_INTERVAL = 10;

	JLabel currentCountLabel;
	JTextField countToField;
	JButton startButton;
	JButton stopButton;
	
	private static List<JCount> listOfCountPanels = new ArrayList<JCount>();
	
	private int countTo = COUNT_TO;
	private int currentCount = 0;
	
	private Worker worker;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}
		JFrame frame = createFrame();
	}
	
	private static JFrame createFrame() {
		JFrame frame = new JFrame("Counter");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		for (int i = 0; i < NUM_THREADS; i++) {
			JCount counterPanel = new JCount();
			listOfCountPanels.add(counterPanel);
			frame.add(counterPanel);
			frame.add(Box.createRigidArea(new Dimension(0,40)));
		}
		
		frame.setVisible(true);
		frame.pack();
		return frame;
	}
	
	public JCount() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		countToField = new JTextField();
		countToField.setText(Integer.toString(countTo));
		add(countToField);
		
		currentCountLabel = new JLabel(Integer.toString(currentCount));
		add(currentCountLabel);
		
		startButton = new JButton("Start");
		add(startButton);
		
		stopButton = new JButton("Stop");
		add(stopButton);
		
		addListners();		
	}
	
	private void addListners() {
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				readToInput();
				startWoker();
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stopWorker();
			}
		});
	}
	
	private void readToInput() {
		countTo = Integer.parseInt(countToField.getText());
	}
	
	private void startWoker() {
		currentCount = 0;
		
		if (worker == null) {
			worker = new Worker();
		} else if (!worker.isInterrupted()) {
			worker.interrupt();
			worker = new Worker();
		}
		
		worker.start();
	}
	
	private void stopWorker() {
		if (worker != null && !worker.isInterrupted()) {
			worker.interrupt();
		} 
	}
	
	private class Worker extends Thread {
		@Override
		public void run() {
			int countCycles = 0;
			while (currentCount < countTo) {
				countCycles++;
				currentCount++;
				if (countCycles >= REFRESH_INTERVAL) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							currentCountLabel.setText(Integer.toString(currentCount));
						}
					});
					countCycles = 0;
				}
				
				try {
					sleep(1);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}	
	
}
