import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * updated system to be more dynamic
 * instead of just a 2d int array
 * probably could have used a 1d array since each piece knows its x and y coords but that seemed unnecessary
 * i guess it could have been useful if i ever needed to pack the board to a 1d array for any reason
 * idk this should be fine
 */
public class Board{
		/*
		 * changed to piece array array for a more dynamic system
		 * saved my life
		 */
		private Piece[][] arr = new Piece[6][7];
		
		//Do it this way so you can copy the board if you want to
		//Should solve difficult to find reference issues
		public Board(Piece[][] arr) {
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					this.arr[i][j] = new Piece(arr[i][j].getRow(), arr[i][j].getCol(), arr[i][j].getPlayer());
				}
			}
		}
		
		//Fill the board with empty pieces
		//idk just general new board functionality
		public Board() {
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					arr[i][j] = new Piece(i, j, 0);
				}
			}
		}
		
		//Convenience
		public Piece[][] getValues() {
			return arr;
		}
		
		/*
		 * I use this so when evaluating a board you can place the piece on the board to get the new board state
		 * pretty self explanatory
		 */
		public void place(Piece piece) {
			if(isEmpty(piece)) {
				set(piece.getRow(), piece.getCol(), piece.getPlayer());
			}
		}
	
		//checks if the position that p is trying to fill is empty
		//Reference logic gets iffy here but i think this is fine
		public boolean isEmpty(Piece p) {
			return get(p).isEmpty();
		}
		
		//kind of like a getter because the underlying array is hidden
		public Piece get(int x, int y) {
			return arr[x][y];
		}
		
		public Piece get(Piece p) {
			return arr[p.getRow()][p.getCol()];
		}
		
		public void set(int x, int y, int value) {
			arr[x][y] = new Piece(x, y, value);
		}
		
		public void set(Piece p) {
			arr[p.getRow()][p.getCol()] = p;
		}
		
		//shorthand for arr.length
		public int numRows() {
			return arr.length;
		}
		
		//returns the size of the first column because the columns will always have the same size
		//unless i decide to mess with the systems
		//which shouldn't happen
		public int numCols() {
			return arr[0].length;
		}
		
		//get the index of the bottom row
		public int bottomRowIx() {
			return numRows()-1;
		}
		
		//get the index of the back column
		public int backColIx() {
			return numCols()-1;
		}
		
		/*
		 * finds all of the positions that can have a piece placed in them
		 * the regular connect four code does this anyway but just for each separate column the player tries to place in 
		 * the whole point of this is so that the ai knows which positions to evaluate for scoring
		 * it goes through each one and checks for horizontal vertical and diagonal conditions
		 * prevents the ai from having to check spaces that couldnt have a piece placed in them anyway
		 */
		public List<Piece> validPlacingPositions(){
			ArrayList<Piece> positions = new ArrayList<Piece>();
			for (int i = 0; i < arr[0].length; i++) {
				int available = availableIndex(i);
				Piece p = get(available, i);
				if(p.isEmpty()) {
					positions.add(new Piece(p.getRow(), p.getCol(), p.getPlayer()));
				}
			}
			return positions;
		}
		
		//used by the basic game, just returns the row that can have a piece placed in it by the given column
		public int availableIndex(int col) {
			int step = numRows()-1;
			while(get(step, col).getPlayer() != 0 && step > 0) {
				step--;
			}
			return step;
		}
		
		//this is a stupid fucking hack
		//so fucking inefficient
		//gets the next four pieces in a given direction INCLUDING THE GIVEN PIECE
		//really it gets the next three but whatever
		//so stupid to call getNext four times but its easier
		//if it takes the ai 10 minutes for each turn i might know why
		public Piece[] getNextFour(EvalDirection direction, Piece piece) {
			Piece[] arr = new Piece[4];
			arr[0] = piece;
			for (int i = 1; i < 4; i++) {
				try {
					arr[i] = getNext(direction, arr[i-1]);
				}
				catch(Exception e) {
					return null;
				}
			}
			return arr;
		}
		
		public Piece[] getPreviousFour(EvalDirection direction, Piece piece) {
			Piece[] arr = new Piece[4];
			arr[0] = piece;
			for (int i = 1; i < 4; i++) {
				try {
					arr[i] = getPrevious(direction, arr[i-1]);
				}
				catch(IndexOutOfBoundsException e) {
					return null;
				}
			}
			return arr;
		}
		
		//gets the piece "ahead" of the given piece in the given direction
		//this will be used for the board to build the 4 peices used to evaluate a win potential
		public Piece getNext(EvalDirection direction, Piece p) {
			switch(direction) {
				case Diagonal:
					return this.get(p.getRow()-1, p.getCol()+1);
				case Horizontal:
					return this.get(p.getRow(), p.getCol()+1);
				case Vertical:
					return this.get(p.getRow()-1, p.getCol());
				default:
					return this.get(p.getRow(), p.getCol());
			}
		}
		
		//gets the piece "behind" the given piece in the given direction
		public Piece getPrevious(EvalDirection direction, Piece p) {
			switch(direction) {
				case Diagonal:
					return this.get(p.getRow()+1, p.getCol()-1);
				case Horizontal:
					return this.get(p.getRow(), p.getCol()-1);
				case Vertical:
					return this.get(p.getRow()+1, p.getCol());
				case DiagonalBack:{
					return this.get(p.getRow()+1, p.getCol()+1);
				}
				default:
					return this.get(p.getRow(), p.getCol());
			}
		}
		
		/*
		 * takes a evaluation direction, and returns a piece amt units away from p in that direction
		 * this shit is NOT working idk why
		 * working on a fix but for now hacking together the getNext function as a placeholder
		 */
		public Piece getIn(EvalDirection direction, Piece p, int amt) {
			switch(direction) {
				case Diagonal:
					return this.get(p.getRow()-amt, p.getCol()+amt);
				case Horizontal:
					return this.get(p.getRow(), p.getCol()+amt);
				case Vertical:
					return this.get(p.getRow()-amt, p.getCol());
				default:
					return this.get(p.getRow(), p.getCol());
			}
		}
		
		//counts how many pieces of the given player are currently on the board
		public int piecesForPlayer(int player) {
			int count = 0;
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					if(arr[i][j].getPlayer() == player) {
						count++;
					}
				}
			}
			return count;
		}
		
		public void print() {
			for(int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					System.out.print(arr[i][j].getPlayer() + " ");
				}
				System.out.println();
			}
		}
	}
