/**
 * @file TestGameBoard.java
 * @date 2015-05-06
 * @authors rickard(rijo1001), lars(lama1205)
 * @brief File contains TestClass for GameBoard.java
 * */
package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import battleship.gameboard.Gameboard;
import battleship.gameboard.Grid;
import battleship.ships.Carrier;
import battleship.ships.Ship;

/**
 * @package test
 * @class TestGameBoard tests the <code>Gameboard</code> class.
 * @brief Test class to test Gameboard class
 * */
public class TestGameboard {
	private Gameboard gameboard;
	
	public TestGameboard() {}
	
	/**
	 * @name setUp
	 * @brief set up method instantiate gameboard, assertNotNull for gameboard and ship
	 * */
	@Before
	@Test
	public void setUp() {
		gameboard = new Gameboard();
		assertNotNull(gameboard);
	}
	
	/**
	 * @name testAddHit
	 * @brief test if a hit is added to gameboard
	 * */
	@Test
	public void testAddHit() {
		gameboard.addHit(5, 5);
		assertFalse(gameboard.checkHit(5, 5));
		assertFalse(gameboard.checkFire(5, 5));
	}
	
	
	/**
	 * @name testAddMiss
	 * @brief test if a miss is added to gameboard
	 * */
	@Test
	public void testAddMiss() {
		gameboard.addMiss(8, 8);
		assertFalse(gameboard.checkHit(8, 8));
		assertFalse(gameboard.checkFire(8, 8));
	}
	
	/**
	 * @name testCheckHit
	 * @brief test if a checkHit returns the right flag
	 * */
	@Test
	public void testCheckHit() {
		Ship ship = new Carrier();
		assertEquals(gameboard.checkHit(3, 3), false);
		gameboard.placeShip(ship, 3, 3);
		assertEquals(gameboard.checkHit(3, 3), true);
	}
	
	
	/**
	 * @name testCheckFire
	 * @brief test if a checkFire returns the right flag
	 * */
	@Test
	public void testCheckFire() {
		assertEquals(gameboard.checkFire(4, 4), true);
		gameboard.addHit(4, 4);
		assertEquals(gameboard.checkFire(4, 4), false);
	}
	
	/**
	 * @name testPlaceShip
	 * @brief test if a ship is placed on the gameboard
	 * */
	@Test
	public void testPlaceShip() {
		Ship ship = new Carrier();
		gameboard.placeShip(ship, 2, 2);
		int row = 2;
		int counter = 2;
		assertEquals(ship.getStartPosition(), new Grid(2, 2));
		for(Grid grid : ship.getPosition()) {
			assertTrue(grid.getRow() == 2);
			assertFalse(grid.getRow() != row);
			assertFalse(grid.getCol() != counter);
			assertTrue(grid.getCol() == counter++);
		}
	}
	
	/**
	 * @name TestCheckShipPlacement
	 * @brief test if a checkShipPlacement returns correct flag
	 * */
	@Test
	public void TestCheckShipPlacement() {
		Ship ship = new Carrier();
		assertTrue(gameboard.checkShipPlacement(ship, 7, 4));
		gameboard.placeShip(ship, 7, 2);
		assertFalse(gameboard.checkShipPlacement(ship, 7, 4));
	}
	
	/**
	 * @name tearDown
	 * @brief tearDown method sets gameboard to null
	 * */
	@After
	@Test
	public void tearDown() {
		gameboard = null;
		assertNull(gameboard);
	}
	
}
