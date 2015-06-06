/**
 * @file Submarine.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.ships;



/**
 * @package battleship.ship
 * @class Submarine class Submarine represents a ship of length 1
 * @extends Ship
 * @brief Class defines a Submarine ship 
 **/
public class Submarine extends Ship {

	public Submarine() {
		type = "Submarine";
		length = health = 1;
		//default 
		alignment = Alignment.HORIZONTAL;
	}
}
