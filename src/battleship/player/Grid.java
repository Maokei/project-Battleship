/**
 * @file Grid.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.player;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * @class Grid
 * @package battleship.player
 * @brief Class describes a battleship 10 * 10 grid
 * @extends JLabel
 * */
public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private int col, row;
	private boolean empty = true;
	private boolean hit = false;

	public Grid(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		this.setPreferredSize(new Dimension(32, 32));
	}
	
	public void setOccupied() {
		empty = false;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	public boolean isHit() {
		return hit;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
