/**
 * @file Destroyer.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.ships;


/**
 * @package battleship.ships
 * @class Destroyer class Destroyer represents a ship of length 3
 * @extends Ship
 * @brief Class defines a Destroyer ship 
 **/
public class Destroyer extends Ship {

	public Destroyer() {
		type = "Destroyer";
		length = health = 3;
		alignment = Alignment.HORIZONTAL;
	}
	
}
