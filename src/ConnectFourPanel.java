// ConnectFour Panel
// Written by: Mr. Swope
// Date: May 13th, 2016
// This project extends the Jpanel class. In order to draw items on this panel you need use the Graphics2D's methods.
// Update these comments by writing when, who and how you modified this class.
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;



public class ConnectFourPanel extends JPanel implements MouseListener, KeyListener{
	
	private int turn;		// will equal -1 or 1 to represent who's turn it is. use -1 for blue 1 for red
	private Board board;  // two-dimensional array of ints.  All values should be initialized to 0.  When
							// blue places a checker value should be changed to -1.  When red places a cheker
							// value should be changed to 1.
	private static final int WIDTH = 701;
	private static final int HEIGHT = 601;
	private boolean gameOver;
	
	public static final int AI_PLAYER = 1;
	public static final int HUMAN_PLAYER = -1;
	
	private boolean gameStarted = false;
	private boolean playAgainstAI = false;
	private boolean thinking = false;
	
	public static int numCheckCompl = 0;
	public static int numChecks = 0;
	private static boolean mouseDown = false;
	
	// method: ConnectFourPanel Constructor
	// description: This 'method' runs when a new instance of this class in instantiated.  It sets default values  
	// that are necessary to run this project.  
	public ConnectFourPanel(){
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);			// for mouselistener
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.board = new Board();
		turn = -1;
	}
	
	// method: paintComponent
	// description: This method is called when the Panel is painted.  It contains 
	// code that draws shapes onto the panel.
	// parameters: Graphics g - this object is used to draw shapes onto the JPanel.
	// return: void
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
	
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, WIDTH, HEIGHT); 
		
		// this code loads an image so that you can later paint it onto your component.
		// this would load a picture named blue.png that should be saved in a folder
		// named images, which should be located in your src folder.
		ImageIcon blueImage;
		ImageIcon redImage;
		
		ClassLoader cldr = this.getClass().getClassLoader();
		String imagePath = "images/blue.png";
		URL imageURL = cldr.getResource(imagePath);
		blueImage = new ImageIcon(imageURL);
		
		imagePath = "images/red.png";
		imageURL = cldr.getResource(imagePath);
		redImage = new ImageIcon(imageURL);
		
		// draws the connect four board	
		for(int r = 0; r < board.getValues().length; r++){
			for(int c = 0; c < board.numCols(); c++){
				g2.setColor(Color.GRAY);
				g2.drawLine(c*100, 0, c*100, HEIGHT);
				g2.drawOval(c*100+5, r*100+5, 90, 90);
				g2.fillOval(c*100+9, r*100+9, 82, 82);
			}
			g2.drawLine(0, r*100, WIDTH, r*100);
		}
		
		// this is how you paint your image. the last two parameters are the x and y coordinates 
		// of the top left hand corner of the image.  Right now this will only draw one blue checker
		// and one red image drawn on the board.  You should instead use nested for loops to loop 
		// through your two-dimensional array and paint blue checkers where ever there is -1 and 
		// a red checker wherever there is 1 in the array.
		for(int r = 0; r < board.numRows(); r++){
			for(int c = 0; c < board.numCols(); c++){
				if(board.get(r, c).getPlayer() == -1)
					blueImage.paintIcon(this, g, c*100+10, r*100+10);
				else if(board.get(r, c).getPlayer() == 1 )
					redImage.paintIcon(this, g, c*100+10, r*100+10);
			}
		}
		// Display a message if either red or blue has won the game.
		g2.setFont(new Font("Verdana", 0, 40));
		if(gameOver) {
			g2.setColor(Color.black);
			g2.fillRect(125, 235, 450, 100);
			g2.setColor(Color.yellow);
			String player = turn == -1 ? "Blue" : "Red";
			g2.drawString(player + " won the game", 150, 300);
		}
		
		if(thinking) {
			g2.setColor(Color.black);
			g2.fillRect(225, 235, 250, 100);
			g2.setColor(Color.yellow);
			g2.drawString("thinking...", 250, 300);
		}
		
		if(!gameStarted) {
			Color c = new Color(0, 0, 0, 0.85f);
			g2.setColor(c);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2.setColor(Color.white);
			String s = "Play Against AI? y/n";
			int width = g2.getFontMetrics().stringWidth(s);
			g2.drawString(s, this.getWidth()/1.3f-width, this.getHeight()/2);
		}
		
		if(this.getMousePosition() != null && gameStarted && !gameOver) {
			g2.setColor(Color.yellow);
			int x = this.getMousePosition().x/100;
			g2.setStroke(new BasicStroke(10));
			if(!mouseDown) g2.drawOval(x*100+10, board.availableIndex(x)*100+10, 80, 80);
			else g2.fillOval(x*100+10, board.availableIndex(x)*100+10, 80, 80);
		}
		this.repaint();
	}
	
	// Check to see if x or o have won the game.
	// return -1 if x won, 1 if o won and 0 if neither has won.
	public boolean checkWinner(){
		
		int consequtive = 0;
		int t = 0;
		
		// horizontal
		for(int r = board.numRows()-1; r>=0 ; r--){
			for(int c = 0; c<board.numCols(); c++){
				
				if(board.get(r, c).getPlayer() == t && t!=0)
					consequtive++;
				else if(board.get(r, c).getPlayer() != 0){
					t = board.get(r, c).getPlayer();
					consequtive = 1;
				}
				else
					consequtive = 0;
				
				if(consequtive == 4)
					return true;
			}
			consequtive = 0;
		}
		
		// vertical
		for(int c = 0; c<board.numCols(); c++){
			for(int r = board.numRows()-1; r>=0 ; r--){
				
				if(board.get(r, c).getPlayer() == t && t!=0)
					consequtive++;
				else if(board.get(r, c).getPlayer() != 0){
					t = board.get(r, c).getPlayer();
					consequtive = 1;
				}
				else
					consequtive = 0;
				
				if(consequtive == 4)
					return true;
				
			}
			consequtive = 0;
		}
		
		// diagonal up
		for(int r = board.numRows()-1; r>2 ; r--){	
			for(int c = 0; c<board.numCols()-3; c++){
				if(board.get(r, c).getPlayer() != 0){
					t = board.get(r, c).getPlayer();
					do{
						consequtive++;
					}while(consequtive<4 && board.get(r-consequtive, c+consequtive).getPlayer() == t);
					
					if(consequtive == 4)
						return true;
				}
				consequtive = 0;
			}
		}
		
		// diagonal down
		for(int r = 0; r<board.numRows()-3 ; r++){	
			for(int c = 0; c<board.numCols()-3; c++){
				if(!board.get(r, c).isEmpty()){
					t = board.get(r, c).getPlayer();
					do{
						consequtive++;
					}while(consequtive<4 && board.get(r+consequtive, c+consequtive).getPlayer() == t);
					
					if(consequtive == 4)
						return true;
				}
				consequtive = 0;	
			}
		}
		
		return false;
	}


	/*
	 * this method is the method that actually adds the piece to the board
	 */
	public void addPiece(int column){
		thinking = false;
		int row = board.availableIndex(column);
		board.place(new Piece(row, column, turn));
		this.repaint();
		if(AI.isWinningBoard(board).first) {
			gameOver = true;
		}
		else {
			turn *=-1;
			if(turn == AI_PLAYER && playAgainstAI) {
				thinking = true;
				new Thread(() -> {
					int col = AI.evaluate(AI.copy(board));
					addPiece(col);
				}).start();
			}
		}
	}
	
	/*
	 * reset the game
	 */
	public void resetGame() {
		gameOver = false;
		board = new Board();
		turn = -1;
		gameStarted = false;
		playAgainstAI = false;
		this.repaint();
	}
	
	public void mouseClicked(MouseEvent e) {	
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseDown = true;
	}

	/*
	 * handles the clicking
	 * resets the game if its ended
	 * if its the ai turn no clicks for you haha
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(gameOver) {
			resetGame();
			return;
		}
		else if((turn == AI_PLAYER && playAgainstAI) || !gameStarted) {
			return;
		}
		int x = e.getX()/100;
		addPiece(x);
		mouseDown = false;
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_Y) {
			playAgainstAI = true;
			gameStarted = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_N) {
			gameStarted = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_R) {
			resetGame();
		}
		this.repaint();
	}
}
