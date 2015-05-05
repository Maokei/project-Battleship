package battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
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
	
	private void openConnection() {
		try {
			socket = new Socket(address, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());

			while (true) {
				// do something here TODO
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

}
