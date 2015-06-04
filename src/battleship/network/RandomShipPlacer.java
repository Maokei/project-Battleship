/**
 * @file RandomShipPlacer.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.network;

import static battleship.game.Constants.*;

import java.util.Random;
import java.util.Vector;

import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;
import battleship.ships.ShipBuilder;

/**
 * @class RandomShipPlacer
 * @brief Class describes a way to randomly place ships with 1 space in between each ship.
 * */
public class RandomShipPlacer {
	private char[][] gridboard;
	private Vector<Ship> ships;
	private Random r = new Random();

	public RandomShipPlacer() {
		randomizeShipGrid();
	}
	
	/**
	 * getRandomShipGrid
	 * @name getRandomShipGrid
	 * @return return a char[][] grid 10 * 10
	 * */
	public char[][] getRandomShipGrid() {
		return gridboard;
	}

	/**
	 * getRandomShips
	 * @name getRandomShips
	 * @returns Ship vector.
	 * */
	public Vector<Ship> getRandomShips() {
		return ships;
	}

	/**
	 * randomizeShipGrid
	 * @name randomizeShipsGrid
	 * @brief randomize ships on grid.
	 * */
	private void randomizeShipGrid() {
		gridboard = new char[SIZE][SIZE];
		ships = ShipBuilder.buildShips();
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col] = empty;
			}
		}
		placeAllShips();
	}

	/**
	 * placeAllShips
	 * @name placeAllShips
	 * @brief Meta function to start placing ships on char grid.
	 * */
	public void placeAllShips() {
		for (Ship ship : ships) {
			randomizeAlignment(ship);
		}

		for (Ship ship : ships) {
			Grid grid = getValidGrid(ship);
			placeShip(ship, grid);
		}
	}

	/**
	 * placeShip
	 * @name placeShip
	 * @brief Place a ship on the grid.
	 * @param Ship object to be placed and a grid to place the ship on.
	 * */
	private void placeShip(Ship ship, Grid grid) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int row = grid.getRow();
			counter = grid.getCol();
			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(row, counter);
				gridboard[row][counter] = occupied;
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = grid.getRow();
			int col = grid.getCol();

			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(counter, col);
				gridboard[counter][col] = occupied;
				counter++;
			}
		}
	}

	/**
	 * getValidGrid
	 * @name getValidgrid
	 * @param Ship object
	 * @return returns new grid object
	 * */
	private Grid getValidGrid(Ship ship) {
		int row, col;
		do {
			int random = getRandomInt(SIZE * SIZE);
			row = random / SIZE;
			col = random % SIZE;
		} while (!checkGrid(ship, new Grid(row, col)));
		return new Grid(row, col);
	}

	/**
	 * checkGrid
	 * @name checkGrid
	 * @param Ship object, Grid object
	 * @return validates grid, true valid.
	 * */
	private boolean checkGrid(Ship ship, Grid grid) {
		int row = grid.getRow();
		int col = grid.getCol();
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int width = ship.getLength() + 2;
			int height = 3;
			if ((col + ship.getLength() - 1) < SIZE) {
				if (col > 0) {
					--col;
				}
				if(!(col + ship.getLength() < SIZE - 1)) {
					--width;
				}
				if (!(row < (SIZE - 1))) {
					--height;
				}
				
				if (row > 0) {
					--row;
				}
				
				int rowCounter = row;
				int colCounter = col;
				for(int i = 0; i < height; i++) {
					colCounter = col;
					for(int j = 0; j < width; j++) {
						if(!(gridboard[rowCounter][colCounter++] == empty)) {
							return false;
						}
					}
					rowCounter++;
				}
			} else {
				return false;
			}

		} else if(ship.getAlignment() == Alignment.VERTICAL) {
			int width = 3;
			int height = ship.getLength() + 2;
			if ((row + ship.getLength() - 1) < SIZE) {
				if (row > 0) {
					--row;
				}
				if(!(row + ship.getLength() < SIZE - 1)) {
					--height;
				}
				if (!(col < (SIZE - 1))) {
					--width;
				}
				if (col > 0) {
					--col;
					
				}
				int rowCounter = row;
				int colCounter = col;
				for(int i = 0; i < width; i++) {
					rowCounter = row;
					for(int j = 0; j < height; j++) {
						if(!(gridboard[rowCounter++][colCounter] == empty)) {
							return false;
						}
					}
					colCounter++;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * randomizeAlignment
	 * @name randomizeAlignment
	 * @param Ship pointer to randomize ship alignment.
	 * */
	private void randomizeAlignment(Ship ship) {
		ship.setAlignment((r.nextInt(100) < 50) ? Alignment.HORIZONTAL
				: Alignment.VERTICAL);
	}

	/**
	 * getRandomnInt
	 * @name getRandomInt
	 * @param Integer to set random upper bound.
	 * @return Integer returns a random integer.
	 * */
	private int getRandomInt(int number) {
		return r.nextInt(number);
	}

	/**
	 * checkEmptyGrid
	 * @name checkEmptyGrid
	 * @param Integer row and integer column.
	 * @return boolean depending on if grid cell is empty or not.
	 * */
	private boolean checkEmptyGrid(int row, int col) {
		return (gridboard[row][col] == empty);
	}

}