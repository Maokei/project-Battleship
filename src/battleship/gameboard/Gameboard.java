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
 * @brief Class describes a battleship game board session.
 * */
public class Gameboard {
	private battleship.entity.Player red; //1
	private battleship.entity.Player blue; //2
	private Vector<Ship> redShips;
	private Vector<Ship> blueShips;
	private Grid redGrid;
	private Grid blueGrid;
	private boolean playerTurn; 
	public Gameboard() {
		redShips = new Vector<>();
		blueShips = new Vector<>();
		//grids
		redGrid = new Grid();
		blueGrid = new Grid();
		//player turn
		playerTurn = true; //blue player starts always
	}
	
	private void  checkForHits(int x, int y, Vector<Ship> ships) {
		for(Ship ship : ships){
			if(ship.wasHit(x, y)) {
				
				//register hit on grid
				registerShot(x, y);
				//game over or? let player keep shooting
			}
		}
	}
	
	private boolean checkHit(int x, int y, Ship ship) {
		int length = ship.getLength();
		//if()
		
		return false;
	}
	
	/**
	 * placeShip
	 * @param Ship object, int player 1 - 2 blue or red player
	 * @return boolean if ship is valid placement
	 * */
	public boolean placeShip(Ship ship, int player) {
		int x, y;
		x = ship.getX();
		y = ship.getY();
		Alignment a = ship.getAlignment();
		int length = ship.getLength();
		if(player == 1) { //blue player
			if(blueGrid.isEmpty(x, y, a, length)) {
				blueGrid.placeShipOnGrid(ship);
				return false;
			}
		}else if(player == 2){ //red player
			if(redGrid.isEmpty(x, y, a, length)) {
				redGrid.placeShipOnGrid(ship);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * register a shot on the grid
	 * */
	private void registerShot(int x, int y) {
		if(playerTurn) {
			redGrid.registerHit(x, y);
		}else{
			blueGrid.registerHit(x, y);
			
		}
	}
}
