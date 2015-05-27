/**
 * @file Submarine.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.ships;


/**
 * @package battleship.entity
 * @class Submarine
 * */
class Submarine extends Ship {

	public Submarine() {
		type = "Submarine";
		length = health = 1;
		//default 
		alignment = Alignment.HORIZONTAL;
	}
	
	@Override
	public void draw() {
		System.out.println("I'm drawing a Submarine");
	}
	
}
