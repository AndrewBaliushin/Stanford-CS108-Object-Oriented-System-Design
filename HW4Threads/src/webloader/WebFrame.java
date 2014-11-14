package webloader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WebFrame extends JFrame {

	public static final String APP_TITLE = "Metropolis Viewer";
	private static final String FILE_PATH = "testdata/links.txt";
	private static final String DEFAULT_NUM_THREADS = "4";
	
	private static final String RUNNING_LABEL_TEXT = "Running: ";
	private static final String COMPLETED_LABEL_TEXT = "Completed: ";
	private static final String ELAPSED_LABEL_TEXT = "Elapsed: ";
		
	private static List<String> urlList = new ArrayList<String>();
	
	private Container pane;
	private DefaultTableModel model;
	private JTable table;
	private JButton singleFetchButton;
	private JButton concurentFetchButton;
	private JTextField inputThreadNums;
	private JLabel runningThreadsLabel;
	private JLabel comletedThreadsLabel;
	private JLabel elapsedLabel;
	private JProgressBar progressBar;

	public WebFrame() {
		super(APP_TITLE);
		
		urlList = getURLlistFromFile(FILE_PATH);
		
		pane = this.getContentPane();
		setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		createTable(pane);
		createButtons(pane);
		createInputField(pane);
		createStatusLabels(pane);
		createProgressBar(pane);
		
		pack();
		setVisible(true);
	}
	
	private void createTable(Container pane) {
		model = new DefaultTableModel(new String[] { "url", "status"}, 0);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600, 300));
		pane.add(scrollpane);
	}
	
	private void createButtons(Container pane) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		singleFetchButton = new JButton("Single Thread Fetch");
		panel.add(singleFetchButton);
		
		concurentFetchButton = new JButton("Concurent Fetch");
		panel.add(concurentFetchButton);
		
		pane.add(panel);
	}
	
	private void createInputField(Container pane) {
		inputThreadNums = new JTextField(5);
		inputThreadNums.setMaximumSize( new Dimension( 200, 24 ) );
		inputThreadNums.setText(DEFAULT_NUM_THREADS);
		pane.add(inputThreadNums);
	}
	
	private void createStatusLabels(Container pane) {
		runningThreadsLabel = new JLabel(RUNNING_LABEL_TEXT + "0");
		comletedThreadsLabel = new JLabel(COMPLETED_LABEL_TEXT + "0");
		elapsedLabel = new JLabel(ELAPSED_LABEL_TEXT + "0");
		
		pane.add(runningThreadsLabel);
		pane.add(comletedThreadsLabel);
		pane.add(elapsedLabel);
	}
	
	private void createProgressBar(Container pane) {
		progressBar = new JProgressBar(0, 10);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		pane.add(progressBar);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebFrame frame = new WebFrame();

	}

	static List<String> getURLlistFromFile(String filePath) {
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			while ((line = br.readLine()) != null) {
				urlList.add(line);
			}
			
			fstream.close();
			br.close();			
		} catch (FileNotFoundException e) {
			System.err.println("File not found:" + FILE_PATH);
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return urlList;
	}
	

}
