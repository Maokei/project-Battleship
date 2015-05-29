package battleship.ships;

import static battleship.game.Constants.NUM_OF_DESTROYERS;
import static battleship.game.Constants.NUM_OF_SUBMARINES;

import java.util.Vector;

public class ShipBuilder {
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
