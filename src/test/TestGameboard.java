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

import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import battleship.gameboard.Gameboard;
import battleship.gameboard.Grid;
import battleship.ships.BattleShipFactory;
import battleship.ships.Carrier;
import battleship.ships.Ship;
import battleship.ships.ShipType;
/**
 * @package test
 * @class TestGameBoard tests the <code>Gameboard</code> class.
 * @brief Test class to test Gameboard class
 * */
public class TestGameboard {
	private Gameboard gameboard;
	private static Ship ship;
	
	static {
		ship = BattleShipFactory.getShip(ShipType.CARRIER);
	}
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
		assertNotNull(ship);
		assertEquals(ship.getType(), "Carrier");
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
		assertTrue(gameboard.checkHit(3, 3));
		gameboard.addHit(3, 3);
		assertFalse(gameboard.checkHit(3, 3));
	}
	
	
	/**
	 * @name testCheckFire
	 * @brief test if a checkFire returns the right flag
	 * */
	@Test
	public void testCheckFire() {
		assertTrue(gameboard.checkFire(4, 4));
		gameboard.addHit(4, 4);
		assertFalse(gameboard.checkFire(4, 4));
	}
	
	/**
	 * @name testPlaceShip
	 * @brief test if a ship is placed on the gameboard
	 * */
	@Test
	public void testPlaceShip() {
		gameboard.placeShip(ship, 2, 2);
		int row = 2;
		int counter = 2;
		assertTrue(ship.getStartPosition() == new Grid(2, 2));
		for(Grid grid : ship.getPosition()) {
			assertTrue(grid.getRow() == row);
			assertFalse(grid.getRow() != row);
			assertTrue(grid.getCol() == counter++);
		}
	}
	
	@Test
	public void TestcheckShipPlacement() {
		assertTrue(gameboard.checkShipPlacement(ship, 7, 4));
		gameboard.placeShip(ship, 7, 2);
		assertFalse(gameboard.checkShipPlacement(ship, 7, 4));
	}
	
	/**
	 * @name tearDown
	 * @brief tearDown method sets msg to null
	 * */
	@After
	@Test
	public void tearDown() {
		gameboard = null;
		assertNull(gameboard);
	}
	
}
