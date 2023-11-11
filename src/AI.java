/**
 * 
 */

/**
 * @author s019343
 *
 */

import java.util.*;

/*
 * kinda a helper class idk it is super useful because for some reason either java doesn't have a Tuple class or it's hidden somhow
 * this class represents a pair of two objects, generically typed
 * no need for first and second to be reassigned yet so for now they stay final
 */
class Pair<X, Y> { 
  public final X first; 
  public final Y second; 
  public Pair(X first, Y second) { 
    this.first = first; 
    this.second = second; 
  } 
  
  @Override
  public String toString() {
	  return "(1: " + first.toString() + ", 2: " + second.toString() + ")";
  }
} 

public class AI {
	
	//THIS CODE WAS NOT NECESARy BUT IM KEEPING HERE CUZ WHY THE FUCK NOT
	//Evaluate function overloaded for each specific move
	//Then call evaluate with the newly created board based off of the given move
//	public static int evaluate(Board board, Piece piece) {
//		Board eval = new Board(board.getValues());
//		eval.place(piece);
//		return evaluate(eval);
//	}
	
	//constants for seeing whose turn it is
	public static final int AI_PLAYER = ConnectFourPanel.AI_PLAYER;
	public static final int HUMAN_PLAYER = ConnectFourPanel.HUMAN_PLAYER;
	
