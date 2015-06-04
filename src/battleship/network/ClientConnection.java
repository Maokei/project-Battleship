/**
 * @file ClientConnection.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.network;

import static battleship.game.Constants.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import battleship.game.BattlePlayer;
import battleship.game.Message;
import battleship.game.Player;
import battleship.ships.Alignment;
import battleship.ships.BattleShipFactory;
import battleship.ships.Ship;
import battleship.ships.ShipType;

/**
 * ClientConnection 
 * @class ClientConnection
 * @implements Runnable, NetworkOperation
 * @brief Class describes the connection. Client uses this connection class to talk to server and handle messages.
 * */
public class ClientConnection implements Runnable, NetworkOperations {
	private String address;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private BattlePlayer player;
	private Message msg;
	private ArrayList<String> players;
	private JTextArea output; // just for Chat
	private boolean running = true;

	public ClientConnection(String address, int portNumber) {
		this.address = address;
		this.portNumber = portNumber;
		this.players = new ArrayList<String>();
	}

	/**
	 * openConnection
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
	 * setBattlePlayer
	 * @name setBattlePlayer
	 * @param Takes and sets a BattlePlayer pointer.
	 * */
	public void setBattlePlayer(BattlePlayer player) {
		this.player = player;
	}

	/**
	 * setOutput
	 * @name setOutput
	 * @brief Chat test function.
	 * */
	public void setOutput(JTextArea output) {
		this.output = output;
	}

	/**
	 * run
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
	 * @name handleMessage
	 * @param Takes a message and handles in appropriately.
	 * */
	private void handleMessage(Message msg) {
		int type = msg.getType();
		switch (type) {
		case Message.LOGIN:
			parseLogin(msg);
			break;
		case Message.LOGOUT:
			players.remove(msg.getName());
			break;
		case Message.MESSAGE:
			parseMessage(msg);
			break;
		case Message.CHAT:
			if (!msg.getName().equalsIgnoreCase("AI"))
				output.append(msg.getName() + ">> " + msg.getMessage() + "\n");
			break;
		case Message.DEPLOYED:
			player.setOpponentDeployed();
			break;
		case Message.TURN:
			if (!msg.getName().equals(player.getName())) {
				player.setPlayerTurn(true);
			}
			break;
		case Message.LOST:
			if (msg.getName().equalsIgnoreCase("AI"))
				((Player) player).battleWon();
			break;
		case Message.CHALLENGE:
			player.handleChallenge(msg.getName(), msg.getMessage());
			break;
		case Message.AIMATCH:
			player.handleAIMatch();
			break;
		}
	}

	/**
	 * parseLogin
	 * @name parseLogin
	 * @param Takes a login message object to parse.
	 * */
	private void parseLogin(Message msg) {
		System.out.print("parseLogin " + player.getName() + ": ");
		
		if(msg.getName().equalsIgnoreCase("Server")) {
			String[] tokens = msg.getMessage().split(" ");
			for(String name : tokens) {
				System.out.print(name);
				players.add(name);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * getConnectedPlayers
	 * @name getConnectedPlayers
	 * @brief Function meant to be used my lobby to populate players JList.
	 * @returns A list of player names. 
	 * */
	public ArrayList<String> getConnectedPlayers() {
		return players;
	}

	/**
	 * parseMessage
	 * @name parseMessage
	 * @brief Parses a battle message.
	 * @param Takes a message to parse.
	 * */
	private void parseMessage(Message msg) {
		if (msg.getMessage().startsWith("Server full")) {
			JOptionPane.showMessageDialog(null,
					"The server is full\nTry connecting at a later time.");
			running = false;
			return;
		}
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
	 * @name parseMissMessage
	 * @param Takes an Array of string tokens
	 * */
	private void parseMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerMiss(row, col);
	}

	/**
	 * parseShipDownMessage
	 * @name parseShipDownMessage
	 * @param Array of String tokens.
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
	 * @name parseFireMessage
	 * @param String array to be parsed, register FireMessage.
	 * */
	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerFire(row, col);
	}

	/**
	 * parseHitMessage
	 * @name parseHitMessage
	 * */
	private void parseHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerHit(row, col);
	}

	/**
	 * sendMessage
	 * @name sendMessage
	 * @param Takes a Message object to be written out sent sent through stream.
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
	 * @name setRunning
	 * @brief Sets running state.
	 * @param boolean running state to be set.
	 * */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
