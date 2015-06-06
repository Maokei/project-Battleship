/**
 * @file TestSimpleShipName.java
 * @date 2015-05-06
 * @authors rickard(rijo1001), lars(lama1205)
 * @brief File contains TestClass for SimpleShipName.java
 * */
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import battleship.ships.SimpleShipName;

/**
 * @package test
 * @class TestSimpleShipName tests the <code>SimpleShipName</code> class.
 * @brief Test class to test SimpleShipName class
 * */
public class TestSimpleShipName {
	private String test;
	private String names[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "Ghost", "Bosse", "Yamato", "Wolf", "R. Reagan",
			"Gustav", "Erika", "Samurai", "Olle", "Pippi", "Xaorui", "Janne",
			"Warchief", "Assasinator", "Zoee", "Garlic",
			"pain delivery system", "dominator", "ship", "dismantler",
			"harvester", "scream", "soul harvester", "hulk",
			"bonemarrow gatherer", "pirate ship", "scarling", "buttercup",
			"pile of rust", "powder cloud", "blood gutter", "red sail", "the" };
	private ArrayList<String> namesList;

	public TestSimpleShipName() {
		namesList = new ArrayList<String>();
		for(String name : names) {
			namesList.add(name);
		}
	}

	/**
	 * @name setUp
	 * @brief sets the String to "test"
	 * */
	@Before
	@Test
	public void setUp() {
		test = "test";
		test = SimpleShipName.getInstance().generateName();
		assertNotNull(test);
	}

	/**
	 * @name testGenerateName
	 * @brief tests that the name randomization is correct
	 * */
	@Test
	public void testGenerateName() {
		String[] tokens = test.split(" ");
		for(String token : tokens) {
			assertTrue(namesList.contains(token));
		}
	}
	
	/**
	 * @name tearDown
	 * @brief sets the test String to null
	 * */
	@After
	@Test
	public void tearDown() {
		test = null;
		assertNull(test);
	}
	
}
