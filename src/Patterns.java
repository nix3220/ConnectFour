import java.util.*;

/**
 * @author s019343
 * TODO: make this shit
 */
public class Patterns {
	/*
	 * This map holds the binary values for the patterns of four
	 * Use this to get the score for the pattern you are looking at
	 */
	@SuppressWarnings("serial")
	private static Map<Integer, Integer> patterns = new HashMap<Integer, Integer>() 
	{
		{
			//** m = mine, _ = empty
			//8421
			
			//mmmm = 15
			put(15, 100);
			
			//mmm_ = 14
			put(14, 40);
			//mm_m = 13
			put(13, 30);
			//m_mm = 11
			put(11, 30);
			//_mmm = 7
			put(7, 40);
			
			//mm__ = 12
			put(12, 20);
			//m_m_ = 10
			put(10, 10);
			//__mm = 3
			put(3, 20);
			//m__m = 9
			put(9, 5);
			//_m_m = 5
			put(5, 10);
			//_mm_ = 6
			put(6, 20);
			
			//16 8 4 2
			//oooo = 30
			put(30, -100);
			
			//ooom = 29
			put(29, 100);
			//oomo = 13
			put(28, 100);
			//omoo = 26
			put(26, 100);
			//mooo = 22
			put(22, 100);
			
			//mmmo = 16
			put(16, -100);
			//mmom = 17
			put(17, -100);
			//momm = 11
			put(19, -100);
			//ommm = 23
			put(23, -100);
			
//			//mm__ = 12
//			put(12, 20);
//			//m_m_ = 10
//			put(10, 10);
//			//__mm = 3
//			put(3, 20);
//			//m__m = 9
//			put(9, 5);
//			//_m_m = 5
//			put(5, 10);
//			//_mm_ = 6
//			put(6, 20);
			
			//____
			put(0, 0);
		}
	};
	
	public static int getScore(int value) {
		if(patterns.get(value) != null) {
			return patterns.get(value);
		}
		else {
			return 0;
		}
	}
	
	public static int win() {
		return 15;
	}
}
