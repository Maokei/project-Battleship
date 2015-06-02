/**
 * @file GameMode.java
 * @date 2015-05-25
 * */
package battleship.game;

/**
 * @class GameMode
 * @brief Enumerated type differentiates between single and multiplayer mode.
 * */
public enum GameMode {
	SinglePlayer("Singleplayer"),
	MultiPlayer("Multiplayer");
	
	private final String mode;
	
	private GameMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * getMode
	 * @name getMode
	 * @return enumerated playing mode
	 * */
	public String getMode() {
		return mode;
	}
	
	
}
