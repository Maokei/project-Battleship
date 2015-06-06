/**
 * @file TestBattleShipFactory.java
 * @date 2015-05-06
 * @authors rickard(rijo1001), lars(lama1205)
 * @brief File contains TestClass for BattleShipFactory.java
 * */
package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import battleship.ships.BattleShipFactory;
import battleship.ships.Carrier;
import battleship.ships.Destroyer;
import battleship.ships.Ship;
import battleship.ships.ShipType;
import battleship.ships.Submarine;

/**
 * @package test
 * @class TestBattleShipFactory tests the <code>BattleShipFactory</code> class.
 * @brief Test class to test BattleShipFactory class
 * */
public class TestBattleShipFactory {
	private Ship ship = null;
	
	public TestBattleShipFactory() {}
	
	/**
	 * @name setUp
	 * @brief set up method instantiate ship to Carrier, assertNotNull for ship
	 * */
	@Before
	@Test
	public void setUp() {
		ship = BattleShipFactory.getShip(ShipType.CARRIER);
		assertNotNull(ship);
	}
	
	/**
	 * @name testGetShip
	 * @brief set up method to ensure that the factory creates the right class 
	 * */
	@Test
	public void testGetShip() {
		assertEquals(BattleShipFactory.getShip(ShipType.CARRIER).getClass(), new Carrier().getClass());
		assertEquals(BattleShipFactory.getShip(ShipType.SUBMARINE).getClass(), new Submarine().getClass());
		assertEquals(BattleShipFactory.getShip(ShipType.DESTROYER).getClass(), new Destroyer().getClass());
		
		ship = BattleShipFactory.getShip(ShipType.SUBMARINE);
		assertTrue(ship.getType().equalsIgnoreCase("Submarine"));
	}
	
	/**
	 * @name tearDown
	 * @brief sets the ship to null, assertNull on ship 
	 * */
	@After
	@Test
	public void tearDown() {
		ship = null;
		assertNull(ship);
	}
}
