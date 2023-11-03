/**
 * 
 */

/**
 * @author s019343
 *
 */
public class Random {
	public static <T> T choice(T[] arr) {
		int num = (int)(Math.round(Math.random()*(arr.length-1)));
		return arr[num];
	}
}
