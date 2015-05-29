package battleship.game;

public enum GameMode {
	SinglePlayer("Singleplayer"),
	MultiPlayer("Multiplayer");
	
	private final String mode;
	
	private GameMode(String mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}
	
	
}
