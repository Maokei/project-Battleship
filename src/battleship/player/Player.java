/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.player;

import static battleship.player.Constants.NUM_OF_DESTROYERS;
import static battleship.player.Constants.NUM_OF_SUBMARINES;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import battleship.game.Status;
import battleship.network.ClientConnection;
import battleship.network.Message;

/**
 * @package battleship.entity
 * @Class Player
 * @brief Class represent a player human or non-human,
 * */
public class Player {
	private String name;
	private ClientConnection con;
	private int hits;
	private int moves;
	private Vector<Ship> ships;
	private int remainingShips;
	private Gameboard playerGrid, enemyGrid;
	private boolean playerTurn;
	public Status status;
	private boolean placedAll = false;
	private int placeIndex;
	private static final int GRID_SIZE = 32;

	public Player(String name, ClientConnection con) {
		this.name = name;
		this.con = con;
		placeIndex = 0;
		con.setPlayer(this);
	}
	
	public ClientConnection getConnection() {
		return con;
	}

	public void init() {
		status = Status.PLAYING;
		hits = 0;
		moves = 0;
		remainingShips = 9;
		initShips();
	}

	public void setGrid(Gameboard playerGrid, Gameboard enemyGrid) {
		this.playerGrid = playerGrid;
		this.enemyGrid = enemyGrid;
		playerGrid.addMouseListener(new GridListener());
		enemyGrid.addMouseListener(new GridListener());
	}

	private void initShips() {
		ships = ShipBuilder.buildShips();
	}

	public String getName() {
		return name;
	}

	public int getHits() {
		return hits;
	}

	public int getMoves() {
		return moves;
	}

	public void fire(Gameboard grid) {
		if (playerTurn)
			moves++;
	}

	public void enemyFire(Gameboard grid) {
		// implement enemy fire
		// if damage taken, add damage to ship
		// and call updateShips()
	}

	public void listen() {
		new Thread(con).start();
	}

	public void sendMessage(String message) {
		con.sendChatMessage(message);
	}

	public void updateShips() {
		for (Ship ship : ships) {
			if (!ship.isAlive()) {
				ships.remove(ship);
				if (remainingShips > 0) {
					remainingShips--;
				} else {
					status = Status.LOST;
				}
			}
		}
	}

	private void checkForHits(int x, int y, Vector<Ship> ships) {
		for (Ship ship : ships) {
			if (ship.wasHit(x, y)) {

				// register hit on grid
				registerShot(x, y);
				// game over or? let player keep shooting
			}
		}
	}

	private boolean checkHit(int x, int y, Ship ship) {
		int length = ship.getLength();
		// if()

		return false;
	}

	public Gameboard getGrid() {
		return playerGrid;
	}

	public boolean placeShipByGrid(Ship ship, int row, int col) {
		playerGrid.placeShip(ship, row, col);
		return true;
	}

	/**
	 * placeShip
	 * 
	 * @param Ship
	 *            object, int player 1 - 2 blue or red player
	 * @return boolean if ship is valid placement
	 * */
	public boolean placeShip(Ship ship, int x, int y) {
		// int x, y;
		// x = ship.getX();
		// y = ship.getY();
		Alignment a = ship.getAlignment();
		int length = ship.getLength();
		if (playerGrid.isEmpty(x, y, a, length)) {
			playerGrid.placeShipOnGrid(ship);
			return true;
		}
		/*
		 * if(player == 1) { //blue player if(grid.isEmpty(x, y, a, length)) {
		 * grid.placeShipOnGrid(ship); return true; } }else if(player == 2){
		 * //red player if(grid.isEmpty(x, y, a, length)) {
		 * grid.placeShipOnGrid(ship); return true; } }
		 */
		return false;
	}

	/**
	 * getPlaceShip
	 * @brief get ship position from user
	 * @param
	 * @return string array with X Y Alignment 
	 * */
	public ArrayList<String> getPlaceShip(String shipName, int shipL) throws NullPointerException {
		String cord = "Enter coordinates for: ";
		String ex = " format ex:(32V , XYV) or r for random.";
		String sp = JOptionPane.showInputDialog(new JFrame(), cord + shipName + " Size: " + shipL + ex);
		/*if(sp.startsWith("r") || sp.startsWith("R")) { //random
			//random placement of ship
			return null;
		}*/
		ArrayList<String> out = new ArrayList<String>(); //split cords
		for(String s: sp.split("\\a")) {
			out.add(s.toLowerCase());
		}
		
		if(!(out.indexOf(2) == 'v') || !(out.indexOf(2) == 'h')) {
			return null;
		}
		
		return out;
	}
	
	/**
	 * register a shot on the grid
	 * */
	private void registerShot(int x, int y) {
		if (playerTurn) {
			playerGrid.registerHit(x, y);
		} else {
			playerGrid.registerHit(x, y);

		}
	}

	class GridListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			int row = e.getY() / GRID_SIZE;
			int col = e.getX() / GRID_SIZE;
			if (playerGrid == e.getComponent()) {
				if (!placedAll) {
					Ship ship = ships.get(placeIndex);
					if ((placeIndex + 1) % 2 == 0)
						ship.alignment = Alignment.VERTICAL;
					if (++placeIndex == ships.size())
						placedAll = true;
					
					if ((ship.alignment == Alignment.HORIZONTAL)
							&& (ship.length + col) <= 10)
						placeShipByGrid(ship, row, col);
					else if (((ship.alignment == Alignment.VERTICAL) && (ship.length + row) <= 10))
						placeShipByGrid(ship, row, col);
				}
			} else if (enemyGrid == e.getComponent()) {
				System.out.println("Player fired at Grid[ " + row + ", "
						+ col + "]");
				fireByGrid(row, col);
			}
		}
	}

	public void fireByGrid(int row, int col) {
		enemyGrid.fire(row, col);
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
		for (int i = 0; i < NUM_OF_DESTROYERS; i++, shipCounter++) {
			ships.add(bsf.getShip(ShipType.DESTROYER));
		}

		type = "Submarine#";
		shipCounter = 1;
		for (int i = 0; i < NUM_OF_SUBMARINES; i++, shipCounter++) {
			ships.add(bsf.getShip(ShipType.SUBMARINE));
		}
		return ships;
	}
}
