package webloader;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class WebFrameTest {

	private String fileName = "testdata/links.txt";
	
	@Test
	public void testGetUrl() {
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		List<String> urlList = WebWorker.getURLlistFromFile(fileName);
		//System.out.println(urlList);
		assertEquals("http://www.stanford.edu/class/cs108/foo.txt", urlList.get(0));
	}

}
