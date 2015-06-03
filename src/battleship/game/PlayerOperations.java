/**
 * @file PlayerOperations.java
 * @author rickard, lars
 * @date 2015-05-25
 * */
package battleship.game;

import battleship.network.NetworkOperations;

/**
 * @interface PlayerOperations
 * */
public interface PlayerOperations {
	public void init();
	public void sendMessage(Message message);
	public String getName();
}
