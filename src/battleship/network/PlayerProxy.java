package battleship.network;

import static battleship.game.Constants.SIZE;
import static battleship.game.Constants.empty;
import static battleship.game.Constants.hit;
import static battleship.game.Constants.miss;
import static battleship.game.Constants.occupied;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import battleship.game.GameMode;
import battleship.game.Message;
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
	private String receiver, toServer;

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
			server.sendMessageToOpponent(msg);
			/*
			 * if (checkMessage()) { server.sendMessageToOpponent(msg); }
			 */
			break;
		case Message.CHAT:
			server.sendMessageToAll(msg);
			break;
		case Message.CHALLENGE:
			parseChallengeMessage(msg);
			break;
		case Message.DEPLOYED:
			deployed = true;
			server.randomizePlayerTurn();
			break;
		case Message.TURN:
			server.sendMessageToOpponent(msg);
			break;
		case Message.LOST:
			server.sendMessageToOpponent(msg);
			break;
		case Message.MODE: 
			parseModeMessage(msg);
			break;
		}
	}

	private void parseModeMessage(Message msg) {
		if(msg.getMessage().equalsIgnoreCase("SinglePlayer")) {
			System.out.println(msg.getName() + " wants a singlePalyer game");
			mode = GameMode.SinglePlayer;
			aiPlayer = new AIPlayer(msg.getName());
		} else if (msg.getMessage().equalsIgnoreCase("MultiPlayer")) {
			mode = GameMode.MultiPlayer;
		}
	}

	private void parseChallengeMessage(Message msg2) {
		System.out.println(msg.getName() + " sends a Challenge");
		if (msg.getReciever().equalsIgnoreCase("AI")) {
			aiPlayer = new AIPlayer(msg.getName());
		} else {
			server.sendChallengeRequest(msg);
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
		// server.sendPlayers(name);
		/*
		 * if (msg.getMessage().equalsIgnoreCase("Singleplayer")) { mode =
		 * GameMode.SinglePlayer; aiPlayer = new AIPlayer(name); aiMatch = true;
		 * playing = true; } else if
		 * (msg.getMessage().equalsIgnoreCase("Multiplayer")) { playing = false;
		 * mode = GameMode.MultiPlayer; // server.sendPlayers(name); // see if
		 * there is players to start a match if (!server.lookForPlayerMulti()) {
		 * playing = false; // wait } else { // get opponent and start match } }
		 */
	}

	private void handleLogout() {
		server.removePlayerProxy(this.playerId);
		server.sendMessageToOpponent(new Message(Message.CHAT, msg.getName(),
				toServer, msg.getMessage()));
	}

	private boolean checkMessage() {
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
			e.printStackTrace();
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
}
