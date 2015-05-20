package battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static int id;
	private int portNumber;
	private final int numberOfPlayers = 2;
	private ServerSocket server;
	private ArrayList<PlayerProxy> players;

	public static final int DEFAULT_PORT = 10001;

	public Server(int portNumber) {
		this.portNumber = portNumber;
		players = new ArrayList<PlayerProxy>();
	}

	public void listen() {
		Socket socket = new Socket();
		try {
			server = new ServerSocket(portNumber);
			System.out.println("Server Listening on port " + portNumber);

			while (true) {
				if (players.size() < numberOfPlayers) {
					socket = server.accept();
					PlayerProxy player = new PlayerProxy(socket);
					players.add(player);
					player.start();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if (server != null) {
				try {
					System.out.println("I'm in server finally");
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

	private void removePlayerProxy(int id) {
		for (int i = players.size() - 1; i >= 0; i--) {
			if (players.get(i).playerId == id) {
				players.get(i).closeConnection();
				players.remove(i);
			}
		}
	}

	private synchronized void sendMessageToAll(Message msg) {
		for (PlayerProxy player : players) {
			player.sendMessage(msg);
		}
	}

	class PlayerProxy extends Thread {
		private Socket socket;
		private String address;
		private Message msg;
		private int playerId;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private boolean running = true;

		public PlayerProxy(Socket socket) {
			this.socket = socket;
			playerId = ++id;
			address = socket.getInetAddress().getHostAddress();
			System.out.println("Client with address " + address + " and port "
					+ socket.getPort()
					+ "\nhas established a connection with the server.\n ");
		}

		public void run() {
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				in = new ObjectInputStream(socket.getInputStream());
				while (running) {
					try {
						msg = (Message) in.readObject();
						// msg = (TmpMessage) in.readObject();
						handleChatMessage(msg);
						// handleMessage(msg);
					} catch (ClassNotFoundException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				System.out.println("I'm in PlayerProxy finally");
				closeConnection();
			}
		}

		private void handleChatMessage(Message msg) {
			sendMessageToAll(msg);
		}

		private void handleMessage(Message msg) {
			/*
			 * int type = msg.getType(); switch (type) { case TmpMessage.LOGOUT:
			 * running = false; removePlayerProxy(this.playerId);
			 * sendMessageToAll(msg); break; case TmpMessage.MESSAGE:
			 * sendMessageToAll(msg); break; }
			 */
		}

		private void sendMessage(Message msg) {
			try {
				out.writeObject(msg);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		private void closeConnection() {
			int port = socket.getPort();
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println("Client with address " + address
						+ " and port " + port
						+ "\nhas closed the connection with the server.\n ");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server(DEFAULT_PORT);
		server.listen();
	}

}