	//returns the index in a list of integers that has the highest value
	public static int indexOfHighest(List<Integer> list) {
		int index = 0;
		int best = list.get(0);
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) > best) {
				best = list.get(i);
				index = i;
			}
		}
		return index;
	}
	
	/*
	 * returns the column that the ai evaluates to be the best
	 * uses the minimax algorithm to determine the best column
	 * for each available placement position it runs the minimax algorithm to find the score of that move
	 * it then returns the column of the position that got the highest score
	 */
	public static int evaluate(Board b) {
		List<Piece> pieces = b.validPlacingPositions();
		List<Pair<Integer, Integer>> evaluations = new ArrayList<Pair<Integer, Integer>>();
		for(Piece p : pieces) {
			p.setPlayer(AI_PLAYER);
			Board newboard = copy(b);
			newboard.place(p);
			int depth = 6;
			ConnectFourPanel.numChecks = (int)Math.pow(7, depth);
			ConnectFourPanel.numCheckCompl = 0;
			evaluations.add(new Pair<Integer, Integer>(minimax(copy(newboard), depth, false, -Integer.MAX_VALUE, Integer.MAX_VALUE).first, p.getCol()));
		}
		Helpers.printArray(evaluations.toArray());
		int index = indexOfHighest(listOfFirst(evaluations));
		return evaluations.get(index).second;
	}
	
	//Takes a list of pairs or integers and returns a list of the first items in those pairs
	public static List<Integer> listOfFirst(List<Pair<Integer, Integer>> pairs){
		List<Integer> list = new ArrayList<Integer>();
		for(Pair<Integer, Integer> pair : pairs) {
			list.add(pair.first);
		}
		return list;
	}
	
	//Takes a list of pairs or integers and returns a list of the second items in those pairs
	public static List<Integer> listOfSecond(List<Pair<Integer, Integer>> pairs){
		List<Integer> list = new ArrayList<Integer>();
		for(Pair<Integer, Integer> pair : pairs) {
			list.add(pair.second);
		}
		return list;
	}
	
	/*
	 * Scores each valid placement position using the following system:
	 * 
	 * (HVD: horizontal, vertical, diagonal)
	 * (_: empty)
	 * (R: red)
	 * (B: blue)
	 * (e: either, doesn't matter which color)
	 * Points out of 100 in total
	 * Condition list for how many points we should get for each type of move
	 * 3 in a row HV where one is open on either side, pretty good other player most likely counter if possible
	 * 3 in a row HV where one is open on EACH side, very good, instant win
	 * 3 in a row diagonal is generally good but need to know whether or not the spaces below are built up enough to support the next diagonal move
	 * 2 in a row in any direction is generally a good step so scored decently but its never going to give you the win UNLESS there is an empty space and then another piece
	 * i.e) R RR or RR R this is good because it sets up a win condition
	 * 
	 * HORIZONTAL:
	 * _RRR
	 * R_RR
	 * RR_R
	 * RRR_
	 * 
	 * VERTICAL:
	 * _
	 * R
	 * R
	 * R
	 * 
	 * DIAGONAL:
	 * ____
	 * __Re
	 * _Ree
	 * Reee
	 * ___R
	 * ___e
	 * _Ree
	 * Reee
	 * 
	 * ___R
	 * __Re
	 * __ee
	 * Reee
	 * 
	 * ___R
	 * __Re
	 * _Ree
	 * _eee
	 * 
	 * HOLY GRAIL: 
	 * ___R
	 * ___e
	 * RR_R
	 * Re_e
	 * 
	 * Returns all the scores added together to get a general score for the simulated board state
	*/
	public static int evaluatePositions(Board board, int player) {
		List<Piece> positions = board.validPlacingPositions();
		int result = 0;
		for(Piece p : positions) {
			int score = (scorePosition(copy(board), p, player));
			result += score;
		}
		return result;
	}
	
	/*
	 * finds the binary pattern for the set of four pieces and scores in by the lookup table in Patterns
	 * before doing that, it places the given piece on the simulated board in order to properly score it
	 * it does this process for horizontal, vertical, and diagonal
	 */
	public static int scorePosition(Board board, Piece piece, int player) {
		board.place(piece);
		int score = 0;
		for(int i = 0; i < EvalDirection.values().length; i++) {
			//System.out.println(EvalDirection.values()[i]);
			Piece[] pieces = board.getPreviousFour(EvalDirection.values()[i], piece);
//			if(pieces != null) {
//				Helpers.printArray(pieces);
//				System.out.println();
//			}
			if(pieces != null) {
				int patternBinary = value(pieces, player);
				int patternValue = Patterns.getScore(patternBinary);
				score += patternValue;
			}
			else {
				score += 0;
			}
		}
		return score;
	}
	
	/*
	 * Goes through with my matching algorithm to check if anyone has won yet
	 * It basically just looks for the pattern '15' which is mmmm meaning winner
	 * it checks for both players and returns whether or not somebody won and who it was if they did
	 * it is a little clunky but honestly it's not too bad
	 */
	public static Pair<Boolean, Piece> isWinningBoard(Board b) {
		boolean foundWin = false;
		Piece playerWon = null;
		for (int i = 0; i < b.numRows(); i++) {
			for (int j = 0; j < b.numCols(); j++) {
				for(int k = 0; k < EvalDirection.values().length; k++) {
					//System.out.println(EvalDirection.values()[i]);
					Piece[] pieces = b.getPreviousFour(EvalDirection.values()[k], b.get(i, j));
//					if(pieces != null) {
//						Helpers.printArray(pieces);
//						System.out.println();
//					}
					if(pieces != null) {
						for(int l = 0; l < 2; l++) {
							int player = (l == 0 ? -1 : 1); 
							int patternBinary = value(pieces, player);
							if(patternBinary == Patterns.win()) {
								foundWin = true;
								playerWon = b.get(i, j);
							}
						}
					}
				}
			}
		}
		return new Pair<Boolean, Piece>(foundWin, playerWon);
	}
	
	//checks if every space in the board has been filled
	//not likely to happen
	public static boolean noSpacesLeft(Board board) {
		for (int i = 0; i < board.numRows(); i++) {
			for (int j = 0; j < board.numCols(); j++) {
				if(board.get(i, j).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	//checks if a board is a "terminal node" meaning it is at the end of a branch
	public static boolean isTerminalNode(Board board) {
		return isWinningBoard(board).first || noSpacesLeft(board);
	}
	
	/*
	 * m = my piece
	 * _ = empty
	 * this is the big part of evaluation process
	 * it turns the four pieces into a semi-binary number based on whether or not a piece is yours
	 * m = 1
	 * empty = 0
	 * other = 2
	 * 
	 * it keeps track of the other players pieces in order to incentives blocking the human player
	 * i.e) 8420 = 16  8021 = 11  8401 = 13
	 *      mmm_ = 16, m_mm = 11, mm_m = 13
	 *      
	 * multiplies the 8 4 2 or 1 by 2 if its the other player
	 * i.e) 16 8 2 2 = 28
	 *      o-o-m-o = 28
	 * 
	 * these can then be used in a LUT to match with a pattern on the board so that you know what pattern you found
	 * if that pattern is helpful in any way you can score it!
	 * hopefully this makes sense
	 */
	public static int value(Piece[] pieces, int player) {
		int value = 0;
		int[] binaryCor = {8, 4, 2, 1};
		for (int i = 0; i < pieces.length; i++) {
			value += binaryCor[i]*(value(pieces[i], player));
//			if(value(pieces[i], player) == 1) {
//				
//			}
		}
		return value;
	}
	
	//returns 1 if the piece is ours
	//0 if empty
	//2 if other player
	//this should work i think
	//simple but crucial to the algorithm
	public static int value(Piece piece, int player) {
		if(piece.isEmpty()) {
			return 0;
		}
		if(piece.getPlayer() == player) {
			return 1;
		}
		else {
			return 2;
		}
	}
	
	/*
	 * does an abstract copy of a board
	 * i was having problems with reference errors earlier so this is my solution
	 * it makes a full new object for each piece in order to eliminate any unrecognized sticky pointers
	 */
	public static Board copy(Piece[][] arr) {
		Piece[][] pieces = new Piece[6][7];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				pieces[i][j] = new Piece(arr[i][j].getRow(), arr[i][j].getCol(), arr[i][j].getPlayer());
			}
		}
		return new Board(pieces);
	}
	
	//overload
	public static Board copy(Board b) {
		return copy(b.getValues());
	}
	
	//returns the actual piece available at a column, not sure if i'm going to need this but you never know
	public static Piece availablePiece(Board b, int col) {
		return b.get(b.availableIndex(col), col);
	}
	
	/*
	 * TIME COMPLEXITY
	 * O(b^m)
	 * where b is the number of branches and m is the maximum depth
	 * the ai can get really good at high depths but holy fuck is it slow
	 * anything past 6 depth takes a really long time per turn
	 * 
	 * ALGORITHM:
	 * This is the real meat and potatoes
	 * the minimax algorithm is arguably the most important part of this ai
	 * i did the research from a bunch of places
	 * this one python implementation was super helpful in reading through and understanding how it worked so i could implement it into my program
	 * 
	 * HOW IT WORKS:
	 * It gets each valid column on the board
	 * it then makes sure we haven't reached the end
	 * if we have, we check if anyone has won in this simulated board
	 * otherwise we return the score of the current simulated board state
	 * it then does the minimax
	 * loop through the list of valid positions, and score it recursively using the minimax function
	 * for the maximizer, we find the greatest score, and for the minimizer the smallest
	 * we then return the score and the column we are on
	 * 
	 * ALPHA-BETA PRUNING:
	 * I understand it like 70%
	 * it uses two values, alpha and beta to keep track of the maximizer and minimizers values
	 * these values are used to prune unneccesary leaves from the generated tree
	 * it doesnt change the output of the minimax function, but if alpha < beta it exits the current leaf
	 */
	public static Pair<Integer, Integer> minimax(Board board, int depth, boolean max, int alpha, int beta) {
		List<Piece> valid = board.validPlacingPositions();
		int value = 0;
		ConnectFourPanel.numCheckCompl++;
		if(depth == 0 || isTerminalNode(board)) {
			Pair<Boolean, Piece> winning = isWinningBoard(board);
			if(winning.first) {
				//System.out.println("winner: " + winning.second.getPlayer());
				if (winning.second.getPlayer() == HUMAN_PLAYER){
					return new Pair<Integer, Integer>(-Integer.MAX_VALUE, null);
				}
				else if(winning.second.getPlayer() == AI_PLAYER) {
					return new Pair<Integer, Integer>(Integer.MAX_VALUE, null);
				}
				else {
					return new Pair<Integer, Integer>(0, null);
				}
			}
			else {
				return new Pair<Integer, Integer>(evaluatePositions(copy(board), AI_PLAYER), null);
			}
		}
		
		if(max) {
			value = -Integer.MAX_VALUE;
			int col = ((Piece)Random.choice(valid.toArray())).getCol();
			for(Piece p : valid) {
				p.setPlayer(AI.AI_PLAYER);
				Board b = copy(board);
				b.place(p);
				
				Pair<Integer, Integer> newScore = minimax(copy(b), depth-1, false, alpha, beta);
				if(newScore.first > value) {
					value = newScore.first;
					col = p.getCol();
				}
				alpha = Math.max(alpha, value);
				if(alpha >= beta)
					break;
			}
			return new Pair<Integer, Integer>(value, col);
		}
		else {
			value = Integer.MAX_VALUE;
			int col = ((Piece)Random.choice(valid.toArray())).getCol();
			for(Piece p : valid) {
				p.setPlayer(AI.HUMAN_PLAYER);
				Board b = copy(board);
				b.place(p);
				
				Pair<Integer, Integer> newScore = minimax(copy(b), depth-1, true, alpha, beta);
				if(newScore.first < value) {
					value = newScore.first;
					col = p.getCol();
				}
				beta = Math.min(beta, value);
				if(alpha >= beta)
					break;
			}
			return new Pair<Integer, Integer>(value, col);
		}
	}
	
	
}


