package battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

import battleship.player.Alignment;
import battleship.player.BattleShipFactory;
import battleship.player.Player;
import battleship.player.Ship;
import battleship.player.ShipType;

public class ClientConnection implements Runnable {
	private String address;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Player player;
	private Message msg;
	private JTextArea output; // just for Chat
	private boolean running = true;

	public ClientConnection(String address, int portNumber) {
		this.address = address;
		this.portNumber = portNumber;
	}

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

	public void setPlayer(Player player) {
		this.player = player;
	}

	// just to demonstrate Chat
	public void setOutput(JTextArea output) {
		this.output = output;
	}

	public void run() {
		try {
			while (running) {
				msg = (Message) in.readObject();
				handleMessage(msg);
			}
		} catch (IOException e) {
			// System.err.println(e.getMessage());
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

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
	}

	private void handleMessage(Message msg) {
		int type = msg.getType();
		switch (type) {
		case Message.LOGIN: // only handled in server
			break;
		case Message.LOGOUT: // only handled in server
			break;
		case Message.MESSAGE:
			parseMessage(msg);
			break;
		case Message.CHAT:
			output.append(msg.getName() + ">> " + msg.getMessage() + "\n");
			break;
		}
	}

	private void parseMessage(Message msg) {
		String[] tokens = msg.getMessage().split(" ");
		switch (tokens[0].toUpperCase()) {
		case "SHIP":
			parseShipMessage(tokens);
			break;
		case "FIRE":
			parseFireMessage(tokens);
			break;
		case "HIT":
			parseHitMessage(tokens);
			break;
		case "WIN":
			parseWinMessage();
			break;
		}
	}

	private void parseShipMessage(String[] tokens) {
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

		if (tokens[2].equalsIgnoreCase("V")) {
			alignment = Alignment.VERTICAL;
		}
		ship = BattleShipFactory.getShip(type);
		ship.setAlignment(alignment);
		int row = Integer.parseInt(tokens[3]);
		int col = Integer.parseInt(tokens[4]);
		
		player.placeEnemyShip(ship, row, col);

	}

	// maybe not necessary
	/*
	 * private void parseShipPositionMessage(String[] tokens) { String ship =
	 * tokens[1]; int row = Integer.parseInt(tokens[2]); int col =
	 * Integer.parseInt(tokens[3]); player.setEnemyShip(ship, row, col); }
	 */
	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerHit(row, col);
	}

	private void parseHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		int health = Integer.parseInt(tokens[0]);
		player.registerEnemyHit(row, col, health);
	}

	private void parseWinMessage() {
		// TODO Auto-generated method stub
	}

	public void sendMessage(Message message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
