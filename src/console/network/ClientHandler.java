package console.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientHandler extends Thread {
	private Socket socket;
	private String address;
	ObjectOutputStream out = null;
	ObjectInputStream in = null;

	public ClientHandler(Socket socket) {
		this.socket = socket;
		address = socket.getInetAddress().getHostAddress();
		System.out.println("Client with address " + address + " and port "
				+ socket.getPort()
				+ "\nhas established a connection with the server.\n ");
	}

	public void run() {
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			String message = "This is a response from server";
			while (true) {
				receiveMessage();
				sendMessage(message);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
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
	
	private void sendMessage(String message) {
		try {
			out.writeObject(message);
			out.flush();
			System.out.println("SERVER SEND >> " + message);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private void receiveMessage() {
		String message = "";
		try {
			message = (String) in.readObject();
			System.out.println("SERVER RECEIVE >> " + message);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

}