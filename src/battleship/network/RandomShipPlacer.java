package battleship.network;

import static battleship.game.Constants.SIZE;

import java.util.Random;
import java.util.Vector;

import battleship.gameboard.Grid;
import battleship.ships.Alignment;
import battleship.ships.Ship;
import battleship.ships.ShipBuilder;

public class RandomShipPlacer {
	private static char[][] gridboard;
	private static Vector<Ship> ships;
	private static Vector<Grid> occupied;
	private static Random r = new Random();

	public static char[][] getRandomShipGrid() {
		randomizeShipGrid();
		return gridboard;
	}

	public static Vector<Ship> getRandomShips() {
		randomizeShipGrid();
		return ships;
	}

	private static void randomizeShipGrid() {
		gridboard = new char[SIZE][SIZE];
		ships = ShipBuilder.buildShips();
		occupied = new Vector<Grid>();
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col] = 'e';
			}
		}

		placeAllShips();
	}

	public static void placeAllShips() {
		for (Ship ship : ships) {
			randomizeAlignment(ship);
		}

		for (Ship ship : ships) {
			Grid grid = getValidGrid(ship);
			placeShip(ship, grid);
		}
	}

	private static void placeShip(Ship ship, Grid grid) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int row = grid.getRow();
			counter = grid.getCol();
			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(row, counter);
				gridboard[row][counter] = 'o';
				occupied.add(new Grid(row, counter));
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = grid.getRow();
			int col = grid.getCol();

			for (int i = 0; i < ship.getLength(); i++) {
				ship.addPositionGrid(counter, col);
				gridboard[counter][col] = 'o';
				occupied.add(new Grid(counter, col));
				counter++;
			}
		}
	}

	private static Grid getValidGrid(Ship ship) {
		int row, col;
		do {
			int random = getRandomInt(SIZE * SIZE);
			row = random / SIZE;
			col = random % SIZE;
		} while (!checkGrid(ship, new Grid(row, col)));
		return new Grid(row, col);
	}

	private static boolean checkGrid(Ship ship, Grid grid) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int row = grid.getRow();
			counter = grid.getCol();
			if(!(counter + ship.getLength() < SIZE))
				return false;

			for (int i = 0; i < ship.getLength(); i++) {
				for (Grid g : occupied) {
					if (g.getRow() == row && g.getCol() == counter)
						return false;
				}
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = grid.getRow();
			int col = grid.getCol();
			if(!(counter + ship.getLength() < SIZE))
				return false;
			for (int i = 0; i < ship.getLength(); i++) {
				for (Grid g : occupied) {
					if (g.getRow() == counter && g.getCol() == col)
						return false;
				}
			}
		}
		return true;
	}

	private static void randomizeAlignment(Ship ship) {
		ship.setAlignment((r.nextInt(100) < 50) ? Alignment.HORIZONTAL
				: Alignment.VERTICAL);
	}

	private static int getRandomInt(int number) {
		return r.nextInt(number);
	}

	/*
	 * 
	 * public static char[][] getRandomShipGrid() { randomizeShipGrid(); return
	 * grid; }
	 * 
	 * public static Vector<Ship> getRandomShips() { randomizeShipGrid(); return
	 * ships; }
	 * 
	 * private static void placeShipRandom(Ship ship) { int rowSize = SIZE,
	 * colSize = SIZE; int gridNumber, rowNumber, colNumber; int length =
	 * ship.getLength(); randomizeAlignment();
	 * 
	 * if (alignment == Alignment.HORIZONTAL) colSize -= length; else if
	 * (alignment == Alignment.VERTICAL) rowSize -= length;
	 * 
	 * gridNumbers = new Vector<Integer>(colSize * rowSize);
	 * 
	 * for (int row = 0; row < rowSize; row++) { // System.out.print("["); for
	 * (int col = 0; col < colSize; col++) { gridNumbers.add((row * SIZE - 1) +
	 * col); // System.out.print((row * SIZE) + col); } //
	 * System.out.print("]\n"); }
	 * 
	 * boolean canPlace; do { canPlace = true;
	 * 
	 * gridNumber = getRandomGrid(gridNumbers); rowNumber = gridNumber / SIZE;
	 * colNumber = gridNumber % SIZE; System.out.println(" random Grid [" +
	 * rowNumber + ", " + colNumber + "] " + alignment);
	 * 
	 * if ((rowNumber + length < SIZE - 1) && (colNumber + length < SIZE - 1)) {
	 * if (alignment == Alignment.HORIZONTAL) { int counter = colNumber;
	 * 
	 * for (int i = 1; i < length; i++) { if (!checkEmptyGrid(rowNumber,
	 * counter)) { canPlace = false; } counter++; } } else if (alignment ==
	 * Alignment.VERTICAL) { int counter = rowNumber;
	 * 
	 * for (int i = 1; i < length - 1; i++) { if (counter == 10) {
	 * System.out.println("row is 10"); } if (!checkEmptyGrid(counter,
	 * colNumber)) { canPlace = false; } counter++; } } if (!canPlace) {
	 * gridNumbers.remove(gridNumber); System.out.println("Removed grid [" +
	 * rowNumber + ", " + colNumber + "]"); } } else { canPlace = false; } }
	 * while (!canPlace);
	 * 
	 * if (alignment == Alignment.HORIZONTAL) { int counter = colNumber;
	 * 
	 * for (int i = 0; i < length; i++) { ship.addPositionGrid(rowNumber,
	 * counter); grid[rowNumber][counter] = 'x'; counter++; } } else if
	 * (alignment == Alignment.VERTICAL) { int counter = rowNumber;
	 * 
	 * for (int i = 0; i < length; i++) { ship.addPositionGrid(counter,
	 * colNumber); grid[counter][colNumber] = 'x'; counter++; } } }
	 * 
	 * private static void randomizeAlignment() { Random r = new Random(); int
	 * randomNumber = r.nextInt(1 + 100); System.out.println(randomNumber);
	 * if(randomNumber < 50) { alignment = Alignment.HORIZONTAL; } else {
	 * alignment = Alignment.VERTICAL; } }
	 * 
	 * private static Integer getRandomGrid(Vector<Integer> gridNumbers) {
	 * Random r = new Random(); return r.nextInt(gridNumbers.size()); }
	 * 
	 * private static boolean checkEmptyGrid(int row, int col) { return
	 * (grid[row][col] == 'e'); }
	 */

}
