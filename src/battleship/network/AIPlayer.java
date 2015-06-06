/**
 * @file AIPlayer.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.network;

import static battleship.game.Constants.SIZE;
import static battleship.game.Constants.empty;
import static battleship.game.Constants.hit;
import static battleship.game.Constants.miss;
import static battleship.game.Constants.occupied;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.Timer;

import battleship.game.Message;
import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;

/**
 * AIPlayer
 * 
 * @class AIPlayer
 * @package battleship.network
 * @brief Class represents a server AI player
 * */
public class AIPlayer {
		private PlayerProxy player;
		private RandomShipPlacer shipPlacer;
		private Random r;
		private char[][] aiPlayerGrid, enemyGrid;
		private Vector<Ship> playerShips;
		private int remainingShips;
		private ArrayList<Grid> validTargets;
		protected boolean running = true;
		private Set<Grid> probableTargets;
		private Vector<Ship> shipsDown;
		private boolean playerTurn = false;
		private boolean enemyShipDown = false;
		private Grid prevHit, currHit;
		private Alignment enemyShipAlignment;

		/**
		 * AIPlayer
		 * 
		 * @name AIPlayer
		 * @constructor One argument constructor
		 * @brief sets the PlayerProxy instance
		 * @param player instance of PlayerProxy used to send messages to player
		 * */
		public AIPlayer(PlayerProxy player) {
			this.player = player;
			init();
		}

		/**
		 * init
		 * 
		 * @name init
		 * @brief initiates the AI player Grid and ships at random
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
			playerTurn = false;
		}

		/**
		 * initEnemyGrid
		 * 
		 * @name initEnemyGrid
		 * @brief initiates the enemyGrid and adds valid targets
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
		 * fire
		 * 
		 * @name fire
		 * @brief fires on enemyGrid at random or based on probable targets
		 * */
		public void fire() {
			if (checkProbableTargets()) {
				calculateNextTarget();
			} else {

				Grid grid = validTargets.get((validTargets.size() > 0) ? r
						.nextInt(validTargets.size()) : 0);
				int row = grid.getRow();
				int col = grid.getCol();
				if (checkBounds(row, col)) {
					player.sendMessage(new Message(Message.MESSAGE, "AI", player.name,
							"FIRE " + Integer.toString(row) + " "
									+ Integer.toString(col)));
					validTargets.remove(grid);
				}
			}
		}

		/**
		 * checkProbableTargets
		 * 
		 * @name checkProbableTargets
		 * @brief checks whether there are probable targets
		 * @returns true if probableTargets is not empty
		 * */
		private boolean checkProbableTargets() {
			if (probableTargets.size() > 0) {
				return true;
			}
			return false;
		}

