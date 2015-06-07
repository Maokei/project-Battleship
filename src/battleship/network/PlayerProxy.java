/**
 * @file PlayerProxy.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.network;

import static battleship.game.Constants.Challenge_Accept;
import static battleship.game.Constants.Challenge_Name;
import static battleship.game.Constants.Challenge_Request;
import static battleship.game.Constants.NonValid_Move;
import static battleship.game.Constants.SIZE;
import static battleship.game.Constants.Valid_Move;
import static battleship.game.Constants.empty;
import static battleship.game.Constants.hit;
import static battleship.game.Constants.miss;
import static battleship.game.Constants.occupied;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import battleship.game.GameMode;
import battleship.game.Message;
import battleship.ships.Alignment;
import battleship.ships.BattleShipFactory;
import battleship.ships.Ship;
import battleship.ships.ShipType;

/**
 * PlayerProxy
 * 
 * @package battleship.network
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
	 * PlayerProxy 
	 * 
	 * @constructor PlayerProxy
	 * @name PlayerProxy
	 * @brief receives instances of Socket, Server and an int id to handle communication with Player
	 * @param socket The socket that received the Player connection
	 * @param server the Server instance used to handle communication with all players
	 * @param id the unique player number
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
	 * @return returns player name as string
	 * */
	public String getPlayerName() {
		return name;
	}

	/**
	 * getDeployed
	 * 
	 * @name getDeployed
	 * @brief returns boolean flag that is true if the player has deployed his ships, false otherwise
	 * @return deployed falg is true if player ships is deployed
	 * */
	public boolean getDeployed() {
		return deployed;
	}

	/**
	 * getMode
	 * 
	 * @name getMode
	 * @brief returns an instance of class GameMode, representing single. or multiplayer
	 * @return mode instance of GameMode
	 * */
	public GameMode getMode() {
		return mode;
	}

	/**
	 * getPlaying
	 * 
	 * @name getPlaying
	 * @brief returns if player currently is engaged in single-or multiplayer game
	 * @return boolean playing
	 * */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * setPlaying
	 * 
	 * @name setPlaying
	 * @brief sets the flag representing if player is playing or not
	 * @param playing sets the playing flag to true or false
	 * */
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	/**
	 * setHasOpponent
	 * 
	 * @name setHasOpponent
	 * @brief sets the flag representing if player has an opponent or not
	 * @param hasOpponent sets the hasOpponent flag to true or false
	 * */
	public void setHasOpponent(boolean hasOpponent) {
		this.hasOpponent = hasOpponent;
	}

	/**
	 * getHasOpponent
	 * 
	 * @name getHasOpponent
	 * @brief returns flag representing if player has an opponent
	 * @return hasOpponent is true if player has an opponent, false otherwise
	 * */
	public boolean getHasOpponent() {
		return hasOpponent;
	}

	/**
	 * initGrid
	 * 
	 * @name initGrid
	 * @brief Function initiates a playerGrid with empty cells aka water
	 * @param grid multi-dimensional char array representing a player grid
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
	 * @brief proxy run loop to handle messages from player
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
	 * @param msg the message to be handled
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
	 * 
	 * @name parseValidMessage
	 * @brief checks whether a move by the player is valid, sends message to player of result
	 * @param msg the message to be parsed
	 * */
	private void parseValidMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		if(checkMessage(msg)) {
			sendMessage(new Message(Message.VALID, Valid_Move, tokens[0], msg.getMessage()));
		} else {
			sendMessage(new Message(Message.VALID, NonValid_Move, tokens[0], msg.getMessage()));
		}
	}

	/**
	 * parseModeMessage
	 * @name parseModeMessage
	 * @brief changes GameMode to MultiPlayer 
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
	 * parseChallengeMessage
	 * 
	 * @name parseChallengeMessage
	 * @brief parses and handles a Message of type CHALLENGE
	 * @param msg a Message of type CHALLENGE to be parsed
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
			} else {
				sendMessage(new Message(Message.AIMATCH, name, "", ""));
			}
		}
	}

	/**
	 * randomizePlayerTurn
	 * 
	 * @name randomizePlayerTurn
	 * @brief randomizes the player turn for a singlePlayer match and notifies a player 
	 * */
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

	/**
	 * handleLogout
	 * 
	 * @name handleLogout
	 * @brief removes player from the servers list of players and sends message to opponent 
	 * */
	private void handleLogout() {
		server.removePlayerProxy(this.playerId);
		server.sendMessage(new Message(Message.CHAT, msg.getSender(), opponent,
				msg.getMessage()));
	}

	/**
	 * checkMessage
	 * 
	 * @name checkMessage
	 * @brief checks whether a Message sent by the player is a valid move
	 * @param msg the Message to be checked
	 * @return a boolean flag representing if player move is valid 
	 * */
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

	/**
	 * checkPlaceMessage
	 * 
	 * @name checkPlaceMessage
	 * @brief checks whether a player can place a ship at given coordinates
	 * @param tokens an array containing the ship attributes and coordinates
	 * @return a boolean flag representing if player move is valid 
	 * */
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

	/**
	 * checkShipDownMessage
	 * 
	 * @name checkShipDownMessage
	 * @brief checks whether a player ship is sunk
	 * @param tokens an array containing the ship attributes and coordinates
	 * @return a boolean flag representing if player move is valid 
	 * */
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

	/**
	 * checkFireMessage
	 * 
	 * @name checkFireMessage
	 * @brief checks whether a player can fire at the coordinates
	 * @param tokens an array containing the coordinates
	 * @return a boolean flag representing if player move is valid 
	 * */
	private boolean checkFireMessage(String[] tokens) {
		return true;
	}

	/**
	 * checkHitMessage
	 * 
	 * @name checkHitMessage
	 * @brief checks whether a player is hit at the given coordinates
	 * @param tokens an array containing the coordinates
	 * @return a boolean flag representing if player move is valid 
	 * */
	private boolean checkHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isOccupied(row, col)) {
			return true;
		}
		return false;
	}

	/**
	 * checkMissMessage
	 * 
	 * @name checkMissMessage
	 * @brief checks whether a player is missed at the given coordinates
	 * @param tokens an array containing the coordinates
	 * @return a boolean flag representing if player move is valid 
	 * */
	private boolean checkMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);

		if (isEmpty(row, col)) {
			return true;
		}
		return false;
	}

	/**
	 * isEmpty
	 * 
	 * @name isEmpty
	 * @brief checks whether the grid is empty at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @return a boolean flag representing if grid is empty
	 * */
	private boolean isEmpty(int row, int col) {
		return playerGrid[row][col] == empty;
	}

	/**
	 * isHit
	 * 
	 * @name isHit
	 * @brief checks whether the grid is hit at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @return a boolean flag representing if grid is hit
	 * */
	private boolean isHit(int row, int col) {
		return playerGrid[row][col] == hit;
	}


	/**
	 * isMissed
	 * 
	 * @name isMissed
	 * @brief checks whether the grid is missed at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @return a boolean flag representing if grid is missed
	 * */
	private boolean isMissed(int row, int col) {
		return playerGrid[row][col] == miss;
	}

	/**
	 * isOccupied
	 * 
	 * @name isOccupied
	 * @brief checks whether the grid is occupied at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @return a boolean flag representing if grid is occupied
	 * */
	private boolean isOccupied(int row, int col) {
		return playerGrid[row][col] == occupied;
	}

	/**
	 * setOccupied
	 * 
	 * @name setOccupied
	 * @brief sets the grid to occupied at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * */
	private void setOccupied(int row, int col) {
		playerGrid[row][col] = occupied;
	}

	/**
	 * setHit
	 * 
	 * @name setHit
	 * @brief sets the grid to hit at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * */
	private void setHit(int row, int col) {
		playerGrid[row][col] = hit;
	}

	/**
	 * setMiss
	 * 
	 * @name setMiss
	 * @brief sets the grid to miss at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * */
	private void setMiss(int row, int col) {
		playerGrid[row][col] = miss;
	}

	/**
	 * checkShipPlacement
	 * 
	 * @name checkShipPlacement
	 * @brief checks whether a ship can be placed at the given coordinates
	 * @param ship the ship to be placed
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @return a boolean flag representing if player move is valid 
	 * */
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

	/**
	 * checkShipDown
	 * 
	 * @name checkShipDown
	 * @brief checks whether a ship is sunk at the given coordinates
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * @param length the length of the ship
	 * @param alignment Alignment of the ship
	 * @return a boolean flag representing if player move is valid 
	 * */
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

	/**
	 * placeShip
	 * 
	 * @name placeShip
	 * @brief places a ship at the given coordinates
	 * @param ship the ship to be placed
	 * @param row the grid row coordinate
	 * @param col the grid col coordinate
	 * */
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

	/**
	 * parseMessage
	 * 
	 * @name parseMessage
	 * @brief parses a Message from the player
	 * @param msg the Message to be parsed
	 * */
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

	/**
	 * parseMissMessage
	 * 
	 * @name parseMissMessage
	 * @brief parses a Miss Message from the player, sets the grid to miss, calls aiPlayer.registerPlayerMiss
	 * @param tokens a String array containing the coordinates
	 * */
	private void parseMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		setMiss(row, col);
		aiPlayer.registerPlayerMiss(row, col);
	}

	/**
	 * parseShipDownMessage
	 * 
	 * @name parseShipDownMessage
	 * @brief parses a shipDown Message from the player and calls aiPlayer.placeEnemyShip
	 * @param tokens a String array containing the coordinates
	 * */
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

	/**
	 * parseFireMessage
	 * 
	 * @name parseFireMessage
	 * @brief parses a Fire Message from the player and calls aiPlayer.registerFire
	 * @param tokens a String array containing the coordinates
	 * */
	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		aiPlayer.registerFire(row, col);
	}

	/**
	 * parseHitMessage
	 * 
	 * @name parseHitMessage
	 * @brief parses a Hit Message from the player, sets grid to hit, and calls aiPlayer.registerPlayerHit
	 * @param tokens a String array containing the coordinates
	 * */
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
	 * @brief Function to send messages to player
	 * @param msg the Message to be sent to player
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
		} catch (IOException e) {}
	}
}
