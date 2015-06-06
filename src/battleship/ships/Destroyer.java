/**
 * @file Destroyer.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.ships;


/**
 * @class Destroyer
 * @extends Ship
 * */
public class Destroyer extends Ship {

	public Destroyer() {
		type = "Destroyer";
		length = health = 3;
		alignment = Alignment.HORIZONTAL;
	}
	
	@Override
	public void draw() {
		System.out.println("I'm drawing a Destroyer");
	}
	
}