		/**
		 * calculateNextTarget
		 * 
		 * @name calculateNextTarget
		 * @brief calculates and fires based on random probable target, sends message to player
		 * */
		private void calculateNextTarget() {
			int randomProbable = r.nextInt(probableTargets.size());
			Grid grid = (Grid) probableTargets.toArray()[randomProbable];
			probableTargets.remove(grid);
			validTargets.remove(grid);
			int row = grid.getRow();
			int col = grid.getCol();
			player.sendMessage(new Message(Message.MESSAGE, "AI", player.name, "FIRE "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		}

		/**
		 * checkHit
		 * 
		 * @name checkHit
		 * @brief checks whether aiPlayerGrid is occupied
		 * @param row the aiPlayerGrid row coordinate
		 * @param row the aiPlayerGrid col coordinate
		 * @return true if occupied, false otherwise
		 * */
		public boolean checkHit(int row, int col) {
			if (aiPlayerGrid[row][col] == occupied) {
				return true;
			}
			return false;
		}

		/**
		 * registerFire
		 * 
		 * @name registerFire
		 * @brief checks if AI enemyGrid is hit or missed, calls appropriate function based on check
		 * @param row the aiPlayerGrid row coordinate
		 * @param row the aiPlayerGrid col coordinate
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
		 * 
		 * @name registerPlayerHit
		 * @brief register a hit on enemyGrid, starts a gameTimer to fire again
		 * @param row the aiPlayerGrid row coordinate
		 * @param row the aiPlayerGrid col coordinate
		 * */
		public void registerPlayerHit(int row, int col) {
			if (checkBounds(row, col)) {
				enemyGrid[row][col] = hit;
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
		 * 
		 * @name comparePrevHits
		 * @brief compares a previous hit with param grid to determine enemy ship alignment
		 * @param current Grid hit
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
		 * 
		 * @name updateProbableTargets
		 * @brief removes probableTargets based on determined ship alignment
		 * */
		private void updateProbableTargets() {
			if (enemyShipAlignment == Alignment.HORIZONTAL) {
				for (Iterator<Grid> i = probableTargets.iterator(); i.hasNext();) {
					Grid grid = i.next();
					if (grid.getRow() != currHit.getRow()) {
						i.remove();
					}
				}
			} else if (enemyShipAlignment == Alignment.VERTICAL) {
				for (Iterator<Grid> i = probableTargets.iterator(); i.hasNext();) {
					Grid grid = i.next();
					if (grid.getCol() != currHit.getCol()) {
						i.remove();
					}
				}
			}
		}

		/**
		 * addNextPossibleTargets
		 * 
		 * @name addNextPossibleTargets
		 * @brief adds Grids to probableTargets surrounding a hit 
		 * @param grid the grid that was hit
		 * */
		private void addNextPossibleTargets(Grid grid) {
			if (prevHit.getRow() == -1 || currHit.getRow() == -1) {
				if (checkLeftGrid(grid))
					probableTargets.add(new Grid(grid.getRow(),
							grid.getCol() - 1));
				if (checkTopGrid(grid))
					probableTargets.add(new Grid(grid.getRow() - 1, grid
							.getCol()));
				if (checkRightGrid(grid))
					probableTargets.add(new Grid(grid.getRow(),
							grid.getCol() + 1));
				if (checkBottomGrid(grid))
					probableTargets.add(new Grid(grid.getRow() + 1, grid
							.getCol()));
			} else {
				if (enemyShipAlignment == Alignment.HORIZONTAL) {
					if (checkLeftGrid(grid))
						probableTargets.add(new Grid(grid.getRow(), grid
								.getCol() - 1));
					if (checkRightGrid(grid))
						probableTargets.add(new Grid(grid.getRow(), grid
								.getCol() + 1));
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
		 * 
		 * @name checkLeftGrid
		 * @brief checks whether a grid to the left is not missed or hit 
		 * @param grid the grid that was hit
		 * @return true if the grid is not missed or hit, false otherwise
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
		 * 
		 * @name checkTopGrid
		 * @brief checks whether a grid above is not missed or hit 
		 * @param grid the grid that was hit
		 * @return true if the grid is not missed or hit, false otherwise
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
		 * 
		 * @name checkRightGrid
		 * @brief checks whether a grid to the right is not missed or hit 
		 * @param grid the grid that was hit
		 * @return true if the grid is not missed or hit, false otherwise
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
		 * 
		 * @name checkBottomGrid
		 * @brief checks whether a grid below is not missed or hit 
		 * @param grid the grid that was hit
		 * @return true if the grid is not missed or hit, false otherwise
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
		 * 
		 * @name registerEnemyHit
		 * @brief registers a hit on aiPlayerGrid and checks whether a ship is sunk, send message to player
		 * @param ship
		 *            the ship that was hit
		 * @param row
		 *            the aiPlayerGrid row coordinate
		 * @param col
		 *            the aiPlayerGrid col coordinate
		 * */
		public void registerEnemyHit(Ship ship, int row, int col) {
			if (checkBounds(row, col)) {
				player.sendMessage(new Message(Message.MESSAGE, "AI", player.name, "HIT "
						+ Integer.toString(row) + " " + Integer.toString(col)));
				aiPlayerGrid[row][col] = hit;
				ship.hit();
				if (!ship.isAlive()) {
					sinkShip(ship);
					--remainingShips;
					if (remainingShips == 0) {
						battleLost();
					}
				}
			}
		}

		/**
		 * sinkShip
		 * 
		 * @name sinkShip
		 * @brief sends message about the ship sunk
		 * @param ship
		 *            the ship that was sunk
		 * */
		public void sinkShip(Ship ship) {
			int row = ship.getStartPosition().getRow();
			int col = ship.getStartPosition().getCol();
			if (checkBounds(row, col)) {
				player.sendMessage(new Message(Message.MESSAGE, "AI", player.name,
						"SHIP_DOWN " + ship.getType() + " "
								+ ship.getAlignment() + " "
								+ Integer.toString(row) + " "
								+ Integer.toString(col)));
			}

		}

		/**
		 * registerPlayerMiss
		 * 
		 * @name registerPlayerMiss
		 * @brief register player miss and sends message to player
		 * @param row
		 *            the enemyGrid row coordinate
		 * @param col
		 *            the enemyGrid col coordinate
		 * */
		public void registerPlayerMiss(int row, int col) {
			if (checkBounds(row, col)) {
				playerTurn = false;
				enemyGrid[row][col] = miss;
				probableTargets.remove(new Grid(row, col));
				player.sendMessage(new Message(Message.TURN, "AI", player.name, ""));
			}
		}

		/**
		 * checkBounds
		 * 
		 * @name checkBounds
		 * @brief checks whether the row and col arguments are within grid size bounds
		 * @param row
		 *            the enemyGrid row coordinate
		 * @param col
		 *            the enemyGrid col coordinate
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
		 * registerEnemyMiss
		 * 
		 * @name registerEnemyMiss
		 * @brief register Player miss and sends message to player
		 * @param row
		 *            the enemyGrid row coordinate
		 * @param col
		 *            the enemyGrid col coordinate
		 * */
		public void registerEnemyMiss(int row, int col) {
			aiPlayerGrid[row][col] = miss;
			System.out.println("Enemy MISS " + row + ", " + col);
			player.sendMessage(new Message(Message.MESSAGE, "AI", player.name, "MISS "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		}

		/**
		 * battleLost
		 * 
		 * @name battleLost
		 * @brief sends message to player that AI lost the game, sets AI turn to false
		 * */
		public void battleLost() {
			player.sendMessage(new Message(Message.LOST, "AI", player.name, ""));
			this.playerTurn = false;
		}

		/**
		 * getName
		 * 
		 * @name getName
		 * @brief returns the name of the player as a String
		 * @return player name
		 * */
		public String getName() {
			return player.name;
		}

		/**
		 * setPlayerTurn
		 * 
		 * @name setPlayerTurn
		 * @brief sets player turn to true and starts game timer
		 * @param playerTurn sets the playerTurn to this value
		 * */
		public void setPlayerTurn(boolean playerTurn) {
			this.playerTurn = playerTurn;
			new GameTimer(2, 1300).run();
		}

		/**
		 * placeEnemyShip
		 * 
		 * @name placeEnemyShip
		 * @brief sets enemyShipDown flag to true, adds ship to shipsDown Vector
		 * @param ship
		 *            the ship that was sunk
		 * @param row
		 *            the aiPlayerGrid row coordinate
		 * @param col
		 *            the aiPlayerGrid col coordinate
		 * */
		public void placeEnemyShip(Ship ship, int row, int col) {
			enemyShipDown = true;
			setShipPositions(ship, row, col);
			shipsDown.add(ship);
		}

		/**
		 * setShipPositions
		 * 
		 * @name setShipPositions
		 * @brief adds positions of ship sunk
		 * @param ship
		 *            the ship that was sunk
		 * @param row
		 *            the aiPlayerGrid row coordinate
		 * @param col
		 *            the aiPlayerGrid col coordinate
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
		 * 
		 * @name checkEnemyDownProbableTargets
		 * @brief removes grids and surrounding grids from valid, and probable targets, from ship that was sunk
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
									probableTargets.remove(new Grid(rowCounter,
											colCounter));
									validTargets.remove(new Grid(rowCounter,
											colCounter));
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
		 * 
		 * @class GameTimer
		 * @name GameTimer
		 * @brief Inner class that starts a Timer based on given time and delay 
		 * */
		class GameTimer {
			private Timer t;
			private int seconds;
			private final int delay;

			/**
			 * GameTimer
			 * 
			 * @constructor GameTimer A two-arguments constructor
			 * @name GameTimer
			 * @param seconds the number of seconds to fire the events
			 * @param delay the frequency to fire events
			 * */
			public GameTimer(int seconds, int delay) {
				this.seconds = seconds;
				this.delay = delay;
			}

			/**
			 * run
			 * 
			 * @name run
			 * @brief starts timer based on delay frequency, events decrements seconds
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
			 * 
			 * @name checkTime
			 * @brief checks whether seconds have reached target value
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