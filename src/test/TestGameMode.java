package test;
/**
 * @file TestGameMode.java
 * @date 2015-05-06
 * @authors rickard(rijo1001), lars(lama1205)
 * @brief File contains TestClass for GameMode.java
 * */
import static org.junit.Assert.*;
import org.junit.*;
import battleship.game.GameMode;

/**
 * @package test
 * @class TestGameMode tests the <code>GameMode</code> class.
 * @brief Test class to test GameMode class
 * */
public class TestGameMode {
	private GameMode mode = null;
	
	public TestGameMode() {}
	
	/**
	 * @name setUp
	 * @brief set up method instantiate mode to singlePlayer
	 * */
	@Before
	@Test
	public void setUp() {
		mode = GameMode.SinglePlayer;
		assertNotNull(mode);
		assertTrue(mode == GameMode.SinglePlayer);
	}
	
	/**
	 * @name testSinglePlayer
	 * @brief test if mode is SinglePlayer
	 * */
	@Test
	public void testSinglePlayer() {
		assertTrue(mode == GameMode.SinglePlayer);
		assertFalse(mode == GameMode.MultiPlayer);
	}
	
	/**
	 * @name testMultiPlayer
	 * @brief test if mode is MultiPlayer
	 * */
	@Test
	public void testMultiPlayer() {
		mode = GameMode.MultiPlayer;
		assertFalse(mode == GameMode.SinglePlayer);
		assertTrue(mode == GameMode.MultiPlayer);
	}
	
	/**
	 * @name testGetMode
	 * @brief test if getMode returns correct String representation
	 * */
	@Test
	public void testGetMode() {
		mode = GameMode.SinglePlayer;
		assertEquals(mode.getMode(), "Singleplayer");
		mode = GameMode.MultiPlayer;
		assertEquals(mode.getMode(), "Multiplayer");
	}
	
	/**
	 * @name tearDown
	 * @brief tearDown method sets mode to null
	 * */
	@After
	@Test
	public void tearDown() {
		mode = null;
		assertNull(mode);
	}
	
}
