package webloader;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;


public class WebWorker extends Thread {
	
	private final static String ERR_MSG = "err";
	private final static String INTERUPTED_MSG = "interupted";
	
	private WebFrame.Launcher launcher;
	
	private WebFrame.URLtoDownload urlToDowload;
	
	public WebWorker(WebFrame.URLtoDownload url, WebFrame.Launcher launcher) {
		this.launcher  = launcher;
		this.urlToDowload = url;
	}
	
	@Override
	public void run() {
		launcher.startThread();
		launcher.updateInfoInGUI();
		download();
		launcher.stopThread();
		launcher.completeThread();
		launcher.updateInfoInGUI();
		launcher.updateDataInGUI();
		launcher.semaphore.release();
	}
	
	private void download() {
		InputStream input = null;
		StringBuilder contents = null;
		try {
			long startTime = System.currentTimeMillis();
			
			URL url = new URL(urlToDowload.getAdress());
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			
			long endTime = System.currentTimeMillis();
			
			String resultMsg = new SimpleDateFormat("HH:mm:ss").format(new Date(startTime))
                    + "   " + (endTime - startTime)
                    + "ms   " + array.length + "bytes";
			
			urlToDowload.setStatusMessage(resultMsg);
			
		}
		// Otherwise control jumps to a catch...
		catch(IOException ignored) {
			urlToDowload.setStatusMessage(ERR_MSG);
		}
		catch(InterruptedException exception) {
			urlToDowload.setStatusMessage(INTERUPTED_MSG);
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}
	}
	
	
}
