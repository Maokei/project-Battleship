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
	
	public TestShips() {}

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
		carrier.hit();
		assertFalse(carrier.isAlive());
	}
	
	@Test 
	public void testShipBuilder() {
		Vector<Ship> ships = ShipBuilder.buildShips();
		assertTrue(ships.get(0).getType() == "Carrier");
		assertTrue(ships.get(1).getType() == "Destroyer");
		assertTrue(ships.get(2).getType() == "Destroyer");
		assertTrue(ships.get(3).getType() == "Destroyer");
		assertTrue(ships.get(4).getType() == "Submarine");
		assertTrue(ships.get(5).getType() == "Submarine");
		assertTrue(ships.get(6).getType() == "Submarine");
		assertTrue(ships.get(7).getType() == "Submarine");
		assertTrue(ships.get(8).getType() == "Submarine");
	}
}
