package battleship.network;

import static battleship.game.Constants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import battleship.game.BattlePlayer;
import battleship.game.GameMode;
import battleship.game.Message;
import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.BattleShipFactory;
import battleship.ships.Ship;
import battleship.ships.ShipType;

/**
 * PlayerProxy
 * 
 * @class PlayerProxy
 * @extends Thread
 * @brief Client proxy class of the player.
 * */
public class PlayerProxy extends Thread {
	protected Socket socket;
	protected Server server;
	protected String address;
	protected Message msg;
	protected String name;
	protected int playerId;
	private GameMode mode;
	private boolean playing;
	private char[][] playerGrid;
	private AIPlayer aiPlayer;
	private boolean aiMatch;
	private boolean deployed;
	private boolean playerTurn;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected boolean running = true;

	/**
	 * PlayerProxy Constructor
	 * 
	 * @name PlayerProxy
	 * */
	public PlayerProxy(Socket socket, Server server, int id) {
		this.socket = socket;
		this.server = server;
		playerId = id;
		address = socket.getInetAddress().getHostAddress();
		System.out.println("Client with address " + address + " and port "
				+ socket.getPort()
				+ "\nhas established a connection with the server.\n ");
		playerGrid = new char[SIZE][SIZE];
		initGrid(playerGrid);
		aiMatch = false;
		playing = false;
	}

	/**
	 * getPlayerName
	 * 
	 * @name getPlayerName
	 * @brief Return player names.
	 * @param None
	 * @return returns player name as string
	 * */
	public String getPlayerName() {
		return name;
	}

	/**
	 * getDeployed
	 * 
	 * @name getDeployed
	 * @return boolean, has player deployed his ships
	 * */
	public boolean getDeployed() {
		return deployed;
	}

	/**
	 * getMode
	 * 
	 * @name getMode
	 * @return return the player mode for playerProxy
	 * */
	public GameMode getMode() {
		return mode;
	}

	/**
	 * getPlayering
	 * 
	 * @name getPlaying
	 * @return boolean
	 * */
	public boolean getPlaying() {
		return playing;
	}

