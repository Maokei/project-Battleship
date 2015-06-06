/**
 * @file ShipBuilder.java
 * @author rickard, lars
 * @date 2015-05-25
 * */
package battleship.ships;

import static battleship.game.Constants.NUM_OF_DESTROYERS;
import static battleship.game.Constants.NUM_OF_SUBMARINES;

import java.util.Vector;

/**
 * @package battleship.ships
 * @class ShipBuilder class populates a Vector of Ship objects
 * @brief Class builds ships for the players.
 * */
public class ShipBuilder {
	
	/**
	 * buildShips
	 * @name buildShips
	 * @brief static method that returns all ships
	 * @return Returns Vector of Ship containing all of the ships a player needs for a battle.
	 * */
	public static Vector<Ship> buildShips() {
		Vector<Ship> ships = new Vector<Ship>(9);
		ships.add(BattleShipFactory.getShip(ShipType.CARRIER));
		for (int i = 0; i < NUM_OF_DESTROYERS; i++) {
			ships.add(BattleShipFactory.getShip(ShipType.DESTROYER));
		}
		for (int i = 0; i < NUM_OF_SUBMARINES; i++) {
			ships.add(BattleShipFactory.getShip(ShipType.SUBMARINE));
		}
		return ships;
	}
}
