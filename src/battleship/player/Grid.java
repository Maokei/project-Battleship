package battleship.player;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Grid extends JLabel {
	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private final int size = 32;
	private int col, row;
	
	public Grid(int row, int col, ImageIcon icon) {
		super(icon);
		this.row = row;
		this.col = col;
		this.icon = icon;
	}
	
	public boolean getBounds(int x, int y){
		return inRangeX(x) && inRangeY(y);
	}
	
	private boolean inRangeX(int x) {
		return x >= (col * size) && x < ((col * size) + size);
	}
	
	private boolean inRangeY(int y) {
		return y >= (row * size) && y < ((row * size) + size);
	}
}
