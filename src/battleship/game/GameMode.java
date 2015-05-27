package battleship.game;

public enum GameMode {
	SinglePlayer("Single player"),
	MultiPlayer("Multi player");
	
	private final String mode;
	
	private GameMode(String type) {
		this.mode = type;
	}
	public String toString() {
		return mode;
	}
	
	
}
