package console.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Subject {
	private int portNumber;
	private ServerSocket server;

	public Server(int portNumber) {
		this.portNumber = portNumber;
	}

	public void openClientConnection() {
		Socket client = new Socket();
		try {
			server = new ServerSocket(portNumber);
			System.out.println("Server Listening on port " + portNumber);

			while (true) {
				client = server.accept();
				new ClientHandler(client).start();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if (server != null) {
				try {
					server.close();
					System.out.println("The server closed the connection");
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	@Override
	public void register(Observer obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregister(Observer obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObservers(Observer obs) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Server server = null;
		if(args.length != 1) {
			server = new Server(10001);
		} else {
			int port = Integer.parseInt(args[0]);
			server = new Server(port);
		}
		server.openClientConnection();
	}
}
