package battleship.gameboard;

import java.awt.Color;

public class Grid {
	private static Color color = Color.BLUE;
	private static final int size = 32;
	public int row;
	public int col;
	
	public Grid(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getGridSize() {
		return size;
	}

}
