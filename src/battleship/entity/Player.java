/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.entity;

import java.util.Vector;

import battleship.game.Status;
import battleship.gameboard.Grid;
import battleship.entity.ShipType;
import static battleship.entity.Constants.NUM_OF_SUBMARINES;
import static battleship.entity.Constants.NUM_OF_DESTROYERS;
import static battleship.entity.Constants.NUM_OF_CARRIERS;


/**
 * @package battleship.entity
 * @Class Player
 * @brief Class represent a player human or non-human,
 * */
public class Player {
	private String id;
	private String name;
	private int hits;
	private int moves;
	private Vector<Ship> ships;
	private int remainingShips;
	
	public Status status;
	
	public Player(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void init() {
		status = Status.PLAYING;
		hits = 0;
		moves = 0;
		remainingShips = 9;
		initShips();
	}
	
	private void initShips() {
		ships = ShipBuilder.buildShips();
	}
	
	public String getId() { return id; }
	public String getName() { return name; }
	public int getHits() { return hits; }
	public int getMoves() { return moves; }
	
	public void fire(Grid grid) {
		moves++;
		// implement fire TODO
		// if enemy hit -> hits++
	}
	
	public void enemyFire(Grid grid) {
		// implement enemy fire
		// if damage taken, add damage to ship
		// and call updateShips()
	}
	
	public void updateShips() {
		for(Ship ship : ships) {
			if(!ship.isAlive()) {
				ships.remove(ship);
				if(remainingShips > 0) {
					remainingShips--;
				} else {
					status = Status.LOST;
				}
			}
		}
	}
}

class ShipBuilder {
	public static Vector<Ship> buildShips() {
		Vector<Ship> ships = new Vector<Ship>(9);
		BattleShipFactory bsf = new BattleShipFactory();
		String type = "Carrier#";
		int shipCounter = 1;
		
		ships.add(bsf.getShip(ShipType.CARRIER));
		
		type = "Destroyer#";
		for(int i = 0; i < NUM_OF_DESTROYERS; i++, shipCounter++) {
			ships.add(bsf.getShip(ShipType.DESTROYER));
		}
		
		type = "Submarine#";
		shipCounter = 1;
		for(int i = 0; i < NUM_OF_SUBMARINES; i++, shipCounter++) {
			ships.add(bsf.getShip(ShipType.SUBMARINE));
		}
		return ships;
	}
}