	/**
	 * initGrid
	 * 
	 * @name initGrid
	 * @brief Function initiates a playerGrid with empty cells aka water
	 * */
	private void initGrid(char[][] grid) {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				grid[row][col] = empty;
			}
		}
	}

	/**
	 * run
	 * 
	 * @name run
	 * @brief proxy run loop
	 * @param None
	 * @return void
	 * */
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			while (running) {
				try {
					msg = (Message) in.readObject();
					handleMessage(msg);
				} catch (ClassNotFoundException e) {
					System.out.println(name + " ClassNotFoundException");
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println(name + " IOException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	/**
	 * handleMessage
	 * 
	 * @name handleMessage
	 * @brief Function is responsible for handling messages accordingly.
	 * @param Takes
	 *            a Message.
	 * @return void
	 * */
	private void handleMessage(Message msg) {
		switch (msg.getType()) {
		case Message.LOGIN:
			handleLogin();
			break;
		case Message.LOGOUT:
			handleLogout();
			break;
		case Message.MESSAGE:
			if (mode == GameMode.SinglePlayer) {
				if (checkMessage(msg)) {
					parseMessage(msg);
				} else {
					sendMessage(new Message(Message.MESSAGE, "ERROR", ""));
				}
			} else {
				server.sendMessageToOpponent(msg);
			}
			/*
			 * if (checkMessage()) { server.sendMessageToOpponent(msg); }
			 */
			break;
		case Message.CHAT:
			server.sendMessageToAll(msg);
			break;
		case Message.DEPLOYED:
			deployed = true;
			if(mode == GameMode.SinglePlayer) {
			randomizePlayerTurn();
			} else if(mode == GameMode.MultiPlayer) {
				server.randomizePlayerTurn();
			}
			break;
		case Message.TURN:
			server.sendMessageToOpponent(msg);
			break;
		case Message.LOST:
			server.sendMessageToOpponent(msg);
			break;
		}
	}

	/**
	 * handleLogin
	 * 
	 * @name handleLogin
	 * @brief handles the client login procedure, depending on selected GameMode
	 * */
	private void handleLogin() {
		name = msg.getName();
		if (msg.getMessage().equalsIgnoreCase("Singleplayer")) {
			mode = GameMode.SinglePlayer;
			aiPlayer = new AIPlayer();
			aiMatch = true;
			playing = true;
			deployed = true;
			
		} else if (msg.getMessage().equalsIgnoreCase("Multiplayer")) {
			playing = false;
			mode = GameMode.MultiPlayer;
			server.sendPlayers(name);
			// see if there is players to start a match
			if (!server.lookForPlayerMulti()) {
				playing = false; // wait
			} else {
				// get opponent and start match
			}
		}
	}
	
	private void randomizePlayerTurn() {
		Random r = new Random();
		int value = r.nextInt(100);
		if (value < 50) {
			sendMessage(new Message(Message.TURN, name, ""));
		} else {
			sendMessage(new Message(Message.TURN, "AI", ""));
			aiPlayer.setPlayerTurn(true);
		}
	}

	private void handleLogout() {
		server.removePlayerProxy(this.playerId);
		server.sendMessageToOpponent(new Message(Message.CHAT, msg.getName(),
				msg.getMessage()));
	}

	private boolean checkMessage(Message msg) {
		boolean checked = false;
		String[] tokens = msg.getMessage().split(" ");
		switch (tokens[0].toUpperCase()) {
		case "PLACING":
			checked = checkPlaceMessage(tokens, playerGrid);
			break;
		case "SHIP_DOWN":
			checked = checkShipDownMessage(tokens, playerGrid);
			break;
		case "FIRE":
			checked = checkFireMessage(tokens, playerGrid);
			break;
		case "HIT":
			checked = checkHitMessage(tokens, playerGrid);
			break;
		case "MISS":
			checked = checkMissMessage(tokens, playerGrid);
			break;
		}
		return checked;
	}

	private boolean checkPlaceMessage(String[] tokens, char[][] grid) {
		Ship ship = null;
		ShipType type;
		Alignment alignment = Alignment.HORIZONTAL;
		switch (tokens[1].toUpperCase()) {
		default:
		case "CARRIER":
			type = ShipType.CARRIER;
			break;
		case "DESTROYER":
			type = ShipType.DESTROYER;
			break;
		case "SUBMARINE":
			type = ShipType.SUBMARINE;
			break;
		}

		if (tokens[2].equalsIgnoreCase("vertical")) {
			alignment = Alignment.VERTICAL;
		}
		ship = BattleShipFactory.getShip(type);
		ship.setAlignment(alignment);
		int row = Integer.parseInt(tokens[3]);
		int col = Integer.parseInt(tokens[4]);

		if (checkShipPlacement(ship, row, col, grid)) {
			placeShip(ship, row, col, grid);
			return true;
		}
		return false;
	}

	private boolean checkShipDownMessage(String[] tokens, char[][] grid) {
		Ship ship = null;
		ShipType type;
		Alignment alignment = Alignment.HORIZONTAL;
		switch (tokens[1].toUpperCase()) {
		default:
		case "CARRIER":
			type = ShipType.CARRIER;
			break;
		case "DESTROYER":
			type = ShipType.DESTROYER;
			break;
		case "SUBMARINE":
			type = ShipType.SUBMARINE;
			break;
		}

		if (tokens[2].equalsIgnoreCase("vertical")) {
			alignment = Alignment.VERTICAL;
		}
		ship = BattleShipFactory.getShip(type);
		int row = Integer.parseInt(tokens[3]);
		int col = Integer.parseInt(tokens[4]);
		return checkShipDown(row, col, ship.getLength(), alignment, grid);
	}

	private boolean checkFireMessage(String[] tokens, char[][] grid) {
		return true;
	}

	private boolean checkHitMessage(String[] tokens, char[][] grid) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isOccupied(row, col, grid) && !isHit(row, col, grid)) {
			setHit(row, col, grid);
			return true;
		}
		return false;
	}

	private boolean checkMissMessage(String[] tokens, char[][] grid) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isEmpty(row, col, grid) && !isMissed(row, col, grid)) {
			setMiss(row, col, grid);
			return true;
		}
		return false;
	}

	private boolean isEmpty(int row, int col, char[][] grid) {
		return grid[row][col] == empty;
	}

	private boolean isHit(int row, int col, char[][] grid) {
		return grid[row][col] == hit;
	}

	private boolean isMissed(int row, int col, char[][] grid) {
		return grid[row][col] == miss;
	}

	private boolean isOccupied(int row, int col, char[][] grid) {
		return grid[row][col] == occupied;
	}

	private void setOccupied(int row, int col, char[][] grid) {
		grid[row][col] = occupied;
	}

	private void setHit(int row, int col, char[][] grid) {
		grid[row][col] = hit;
	}

	private void setMiss(int row, int col, char[][] grid) {
		grid[row][col] = miss;
	}

	private boolean checkShipPlacement(Ship ship, int row, int col,
			char[][] grid) {
		int length = ship.getLength();
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			if ((col + length - 1) < SIZE) {
				counter = col;
				for (int i = 0; i < length; i++) {
					if (!isEmpty(row, counter, grid)) {
						return false;
					}
					counter++;
				}
			} else {
				return false;
			}

		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			if ((row + length - 1) < SIZE) {
				counter = row;
				for (int i = 0; i < length; i++) {
					if (!isEmpty(counter, col, grid)) {
						return false;
					}
					counter++;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean checkShipDown(int row, int col, int length,
			Alignment alignment, char[][] grid) {
		int counter;
		if (alignment == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < length; i++) {
				if (!isHit(row, counter, grid)) {
					return false;
				}
				counter++;
			}
		} else if (alignment == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < length; i++) {
				if (!isHit(counter, col, grid)) {
					return false;
				}
				counter++;
			}
		}

		return true;
	}

	private void placeShip(Ship ship, int row, int col, char[][] grid) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < ship.getLength(); i++) {
				setOccupied(row, counter, grid);
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < ship.getLength(); i++) {
				setOccupied(counter, col, grid);
				counter++;
			}
		}
	}

	private void parseMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		switch (tokens[0].toUpperCase()) {
		case "SHIP_DOWN":
			parseShipDownMessage(tokens);
			break;
		case "FIRE":
			parseFireMessage(tokens);
			break;
		case "HIT":
			parseHitMessage(tokens);
			break;
		case "MISS":
			parseMissMessage(tokens);
			break;
		}
	}

	private void parseMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		aiPlayer.registerPlayerMiss(row, col);
	}

	private void parseShipDownMessage(String[] tokens) {
		Ship ship = null;
		ShipType type;
		Alignment alignment = Alignment.HORIZONTAL;
		switch (tokens[1].toUpperCase()) {
		default:
		case "CARRIER":
			type = ShipType.CARRIER;
			break;
		case "DESTROYER":
			type = ShipType.DESTROYER;
			break;
		case "SUBMARINE":
			type = ShipType.SUBMARINE;
			break;
		}

		if (tokens[2].equalsIgnoreCase("vertical")) {
			alignment = Alignment.VERTICAL;
		}
		ship = BattleShipFactory.getShip(type);
		ship.setAlignment(alignment);
		int row = Integer.parseInt(tokens[3]);
		int col = Integer.parseInt(tokens[4]);
		aiPlayer.placeEnemyShip(ship, row, col);
	}

	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		aiPlayer.registerFire(row, col);
	}

	private void parseHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		aiPlayer.registerPlayerHit(row, col);
	}

	/**
	 * sendMessage
	 * 
	 * @name sendMessage
	 * @brief Function sends messages
	 * @param Takes
	 *            a Message
	 * @return void
	 * */
	public void sendMessage(Message msg) {
		try {
			out.writeObject(msg);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * closeConnection
	 * 
	 * @name closeConnection
	 * @brief Responsible to closing the connection between server and player
	 * @param None
	 * @return void
	 * */
	protected void closeConnection() {
		int port = socket.getPort();
		try {
			in.close();
			out.close();
			socket.close();
			System.out.println("Client " + name + " with address " + address
					+ " and port " + port
					+ "\nhas closed the connection with the server.\n ");
		} catch (IOException e) {
			// System.err.println(e.getMessage());
		}
	}

	class AIPlayer {
		// private String AIName = "AI";
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
		// private boolean playerTurn;
		private boolean opponentDeployed = false;
		private boolean enemyShipDown = false;
		private Grid prevHit, currHit;
		private Alignment enemyShipAlignment;

		public AIPlayer() {
			init();
			// sendMessage(new Message(Message.LOGIN, name, "MultiPlayer"));
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

				Grid grid = validTargets.get((validTargets.size() > 0) ? r
						.nextInt(validTargets.size()) : 0);
				int row = grid.getRow();
				int col = grid.getCol();
				if (checkBounds(row, col)) {
					System.out.println("FIRE " + row + ", " + col);
					sendMessage(new Message(Message.MESSAGE, "AI", "FIRE "
							+ Integer.toString(row) + " "
							+ Integer.toString(col)));
					System.out.println("Removing grid [ " + row + "," + col
							+ "]");
					validTargets.remove(grid);
					System.out.println("Valid target size: "
							+ validTargets.size());
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
			validTargets.remove(grid);
			System.out.println("Caluclate Valid target size: "
					+ validTargets.size());
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
					updateProbableTargets();
				} else if (prevHit.getCol() == currHit.getCol()) {
					enemyShipAlignment = Alignment.VERTICAL;
					updateProbableTargets();
				}
			}
		}

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

		public String getName() {
			return name;
		}

		public void setOpponentDeployed() {
			opponentDeployed = true;
		}

		public void setPlayerTurn(boolean playerTurn) {
			// this.playerTurn = playerTurn;
			if (playerTurn) {
				new GameTimer(2, 1000).run();
			}
		}

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
}
