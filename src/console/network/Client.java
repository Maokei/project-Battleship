/**
 * @file Client.java
 * @date 2015-05-05
 * */
package console.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Observer {
	private Socket socket;
	private String address;
	private int portNumber;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String msg = "";
	
	public Client(String address, int portNumber) {
		this.address = address;
		this.portNumber = portNumber;
	}
	
	public String getAddress() { return address; }
	public int getPort() { return portNumber; }
	public String getMessage() { return msg; }
	
	/**
	 * @brief prepare to open connection to server
	 * */
	public void openConnection() {
		try {
			socket = new Socket(address, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			msg = "This is a message from client";
			while (true) {
				sendMessage(msg);
				receiveMessage();
			}

		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	private void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("CLIENT SEND >> " + msg);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private void receiveMessage() {
		try {
			msg = (String) in.readObject();
			System.out.println("CLIENT RECEIVE >> " + msg);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * closeConnection
	 * @brief Close client socket connections safely.
	 * */
	private void closeConnection() {
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

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSubject(Subject sub) {
		// TODO Auto-generated method stub
		
	}
	
	@Override 
	public String getName() {
		return "";
	}
	
	public static void main(String[] args) {
		Client client = null;
		if(args.length != 2) {
			client = new Client("localhost", 10001);
		} else {
			int port = Integer.parseInt(args[1]);
			client = new Client(args[0], port);
		}
		client.openConnection();
	}

}
