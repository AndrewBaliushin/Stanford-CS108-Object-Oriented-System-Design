package bank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class Bank {
	
	private static final String PATH_TO_DATA_FOLDER = "../testdata/";
	private static final int NUM_OF_ACCOUNTS = 20;
	private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000");
	
	private int numOfThreads;
	
	private CountDownLatch latch;
	
	private Map<Integer, Account> accountsMap = new HashMap<Integer, Account>();
	private BlockingQueue<Transaction> transactionsQueue = new LinkedBlockingQueue<Transaction>();
	
	private final Transaction nullTransaction = new Transaction(-1, 0, new BigDecimal("0"));
	
	/**
	 * example: java bank.Bank 100k.txt 5
	 * @param args[0]	name of file in default data folder;
	 * @param args[1]	number of threads to run transactions
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Wrong input format. Use: java bank.Bank fileName numThreads");
			System.exit(-1);
		}
		
		Bank bank = new Bank();
		bank.launch(args[0], args[1]);
	}
	
	public void launch(String fileName ,String numberOfThreads) {
		setNumOfThreads(numberOfThreads);
		createCountDownLatchForThreads();
		startWorkers();
		populateAccountsMap(NUM_OF_ACCOUNTS, DEFAULT_BALANCE);
		makeTransactionQueueFromFile(fileName);
		
		awaitForLatch();
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, Account> entry : accountsMap.entrySet())
		{
		    sb.append(entry.getValue().toString());
		    sb.append('\n');
		}
		return sb.toString();
	}
	
	private void makeTransactionQueueFromFile(String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(PATH_TO_DATA_FOLDER + fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			while ((line = br.readLine()) != null) {
				String[] transactionCommand = line.split("\\W+");
				
				int fromAcc = Integer.parseInt(transactionCommand[0]);
				int toAcc = Integer.parseInt(transactionCommand[1]);
				BigDecimal amount = new BigDecimal(transactionCommand[2]);
				
				transactionsQueue.put(new Transaction(fromAcc, toAcc, amount));
			}
			
			fstream.close();
			br.close();
			
			addStopMarkersToTransQuery();
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found in " + PATH_TO_DATA_FOLDER );
			System.exit(-1);
		} catch (InterruptedException | NumberFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void setNumOfThreads(String input) {
		try {
			int n = Integer.parseInt(input);
			numOfThreads = (n > 0) ? n : 1;
			echoNumberOfThreads();
		} catch (NumberFormatException e) {
			System.err.println("Wrong input format for number of threads. Use integers.");
			System.exit(-1);
		}
	}
	
	private void populateAccountsMap (int qty, BigDecimal starterBalance) {
		for (int i = 0; i < qty; i++) {
			accountsMap.put(i, new Account(i, starterBalance));
		}
	}
	
	private void createCountDownLatchForThreads() {
		latch = new CountDownLatch(numOfThreads);
	}
	
	private void awaitForLatch() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void addStopMarkersToTransQuery() {
		for (int i = 0; i < numOfThreads; i++) {
			try {
				transactionsQueue.put(nullTransaction);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void echoNumberOfThreads() {
		System.out.println("Number of threads is set to " + numOfThreads);
	}
	
	private class Worker extends Thread {
		
		@Override
		public void run() {
			int transactionCounter = 0;
			while(true) {
				try {
					Transaction t = transactionsQueue.take();
					
					if (t == nullTransaction) {
						break;
					}
					
					accountsMap.get(t.getFromAccount()).withdrawMoney(t.getAmount());
					accountsMap.get(t.getToAccount()).depositMoney(t.getAmount());
					
					transactionCounter++;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			System.out.println("Worker done after " + transactionCounter + " transactions.");
			latch.countDown();
		}
	}
	
	private void startWorkers() {
		for (int i = 0; i < numOfThreads; i++) {
			new Worker().start();
		}
	}

}
