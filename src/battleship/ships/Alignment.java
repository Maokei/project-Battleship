/**
 * @file Alignment.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.ships;

/**
 * Alignment
 * @Enumerated Alignment
 * @brief Describes the alignment of a ship on a battleship grid.
 * Vertical and Horizontal.
 * */
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
