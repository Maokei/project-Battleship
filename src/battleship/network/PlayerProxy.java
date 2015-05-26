package battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import battleship.network.Message;

/**
 * @class PlayerProxy
 * @extends Thread
 * @brief Clientproxy class
 * */
public class PlayerProxy extends Thread {
	protected Socket socket;
	protected Server server;
	protected String address;
	protected Message msg;
	protected String name;
	protected int playerId;
	private boolean deployed;
	private boolean playerTurn;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected boolean running = true;

	public PlayerProxy(Socket socket, Server server, int id) {
		this.socket = socket;
		this.server = server;
		playerId = id;
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
			if (server.getNumberOfCurrentPlayers() > server.getNumberOfPlayers()) {
				name = msg.getName();
				server.sendMessageToSender(new Message(Message.MESSAGE,msg.getName(), "Server full"));
				this.closeConnection();
				server.removePlayerProxy(this);
				//messages.append("Server full, attempt to join game failed.\nConnection closed for " + name + "\n");
			} else {
				name = msg.getName();
				//sendMessageToOpponent(new Message(Message.CHAT,msg.getName(), "Logged in"));
			}
			break;
		case Message.LOGOUT:
			server.removePlayerProxy(this.playerId);
			server.sendMessageToOpponent(new Message(Message.CHAT, msg.getName(), msg.getMessage()));
			break;
		case Message.MESSAGE:
			server.sendMessageToOpponent(msg);
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
	
	public boolean getDeployed() {
		return deployed;
	}
}