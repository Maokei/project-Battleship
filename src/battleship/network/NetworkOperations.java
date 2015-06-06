/**
 * @file NetworkOperations.java
 * @author rickard, lars
 * @date 2015-05-25
 * */
package battleship.network;

import battleship.game.Message;

/**
 * @interface NetworkOperations
 * @brief Describes network actions.
 * */
public interface NetworkOperations {
	public boolean openConnection();
	public void closeConnection();
	public void sendMessage(Message message);
	
}
