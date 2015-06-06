/**
 * @file Ship.java
 * @date 2015-05-05
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.ships;

import java.util.Vector;

import battleship.gameboard.Grid;

/**
 * @package battleship.ships
 * @class Ship class represents a Ship
 * @brief Abstract class describes a Ship.
 * */
public abstract class Ship {
	protected String type;
	protected String shipName;
	protected int length;
	protected int health;
	protected Vector<Grid> position;
	protected Alignment alignment;
	protected boolean alive = true;

	/**
	 * @constructor Ship
	 * @name Ship
	 * @brief Ship constructor generates a ship name and initiates the ship
	 *        grid.
	 * */
	public Ship() {
		shipName = SimpleShipName.getInstance().generateName();
		position = new Vector<Grid>();
	}

	/**
	 * toString
	 * 
	 * @name toString
	 * @brief returns Ship attributes as a String
	 * @return String representation of a Ship
	 * */
	public String toString() {
		return String.format("%s, %d, %d, %s", type, length, health, alignment);
	}

	/**
	 * getType
	 * 
	 * @name getType
	 * @brief returns a Ship type
	 * @return type a Ship type
	 * */
	public String getType() {
		return type;
	}

	/**
	 * getLength
	 * 
	 * @name getLength
	 * @brief returns a Ship length
	 * @return type a Ship length
	 * */
	public int getLength() {
		return length;
	}

	/**
	 * getHealth
	 * 
	 * @name getHealth
	 * @brief returns a Ship health
	 * @return type a Ship health
	 * */
	public int getHealth() {
		return health;
	}

	/**
	 * getAlignment
	 * 
	 * @name getAlignment
	 * @brief returns a Ship alignment
	 * @return type a Ship alignment
	 * */
	public Alignment getAlignment() {
		return alignment;

	}

	/**
	 * isAlive
	 * 
	 * @name isAlive
	 * @brief returns flag representing if Ship is alive
	 * @return alive true if Ship is alive, false otherwise
	 * */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * getShipName
	 * 
	 * @name getShipName
	 * @brief returns a Ship SimpleShipName
	 * @return type a String of Ship SimpleShipName
	 * */
	public String getShipName() {
		return shipName;
	}

	/**
	 * getPosition
	 * 
	 * @name getPosition
	 * @brief returns a Vector of Grid representing Ship positions 
	 * @return position Vector of Grid
	 * */
	public Vector<Grid> getPosition() {
		return position;
	}

	/**
	 * getStartPosition
	 * 
	 * @name getStartPosition
	 * @brief returns a Grid representing a Ship start position
	 * @return position first Grid of position Vector
	 * */
	public Grid getStartPosition() {
		return position.elementAt(0);
	}

	/**
	 * setAlignment
	 * 
	 * @name setAlignment
	 * @brief sets Ship Alignment
	 * @param Takes
	 *            Alignment enumerated and sets ship alignment.
	 * */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * addPositionGrid
	 * 
	 * @name addPositionGrid
	 * @brief adds a new Grid to ship position
	 * @param row
	 *            to add to ship grid.
	 * @param col
	 *            to add to ship grid.
	 * */
	public void addPositionGrid(int row, int col) {
		position.add(new Grid(row, col));
	}

	/**
	 * isInPosition
	 * 
	 * @name isInPosition
	 * @brief checks if ship is in position
	 * @param row
	 *            to check
	 * @param col
	 *            to check
	 * @return True if position matches, false otherwise.
	 * */
	public boolean isInPosition(int row, int col) {
		for (Grid grid : position) {
			if ((grid.getRow() == row) && (grid.getCol() == col)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checkHit
	 * 
	 * @name checkHit
	 * @brief check if ship is hit
	 * @param row
	 *            to check
	 * @param col
	 *            to check
	 * @return Boolean true if was hit on given coordinates.
	 * */
	public boolean checkHit(int row, int col) {
		for (Grid grid : position) {
			if (alive && ((grid.getRow() == row) && (grid.getCol() == col))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * hit
	 * 
	 * @name hit
	 * @brief decrements one from health, if health is zero, sets alive to false
	 * */
	public void hit() {
		--health;
		if (health == 0)
			alive = false;
	}

}
