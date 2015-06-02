/**
 * @file Server.java
 * @authors rickard, lars
 * @date 2015-05-18
 * */
package battleship.network;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import battleship.game.Message;
/**
 * @class Server
 * @package battleship.network
 * @brief Battleship server to handle message and relay game play events
 * */
public class Server extends JFrame {
	private static final long serialVersionUID = 6118484193736984812L;
	private static int id;
	private int portNumber;
	private final int numberOfPlayers = 2;
	private ServerSocket server;
	private ArrayList<PlayerProxy> players;
	private ArrayList<Battle> battles;
	
	// gui components
	private JButton resetBtn;
	private JTextArea messages;
	private JTextField input;

	public static final int DEFAULT_PORT = 10001;

	public Server(int portNumber) {
		super("*** Battleship server ***");
		setupGui();
		this.portNumber = portNumber;
		players = new ArrayList<PlayerProxy>();
	}

	/**
	 * listen
	 * 
	 * @name listen
	 * @brief Listen for clients that connect to server, adds proxie's to
	 *        players a ArrayList
	 * @param None
	 * @return void
	 * */
	public void listen() {
		Socket socket = new Socket();
		try {
			server = new ServerSocket(portNumber);
			System.out.println("Server Listening on port " + portNumber);

			while (true) {
				socket = server.accept();
				id++;
				PlayerProxy player = new PlayerProxy(socket, this, id);
				players.add(player);
				player.start();
				messages.append("\nNew connection accepted.\n"
						+ "Connected players: " + players.size() + " id: " + id + " Name: " + player.getName()  + "\n");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if (server != null) {
				try {
					for (PlayerProxy player : players) {
						player.closeConnection();
					}
					server.close();
					System.out.println("The server closed the connection");
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * removePlayerProxy
	 * 
	 * @name removePlayerProxy
	 * @brief Function to remove a client proxy
	 * @param int id client proxy id, client to be removed
	 * @return void
	 * */
	public void removePlayerProxy(int id) {
		for (int i = players.size() - 1; i >= 0; i--) {
			if (players.get(i).playerId == id) {
				players.get(i).closeConnection();
				messages.append("Removed player: " + players.get(i).name);
				players.remove(i);
				messages.append("\nConnected players: " + players.size() + "\n");
			}
		}
	}
	/**
	 * removePlayerProxy
	 * 
	 * @name removePlayerProxy
	 * @brief Function to remove a client proxy
	 * @param Takes a player proxy object, and removes it if it exists
	 * @return void
	 * */
	public void removePlayerProxy(battleship.network.PlayerProxy pp) {
		if(players.equals(pp)) {
			players.remove(pp);
		}
	}

	/*
	 * private void removeByName(Message msg) { for (PlayerProxy player :
	 * players) { if (player.name == msg.getName()) {
	 * messages.append("Removed player: " + player.name);
	 * players.remove(player); player.running = false; } } }
	 */
	/**
	 * sendMessageToAll
	 * 
	 * @brief Function to handle massage that will be sent to all clients
	 * @param Message
	 *            to be send to everyone
	 * @return void
	 * */
	public synchronized void sendMessageToAll(Message msg) {
		messages.append(msg.getName() + " all:" + msg.getMessage() + "\n");
		for (PlayerProxy player : players) {
			if (player.name != msg.getName()) {
				player.sendMessage(msg);
			}
		}
	}

	/**
	 * sendMessageToOpponent
	 * 
	 * @brief Function to handle sending message to opponent in a battleship
	 *        match
	 * @param Message
	 *            to be sent.
	 * @return void
	 * */
	public synchronized void sendMessageToOpponent(Message msg) {
		messages.append(msg.getName() + " MsgOP:" + msg.getMessage() + "\n");
		for (PlayerProxy player : players) {
			if (!player.getPlayerName().equalsIgnoreCase(msg.getName())) {
				player.sendMessage(msg);
			}
		}
	}

	public synchronized void sendMessageToSender(Message msg) {
		for (PlayerProxy player : players) {
			if (player.getPlayerName().equalsIgnoreCase(msg.getName())) {
				player.sendMessage(msg);
			}
		}
	}

	public void randomizePlayerTurn() {
		if (players.size() > 1 && checkDeployment()) {
			sendAllDeployed();

			Random r = new Random();
			int value = r.nextInt(100);
			if (value < 50) {
				players.get(0).sendMessage(
						new Message(Message.TURN, players.get(1).name, ""));
			} else {
				players.get(1).sendMessage(
						new Message(Message.TURN, players.get(0).name, ""));
			}
		}
	}
	
	public synchronized void sendPlayers(String name) {
		StringBuilder builder = new StringBuilder();
		for(PlayerProxy player : players) {
			if (!player.getPlayerName().equalsIgnoreCase(name)) {
				builder.append(player.getName());
				builder.append(' ');
			}
		}
		sendMessageToAll(new Message(Message.LOGIN, "Server", builder.toString().trim()));
	}
	

	public boolean checkDeployment() {
		for (PlayerProxy player : players) {
			if (!player.getDeployed() == true) {
				System.out.println(player.getName() + " is not deployed");
				return false;
			}
		}
		return true;
	}
	
	public synchronized void sendAllDeployed() {
		for (PlayerProxy player : players) {
			player.sendMessage(new Message(Message.DEPLOYED, player.name, ""));
		}
	}
	

	/**
	 * setupGui
	 * 
	 * @name setupGui
	 * @brief Function set's up the server GUI, and button listeners.
	 * @param none
	 * @return void
	 * */
	private void setupGui() {
		// Set system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// set up componenets
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // override
																		// default
																		// behavior
		this.add(resetBtn = new JButton("Reset server"), BorderLayout.NORTH);
		// setup text area
		this.messages = new JTextArea(15, 15);
		this.add(new JScrollPane(messages), BorderLayout.CENTER);
		this.messages.setLineWrap(true);
		this.messages.setBackground(Color.ORANGE);
		this.messages.setEditable(false);
		this.messages.append("Server started\n");
		// Input field for server messages
		this.add(input = new JTextField(), BorderLayout.SOUTH);
		this.input.addActionListener(new AbstractAction() {
			

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessageToAll(new Message(1, "server", input.getText()));
				input.setText("");
			}
		});
		// reset server button
		this.resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetServer();
			}
		});

		this.setSize(400, 400);
		this.setVisible(true);
	}
	public int getNumberOfPlayers() {return numberOfPlayers;}
	public int getNumberOfCurrentPlayers() {return players.size();}
	
	private void resetServer() {
		// add reset message to event log
		// messages.
		// closing all connections
		players = new ArrayList<PlayerProxy>();
		//reset id counter
		id = 0;
		this.messages.append("Server reset\n");
	}

	public static void main(String[] args) {
		Server server = new Server(DEFAULT_PORT);
		server.listen();
	}

	

}
