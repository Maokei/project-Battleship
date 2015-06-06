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
 * @class Ship 
 * @brief Abstract class describes a Ship.
 * */
public abstract class Ship {
	protected String type;
	protected String shipName;
	protected int length;
	protected int health;
	protected int posX;
	protected int posY;
	protected int posX1;
	protected int posY1;
	protected Vector<Grid> position;
	protected Alignment alignment;
	protected boolean alive = true;
	
	/**
	 * Ship Constructor
	 * @name Ship
	 * @brief Generates a ship name and initiates the ship grid.
	 * */
	public Ship() {
		shipName = SimpleShipName.getInstance().generateName();
		position = new Vector<Grid>();
	}
	
	/**
	 * toString
	 * @name toString
	 * */
	public String toString() {
		return String.format("%s, %d, %d, %d, %d, %s", type, length, health, posX, posY, alignment);
	}
	
	/**
	 * Getters
	 * */
	public String getType() { return type; }
	public int getLength() { return length; }
	public int getHealth() { return health; }
	public Alignment getAlignment() { return alignment; }
	public boolean isAlive() { return alive; }
	public int getX() {return posX;}
	public int getY() {return posY;}
	public int getX1() {return posX1;}
	public int getY1() {return posY1;}
	public String getShipName() {return shipName;}
	public Vector<Grid> getPosition() { return position; }
	public Grid getStartPosition() {
		return position.elementAt(0);
	}
	
	/**
	 * setAlignment
	 * @name setAlignment
	 * @param Takes Alignment enumerated and sets ship alignment.
	 * */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * addPositionGrid
	 * @name addPositionGrid
	 * @param Integer row and integer column to add to ship grid.
	 * */
	public void addPositionGrid(int row, int col) {
		position.add(new Grid(row, col));
	}
	
	/**
	 * isInPosition
	 * @name isInPosition
	 * @param Integer row and integer column on grid.
	 * @return True if position matches.
	 * */
	public boolean isInPosition(int row, int col) {
		for(Grid grid : position) {
			if((grid.getRow() == row) && (grid.getCol() == col)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checkHit
	 * @name checkHit
	 * @param Integer row and integer column on grid.
	 * @return Boolean true if was hit on given coordinates.
	 * */
	public boolean checkHit(int row, int col) {
		for(Grid grid : position) {
			if(alive && ((grid.getRow() == row) && (grid.getCol() == col))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * setPosition
	 * @name setPosition
	 * @param Integer row and integer column on grid.
	 * @brief Set Ship position on grid.
	 * */
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
		if(alignment == Alignment.HORIZONTAL)
			posX1 += length;
		else
			posY1 += length;
	}	
	
	/**
	 * wasHit
	 * @name wasHit
	 * @param Integer x and integer y, register hit on ship.
	 * */
	public boolean wasHit(int x, int y) {
		if(alignment == Alignment.HORIZONTAL) {
			if(y == posY && (x >= posX && x <= posX1)) {
				hit();
				return true;
			}
		}else{ //vertical
			if(x == posX && (y >= posY && y <= posY1)) {
				hit();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * hit
	 * @name hit
	 * @brief Decrement life on ship.
	 * */
	public void hit() {
		--health;
		if(health == 0) 
			alive = false;
	}
	
	
	public abstract void draw();
}






