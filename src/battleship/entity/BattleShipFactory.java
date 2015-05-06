/**
 * @file ShipFactory.java
 * */
package battleship.entity;

/**
 * 
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
