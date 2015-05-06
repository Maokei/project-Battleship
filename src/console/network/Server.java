package console.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Server implements Subject {
	private int portNumber;
	private ServerSocket server;
	private List<Observer> observers;

	public Server(int portNumber) {
		this.portNumber = portNumber;
		//initiate observer list
		this.observers = new ArrayList<Observer>();
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
	public void register(Observer obs, String playerName) {
		//attach observer
		observers.add(obs);
	}

	@Override
	public void unregister(Observer obs, String playerName) {
		
		//notify all
		notifyObservers();
	}

	@Override
	public void notifyObservers() {
		for(Observer obs : observers)
			obs.notify();
	}
	
	@Override
	public void receiveMessage(ChatMessage message) {
		//find specific user or send to everyone.
		if(!(message.getRecevier() == "")) {
			
		}
		//message everyone
		
		for(Observer obs : observers)
			obs.update(message);
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
