package battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import battleship.game.BattlePlayer;
import battleship.game.Message;
import battleship.game.Player;
import battleship.ships.Alignment;
import battleship.ships.BattleShipFactory;
import battleship.ships.Ship;
import battleship.ships.ShipType;

public class ClientConnection implements Runnable, NetworkOperations {
	private String address;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private BattlePlayer player;
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

	public void setBattlePlayer(BattlePlayer player) {
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
		}
	}

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
			System.out.println(msg.getMessage());
			parseMissMessage(tokens);
			break;
		}
	}

	private void parseMissMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerMiss(row, col);
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
		player.placeEnemyShip(ship, row, col);
	}

	private void parseFireMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerFire(row, col);
	}

	private void parseHitMessage(String[] tokens) {
		int row = Integer.parseInt(tokens[1]);
		int col = Integer.parseInt(tokens[2]);
		player.registerPlayerHit(row, col);
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
