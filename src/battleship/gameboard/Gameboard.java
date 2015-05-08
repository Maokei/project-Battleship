/**
 * @file Gameboard.java
 * @author rickard(rijo1001), lars(lama1205)
 * @date 2015-05-05
 * */
package battleship.gameboard;

import java.util.Vector;
import battleship.entity.*;

/**
 * @package battleship.gameboard
 * @class Gameboard
 * @brief Class describes a battleship board.
 * */
public class Gameboard {
	private battleship.entity.Player red; //1
	private battleship.entity.Player blue; //2
	private Vector<Ship> redShips;
	private Vector<Ship> blueShips;
	private Grid redGrid;
	private Grid blueGrid;
	
	public Gameboard() {
		redShips = new Vector<>();
		blueShips = new Vector<>();
		//grids
		redGrid = new Grid();
		blueGrid = new Grid();
	}
	
	private void  checkForHits() {
		
	}
	
	
}
