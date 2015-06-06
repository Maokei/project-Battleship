/**
 * @file RandomShipPlacer.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
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
 * RandomShipPlacer
 * 
 * @package battleship.network
 * @class RandomShipPlacer Randomize placement of ships on a 10x10 Grid
 * @brief Randomizes ship placement 
 * */
public class RandomShipPlacer {
	private char[][] gridboard;
	private Vector<Ship> ships;
	private Random r = new Random();

	/**
	 * RandomShipPlacer
	 * 
	 * @constructor RandomShipPlacer
	 * @name RandomShipPlacer
	 * @brief constructor calls randomizeShipGrid to randomize ship placement 
	 * */
	public RandomShipPlacer() {
		randomizeShipGrid();
	}
	
	/**
	 * getRandomShipGrid
	 * 
	 * @name getRandomShipGrid
	 * @brief returns the grid with randomized ship placements
	 * @return gridboard a multi-dimensional char array 
	 * */
	public char[][] getRandomShipGrid() {
		return gridboard;
	}

	/**
	 * getRandomShips
	 * 
	 * @name getRandomShips
	 * @brief returns a Vector containing ships with randomized placements
	 * @return ships a Vector of Ship
	 * */
	public Vector<Ship> getRandomShips() {
		return ships;
	}

	/**
	 * randomizeShipGrid
	 * 
	 * @name randomizeShipGrid
	 * @brief initiates the gridboard to empty, retrives ships,  and calls placeAllShips to randomize placement
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
	 * 
	 * @name placeAllShips
	 * @brief randomizes alignment, gets a valid grid and places ship
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
	 * 
	 * @name placeShip
	 * @brief randomizes alignment, gets a valid grid and places ship
	 * @param ship the ship to place
	 * @param grid the valid grid onto which place ship
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
	 * 
	 * @name getValidGrid
	 * @brief gets a Grid at random and checks whether the ship can be placed there
	 * @param ship the ship used to determine valid grid
	 * @return a Grid instance
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
	 * 
	 * @name checkGrid
	 * @brief checks whether a ship can be placed  starting from the grid
	 * @param ship the ship to be placed
	 * @param grid the grid to which test the ship placement
	 * @return true if ship can be placed on grid, false otherwise
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
	 * 
	 * @name randomizeAlignment
	 * @brief randomizes ship alignment
	 * @param ship the ship to which set the alignment
	 * */
	private void randomizeAlignment(Ship ship) {
		ship.setAlignment((r.nextInt(100) < 50) ? Alignment.HORIZONTAL
				: Alignment.VERTICAL);
	}

	/**
	 * getRandomInt
	 * 
	 * @name getRandomInt
	 * @brief randomizes a number between zero and number exclusive
	 * @param the number to be used as a range
	 * @return a number between zero and number exclusive
	 * */
	private int getRandomInt(int number) {
		return r.nextInt(number);
	}

}