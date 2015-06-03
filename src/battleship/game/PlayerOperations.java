package battleship.game;

import battleship.network.NetworkOperations;

public interface PlayerOperations {
	public void init();
	public void sendMessage(Message message);
	public String getName();
}
