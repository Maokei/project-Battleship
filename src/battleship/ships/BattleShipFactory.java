/**
 * @file BattleShipFactory.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.ships;


/**
 * @package battleship.ships
 * @class BattleShipFactory class return a Ship object based on name
 * @brief returns a Ship based on name
 * */
public class BattleShipFactory {
	
	/**
	 * getShip
	 * 
	 * @name getShip
	 * @param type the type of Ship
	 * @brief static method that returns a Ship based on name
	 * @return ship a Ship 
	 * */
	public static Ship getShip(ShipType type) {
		Ship ship = null;
		switch(type) {
		case SUBMARINE: return new Submarine(); 
		case DESTROYER: return new Destroyer();
		case CARRIER: return new Carrier();
		default: return ship;
		}
	}
}
