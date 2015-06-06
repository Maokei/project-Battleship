/**
 * @file ClientConnection.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.network;

import static battleship.game.Constants.Valid_Move;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

import battleship.game.Message;
import battleship.game.Player;
import battleship.ships.Alignment;
import battleship.ships.BattleShipFactory;
import battleship.ships.Ship;
import battleship.ships.ShipType;

/**
 * ClientConnection
 * 
 * @class ClientConnection The class handles messages sent to this Player
 * @implements Runnable, NetworkOperation
 * @brief Class describes the connection. Client uses this connection class to
 *        talk to server and handle messages.
 * */
public class ClientConnection implements Runnable, NetworkOperations {
	private String address;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Player player;
	private Message msg;
	private Map<String, String> players;
	private JTextArea output;
	private boolean running = true;

	/**
	 * ClientConnection
	 * @constructor ClientConnection
	 * @brief Two-arguments constructor that receives ip-address and port number
	 * @param address the ip-address of the Player
	 * @param portNumber the port to be used to connect to the server
	 * */
	public ClientConnection(String address, int portNumber) {
		this.address = address;
		this.portNumber = portNumber;
		this.players = new HashMap<String, String>();
	}

	/**
	 * openConnection
	 * 
	 * @name openConnection
	 * @brief Try to open a connection given that ip and port are set already.
	 * */
	public boolean openConnection() {
		try {
			socket = new Socket(address, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * setPlayer
	 * 
	 * @param validMove
	 * @name setPlayer
	 * @param Takes
	 *            and sets a Player pointer.
	 * */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * setOutput
	 * 
	 * @name setOutput
	 * @brief receives a JTextArea to update chat output
	 * @param output instance of JTextField used to display chat messages
	 * */
	public void setOutput(JTextArea output) {
		this.output = output;
	}

	/**
	 * run
	 * 
	 * @name run
	 * @brief Start the thread and listen for messages.
	 * */
	public void run() {
		try {
			while (running) {
				msg = (Message) in.readObject();
				handleMessage(msg);
			}
		} catch (IOException e) {
			System.out.println(player.getName() + " IOException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println(player.getName() + " ClassNotFoundException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	/**
	 * closeConnection
	 * 
	 * @name closeConnection
	 * @brief Attempt to safely close the connection.
	 * */
	public void closeConnection() {
		if (socket != null) {
			int port = socket.getPort();
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println("Client with address " + address
						+ " and port " + port + "\nhas closed"
						+ " the connection with the server.\n ");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		System.exit(0);
	}

	/**
	 * handleMessage
	 * 
	 * @name handleMessage
	 * @param Takes
	 *            a message and handles in appropriately.
	 * */
	private void handleMessage(Message msg) {
		int type = msg.getType();
		switch (type) {
		case Message.LOGIN:
			parseLogin(msg);
			break;
		case Message.LOGOUT:
			players.remove(msg.getSender());
			break;
		case Message.MESSAGE:
			parseMessage(msg);
			break;
		case Message.CHAT:
			if (!msg.getSender().equalsIgnoreCase("AI"))
				output.append(msg.getSender() + ">> " + msg.getMessage() + "\n");
			break;
		case Message.DEPLOYED:
			player.setOpponentDeployed();
			break;
		case Message.TURN:
			if (msg.getReceiver().equalsIgnoreCase(player.getName())) {
				player.setPlayerTurn(true);
			}
			break;
		case Message.LOST:
			player.battleWon();
			break;
		case Message.CHALLENGE:
			if (!msg.getSender().equalsIgnoreCase(player.getName())
					&& !player.getHasOpponent()) {
				player.handleChallenge(msg);
			}
			break;
		case Message.AIMATCH:
			player.handleAIMatch();
			break;
		case Message.VALID:
			if (msg.getSender().equalsIgnoreCase(Valid_Move)) {
				parseValidMessage(msg);
			} else {
				player.handleNonValidMove(msg);
			}
			break;
		}
	}

	/**
	 * parseValidMessage
	 * 
	 * @name parseValidMessage
	 * @brief parses a Message of type VALID and handles it accordingly
	 * @param msg the message to be parsed and handled
	 * */
	private void parseValidMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		int row, col;
		switch (tokens[0].toUpperCase()) {
		case "HIT":
			row = Integer.parseInt(tokens[1]);
			col = Integer.parseInt(tokens[2]);
			player.registerEnemyHit(row, col);
			break;
		case "MISS":
			row = Integer.parseInt(tokens[1]);
			col = Integer.parseInt(tokens[2]);
			player.registerEnemyMiss(row, col);
			break;
		case "PLACING":
			parseMessage(msg);
			break;
		}
	}

	/**
	 * parseLogin
	 * 
	 * @name parseLogin
	 * @brief adds a new Player name to container for use in lobby, and updates lobby if needed
	 * @param msg the message to be parsed 
	 * */
	private void parseLogin(Message msg) {
		if (!(msg.getSender().equalsIgnoreCase(player.getName()))) {
			if (!players.containsKey(msg.getSender())) {
				if(msg.getMessage().equalsIgnoreCase("Playing")) {
					players.put(msg.getSender(), "playing");
				} else {
					players.put(msg.getSender(), "free");
				}
				System.out.println(msg.getSender() + " is added to the list");
				player.updateLobby();
			}
		}
	}

	/**
	 * getConnectedPlayers
	 * 
	 * @name getConnectedPlayers
	 * @brief Function meant to be used by lobby to populate players JList.
	 * @returns A Map of player names.
	 * */
	public Map<String, String> getConnectedPlayers() {
		return players;
	}

	/**
	 * parseMessage
	 * 
	 * @name parseMessage
	 * @brief Parses a Message.
	 * @param msg the Message to be parsed
	 * */
	private void parseMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		switch (tokens[0].toUpperCase()) {
		case "PLACING":
			parsePlaceMessage(tokens);
			break;
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
	 * parsePlaceMessage
	 * 
	 * @name parsePlaceMessage
	 * @brief calls player placePlayerShip to place ship at the given grid coordinates
	 * @param tokens String array with coordinates
	 * */
	private void parsePlaceMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[3]);
		int col = Integer.parseInt(tokens[4]);
		player.placePlayerShip(row, col);
	}

	/**
	 * parseMissMessage
	 * 
	 * @name parseMissMessage
	 * @brief calls player registerPlayerMiss to register miss at the given grid coordinates
	 * @param tokens String array with coordinates
	 * */
	private void parseMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerMiss(row, col);
	}

	/**
	 * parseShipDownMessage
	 * 
	 * @name parseShipDownMessage
	 * @brief calls player placeEnemyShip to place ship miss at the given grid coordinates
	 * @param tokens String array with coordinates and ship attributes
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
		player.placeEnemyShip(ship, row, col);
	}

	/**
	 * parseFireMessage
	 * 
	 * @name parseFireMessage
	 * @brief calls player checkFire to check whether enemy fire is hit or miss
	 * @param tokens String array with coordinates
	 * */
	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.checkFire(row, col);
	}

	/**
	 * parseFireMessage
	 * 
	 * @name parseFireMessage
	 * @brief calls player registerPlayerHit to add hit to playerBoard
	 * @param tokens String array with coordinates
	 * */
	private void parseHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerHit(row, col);
	}

	/**
	 * sendMessage
	 * 
	 * @name sendMessage
	 * @param msg the Message object to be written out sent sent through stream.
	 * */
	public void sendMessage(Message message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * setRunning
	 * 
	 * @name setRunning
	 * @brief Sets running state.
	 * @param boolean running state to be set.
	 * */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
