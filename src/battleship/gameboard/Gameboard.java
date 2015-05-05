package battleship.gameboard;

import java.util.Vector;

public class Gameboard {
	private int rows, cols;
	private Vector<Grid> gameboard;
	
	public Gameboard(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		init();
	}
	
	private void init() {
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				gameboard.add(new Grid(row, col));
			}
		}
	}
}
