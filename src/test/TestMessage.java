/**
 * @file TestMessage.java
 * @date 2015-05-06
 * @authors rickard(rijo1001), lars(lama1205)
 * @brief File contains TestClass for Message.java
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
import battleship.game.Message;

/**
 * @package test
 * @class TestMessage tests the <code>Message</code> class.
 * @brief Test class to test Message class
 * */
public class TestMessage {
	private Message msg = null;
	
	public TestMessage() {}
	
	/**
	 * @name setUp
	 * @brief set up method instantiate msg to Message.LOGIN
	 * */
	@Before
	@Test
	public void setUp() {
		msg = new Message(Message.LOGIN, "JohnDoe", "JaneDoe", "message");
		assertNotNull(msg);
	}
	
	/**
	 * @name testGetType
	 * @brief test if getType is Message.LOGIN
	 * */
	@Test
	public void testGetType() {
		assertTrue(msg.getType() == 0);
		assertFalse(msg.getType() != 0);
	}
	
	/**
	 * @name testGetSender
	 * @brief test if getSender returns correct String
	 * */
	@Test
	public void testGetSender() {
		assertEquals(msg.getSender(), "JohnDoe");
		assertFalse(msg.getSender().equalsIgnoreCase(""));
	}
	
	/**
	 * @name testGetReceiver
	 * @brief test if getReceiver returns correct String
	 * */
	@Test
	public void testGetReceiver() {
		assertEquals(msg.getReceiver(), "JaneDoe");
		assertFalse(msg.getReceiver().equalsIgnoreCase(""));
	}
	
	
	/**
	 * @name testGetMessage
	 * @brief test if getReceiver returns correct String
	 * */
	@Test
	public void testGetMessage() {
		assertEquals(msg.getMessage(), "message");
		assertFalse(msg.getMessage().equalsIgnoreCase(""));
	}
	
	/**
	 * @name tearDown
	 * @brief tearDown method sets msg to null
	 * */
	@After
	@Test
	public void tearDown() {
		msg = null;
		assertNull(msg);
	}
	
}
