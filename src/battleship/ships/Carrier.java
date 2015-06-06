/**
 * @file Carrier.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.ships;


/**
 *@package battleship.entity
 *@class Carrier
 *@brief Class defines a carrier ship 
 **/
public class Carrier extends Ship {
	
	public Carrier() {
		type = "Carrier";
		length = health = 5;
		alignment = Alignment.HORIZONTAL;
	}
	
	@Override
	public void draw() {
		System.out.println("I'm drawing a Carrier");
	}
}
