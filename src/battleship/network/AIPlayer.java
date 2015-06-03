/**
 * @file AIPlayer.java
 * @date 2015-05-25
 * @author rickard, lars
 * */
package battleship.network;

import static battleship.game.Constants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.Timer;

import battleship.game.BattlePlayer;
import battleship.game.Message;
import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;

/**
 * AIPlayer
 * @class AIPlayer
 * @brief class describes an AI player.
 * @implements BattlePlayer, NetworkOperations
 * */
public class AIPlayer implements BattlePlayer, NetworkOperations {
	private String name = "AI";
	private String opname = "";
	private ClientConnection con;
	private RandomShipPlacer shipPlacer;
	private Random r;
	private char[][] aiPlayerGrid, enemyGrid;
	private Vector<Ship> playerShips;
	private int remainingShips;
	private ArrayList<Grid> validTargets;
	protected boolean running = true;
	private Set<Grid> probableTargets;
	private Vector<Ship> shipsDown;
	private boolean playerTurn;
	private boolean opponentDeployed = false;
	private boolean enemyShipDown = false;
	private Grid prevHit, currHit;
	private Alignment enemyShipAlignment;
	private String reciever, server;

	/**
	 *Constructor 
	 * */
	public AIPlayer(String name) {
		reciever = name;
		server = "Server";
		init();
		sendMessage(new Message(Message.LOGIN, name, reciever, "MultiPlayer"));
		sendMessage(new Message(Message.DEPLOYED, "AI", reciever, ""));
	}
	/*
	public AIPlayer(String opname) {
		this.opname = opname;
		init();
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		sb.append(opname);
		sb.append(name);
		sb.append(r.nextInt(10) + 1);
		name = sb.toString();
		//sendMessage(new Message(Message.LOGIN, name, "MultiPlayer"));
		//sendMessage(new Message(Message.DEPLOYED, "AI", ""));
	}
	*/
	
	/**
	 * init
	 * @function init
	 * @brief initioation function for the ai player.
	 * @return void
	 * */
	public void init() {
		r = new Random();
		shipPlacer = new RandomShipPlacer();
		aiPlayerGrid = shipPlacer.getRandomShipGrid();
		enemyGrid = new char[SIZE][SIZE];
		playerShips = shipPlacer.getRandomShips();
		remainingShips = playerShips.size();
		validTargets = new ArrayList<Grid>(SIZE * SIZE);
		probableTargets = new HashSet<Grid>();
		shipsDown = new Vector<Ship>();
		prevHit = new Grid(-1, -1);
		currHit = new Grid(-1, -1);
		initEnemyGrid();
		displayPlayerGrid();
		displayPlayerShips();
		listen();
	}

