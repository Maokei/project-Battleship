/**
 * @file BattleShipFactory.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.player;

/**
 * @package battleship.entity
 * @class BattleShipFactory
 * */
public class BattleShipFactory {
	public Ship getShip(ShipType type) {
		Ship ship = null;
		switch(type) {
		case SUBMARINE: return new Submarine(); 
		case DESTROYER: return new Destroyer();
		case CARRIER: return new Carrier();
		default: return ship;
		}
	}
}
