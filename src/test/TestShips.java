/**
 * @file TestShips.java
 * @author Lars, Rickard
 * @date 2015-05-25
 * @brief File contains test class for ships
 * */
package test;

import static org.junit.Assert.*;

import org.junit.Test;
import battleship.ships.*;

public class TestShips {
	private Submarine submarine;
	private Destroyer destroyer;
	private Carrier carrier;
	
	public void TestShips() {
		submarine = new Submarine();
		destroyer = new Destroyer();
		carrier = new Carrier();
	}

	@Test
	public void testSubmarine() {
		submarine.addPositionGrid(5, 3);
		submarine.setAlignment(Alignment.HORIZONTAL);
		assertTrue(submarine.getX() == 5);
		assertTrue(submarine.getY() == 3);
		submarine.hit();
		assertTrue(submarine.checkHit(5, 3));
		assertFalse(submarine.isAlive());
	}
	
	@Test
	public void testDestroyer() {
		destroyer.addPositionGrid(2, 3);
		destroyer.setAlignment(Alignment.HORIZONTAL);
		assertTrue(destroyer.getX() == 2);
		assertTrue(destroyer.getY() ==3);
		destroyer.hit();
		destroyer.hit();
		destroyer.hit();
		assertFalse(destroyer.isAlive());
	}
	
	@Test
	public void testCarrier() {
		carrier.addPositionGrid(5, 3);
		assertTrue(carrier.getX() == 5);
		assertTrue(carrier.getY() == 3);
		carrier.hit();
		carrier.hit();
		carrier.hit();
		carrier.hit();
		assertTrue(carrier.isAlive());
	}
}
