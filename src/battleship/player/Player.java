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
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
	private Vector<Ship> playerShips;
	private Vector<Ship> enemyShips;
	private int remainingShips;
	private Gameboard playerGrid, enemyGrid;
	private Board playerBoard;
	private boolean playerTurn;
	public Status status;
	private boolean placedAll = false;
	private int placeIndex;
	private int shipPlacementIndex;
	private static final int GRID_SIZE = 32;

	public Player(String name, ClientConnection con) {
		this.name = name;
		this.con = con;
		placeIndex = 0;
		shipPlacementIndex = 0;
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

	public void setBoard(Board playerBoard) {
		this.playerBoard = playerBoard;
		this.playerBoard.addMouseListener(new PlayerBoardListener());
	}

	private void initShips() {
		playerShips = ShipBuilder.buildShips();
		enemyShips = new Vector<Ship>();
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

	public void listen() {
		new Thread(con).start();
	}

	public void sendMessage(Message message) {
		con.sendMessage(message);
	}

	public void updateShips() {
		for (Ship ship : playerShips) {
			if (!ship.isAlive()) {
				playerShips.remove(ship);
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

	public void placeShipByGrid(Ship ship, int row, int col) {
		playerGrid.placeShip(ship, row, col);
	}

	public boolean checkIfEmptyGrid(int row, int col) {
		for (Ship other : playerShips) {
			if (other.isInPosition(row, col)) {
				return false;
			}
		}
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
	 * 
	 * @brief get ship position from user
	 * @param
	 * @return string array with X Y Alignment
	 * */
	public ArrayList<String> getPlaceShip(String shipName, int shipL)
			throws NullPointerException {
		String cord = "Enter coordinates for: ";
		String ex = " format ex:(32V , XYV) or r for random.";
		String sp = JOptionPane.showInputDialog(new JFrame(), cord + shipName
				+ " Size: " + shipL + ex);
		/*
		 * if(sp.startsWith("r") || sp.startsWith("R")) { //random //random
		 * placement of ship return null; }
		 */
		ArrayList<String> out = new ArrayList<String>(); // split cords
		for (String s : sp.split("\\a")) {
			out.add(s.toLowerCase());
		}

		if (!(out.indexOf(2) == 'v') || !(out.indexOf(2) == 'h')) {
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

	public void registerHit(int row, int col) {
		for (Ship ship : playerShips) {
			if (ship.isAlive() && ship.checkHit(row, col)) {
				playerGrid.playerIsHit(row, col);
				sendMessage(new Message(Message.MESSAGE, name, "HIT"
						+ Integer.toString(row) + " " + Integer.toString(col)
						+ " " + Integer.toString(ship.getHealth())));
			}
		}
	}

	public void registerEnemyHit(int row, int col, int health) {
		enemyGrid.enemyIsHit(row, col, health);
	}

	class GridListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			int row = e.getY() / GRID_SIZE;
			int col = e.getX() / GRID_SIZE;
			System.out.println("PlaceIndex: " + placeIndex);
			if (playerGrid == e.getComponent()) {
				if (!placedAll) {
					Ship ship = playerShips.get(placeIndex);

					if (placeIndex > 0)
						System.out.println("PlaceIndex: " + placeIndex);

					if ((placeIndex + 1) % 2 == 0)
						ship.alignment = Alignment.VERTICAL;

					int length = ship.getLength();
					for (int i = 0; i < length; i++) {
						if (ship.alignment == Alignment.HORIZONTAL) {
							int rowCounter = row;
							if (!checkIfEmptyGrid(rowCounter++, col))
								return;
						} else if (ship.alignment == Alignment.VERTICAL) {
							int colCounter = col;
							if (!checkIfEmptyGrid(row, colCounter++))
								return;
						}
					}
					if ((ship.alignment == Alignment.HORIZONTAL)
							&& (ship.length + col) <= 10) {
						placeShipByGrid(ship, row, col);
						sendMessage(new Message(Message.MESSAGE, name, "SHIP "
								+ ship.getType() + " H "
								+ Integer.toString(row) + " "
								+ Integer.toString(col)));
					} else if (((ship.alignment == Alignment.VERTICAL) && (ship.length + row) <= 10)) {
						placeShipByGrid(ship, row, col);
						sendMessage(new Message(Message.MESSAGE, name, "SHIP "
								+ ship.getType() + " V "
								+ Integer.toString(row) + " "
								+ Integer.toString(col)));
					}
				}

				if (++placeIndex == playerShips.size())
					placedAll = true;

			} else if (enemyGrid == e.getComponent()) {
				System.out.println(name + " fired at Grid[ " + row + ", " + col
						+ "]");
				fireByGrid(row, col);
			}
		}
	}

	public void fireByGrid(int row, int col) {
		sendMessage(new Message(Message.MESSAGE, name, "FIRE "
				+ Integer.toString(row) + " " + Integer.toString(col)));
	}

	public void placeEnemyShip(Ship ship, int row, int col) {
		enemyGrid.placeShip(ship, row, col);
		enemyShips.add(ship);
	}
	
	public void setRunning(boolean running) {
		con.setRunning(running);
	}

	class PlayerBoardListener extends MouseAdapter {
		Alignment al = Alignment.HORIZONTAL;
		@Override
		public void mousePressed(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) { //change alignment
				if(al == Alignment.HORIZONTAL)
					al = Alignment.VERTICAL;
				else
					al = Alignment.HORIZONTAL;
			}else{ //place ship
				int row = e.getY() / GRID_SIZE;
				int col = e.getX() / GRID_SIZE;
				if (shipPlacementIndex < playerShips.size()) {
					Ship ship = playerShips.elementAt(shipPlacementIndex);
					
					//if ((shipPlacementIndex + 1) % 2 == 0)
					ship.alignment = al;
					
					if (playerBoard.checkShipPlacement(ship, row, col)) {
						playerBoard.placeShip(ship, row, col);
						shipPlacementIndex++;
					}
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
