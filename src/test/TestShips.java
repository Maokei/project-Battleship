/**
 * @file TestShips.java
 * @author Lars, Rickard
 * @date 2015-05-25
 * @brief File contains test class for ships
 * */
package test;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import battleship.ships.*;

public class TestShips {
	
	public void TestShips() {}

	@Test
	public void testSubmarine() {
		Ship submarine = new Submarine();
		assertNotNull(submarine);
		submarine.addPositionGrid(5, 3);
		submarine.setAlignment(Alignment.HORIZONTAL);
		assertTrue(submarine.isInPosition(5, 3));
		submarine.addPositionGrid(5, 3);
		assertTrue(submarine.checkHit(5, 3));
		submarine.hit();
		assertFalse(submarine.isAlive());
	}
	
	@Test
	public void testDestroyer() {
		Ship destroyer = new Destroyer();
		destroyer.addPositionGrid(2, 3);
		destroyer.setAlignment(Alignment.HORIZONTAL);
		assertTrue(destroyer.getAlignment() == Alignment.HORIZONTAL);
		destroyer.hit();
		destroyer.hit();
		destroyer.hit();
		assertFalse(destroyer.isAlive());
	}
	
	@Test
	public void testCarrier() {
		Ship carrier = new Carrier();
		carrier.addPositionGrid(5, 3);
		carrier.hit();
		carrier.hit();
		carrier.hit();
		carrier.hit();
		assertTrue(carrier.isAlive());
	}
}
