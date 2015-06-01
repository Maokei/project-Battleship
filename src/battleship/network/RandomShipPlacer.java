package battleship.network;

import static battleship.game.Constants.*;

import java.util.Random;
import java.util.Vector;

import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;
import battleship.ships.ShipBuilder;

public class RandomShipPlacer {
	private char[][] gridboard;
	private Vector<Ship> ships;
	private Random r = new Random();

	public RandomShipPlacer() {
		randomizeShipGrid();
	}
	public char[][] getRandomShipGrid() {
		return gridboard;
	}

	public Vector<Ship> getRandomShips() {
		return ships;
	}

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

	public void placeAllShips() {
		for (Ship ship : ships) {
			randomizeAlignment(ship);
		}

		for (Ship ship : ships) {
			Grid grid = getValidGrid(ship);
			placeShip(ship, grid);
		}
	}

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

	private Grid getValidGrid(Ship ship) {
		int row, col;
		do {
			int random = getRandomInt(SIZE * SIZE);
			row = random / SIZE;
			col = random % SIZE;
		} while (!checkGrid(ship, new Grid(row, col)));
		return new Grid(row, col);
	}

	private boolean checkGrid(Ship ship, Grid grid) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int row = grid.getRow();
			counter = grid.getCol();
			if (!(grid.getCol() + ship.getLength() < SIZE))
				return false;

			for (int i = 0; i < ship.getLength(); i++) {
				if (!checkEmptyGrid(row, counter)) {
					return false;
				}
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = grid.getRow();
			int col = grid.getCol();
			if (!(grid.getRow() + ship.getLength() < SIZE))
				return false;
			for (int i = 0; i < ship.getLength(); i++) {
				if (!checkEmptyGrid(counter, col)) {
					return false;
				}
				counter++;
			}
		}
		return true;
	}

	private void randomizeAlignment(Ship ship) {
		ship.setAlignment((r.nextInt(100) < 50) ? Alignment.HORIZONTAL
				: Alignment.VERTICAL);
	}

	private int getRandomInt(int number) {
		return r.nextInt(number);
	}

	private boolean checkEmptyGrid(int row, int col) {
		return (gridboard[row][col] == empty);
	}

}