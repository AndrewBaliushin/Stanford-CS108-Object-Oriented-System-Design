// TabooTest.java
// Taboo class tests -- nothing provided.
package assign1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.junit.Test;


public class TabooTest {

	// TODO ADD TESTS
	
	@Test
	public void testNoFollow() {
		
		List<Character> rules = Arrays.asList('a', 'b', 'c', 'd', null, 'a', 'e');
		
		
		Taboo<Character> taboo = new Taboo<Character>(rules);
		
		char element1 = 'a';
		HashSet<Character> testSetResult = new HashSet<Character>(Arrays.asList('b', 'e'));		
		
		char element2 = 'b';
		HashSet<Character> testSetResult2 = new HashSet<Character>(Arrays.asList('c'));		
		
		
		assertTrue(testSetResult.containsAll(taboo.noFollow(element1)) &&
				taboo.noFollow(element1).containsAll(testSetResult));
		
		assertTrue(testSetResult2.containsAll(taboo.noFollow(element2)) &&
				taboo.noFollow(element2).containsAll(testSetResult2));
		
	}
	
	@Test
	public void testReduce() {

		List<Character> rules = Arrays.asList('a', 'b', 'c', 'd', null, 'a',
				'e');
		
		Taboo<Character> taboo = new Taboo<Character>(rules);
		
		List<Character> testList = new ArrayList<Character>();
		testList.add('a');
		testList.add('b');
		testList.add('a');
		testList.add('e');
		
		//will be
		List<Character> compareList = new ArrayList<Character>();
		compareList.add('a');
		compareList.add('a');
		
		taboo.reduce(testList);
		
		assertTrue(testList.containsAll(compareList) && compareList.containsAll(testList));
		
	}
	
}
