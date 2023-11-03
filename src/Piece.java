/*
 * The piece class is replacing the basic ints in the board
 * its useful so you can pass an arbitrary piece or array of pieces anywhere and they will still know their positions in the board
 * this way you don't actually have to keep track of the positions in x and y coords all the time
 */
public class Piece{
		private int row;
		private int column;
		private int player;
		
		public Piece(int x, int y, int player) {
			this.row = x;
			this.column = y;
			this.player = player;
		}
		
		//shorthand for checking if there is a player placed in a piece
		public boolean isEmpty() {
			return this.getPlayer() == 0;
		}

		/**
		 * @return the player
		 */
		public int getPlayer() {
			return player;
		}

		/**
		 * @param player the player to set
		 */
		public void setPlayer(int player) {
			this.player = player;
		}

		/**
		 * @return the x
		 */
		public int getRow() {
			return row;
		}

		/**
		 * @param x the x to set
		 */
		public void setRow(int x) {
			this.row = x;
		}

		/**
		 * @return the y
		 */
		public int getCol() {
			return column;
		}

		/**
		 * @param y the y to set
		 */
		public void setCol(int y) {
			this.column = y;
		}

		@Override
		public String toString() {
			return "Piece [row=" + row + ", column=" + column + ", player=" + player + "]";
		}
		
		public String toStringReadable() {
			return "(" + getRow() +", " + getCol() + ")";
		}
		
		//really have to make sure they are the exact same
		//if i don't the universe might implode
		public boolean equals(Piece other) {
			return other.getRow() == this.getRow() && this.getCol() == other.getCol() && this.getPlayer() == other.getPlayer();
		}
	}