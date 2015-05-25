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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

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
				PlayerProxy player = new PlayerProxy(socket);
				players.add(player);
				player.start();
				messages.append("\nNew connection accepted.\n"
						+ "Connected players: " + players.size() + "\n");
				
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
	private void removePlayerProxy(int id) {
		for (int i = players.size() - 1; i >= 0; i--) {
			if (players.get(i).playerId == id) {
				players.get(i).closeConnection();
				messages.append("Removed player: " + players.get(i).name);
				players.remove(i);
				messages.append("\nConnected players: " + players.size() + "\n");
			}
		}
	}
	/*
	private void removeByName(Message msg) {
		for (PlayerProxy player : players) {
			if (player.name == msg.getName()) {
				messages.append("Removed player: " + player.name);
				players.remove(player);
				player.running = false;
			}
		}
	}
	*/
	/**
	 * sendMessageToAll
	 * 
	 * @brief Function to handle massage that will be sent to all clients
	 * @param Message
	 *            to be send to everyone
	 * @return void
	 * */
	private synchronized void sendMessageToAll(Message msg) {
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
	private synchronized void sendMessageToOpponent(Message msg) {
		messages.append(msg.getName() + " MsgOP:" + msg.getMessage() + "\n");
		for (PlayerProxy player : players) {
			if (!player.getPlayerName().equalsIgnoreCase(msg.getName())) {
				player.sendMessage(msg);
			}
		}
	}

	private synchronized void sendMessageToSender(Message msg) {
		for (PlayerProxy player : players) {
			if (player.getPlayerName().equalsIgnoreCase(msg.getName())) {
				player.sendMessage(msg);
			}
		}
	}

	/**
	 * @class PlayerProxy
	 * @extends Thread
	 * @brief Clientproxy class
	 * */
	class PlayerProxy extends Thread {
		protected Socket socket;
		protected String address;
		protected Message msg;
		protected String name;
		protected int playerId;
		protected ObjectInputStream in;
		protected ObjectOutputStream out;
		protected boolean running = true;

		public PlayerProxy(Socket socket) {
			this.socket = socket;
			playerId = ++id;
			address = socket.getInetAddress().getHostAddress();
			System.out.println("Client with address " + address + " and port "
					+ socket.getPort()
					+ "\nhas established a connection with the server.\n ");
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
				if (players.size() > numberOfPlayers) {
					name = msg.getName();
					sendMessageToSender(new Message(Message.MESSAGE,
							msg.getName(), "Server full"));
					this.closeConnection();
					players.remove(this);
					messages.append("Server full, attempt to join game failed.\nConnection closed for " + name + "\n");
				} else {
					name = msg.getName();
					sendMessageToOpponent(new Message(Message.CHAT,
							msg.getName(), "Logged in"));
				}
				break;
			case Message.LOGOUT:
				removePlayerProxy(this.playerId);
				sendMessageToOpponent(new Message(Message.CHAT, msg.getName(), msg.getMessage()));
				break;
			case Message.MESSAGE:
				sendMessageToOpponent(msg);
				break;
			case Message.CHAT:
				sendMessageToAll(msg);
				break;
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
		private void sendMessage(Message msg) {
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
		 * @brief Responsible to closing the connection between server and
		 *        player
		 * @param None
		 * @return void
		 * */
		protected void closeConnection() {
			int port = socket.getPort();
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println("Client with address " + address
						+ " and port " + port
						+ "\nhas closed the connection with the server.\n ");
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
	}

	class AiPlayer extends PlayerProxy{
		public AiPlayer(Socket socket) {
			super(socket);
			// TODO Auto-generated constructor stub
		}
		
		
		private void handleMessage(Message msg) {
			int type = msg.getType();
			System.out.println("got message typ: " + type);
			switch (type) {
			case Message.LOGIN:
				if (players.size() > numberOfPlayers) {
					name = msg.getName();
					sendMessageToSender(new Message(Message.MESSAGE,
							msg.getName(), "Server full"));
					this.closeConnection();
					players.remove(this);
					messages.append("Server full, attempt to join game failed.\nConnection closed for " + name + "\n");
				} else {
					name = msg.getName();
					sendMessageToOpponent(new Message(Message.CHAT,
							msg.getName(), "Logged in"));
				}
				break;
			case Message.LOGOUT:
				removePlayerProxy(this.playerId);
				sendMessageToOpponent(new Message(Message.CHAT, msg.getName(), msg.getMessage()));
				break;
			case Message.MESSAGE:
				sendMessageToOpponent(msg);
				break;
			case Message.CHAT:
				sendMessageToAll(msg);
				break;
			}
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
			private static final long serialVersionUID = 7004163908537705429L;

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

	private void resetServer() {
		// add reset message to event log
		// messages.
		// closing all connections
		players = new ArrayList<PlayerProxy>();
		this.messages.append("Server reset\n");
	}

	public static void main(String[] args) {
		Server server = new Server(DEFAULT_PORT);
		server.listen();
	}

}
