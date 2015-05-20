package battleship.player;

import javax.swing.JLabel;

public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private int col, row;
	private boolean empty = true;

	public Grid(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}
	
	public void setOccupied() {
		empty = false;
	}
	
	public boolean isEmpty() {
		return empty;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
