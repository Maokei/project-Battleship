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

import battleship.game.GameMode;
import battleship.game.Message;

/**
 * Server
 * 
 * @class Server
 * @package battleship.network
 * @class Server class Server handles incoming connections, starts a new thread for each connection
 * and handles communication between them
 * @brief Battleship server to handle message and relay game play events
 * */
public class Server extends JFrame {
	private static final long serialVersionUID = 6118484193736984812L;
	private static int id;
	private int portNumber;
	private ServerSocket server;
	private ArrayList<PlayerProxy> players;

	// gui components
	private JButton resetBtn;
	private JTextArea messages;
	private JTextField input;

	/**
	 * Server
	 * 
	 * @constructor Server
	 * @brief Server one-argument constructor, sets up GUI for server and instantiates the list of players
	 * @param portNumber the port number used by players to establish connection
	 * */
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
						+ "Connected players: " + players.size() + " id: " + id
						+ " Name: " + player.getName() + "\n");
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
	 * @param pp the instance of PlayerProxy to remove
	 * */
	public void removePlayerProxy(battleship.network.PlayerProxy pp) {
		if (players.equals(pp)) {
			players.remove(pp);
		}
	}
	
	/**
	 * sendMessage
	 * 
	 * @brief Send message to receiver.
	 * @param msg Message to be sent.
	 * */
	public synchronized void sendMessage(Message msg) {
		messages.append("Sender " + msg.getSender() + "Receiver "
				+ msg.getReceiver() + " MsgOP: " + msg.getMessage() + "\n");
		for (PlayerProxy player : players) {
			if (player.getPlayerName().equalsIgnoreCase(msg.getReceiver())) {
				player.sendMessage(msg);
			}
		}
	}


	/**
	 * sendMessageToAllPlayers
	 * 
	 * @name sendsMessageToallPlayers
	 * @brief sends a message to all connected players.
	 * @param msg Message to send
	 * */
	public synchronized void sendMessageToAllPlayers(Message msg) {
		messages.append("Sender " + msg.getSender() + "Receiver ALL"
				+ " MsgOP: " + msg.getMessage() + "\n");
		for (PlayerProxy player : players) {
			if (player.getPlayerName().equalsIgnoreCase(msg.getReceiver())) {
				player.sendMessage(msg);
			}
		}
	}


	/**
	 * checkDeployment
	 * 
	 * @name checkDeployment
	 * @brief checks whether both sender and receiver is deployed
	 * @param msg Message sent to receiver and sender.
	 * @return returns true if both are deployed, false otherwise
	 * */
	public boolean checkDeployment(Message msg) {
		for (PlayerProxy player : players) {
			player.sendMessage(msg);
			if (player.getPlayerName().equalsIgnoreCase(msg.getSender())) {
				if(!player.deployed) {
					return false;
				}
			} else if(player.getPlayerName().equalsIgnoreCase(msg.getReceiver())) {
				if(!player.deployed) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * randomizePlayerTurn
	 * @name randomizePlayerTurn
	 * @brief Randomizes player turn if both players are deployed, and sends messages to both players 
	 * @param msg the Message of type DEPLOYED to be sent to if all players are deployed
	 * */
	public void randomizePlayerTurn(Message msg) {
		if (players.size() > 1 && checkDeployment(msg)) {
			sendAllDeployed(msg);
			Random r = new Random();
			int value = r.nextInt(100);
			if (value < 50) {
				sendMessage(new Message(Message.TURN, msg.getSender(),
						msg.getReceiver(), ""));
			} else {
				sendMessage(new Message(Message.TURN, msg.getReceiver(),
						msg.getSender(), ""));
			}
		}
	}
	
	/**
	 * sendAllDeployed
	 * @name sendAllDeployed
	 * @brief Send out deployment message.
	 * @param msg the Message of type DEPLOYED to be sent to both players
	 * */
	public synchronized void sendAllDeployed(Message msg) {
		sendMessage(new Message(Message.DEPLOYED, msg.getSender(),
				msg.getReceiver(), ""));
		sendMessage(new Message(Message.DEPLOYED, msg.getReceiver(),
				msg.getSender(), ""));
	}

	/**
	 * sendAllPlayers
	 * 
	 * @name sendAllPlayers
	 * @brief sends a Message of type LOGIN to all players so that they can update their lobby
	 * */
	public synchronized void sendAllPlayers() {
		for (PlayerProxy player : players) {
			for(PlayerProxy p : players) {
				if(p.playing) {
					sendMessage(new Message(Message.LOGIN, p.name, player.name, "Playing"));
				} else {
					sendMessage(new Message(Message.LOGIN, p.name, player.name, "Free"));
				}
			}
		}
	}
	/**
	 * getPlayerCount
	 * @name getPlayerCount
	 * @return integer return number of connected players.
	 * */
	public int getPlayerCount() {
		return players.size();
	}

	/**
	 * setupGui
	 * 
	 * @name setupGui
	 * @brief Function set's up the server GUI, and button listeners.
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
				for (PlayerProxy p : players) {
					sendMessage(new Message(Message.MESSAGE, "Server", p
							.getPlayerName(), input.getText()));
				}
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

	
	/**
	 * getNumberOfPlayers
	 * @name getNumberOfPlayers
	 * @return integer return number of connected players.
	 * */
	public int getNumberOfCurrentPlayers() {
		return players.size();
	}
	
	/**
	 * resetServer
	 * @name resetServer
	 * @brief Closes all connections and removes players and resets id counter.
	 * */
	private void resetServer() {
		// add reset message to event log
		// messages.
		// closing all connections
		players = new ArrayList<PlayerProxy>();
		// reset id counter
		id = 0;
		this.messages.append("Server reset\n");
	}
}