	/**
	 * initEnemyGrid
	 * @name initEnemeyGrid
	 * @brief initiates a copy of the opponent's grid
	 * */
	private void initEnemyGrid() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				enemyGrid[row][col] = empty;
				validTargets.add(new Grid(row, col));
			}
		}
	}

	/**
	 * displayPlayerGrid
	 * @name displayPlayerGrid
	 * @brief debug function to display grid in console
	 * @return void
	 * */
	public void displayPlayerGrid() {
		for (int row = 0; row < SIZE; row++) {
			System.out.print("[");
			for (int col = 0; col < SIZE; col++) {
				System.out.print(aiPlayerGrid[row][col]);
			}
			System.out.print("]\n");
		}
	}

	/**
	 * displayPlayerShips
	 * @name displayPlayerShips
	 * @brief debug function to display ships in console
	 * @return void
	 * */
	public void displayPlayerShips() {
		for (Ship ship : playerShips) {
			System.out.print(ship.getType() + "[ ");
			for (Grid grid : ship.getPosition()) {
				System.out.print(grid.getRow() + "," + grid.getCol() + " ");
			}
			System.out.print(ship.getType() + "]\n");
		}
	}

	/**
	 * displayEnemyGrid
	 * @name displayEnemyGrid
	 * @brief debug function to display grid in console
	 * @return void
	 * */
	public void displayEnemyGrid() {
		for (int row = 0; row < SIZE; row++) {
			System.out.print("[");
			for (int col = 0; col < SIZE; col++) {
				System.out.print(enemyGrid[row][col]);
			}
			System.out.print("]\n");
		}
	}

	/**
	 * fire
	 * @name fire
	 * @brief ai player fires, based on valid targets in grid and sends out a fire message to the player.
	 * @return void
	 * */
	public void fire() {
		if (checkProbableTargets()) {
			calculateNextTarget();
		} else {
			
			Grid grid = validTargets.get((validTargets.size() > 0) ? r.nextInt(validTargets.size()) : 0);
			int row = grid.getRow();
			int col = grid.getCol();
			if (checkBounds(row, col)) {
				System.out.println("FIRE " + row + ", " + col);
				sendMessage(new Message(Message.MESSAGE, "AI", reciever, "FIRE "
						+ Integer.toString(row) + " " + Integer.toString(col)));
				System.out.println("Removing grid [ " + row + "," + col + "]");
				validTargets.remove(grid);
				System.out.println("Valid target size: " + validTargets.size());
			}
		}
	}

	/**
	 * checkProbableTargets
	 * @name checkProbableTargets
	 * @brief help function to evaluate if there are any valid targets.
	 * @return boolean if there are/are not any valid targets
	 * */
	private boolean checkProbableTargets() {
		if (probableTargets.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * calculateNextTarget
	 * @name calculateNextTarget
	 * @brief Calculate the next target for the ai player, using number of probable targets
	 * @return void
	 * */
	private void calculateNextTarget() {
		System.out.print("ProbableTargets [ ");
		for (Grid grid : probableTargets) {
			System.out.print(grid.getRow() + "," + grid.getCol() + " ");
		}
		System.out.print("]\n");
		int randomProbable = r.nextInt(probableTargets.size());
		Grid grid = (Grid) probableTargets.toArray()[randomProbable];
		probableTargets.remove(grid);
		validTargets.remove(grid);
		System.out.println("Caluclate Valid target size: "
				+ validTargets.size());
		int row = grid.getRow();
		int col = grid.getCol();
		System.out.println("FIRE " + row + ", " + col);
		//send fire message
		sendMessage(new Message(Message.MESSAGE, "AI", reciever, "FIRE "
				+ Integer.toString(row) + " " + Integer.toString(col)));
	}

	/**
	 * checkHit
	 * @name checkHit
	 * @brief Function checks if a position is hit or not.
	 * @param int row integer grid and integer column in grid
	 * @return return true if location already hit
	 * */
	public boolean checkHit(int row, int col) {
		if (aiPlayerGrid[row][col] == occupied) {
			return true;
		}
		return false;
	}

	/**
	 * registerFire
	 * @name registerFire
	 * @brief iterate through the grid and register a hit or a miss by the enemy
	 * @param integer row in ai grid and integer column in ai grid 
	 * @return void
	 * */
	public void registerFire(int row, int col) {
		if (checkBounds(row, col)) {
			if (checkHit(row, col)) {
				for (Ship ship : playerShips) {
					if (ship.isAlive() && ship.checkHit(row, col)) {
						registerEnemyHit(ship, row, col);
					}
				}
			} else {
				registerEnemyMiss(row, col);
			}
		}
	}

	
	/**
	 * registerPlayerHit
	 * @name registerPlayerHit
	 * @brief iterate through the grid and register a hit or a miss by the player
	 * @param integer row in grid and integer column in grid 
	 * */
	public void registerPlayerHit(int row, int col) {
		if (checkBounds(row, col)) {
			enemyGrid[row][col] = hit;
			System.out.println("HIT " + row + "," + col);
			comparePrevHits(new Grid(row, col));
			addNextPossibleTargets(new Grid(row, col));
			if (enemyShipDown) {
				checkEnemyDownProbableTargets();
			}
			new GameTimer(2, 1000).run();
		}
	}

	/**
	 * comparePrevHits
	 * @name comparePrevHits
	 * @brief compare hits and decide if next target should be vertical or horizontal for ai.
	 * @param Takes a grid char[][]
	 * @return void
	 * */
	private void comparePrevHits(Grid grid) {
		if (prevHit.getRow() == -1 && prevHit.getCol() == -1) {
			prevHit = grid;
		} else {
			if (currHit.getRow() == -1 && currHit.getCol() == -1) {
				currHit = grid;
			} else {
				prevHit = currHit;
				currHit = grid;
			}

			if (prevHit.getRow() == currHit.getRow()) {
				enemyShipAlignment = Alignment.HORIZONTAL;
				updateProbableTargets();
			} else if (prevHit.getCol() == currHit.getCol()) {
				enemyShipAlignment = Alignment.VERTICAL;
				updateProbableTargets();
			}
		}
	}
	
	/**
	 * updateProbableTargets
	 * @name updateProbableTargets
	 * @brief update list of probable targets based of assumed players ship hits alignment
	 * @return void
	 * */
	private void updateProbableTargets() {
		if(enemyShipAlignment == Alignment.HORIZONTAL) {
			for (Iterator<Grid> i = probableTargets.iterator(); i.hasNext();) {
				 Grid grid = i.next();
				 if(grid.getRow() != currHit.getRow()) {
					 i.remove();
				 }
			}
		} else if(enemyShipAlignment == Alignment.VERTICAL) {
			for (Iterator<Grid> i = probableTargets.iterator(); i.hasNext();) {
				 Grid grid = i.next();
				 if(grid.getCol() != currHit.getCol()) {
					 i.remove();
				 }
			}
		}
	}

	/**
	 * addNextPossibleTargets
	 * @name addNextPossibleTargets
	 * @brief Add the next possible target based of the given grid  
	 * @param takes a char grid 10 * 10
	 * */
	private void addNextPossibleTargets(Grid grid) {
		if (prevHit.getRow() == -1 || currHit.getRow() == -1) {
			if (checkLeftGrid(grid))
				probableTargets.add(new Grid(grid.getRow(), grid.getCol() - 1));
			if (checkTopGrid(grid))
				probableTargets.add(new Grid(grid.getRow() - 1, grid.getCol()));
			if (checkRightGrid(grid))
				probableTargets.add(new Grid(grid.getRow(), grid.getCol() + 1));
			if (checkBottomGrid(grid))
				probableTargets.add(new Grid(grid.getRow() + 1, grid.getCol()));
		} else {
			if (enemyShipAlignment == Alignment.HORIZONTAL) {
				if (checkLeftGrid(grid))
					probableTargets.add(new Grid(grid.getRow(),
							grid.getCol() - 1));
				if (checkRightGrid(grid))
					probableTargets.add(new Grid(grid.getRow(),
							grid.getCol() + 1));
			} else if (enemyShipAlignment == Alignment.VERTICAL) {
				if (checkTopGrid(grid))
					probableTargets.add(new Grid(grid.getRow() - 1, grid
							.getCol()));
				if (checkBottomGrid(grid))
					probableTargets.add(new Grid(grid.getRow() + 1, grid
							.getCol()));
			}
		}
	}

	/**
	 * checkLeftGrid
	 * @name checkLeftGrid
	 * @brief 
	 * @param Takes a player grid pos
	 * @return Return true if position is hit or missed
	 * */
	private boolean checkLeftGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (col > 0
				&& !(enemyGrid[row][col - 1] == miss || enemyGrid[row][col - 1] == hit)) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkTopGrid
	 * @name checkTopGrid
	 * @param Takes a player grid, to be checked
	 * @return Return true if position is hit or missed
	 * */
	private boolean checkTopGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (row > 0
				&& !(enemyGrid[row - 1][col] == miss || enemyGrid[row - 1][col] == hit)) {
			return true;
		}
		return false;
	}

	/**
	 * checkRightGrid
	 * @name checkRightGrid
	 * @brief check grid position valid target to the right
	 * @param takes a grid position to check right of.
	 * @return true if location has be hit or missed
	 * */
	private boolean checkRightGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (col < (SIZE - 1)
				&& !(enemyGrid[row][col + 1] == miss || enemyGrid[row][col + 1] == hit)) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkBottomGrid
	 * @name checkBottomGrid
	 * @brief check grid position valid target to the bottom(south)
	 * @param takes a grid position to check right of.
	 * @return true if location has be hit or missed
	 * */
	private boolean checkBottomGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (row < (SIZE - 1)
				&& !(enemyGrid[row + 1][col] == miss || enemyGrid[row + 1][col] == hit)) {
			return true;
		}
		return false;
	}

	/**
	 * registerEnemyHit
	 * @name registerEnemyHit
	 * @brief register hit on ship and decide if the ship is sunk or not. 
	 * @param takes a ship, integer row and integer column.
	 * @return void
	 * */
	public void registerEnemyHit(Ship ship, int row, int col) {
		if (checkBounds(row, col)) {
			sendMessage(new Message(Message.MESSAGE, "AI", reciever,"HIT "
					+ Integer.toString(row) + " " + Integer.toString(col)));
			aiPlayerGrid[row][col] = hit;
			ship.hit();
			if (!ship.isAlive()) {
				sinkShip(ship); //call sink ship
				--remainingShips;
				if (remainingShips == 0) {
					battleLost();
				}
			}
		}
	}

	
	/**
	 * sinkShip
	 * @name sinkShip
	 * @brief function sinks a ship and sends out appropriate message to opponent player
	 * @param Takes a ship object
	 * @return void
	 * */
	public void sinkShip(Ship ship) {
		int row = ship.getStartPosition().getRow();
		int col = ship.getStartPosition().getCol();
		if (checkBounds(row, col)) {
			System.out.println("Player SHIP DOWN " + ship.getType());
			sendMessage(new Message(Message.MESSAGE, "AI", reciever, "SHIP_DOWN "
					+ ship.getType() + " " + ship.getAlignment() + " "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		}

	}

	/**
	 * registerPlayerMiss
	 * @name registerPlayerMiss
	 * @brief registers a miss on player duplicate grid
	 * @param takes integer row and integer column grid coordinates.
	 * @return void
	 * */
	public void registerPlayerMiss(int row, int col) {
		if (checkBounds(row, col)) {
			playerTurn = false;
			enemyGrid[row][col] = miss;
			probableTargets.remove(new Grid(row, col));
			sendMessage(new Message(Message.TURN, "AI", reciever, ""));
		}
	}

	/**
	 * checkBounds 
	 * @brief Help function to decide if the given position is valid within a grid.
	 * @param integer row and integer column grid coordinate.
	 * @return true if valid false if not valid.
	 * */
	private boolean checkBounds(int row, int col) {
		if ((row >= 0 && row < SIZE) && (col >= 0 && col < SIZE)) {
			return true;
		} else {
			System.out.println("Not within bounds");
			return false;
		}
	}

	/**
	 * registerEnememyMiss 
	 * @name registerEnemyMiss
	 * @brief Function registers a miss by the player on the ai grid.
	 * @param integer row and integer column grid coordinate.
	 * @return void
	 * */
	public void registerEnemyMiss(int row, int col) {
		aiPlayerGrid[row][col] = miss;
		System.out.println("Enemy MISS " + row + ", " + col);
		sendMessage(new Message(Message.MESSAGE, "AI", reciever, "MISS "
				+ Integer.toString(row) + " " + Integer.toString(col)));
	}

	
	/**
	 * battleLost
	 * @name battleLost
	 * @brief Function sends out battleLost message to the opponent player.
	 * @return void
	 * */
	public void battleLost() {
		sendMessage(new Message(Message.LOST, "AI", reciever,""));
	}

	/**
	 * sendMessage
	 * @name sendMessage
	 * @brief Help function to send out a generic message to the player using the client connection class. 
	 * */
	@Override
	public void sendMessage(Message message) {
		con.sendMessage(message);
	}

	/**
	 * getName
	 * @name getName
	 * @return String ai opponent name
	 * */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * openConnection
	 * @name openConnection
	 * @return true if it was possible to open the connection else it's false.
	 * */
	@Override
	public boolean openConnection() {
		con = new ClientConnection("localhost", 10001);
		con.openConnection();
		con.setBattlePlayer(this);
		return true;
	}

	/**
	 * listen
	 * @name listen
	 * @brief Start listening for messages. 
	 * */
	@Override
	public void listen() {
		if (openConnection())
			new Thread(con).start();
	}

	/**
	 * closeConnection
	 * @name closeConnection
	 * @brief Close the connection held clientConnection.
	 * */
	@Override
	public void closeConnection() {
		con.closeConnection();
	}

	/**
	 * setOpponentDeployed
	 * @name setOpponentDeployed
	 * @brief set the state for if the player has deployed his ships.
	 * */
	@Override
	public void setOpponentDeployed() {
		opponentDeployed = true;
	}

	/**
	 * setPlayerTurn
	 * @name setPlayerTurn
	 * @param boolean to set player turn state.
	 * */
	@Override
	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
		if (playerTurn) {
			new GameTimer(2, 1000).run();
		}
	}

	/**
	 * placeEnemeyShip
	 * @name placeEnemyShip
	 * @brief place an an enemy ship on the grid
	 * @param Ship object, integer row and integer column coordinate.
	 * */
	@Override
	public void placeEnemyShip(Ship ship, int row, int col) {
		if (ship == null) {
			System.out.println("Ship is NULL!!!!");
		} else {
			enemyShipDown = true;
			setShipPositions(ship, row, col);
			System.out.print("Adding ship " + ship.getType() + " [");
			for (Grid grid : ship.getPosition()) {
				System.out.print(grid.getRow() + "," + grid.getCol() + " ");
			}
			System.out.print("]\n");
			shipsDown.add(ship);
		}
	}

	/**
	 * setShipPosition
	 * @name setShipPosition
	 * @brief Place a ship on the grid
	 * @param Ship object, integer row and integer column coordinate
	 * */
	public void setShipPositions(Ship ship, int row, int col) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(row, counter);
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(counter, col);
				counter++;
			}
		}
	}

	/**
	 * checkEnemyDownProbableTargets
	 * @name checkEnemyDownProbableTargets
	 * @brief If there are still valid ship targets to aim for 
	 * */
	public void checkEnemyDownProbableTargets() {
		if (!probableTargets.isEmpty()) {
			for (Ship ship : shipsDown) {
				int row = ship.getStartPosition().getRow();
				int col = ship.getStartPosition().getCol();
				if (ship.getAlignment() == Alignment.HORIZONTAL) {
					int width = ship.getLength() + 2;
					int height = 3;
					if ((col + ship.getLength() - 1) < SIZE) {
						if (col > 0) {
							--col;
						}
						if (!(col + ship.getLength() < SIZE - 1)) {
							--width;
						}
						if (!(row < (SIZE - 1))) {
							--height;
						}

						if (row > 0) {
							--row;
						}

						int rowCounter = row;
						int colCounter = col;
						for (int i = 0; i < height; i++) {
							colCounter = col;
							for (int j = 0; j < width; j++) {
								probableTargets.remove(new Grid(rowCounter,
										colCounter));
								validTargets.remove(new Grid(rowCounter,
										colCounter));
								colCounter++;
							}
							rowCounter++;
						}
					}
				} else if (ship.getAlignment() == Alignment.VERTICAL) {
					int width = 3;
					int height = ship.getLength() + 2;
					if ((row + ship.getLength() - 1) < SIZE) {
						if (row > 0) {
							--row;
						}
						if (!(row + ship.getLength() < SIZE - 1)) {
							--height;
						}
						if (!(col < (SIZE - 1))) {
							--width;
						}
						if (col > 0) {
							--col;

						}
						int rowCounter = row;
						int colCounter = col;
						for (int i = 0; i < width; i++) {
							rowCounter = row;
							for (int j = 0; j < height; j++) {
								probableTargets.remove(new Grid(rowCounter,colCounter));
								validTargets.remove(new Grid(rowCounter,colCounter));
								rowCounter++;
							}
							colCounter++;
						}
					}
				}
			}
			shipsDown.clear();
			enemyShipDown = false;
			prevHit = currHit = new Grid(-1, -1);
		}
	}

	/**
	 * GameTimer
	 * @class GameTimer
	 * @brief And inner help class to help with timing of events.
	 * @param integer seconds and integer amount of delay.
	 * */
	class GameTimer {
		private Timer t;
		private int seconds;
		private final int delay;

		public GameTimer(int seconds, int delay) {
			this.seconds = seconds;
			this.delay = delay;
		}

		/**
		 * run
		 * @name run
		 * @briief start's thread
		 * */
		public void run() {
			t = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					--seconds;
					checkTime();
				}
			});
			t.start();
		}

		/**
		 * checkTime
		 * @name checkTime
		 * @brief if no time left stop.
		 * */
		private void checkTime() {
			if (seconds <= 0) {
				fire();
				t.stop();
				t = null;
			}
		}
	}
}
