/**
 * 
 */

/**
 * @author s019343
 *
 */
public class Helpers {
	
	/*
	 * TODO: fix this shit
	 * dont have a fucking clue why it doesnt work
	 * should just clamp a value in a range but nooooo, why would it do what its supposed to
	 * anyway when i tried to use it it filled the board with red pieces for no fucking reason
	 */
	public static double Clamp(double value, double min, double max) {
		if(value < min) {
			return min;
		}
		else if(value > max) {
			return max;
		}
		return value;
	}
	
	//read the method name
	public static double Clamp01(double value) {
		return Clamp(value, 0, 1);
	}
	
	//not much explanation needed
	//if you want it to work though MAKE SURE T HAS A TOSTRING
	//otherwise it prints basic class Class@AB6sb shit
	public static <T> void printArray(T[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " +arr[i].toString());
		}
	}
}
