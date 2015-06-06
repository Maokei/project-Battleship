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

import battleship.game.GameMode;
import battleship.gameboard.Grid;

/**
 * @package test
 * @class TestGrid tests the <code>Grid</code> class.
 * @brief Test class to test Grid class
 * */
public class TestGrid {
	private Grid grid;
	
	public TestGrid() {}
	
	/**
	 * @name setUp
	 * @brief sets the Grid coordinates to -1, -1
	 * */
	@Before
	@Test
	public void setUp() {
		Grid grid = new Grid(-1, -1);
		assertNotNull(grid);
	}
	
	/**
	 * @name testSetOccupied
	 * @brief test setOccupied 
	 * */
	@Test
	public void testSetOccupied() {
		grid = new Grid(3, 3);
		assertEquals(grid.isEmpty(), true);
		grid.setOccupied();
		assertEquals(grid.isEmpty(), false);
	}
	
	/**
	 * @name testEmpty
	 * @brief test isEmpty
	 * */
	@Test
	public void testIsEmpty() {
		grid = new Grid(4, 3);
		assertEquals(grid.isEmpty(), true);
	}
	
	/**
	 * @name testSetHit
	 * @brief test setHit
	 * */
	@Test
	public void testSetHit() {
		grid = new Grid(8, 8);
		assertEquals(grid.isEmpty(), true);
		grid.setHit();
		assertEquals(grid.isHit(), true);
	}
	
	/**
	 * @name testIsHit
	 * @brief test isHit
	 * */
	@Test
	public void testIsHit() {
		grid = new Grid(9, 8);
		assertEquals(grid.isEmpty(), true);
		grid.setHit();
		// assertEquals(grid.isEmpty(), false);
		assertEquals(grid.isHit(), true);
	}
	
	/**
	 * @name testSetMiss
	 * @brief test setMiss
	 * */
	@Test
	public void testSetMiss() {
		grid = new Grid(2, 4);
		assertEquals(grid.isEmpty(), true);
		grid.setMiss();
		assertEquals(grid.isMiss(), true);
	}
	
	/**
	 * @name testIsMiss
	 * @brief test isMiss
	 * */
	@Test
	public void testIsMiss() {
		grid = new Grid(5, 8);
		assertEquals(grid.isEmpty(), true);
		grid.setMiss();
		assertEquals(grid.isMiss(), true);
	}
	
	/**
	 * @name testRowAndColGetters
	 * @brief test if getters return correct row and col
	 * */
	@Test
	public void testRowAndColGetters() {
		grid = new Grid(5, 9);
		assertTrue(grid.getRow() == 5);
		assertTrue(grid.getCol() == 9);
		assertFalse(grid.getRow() != 5);
		assertFalse(grid.getCol() != 9);
	}
	
	/**
	 * @name testEquals
	 * @brief test if equals returns correct flag
	 * */
	@Test
	public void testEquals() {
		grid = new Grid(2, 6);
		assertTrue(grid.equals(new Grid(2, 6)));
		assertEquals(grid, grid);
		assertEquals(grid,  new Grid(2, 6));
	}
	
	/**
	 * @name testHashCode
	 * @brief test if hashCode returns a valid hash
	 * */
	@Test
	public void testHashCode() {
		grid = new Grid(2, 6);
		Grid other = new Grid(2, 6);
		int hashOne = grid.hashCode();
		int hashTwo = other.hashCode();
		assertTrue(hashOne == hashTwo);
		assertFalse(new Grid(1, 4).hashCode() == new Grid(5, 9).hashCode());
	}
	
	/**
	 * @name tearDown
	 * @brief tearDown sets Grid to null
	 * */
	@After
	@Test
	public void tearDown() {
		grid = null;
		assertNull(grid);
	}
	
}
