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
import battleship.ships.Ship;

public class AIPlayer implements BattlePlayer {
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
	private boolean playerTurn;
	private boolean opponentDeployed = false;

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
		initEnemyGrid();
		displayPlayerGrid();
		displayPlayerShips();
		openConnection();
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
			Grid grid = validTargets.get(r.nextInt(validTargets.size()));
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
		if(probableTargets.size() > 0) {
			return true;
		}
		return false;
 	}

	private void calculateNextTarget() {
		System.out.print("ProbableTargets [ ");
		for(Grid grid: probableTargets) {
			System.out.print(grid.getRow() + "," + grid.getCol() + " ");
		}
		System.out.print("]");
		int randomProbable = r.nextInt(probableTargets.size());
		Grid grid = (Grid) probableTargets.toArray()[randomProbable];
		probableTargets.remove(randomProbable);
		int row = grid.getRow();
		int col = grid.getCol();
		System.out.println("FIRE " + row + ", " + col);
		sendMessage(new Message(Message.MESSAGE, "AI", "FIRE "
				+ Integer.toString(row) + " " + Integer.toString(col)));
			validTargets.remove(grid);
			/*
		if (checkBounds(row, col)) {
			
		}
		*/
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
			addNextPossibleTargets(new Grid(row, col));
			new GameTimer(2, 1000).run();
		}
	}

	private void addNextPossibleTargets(Grid grid) {
		if (checkLeftGrid(grid))
			probableTargets.add(new Grid(grid.getRow(), grid.getCol() - 1));
		if (checkTopGrid(grid))
			probableTargets.add(new Grid(grid.getRow() - 1, grid.getCol()));
		if (checkRightGrid(grid))
			probableTargets.add(new Grid(grid.getRow(), grid.getCol() + 1));
		if (checkBottomGrid(grid))
			probableTargets.add(new Grid(grid.getRow() + 1, grid.getCol()));
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
		new Thread(con).start();
		return true;
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
		probableTargets.clear();
		for(Grid grid : ship.getPosition()) {
			if(!(enemyGrid[grid.getRow()][grid.getCol()] == hit)) {
				enemyGrid[grid.getRow()][grid.getCol()] = hit;
			}
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
