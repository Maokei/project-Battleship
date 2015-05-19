package battleship.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

import battleship.player.Player;

public class ClientConnection implements Runnable {
	private String address;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Player player;
	private ChatMessage msg;
	private JTextArea output; // just for Chat 

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
			while (true) {
				msg = (ChatMessage) in.readObject();
				output.append(msg.getSender() + ">> " + msg.getMessage() + "\n");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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

	public void sendMessage(Message message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sendChatMessage(String message) {
		try {
			out.writeObject(new ChatMessage(player.getName(), message));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
