/**
 * @file Gameboard.java
 * @author rickard(rijo1001), lars(lama1205)
 * @date 2015-05-05
 * */
package battleship.gameboard;

import java.util.Vector;

/**
 * @package battleship.gameboard
 * @class Gameboard
 * @brief Class describes a battleship board.
 * */
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
				gameboard.add(new Grid());
			}
		}
	}
}
