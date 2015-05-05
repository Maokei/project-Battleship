package battleship.entity;

public enum Alignment {
	VERTICAL("vertical"),
	HORIZONTAL("horizontal");
	private final String type;
	
	private Alignment(String type) {
		this.type = type;
	}
	public String toString() {
		return type;
	}
}
