package battleship.network;

import battleship.game.Message;

public interface NetworkOperations {
	public boolean openConnection();
	public void closeConnection();
	public void sendMessage(Message message);
}
