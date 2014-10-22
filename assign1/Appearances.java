package assign1;

import java.util.*;

class InputDataApearances {
	static Collection<String> inputCollectionA =
            new ArrayList<String>(Arrays.asList("a", "b", "a", "b", "c"));

    static Collection<String> inputCollectionB =
            new ArrayList<String>(Arrays.asList("c", "a", "a", "d", "b", "b", "b"));	
}

class Appearances {

    private static int sameCountCounter = 0;

    /**
	 * Returns the number of elements that appear the same number
	 * of times in both collections. Static method. (see handout).
	 * @return number of same-appearance elements
	 */
    public static <T> int sameCount( Collection<T> a, Collection<T> b ) {

        Map<T, Integer> countMapA = getCountMap(a);
        Map<T, Integer> countMapB = getCountMap(b);

        Map<T, Integer> smallerMap = new HashMap<T, Integer>();
        Map<T, Integer> biggerMap = new HashMap<T, Integer>();
        if (countMapA.size() <= countMapB.size()) {
            smallerMap = countMapA;
            biggerMap = countMapB;
        } else {
            smallerMap = countMapB;
            biggerMap = countMapA;
        }

        for (Map.Entry<T, Integer> entry : smallerMap.entrySet()) {
            if ( biggerMap.get(entry.getKey()) == entry.getValue() ) {
                System.out.println(entry.getKey() + " appeared in both collections " + entry.getValue()
                        + " times");
                sameCountCounter++;
            }
        }

        return sameCountCounter;
    }

    private static <T> Map<T, Integer> getCountMap(Collection<T> col) {
        Map<T, Integer> counterMap = new HashMap<T, Integer>();
        for (T elementFromCol : col)  {
            int count = ( counterMap.get(elementFromCol) == null ) ? 0 : counterMap.get(elementFromCol);
            counterMap.put(elementFromCol, ++count);
        }
        return counterMap;
    }
    
    public static void  main(String[] args) {

    	
        int countAppears = sameCount(InputDataApearances.inputCollectionA, InputDataApearances.inputCollectionB);
        System.out.println(countAppears + " entries appeared in both collections equal amount of time");

    }
}
/*
out:
a appeared in both collections 2 times
c appeared in both collections 1 times
2 entries appeared in both collections equal amount of time
 */