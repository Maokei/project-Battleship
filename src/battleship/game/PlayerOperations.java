/**
 * @file PlayerOperations.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.game;

import battleship.network.NetworkOperations;

/**
 * PlayerOperatios
 * @interface PlayerOperations
 * @brief describes the possible actions of a player.
 * */
public interface PlayerOperations {
	public void init();
	public void sendMessage(Message message);
	public String getName();
}
