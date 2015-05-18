/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.player;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import battleship.game.Status;
import battleship.player.ShipType;
import static battleship.player.Constants.NUM_OF_CARRIERS;
import static battleship.player.Constants.NUM_OF_DESTROYERS;
import static battleship.player.Constants.NUM_OF_SUBMARINES;

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
	private Grid grid;
	private boolean playerTurn; 
	public Status status;
	
	public Player(String id, String name) {
		this.id = id;
		this.name = name;
		grid = new Grid();
		grid.addMouseListener(new GridListener());
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
		if(playerTurn)
			moves++;
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
			if(grid.isEmpty(x, y, a, length)) {
				grid.placeShipOnGrid(ship);
				return true;
			}
		}else if(player == 2){ //red player
			if(grid.isEmpty(x, y, a, length)) {
				grid.placeShipOnGrid(ship);
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
			grid.registerHit(x, y);
		}else{
			grid.registerHit(x, y);
			
		}
	}
}

class GridListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
    	//if(isPlacingShip)
    	//	player.placeShip(ship, x, y)
    	// else if(isFiring)
    	// player.fire(e.getX(), e.getY()) alt.
    	// client.sendMessage(new FireMessage(player, e.getX(), e.getY())
    	
    	// etc ...
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
