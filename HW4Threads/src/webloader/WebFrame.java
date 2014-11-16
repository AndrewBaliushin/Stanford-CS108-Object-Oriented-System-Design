package webloader;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class WebFrame extends JFrame {

	public static final String APP_TITLE = "Metropolis Viewer";
	private static final String FILE_PATH = "testdata/links.txt";
	private static final String DEFAULT_NUM_THREADS = "4";
	
	private static final String RUNNING_LABEL_TEXT = "Running: ";
	private static final String COMPLETED_LABEL_TEXT = "Completed: ";
	private static final String ELAPSED_LABEL_TEXT = "Elapsed: ";
	
	private List<URLtoDownload> URLs = new ArrayList<URLtoDownload>();
	private Launcher launcher;
	
	private Container pane;
	private LoaderTableModel model;
	private JTable table;
	private JButton singleFetchButton;
	private JButton concurentFetchButton;
	private JButton stopButton;
	private JTextField inputThreadNums;
	private JLabel runningThreadsLabel;
	private JLabel comletedThreadsLabel;
	private JLabel elapsedLabel;
	private JProgressBar progressBar;
	
	
	public WebFrame() {
		super(APP_TITLE);
		
		pane = this.getContentPane();
		setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		createURLs(getURLlistFromFile(FILE_PATH));
		createTable(pane);
		
		createButtons(pane);
		createInputField(pane);
		createStatusLabels(pane);
		createProgressBar(pane);
		
		addListners();
		
		pack();
		setVisible(true);
	}
	
	private void createTable(Container pane) {
		model = new LoaderTableModel(URLs);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600, 300));
		pane.add(scrollpane);
	}
	
	private void createURLs(List<String> urlList) {
		for (String adress : urlList){
			URLs.add(new URLtoDownload(adress));
		}
	}
	
	private void createButtons(Container pane) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		singleFetchButton = new JButton("Single Thread Fetch");
		panel.add(singleFetchButton);
		
		concurentFetchButton = new JButton("Concurent Fetch");
		panel.add(concurentFetchButton);
		
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		
		pane.add(panel);
	}

	private void createInputField(Container pane) {
		inputThreadNums = new JTextField(5);
		inputThreadNums.setMaximumSize( new Dimension( 200, 24 ) );
		inputThreadNums.setText(DEFAULT_NUM_THREADS);
		pane.add(inputThreadNums);
	}
	
	private int getInputFieldInt() {
		try {
			return Integer.parseInt(inputThreadNums.getText());
		} catch (NumberFormatException e) {
			inputThreadNums.setText("Wrong format. Start with 1 thread");
			return 1;
		}
		
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
		progressBar = new JProgressBar(0, URLs.size());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		pane.add(progressBar);
	}
	
	private void addListners() {
		singleFetchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launcher = new Launcher(1);
				launcher.start();
			}
		});
		
		concurentFetchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launcher = new Launcher(getInputFieldInt());
				launcher.start();
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (launcher !=null) {
					launcher.interrupt();
				}
			}
		});
		
	}
	
	class Launcher extends Thread{
		
		private int numThreads;
		
		private List<WebWorker> workersList = new ArrayList<WebWorker>();
		
		private long startTime;
		private Thread timeRefresher;
		private AtomicInteger threadsRunning = new AtomicInteger(0);
		private AtomicInteger threadsComplete = new AtomicInteger(0);
		public Semaphore semaphore;
		
		public Launcher(int numThreads) {
			this.numThreads = numThreads;
		}

		@Override
		public void run() {
			semaphore = new Semaphore(numThreads);
			startTime = System.currentTimeMillis();
			threadsRunning.incrementAndGet();
			startTimerRefresher();
			
			for (URLtoDownload urlObj : URLs) {
				try {
					semaphore.acquire();
					WebWorker worker = new WebWorker(urlObj, this);
					workersList.add(worker);
					worker.start();
				} catch (InterruptedException e) {
					killWorkers();
					break;
				}
			}
			
			threadsRunning.decrementAndGet();
			//stopTimeRefrasher();
		}
		
		public void startThread() {
			threadsRunning.incrementAndGet();
		}
		
		public void stopThread() {
			threadsRunning.decrementAndGet();
		}
		
		public void completeThread() {
			threadsComplete.incrementAndGet();
		}
		
		public void updateDataInGUI() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					model.fireTableDataChanged();
				}
			});
		}
		
		public void startTimerRefresher() {
			timeRefresher = new Thread() {
				@Override
				public void run() {
					while(threadsRunning.get() > 0){
						try {
							sleep(35);
						} catch (InterruptedException e) {
							break;
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {								
								elapsedLabel.setText(ELAPSED_LABEL_TEXT + 
										(System.currentTimeMillis() - startTime) + "ms");
								
							}
						});
					}
					
				}
			};
			timeRefresher.start();
		}
		
		public void stopTimeRefrasher() {
			timeRefresher.interrupt();
		}
		
		public void updateInfoInGUI() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					runningThreadsLabel.setText(RUNNING_LABEL_TEXT + 
							Integer.toString(threadsRunning.get()));
					
					comletedThreadsLabel.setText(COMPLETED_LABEL_TEXT + 
							Integer.toString(threadsComplete.get()));
					
					progressBar.setValue(threadsComplete.get());					
				}
			});
		}
		
		public void killWorkers() {
			for (WebWorker webWorker : workersList) {
				webWorker.interrupt();
				updateDataInGUI();
				updateInfoInGUI();
			}
		}
		
		
	}
	
	class URLtoDownload {
		
		private String adress;
		private String statusMessage;

		public URLtoDownload(String adress) {
			this.adress = adress;
		}

		public String getAdress() {
			return adress;
		}
		public String getStatusMessage() {
			return statusMessage;
		}

		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebFrame frame = new WebFrame();

	}

	static List<String> getURLlistFromFile(String filePath) {
		List<String> urlList = new ArrayList<String>();
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
