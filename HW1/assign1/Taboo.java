/*
 HW1 Taboo problem class.
 Taboo encapsulates some rules about what objects
 may not follow other objects.
 (See handout).
*/
package assign1;

import java.util.*;


public class Taboo<T> {
	
	private Map<T, HashSet<T>> forbidens;
	
	/**
	 * Constructs a new Taboo using the given rules (see handout.)
	 * @param rules rules for new Taboo
	 */
	public Taboo(List<T> rules) {
		/* example:
		 * convert the List 'a', 'b', 'c', 'd', null, 'a', 'e'
		 * to format Map<T, HashSet<T>> 
		 * b -- [c] 
		 * c -- [d] 
		 * a -- [e, b]
		 */
		
		Map<T, HashSet<T>> forbidens = new HashMap<T, HashSet<T>>();
		
		Iterator<T> it = rules.iterator();
		
		T previousElem = null;
		while (it.hasNext()) {
			
			if (previousElem == null) {
				previousElem = it.next();
				continue;
			}
			
			T elem = it.next();
			
			if (elem == null) {
				previousElem = null;
				continue;
			}
			
			if (forbidens.containsKey(previousElem)) {				
				HashSet<T> forbidenSetForElem = forbidens.get(previousElem);
				forbidenSetForElem.add(elem);
			} else {
				forbidens.put(previousElem, new HashSet<T>(Arrays.asList(elem)));
			}
			
			previousElem = elem; //iterate
			
		}
		
		this.forbidens = forbidens;

//		/*for debug*/
//		for (T name : forbidens.keySet()) {
//			String key = name.toString();
//			String value = forbidens.get(name).toString();
//			System.out.println(key + " -- " + value);
//		}
		
	}
	
	/**
	 * Returns the set of elements which should not follow
	 * the given element.
	 * @param elem
	 * @return elements which should not follow the given element
	 */
	public Set<T> noFollow(T elem) {
		return forbidens.get(elem);
		//return null; // TODO YOUR CODE HERE
	}
	
	/**
	 * Removes elements from the given list that
	 * violate the rules (see handout).
	 * @param list collection to reduce
	 */
	public void reduce(List<T> list) {
		
		Iterator<T> it = list.iterator();
		
		T previousElem = null;
		while (it.hasNext()) {

			if (previousElem == null) {
				previousElem = it.next();
				continue;
			}

			T elem = it.next();
			HashSet<T> forbidenCharsForPrevious = forbidens.get(previousElem);

			if (forbidenCharsForPrevious != null
					&& forbidenCharsForPrevious.contains(elem)) {
				it.remove();
			}
			
			previousElem = elem; //iterate

		}
		
		/*for debug*/
//		for (T name : list) {
//			System.out.println(name.toString());
//		}
		
	}
}
