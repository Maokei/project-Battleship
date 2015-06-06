/**
 * @file Constants.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.game;

/**
 * @package battleship.entity
 * @class Constants
 * @brief This class only meant to hold constant values and cannot be instantiated.
 * */
public class Constants {
	
	private Constants() {
		//this is a restricted area!
	}
	
	/**
	 * Number of ships for each player constants
	 * */
	public static final int NUM_OF_CARRIERS = 1;
	public static final int NUM_OF_DESTROYERS = 3;
	public static final int NUM_OF_SUBMARINES = 5;
	
	// Size of each grid in GameBoard
	public static final int SIZE = 10;
	public static final int GRID_SIZE = 32;
	
	public static final char empty = 'e';
	public static final char occupied = 'o';
	public static final char hit = 'h';
	public static final char miss = 'm';
	
	public static final String Challenge_Request = "Request";
	public static final String Challenge_Accept = "Accept";
	public static final String Challenge_Deny = "Deny";
	public static final String Challenge_Name = "Name";
	
	public static final String Valid_Move = "Valid";
	public static final String NonValid_Move = "NonValid";
}
