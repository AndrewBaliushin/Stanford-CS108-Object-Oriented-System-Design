package cracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();	
	
	//default hash and length for password ==  "fm"
	public static final String DEFAULT_HASH_TO_CRACK = "adeb6f2a18fe33af368d91b09587b68e3abcb9a7";
	public static final String DEFAULT_PASS_LENGTH = "2";
	public static final String DEFAULT_NUM_THREADS = "10";
	
	private String hashToCrack;
	private int passLength;
	private int numOfThreads;
	private CountDownLatch latch;
	
	private List<String> crackedPasses = new ArrayList<String>();
	
	public static void main(String[] args) {
		if (args.length != 3 && args.length != 1) {
			System.err.println("Wrong input format.");
			System.err.println("Use java cracker.Cracker [toHash]");
			System.err.println("OR");
			System.err.println("Use java cracker.Cracker [hash] [passLength] [numOfThreads]");

			System.err.println("Laaunching cracker with defaults args.");
			
			Cracker cracker = new Cracker();
			cracker.launcher(DEFAULT_HASH_TO_CRACK, DEFAULT_PASS_LENGTH, DEFAULT_NUM_THREADS);			
		}
		
		if (args.length == 1) {
			System.out.println(convertToHash(args[0]));
		}
		
		if (args.length == 3) {
			Cracker cracker = new Cracker();
			cracker.launcher(args[0], args[1], args[2]);
		}
	}
	
	private static String convertToHash(String toHash) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(toHash.getBytes());
			return hexToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void launcher(String hash, String passLength, String numberOfThreads) {
		setHash(hash);
		setPassLength(passLength);
		setNumOfThreads(numberOfThreads);
		createCountDownLatchForThreads();
		startWorkers();
		
		awaitForLatch();
		System.out.println("Possible passwords: " + crackedPasses);
	}
	
	private void setHash(String hash) {
		hashToCrack = hash;		
	}

	private void setPassLength(String passLength) {
		try {
			int n = Integer.parseInt(passLength);
			this.passLength = n;
		} catch (NumberFormatException e) {
			System.err.println("Wrong input format for pass length. Use integers.");
			System.exit(-1);
		}
	}

	private void setNumOfThreads(String input) {
		try {
			int n = Integer.parseInt(input);
			numOfThreads = (n > 0) ? n : 1;
		} catch (NumberFormatException e) {
			System.err.println("Wrong input format for number of threads. Use integers.");
			System.exit(-1);
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
	
	private void startWorkers() {		
		//distribute first chars between workers
		if (numOfThreads > CHARS.length) {
			numOfThreads = CHARS.length;
		}
		int segmentLength = CHARS.length / numOfThreads;
		int startPoint = 0;
		for (int i = 0; i < numOfThreads; i++) {
			char[] segment;
			if (i != (numOfThreads - 1)) {
				segment = Arrays.copyOfRange(CHARS, startPoint, (startPoint 
						+ segmentLength));
				startPoint += segmentLength;
			} else {
				segment = Arrays.copyOfRange(CHARS, startPoint, CHARS.length);
			}
			
			new Worker(segment).start();
		}
	}
	
	private class Worker extends Thread {
		
		private char[] firstChars;
		
		Worker(char[] firstChars) {
			this.firstChars = firstChars;
		}
		
		@Override
		public void run() {
			String threadName = Thread .currentThread().getName();
			System.out.println(threadName + " is using this chars: \n" + Arrays.toString(firstChars));
			
			for (int i = 0; i < firstChars.length; i++) {
				recursivePassExaminator(Character.toString(firstChars[i]));
			}
			
			latch.countDown();
		}
		
		private void recursivePassExaminator(String s) {
			if (s.length() == passLength) {
				if (isCorrectPass(s)) {
					crackedPasses.add(s);
				}
			} else {
				for (int i = 0; i < CHARS.length; i++) {
					recursivePassExaminator(s + CHARS[i]);
				}
			}
		}
		
		private boolean isCorrectPass(String inputString) {
			String inputHash = convertToHash(inputString);
			return (inputHash.equals(hashToCrack));
		}
	}
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	// possible test values:
	// a 86f7e437faa5a7fce15d1ddcb9eaeaea377667b8
	// fm adeb6f2a18fe33af368d91b09587b68e3abcb9a7
	// a! 34800e15707fae815d7c90d49de44aca97e2d759
	// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
}
