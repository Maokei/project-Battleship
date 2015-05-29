package battleship.network;

import static battleship.game.Constants.SIZE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

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
	private ArrayList<Ship> playerProxyShips;
	private char[][] playerGrid, proxyGrid;
	private ArrayList<Integer> nonDeployedGrids;
	private ArrayList<Integer> nonShotOrMissedGrids;
	private boolean deployed;
	private boolean playerTurn;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected boolean running = true;
	private char empty = 'e';
	private char occupied = 'o';
	private char hit = 'h';
	private char miss = 'm';
	
	/**
	 * PlayerProxy Constructor
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
		initProxyDummy();
	}
	
	private void initProxyDummy() {
		proxyGrid = RandomShipPlacer.getRandomShipGrid();
		for(int row = 0; row < SIZE; row++) {
			System.out.print("[");
			for(int col = 0; col < SIZE; col++) {
				System.out.print(proxyGrid[row][col]);
			}
			System.out.print("]\n");
		}
		nonDeployedGrids = new ArrayList<Integer>(100);
		nonShotOrMissedGrids = new ArrayList<Integer>(100);
		fillGridList(nonDeployedGrids);
		fillGridList(nonShotOrMissedGrids);
	}

	/**
	 * initGrid
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
	
	private void fillGridList(ArrayList<Integer> grid) {
		int counter = 0;
		for(Integer i : grid) {
			grid.add(i, counter++);
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
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// System.err.println(e.getMessage());
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
		int type = msg.getType();
		System.out.println("got message typ: " + type);
		switch (type) {
		case Message.LOGIN:
			if (server.getNumberOfCurrentPlayers() > server
					.getNumberOfPlayers()) {
				name = msg.getName();
				server.sendMessageToSender(new Message(Message.MESSAGE, msg
						.getName(), "Server full"));
				this.closeConnection();
				server.removePlayerProxy(this);
				// messages.append("Server full, attempt to join game failed.\nConnection closed for "
				// + name + "\n");
			} else {
				name = msg.getName();
				if(msg.getMessage().equalsIgnoreCase("Singleplayer")) {
					mode = GameMode.SinglePlayer;
					System.out.println(name + "Choosed to play a singlePlayer game");
				} else if(msg.getMessage().equalsIgnoreCase("Multiplayer")) {
					mode = GameMode.MultiPlayer;
					System.out.println(name + "Choosed to play a MultiPlayer game");
				}
				// sendMessageToOpponent(new Message(Message.CHAT,msg.getName(),
				// "Logged in"));
			}
			break;
		case Message.LOGOUT:
			server.removePlayerProxy(this.playerId);
			server.sendMessageToOpponent(new Message(Message.CHAT, msg
					.getName(), msg.getMessage()));
			break;
		case Message.MESSAGE:
			if (checkMessage()) {
				System.out.println("Checking " + msg.getName() + " message");
				server.sendMessageToOpponent(msg);
			}
			break;
		case Message.CHAT:
			server.sendMessageToAll(msg);
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
		}
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

	private boolean checkShipPlacement(Ship ship, int row, int col, char[][] grid) {
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
			System.out.println("Client with address " + address + " and port "
					+ port + "\nhas closed the connection with the server.\n ");
		} catch (IOException e) {
			// System.err.println(e.getMessage());
		}
	}

	/**
	 * getPlayerName
	 * 
	 * @name getPlaterName
	 * @brief Return plater names.
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
}
