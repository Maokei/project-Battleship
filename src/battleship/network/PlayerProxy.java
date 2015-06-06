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
	private Socket socket;
	private Server server;
	private String address;
	private Message msg;
	protected String name;
	protected int playerId;
	protected GameMode mode;
	protected boolean playing;
	protected boolean hasOpponent;
	protected String opponent = "";
	private char[][] playerGrid;
	private AIPlayer aiPlayer;
	private boolean aiMatch;
	protected boolean deployed;
	private boolean playerTurn;
	private ObjectInputStream in;
	private ObjectOutputStream out;
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
		hasOpponent = false;
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
	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public void setHasOpponent(boolean hasOpponent) {
		this.hasOpponent = hasOpponent;
	}

	public boolean getHasOpponent() {
		return hasOpponent;
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
				parseMessage(msg);
			} else if (mode == GameMode.MultiPlayer){
				server.sendMessage(msg);
			}
			break;
		case Message.CHAT:
			server.sendMessage(msg);
			break;
		case Message.DEPLOYED:
			deployed = true;
			if (mode == GameMode.SinglePlayer) {
				randomizePlayerTurn();
			} else if (mode == GameMode.MultiPlayer) {
				if(hasOpponent)
					server.randomizePlayerTurn(msg);
			}
			break;
		case Message.TURN:
			if (mode == GameMode.SinglePlayer) {
				aiPlayer.setPlayerTurn(true);
			} else {
				server.sendMessage(msg);
			}
			break;
		case Message.LOST:
			if (mode == GameMode.SinglePlayer) {
				aiPlayer.setPlayerTurn(false);
			} else {
				server.sendMessage(msg);
			}
			break;
		case Message.CHALLENGE:
			parseChallengeMessage(msg);
			break;
		case Message.MODE:
			parseModeMessage(msg);
			break;
		case Message.VALID:
			parseValidMessage(msg);
			break;
		}
	}

	/**
	 * parseValidMessage
	 * @name parseValidMessage
	 * @param Takes a Message to be parsed.
	 * */
	private void parseValidMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		if(checkMessage(msg)) {
			sendMessage(new Message(Message.VALID, Valid_Move, tokens[0], msg.getMessage()));
			System.out.println("Message " + msg.getMessage() + " TRUE");
		} else {
			String message = msg.getMessage();
			sendMessage(new Message(Message.VALID, NonValid_Move, tokens[0], msg.getMessage()));
			System.out.println("Message " + msg.getMessage() + " FALSE");
		}
	}

	/**
	 * parseModeMessage
	 * @name parseModeMessage
	 * @param Takes a GameMode message to parse. 
	 * */
	private void parseModeMessage(Message msg) {
		if (msg.getSender().equalsIgnoreCase(name)) {
			if (msg.getMessage().equalsIgnoreCase("SinglePlayer")) {
				mode = GameMode.SinglePlayer;
				aiPlayer = new AIPlayer(this);
				aiMatch = true;
				playing = true;
			}
		}
	}

	/**
	 * parseChallangeMessage
	 * @name parseChallangeMessage
	 * @param Takes a Challange message to parse. 
	 * */
	private void parseChallengeMessage(Message msg) {
		if(msg.getMessage().equalsIgnoreCase(Challenge_Request)) {
			server.sendMessageToAllPlayers(msg);
		} else if(msg.getMessage().equalsIgnoreCase(Challenge_Name)) {
			opponent = msg.getReceiver();
			hasOpponent = true;
			playing = true;
		}else {
			if(msg.getMessage().equalsIgnoreCase(Challenge_Accept)) {
				opponent = msg.getReceiver();
				hasOpponent = true;
				playing = true;
			}
			server.sendMessage(msg);
		}
	}

	/**
	 * handleLogin
	 * 
	 * @name handleLogin
	 * @brief handles the client login procedure, depending on selected GameMode
	 * */
	private void handleLogin() {
		name = msg.getSender();
		if (msg.getMessage().equalsIgnoreCase("Singleplayer")) {
			mode = GameMode.SinglePlayer;
			aiPlayer = new AIPlayer(this);
			aiMatch = true;
			playing = true;
		} else if (msg.getMessage().equalsIgnoreCase("Multiplayer")) {
			playing = false;
			mode = GameMode.MultiPlayer;
			if (server.getPlayerCount() > 1) {
				System.out.println("Sending all players in Proxy");
				server.sendAllPlayers();
				/*
				server.sendMessageToAllPlayers(new Message(Message.CHALLENGE, name, "",
						Challenge_Request));
				// server.checkForOpponentTo(name);
				
				 */
			} else {
				sendMessage(new Message(Message.AIMATCH, name, "", ""));
			}
			// server.sendPlayers(name);
			// see if there is players to start a match
			if (!server.lookForPlayerMulti()) {
				playing = false; // wait
			} else {
				// get opponent and start match
			}
		}
	}

	private void randomizePlayerTurn() {
		if (deployed) {
			Random r = new Random();
			int value = r.nextInt(100);
			if (value < 50) {
				sendMessage(new Message(Message.TURN, "AI", name,""));
			} else {
				aiPlayer.setPlayerTurn(true);
			}
		}
	}

	private void handleLogout() {
		server.removePlayerProxy(this.playerId);
		server.sendMessage(new Message(Message.CHAT, msg.getSender(), opponent,
				msg.getMessage()));
	}

	private boolean checkMessage(Message msg) {
		boolean checked = false;
		String[] tokens = msg.getMessage().split(" ");
		switch (tokens[0].toUpperCase()) {
		case "PLACING":
			checked = checkPlaceMessage(tokens);
			break;
		case "SHIP_DOWN":
			checked = checkShipDownMessage(tokens);
			break;
		case "FIRE":
			checked = checkFireMessage(tokens);
			break;
		case "HIT":
			checked = checkHitMessage(tokens);
			break;
		case "MISS":
			checked = checkMissMessage(tokens);
			break;
		}
		return checked;
	}

	private boolean checkPlaceMessage(String[] tokens) {
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

		if (checkShipPlacement(ship, row, col)) {
			placeShip(ship, row, col);
			return true;
		}
		return false;
	}

	private boolean checkShipDownMessage(String[] tokens) {
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
		return checkShipDown(row, col, ship.getLength(), alignment);
	}

	private boolean checkFireMessage(String[] tokens) {
		return true;
	}

	private boolean checkHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isOccupied(row, col) && !isHit(row, col)) {
			return true;
		}
		return false;
	}

	private boolean checkMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isEmpty(row, col) && !isMissed(row, col)) {
			return true;
		}
		return false;
	}

	private boolean isEmpty(int row, int col) {
		return playerGrid[row][col] == empty;
	}

	private boolean isHit(int row, int col) {
		return playerGrid[row][col] == hit;
	}

	private boolean isMissed(int row, int col) {
		return playerGrid[row][col] == miss;
	}

	private boolean isOccupied(int row, int col) {
		return playerGrid[row][col] == occupied;
	}

	private void setOccupied(int row, int col) {
		playerGrid[row][col] = occupied;
	}

	private void setHit(int row, int col) {
		playerGrid[row][col] = hit;
	}

	private void setMiss(int row, int col) {
		playerGrid[row][col] = miss;
	}

	private boolean checkShipPlacement(Ship ship, int row, int col) {
		int length = ship.getLength();
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			if ((col + length - 1) < SIZE) {
				counter = col;
				for (int i = 0; i < length; i++) {
					if (!isEmpty(row, counter)) {
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
					if (!isEmpty(counter, col)) {
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

	private boolean checkShipDown(int row, int col, int length, Alignment alignment) {
		int counter;
		if (alignment == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < length; i++) {
				if (!isHit(row, counter)) {
					return false;
				}
				counter++;
			}
		} else if (alignment == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < length; i++) {
				if (!isHit(counter, col)) {
					return false;
				}
				counter++;
			}
		}
		return true;
	}

	private void placeShip(Ship ship, int row, int col) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < ship.getLength(); i++) {
				setOccupied(row, counter);
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < ship.getLength(); i++) {
				setOccupied(counter, col);
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
		setMiss(row, col);
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
		setHit(row, col);
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
}
