package battleship.network;

import static battleship.game.Constants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.Timer;

import battleship.game.BattlePlayer;
import battleship.game.Message;
import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;

public class AIPlayer implements BattlePlayer, NetworkOperations {
	private String name = "AI";
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

	public AIPlayer() {
		init();
		sendMessage(new Message(Message.LOGIN, name, "MultiPlayer"));
		sendMessage(new Message(Message.DEPLOYED, "AI", ""));
	}

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

	private void initEnemyGrid() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				enemyGrid[row][col] = empty;
				validTargets.add(new Grid(row, col));
			}
		}
	}

	public void displayPlayerGrid() {
		for (int row = 0; row < SIZE; row++) {
			System.out.print("[");
			for (int col = 0; col < SIZE; col++) {
				System.out.print(aiPlayerGrid[row][col]);
			}
			System.out.print("]\n");
		}
	}

	public void displayPlayerShips() {
		for (Ship ship : playerShips) {
			System.out.print(ship.getType() + "[ ");
			for (Grid grid : ship.getPosition()) {
				System.out.print(grid.getRow() + "," + grid.getCol() + " ");
			}
			System.out.print(ship.getType() + "]\n");
		}
	}

	public void displayEnemyGrid() {
		for (int row = 0; row < SIZE; row++) {
			System.out.print("[");
			for (int col = 0; col < SIZE; col++) {
				System.out.print(enemyGrid[row][col]);
			}
			System.out.print("]\n");
		}
	}

	public void fire() {
		if (checkProbableTargets()) {
			calculateNextTarget();
		} else {
			System.out.println("Valid target size: " + validTargets.size());
			Grid grid = validTargets.get((validTargets.size() > 0) ? r
					.nextInt(validTargets.size()) : 0);
			int row = grid.getRow();
			int col = grid.getCol();
			if (checkBounds(row, col)) {
				System.out.println("FIRE " + row + ", " + col);
				sendMessage(new Message(Message.MESSAGE, "AI", "FIRE "
						+ Integer.toString(row) + " " + Integer.toString(col)));
				System.out.println("Removing grid [ " + row + "," + col + "]");
				validTargets.remove(grid);
			}
		}
	}

	private boolean checkProbableTargets() {
		if (probableTargets.size() > 0) {
			return true;
		}
		return false;
	}

	private void calculateNextTarget() {
		System.out.print("ProbableTargets [ ");
		for (Grid grid : probableTargets) {
			System.out.print(grid.getRow() + "," + grid.getCol() + " ");
		}
		System.out.print("]\n");
		int randomProbable = r.nextInt(probableTargets.size());
		Grid grid = (Grid) probableTargets.toArray()[randomProbable];
		probableTargets.remove(grid);
		System.out.println("Caluclate Valid target size: "
				+ validTargets.size());
		validTargets.remove(grid);
		int row = grid.getRow();
		int col = grid.getCol();
		System.out.println("FIRE " + row + ", " + col);
		sendMessage(new Message(Message.MESSAGE, "AI", "FIRE "
				+ Integer.toString(row) + " " + Integer.toString(col)));
	}

	public boolean checkHit(int row, int col) {
		if (aiPlayerGrid[row][col] == occupied) {
			return true;
		}
		return false;
	}

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
			} else if (prevHit.getCol() == currHit.getCol()) {
				enemyShipAlignment = Alignment.VERTICAL;
			}
		}
	}

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

	private boolean checkLeftGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (col > 0
				&& !(enemyGrid[row][col - 1] == miss || enemyGrid[row][col - 1] == hit)) {
			return true;
		}
		return false;
	}

	private boolean checkTopGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (row > 0
				&& !(enemyGrid[row - 1][col] == miss || enemyGrid[row - 1][col] == hit)) {
			return true;
		}
		return false;
	}

	private boolean checkRightGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (col < (SIZE - 1)
				&& !(enemyGrid[row][col + 1] == miss || enemyGrid[row][col + 1] == hit)) {
			return true;
		}
		return false;
	}

	private boolean checkBottomGrid(Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (row < (SIZE - 1)
				&& !(enemyGrid[row + 1][col] == miss || enemyGrid[row + 1][col] == hit)) {
			return true;
		}
		return false;
	}

	public void registerEnemyHit(Ship ship, int row, int col) {
		if (checkBounds(row, col)) {
			sendMessage(new Message(Message.MESSAGE, "AI", "HIT "
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

	public void sinkShip(Ship ship) {
		int row = ship.getStartPosition().getRow();
		int col = ship.getStartPosition().getCol();
		if (checkBounds(row, col)) {
			System.out.println("Player SHIP DOWN " + ship.getType());
			sendMessage(new Message(Message.MESSAGE, "AI", "SHIP_DOWN "
					+ ship.getType() + " " + ship.getAlignment() + " "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		}

	}

	public void registerPlayerMiss(int row, int col) {
		if (checkBounds(row, col)) {
			playerTurn = false;
			enemyGrid[row][col] = miss;
			probableTargets.remove(new Grid(row, col));
			sendMessage(new Message(Message.TURN, "AI", ""));
		}
	}

	private boolean checkBounds(int row, int col) {
		if ((row >= 0 && row < SIZE) && (col >= 0 && col < SIZE)) {
			return true;
		} else {
			System.out.println("Not within bounds");
			return false;
		}
	}

	public void registerEnemyMiss(int row, int col) {
		aiPlayerGrid[row][col] = miss;
		System.out.println("Enemy MISS " + row + ", " + col);
		sendMessage(new Message(Message.MESSAGE, "AI", "MISS "
				+ Integer.toString(row) + " " + Integer.toString(col)));
	}

	public void battleLost() {
		sendMessage(new Message(Message.LOST, "AI", ""));
	}

	@Override
	public void sendMessage(Message message) {
		con.sendMessage(message);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean openConnection() {
		con = new ClientConnection("localhost", 10001);
		con.openConnection();
		con.setBattlePlayer(this);
		return true;
	}

	@Override
	public void listen() {
		if (openConnection())
			new Thread(con).start();
	}

	@Override
	public void closeConnection() {
		con.closeConnection();
	}

	@Override
	public void setOpponentDeployed() {
		opponentDeployed = true;
	}

	@Override
	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
		if (playerTurn) {
			new GameTimer(2, 1000).run();
		}
	}

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
						if(col == 0 || !((col + ship.getLength()) < SIZE - 1)) {
							--width;
						}
						if (row == 0 || !(row < (SIZE - 1))) {
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
										colCounter++));
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
						if(row == 0 || !(row + ship.getLength() < SIZE - 1)) {
							--height;
						}
						if (col == 0 || !(col < (SIZE - 1))) {
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
								validTargets.remove(new Grid(rowCounter++,
										colCounter));
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

	class GameTimer {
		private Timer t;
		private int seconds;
		private final int delay;

		public GameTimer(int seconds, int delay) {
			this.seconds = seconds;
			this.delay = delay;
		}

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

		private void checkTime() {
			if (seconds <= 0) {
				fire();
				t.stop();
				t = null;
			}
		}
	}
}
