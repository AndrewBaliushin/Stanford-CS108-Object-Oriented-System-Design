package assign1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// CS108 HW1 -- String static methods

public class StringCode {

	/**
	 * Given a string, returns the length of the largest run.
	 * A a run is a series of adajcent chars that are the same.
	 * @param str
	 * @return max run length
	 */
	public static int maxRun(String str) {

		int startPos = 0;
		int currentPos = 0;
		
		int run = 0;
		int maxRun = 0;
		
		while (currentPos < str.length()) {
			if (str.charAt(startPos) == str.charAt(currentPos)) {
				run++;
				currentPos++;
				maxRun = (run > maxRun) ? run : maxRun;
			} else {
				maxRun = (run > maxRun) ? run : maxRun;
				run = 0;
				startPos = currentPos;
			}
		}

		return maxRun; // YOUR CODE HERE

	}

	
	/**
	 * Given a string, for each digit in the original string, replaces the digit
	 * with that many occurrences of the character following. So the string
	 * "a3tx2z" yields "attttxzzz".
	 * 
	 * @param str
	 * @return blown up string
	 */
	public static String blowup(String str) {

		Pattern pt = Pattern.compile("\\d(?=(.|$))");
		Matcher matcher = pt.matcher(str);

		Map<Integer, String> replMap = new TreeMap<Integer, String>();

		while (matcher.find()) {
			int n = Integer.parseInt(matcher.group(0));

			String character = matcher.group(1);

			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < n; i++) {
				sp.append(character);
			}
			String replacement = sp.toString();

			if (matcher.start() == (str.length() - 1)) {
				replMap.put(matcher.start(), ""); //if string ends with digit
			} else {
				replMap.put(matcher.start(), replacement);
			}

		}

		// string builder
		StringBuilder spFinal = new StringBuilder();

		int lastPosition = 0;
		for (Map.Entry<Integer, String> entry : replMap.entrySet()) {

			spFinal.append(str.substring(lastPosition, entry.getKey()));
			spFinal.append(entry.getValue());
			lastPosition = entry.getKey() + 1;

		}

		if (str.length() >= lastPosition) {
			spFinal.append(str.substring(lastPosition));
		}

		String out = spFinal.toString();

		// System.out.println(out);

		return out; // TODO ADD YOUR CODE HERE
	}
	
	/**
	 * Given 2 strings, consider all the substrings within them
	 * of length len. Returns true if there are any such substrings
	 * which appear in both strings.
	 * Compute this in linear time using a HashSet. Len will be 1 or more.
	 */
	public static boolean stringIntersect(String a, String b, int len) {
		return false; // TO DO ADD YOUR CODE HERE
	}
}
