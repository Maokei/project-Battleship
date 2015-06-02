package battleship.gameboard;

import static battleship.game.Constants.GRID_SIZE;
import static battleship.game.Constants.SIZE;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import battleship.network.RandomShipPlacer;
import battleship.resources.SpriteLoader;
import battleship.ships.Alignment;
import battleship.ships.Ship;

public class Gameboard extends JPanel {
	private static final long serialVersionUID = 1L;
	private Grid[][] gridboard;
	private SpriteLoader sprites;
	private BufferedImage background;
	private boolean defeat = false;
	private boolean victory = false;

	public Gameboard() {
		super();
		setLayout(new GridLayout(SIZE, SIZE));
		setSize(new Dimension((SIZE * GRID_SIZE), (SIZE * GRID_SIZE)));
		gridboard = new Grid[SIZE][SIZE];

		sprites = SpriteLoader.getInstance(32, 32, 8, 8, 13);
		sprites.loadSprites("src/res/sprite/spritesheet_battleship.png");
		background = sprites.getSprite("water");
		initGrid();

	}

	private void initGrid() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col] = new Grid(row, col);
				add(gridboard[row][col]);
			}
		}
	}

	public void addHit(int row, int col) {
		gridboard[row][col].setIcon(new ImageIcon(sprites.getSprite("hit")));
	}

	public void addMiss(int row, int col) {
		gridboard[row][col].setIcon(new ImageIcon(sprites.getSprite("miss")));
	}

	public void fadeGridOut(int row, int col) {
		gridboard[row][col].fadeOut();
	}

	public void fadeGridIn(int row, int col) {
		gridboard[row][col].fadeIn();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!(defeat || victory)) {
			for (int row = 0; row <= SIZE; row++) {
				int y = row * GRID_SIZE;
				for (int col = 0; col <= SIZE; col++) {
					int x = col * GRID_SIZE;
					g.drawLine(0, y, SIZE * GRID_SIZE, y);
					g.drawLine(x, 0, x, SIZE * GRID_SIZE);
					g.drawImage(background, x, y, null);
				}
			}
		} else {
			setGridsNotVisisble();
			// g.clearRect(0, 0, getWidth(), getHeight());
			try {
				if (defeat) {
					background = ImageIO.read(new File(
							"src/res/sprite/battle_lost.png"));
				} else if (victory) {
					background = ImageIO.read(new File(
							"src/res/sprite/battle_won.png"));
				}
				g.drawImage(background, 0, 0, null);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public boolean checkShipPlacement(Ship ship, int row, int col) {
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int width = ship.getLength() + 2;
			int height = 3;
			if ((col + ship.getLength() - 1) < SIZE) {
				if (col > 0) {
					--col;
				} 
				if(col == 0 || !((col + ship.getLength()) < SIZE - 1)) {
					--width;
				}
				if (row == 0 || !(row < (SIZE - 1))) {
					--height;
				}
				if (row > 0) {
					--row;
				}
				
				int rowCounter = row;
				int colCounter = col;
				System.out.println("\nChecking grids:");
				for(int i = 0; i < height; i++) {
					colCounter = col;
					System.out.print("[ ");
					for(int j = 0; j < width; j++) {
						System.out.print(rowCounter + "," + colCounter + " ");
						if(!gridboard[rowCounter][colCounter++].isEmpty()) {
							return false;
						}
					}
					System.out.print("]\n");
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
				if(row == 0 || !(row + ship.getLength() < SIZE - 1)) {
					--height;
				}
				if (col == 0 || !(col < (SIZE - 1))) {
					--width;
				}
				if (col > 0) {
					--col;
				}
				int rowCounter = row;
				int colCounter = col;
				System.out.println("\nChecking grids:");
				for(int i = 0; i < width; i++) {
					rowCounter = row;
					System.out.print("[ ");
					for(int j = 0; j < height; j++) {
						System.out.print(rowCounter + "," + colCounter + " ");
						if(!gridboard[rowCounter++][colCounter].isEmpty()) {
							return false;
						}
					}
					colCounter++;
					System.out.print("]\n");
				}
				
			} else {
				return false;
			}
		}
		return true;
	}

	
	public void placeShip(Ship ship, int row, int col) {
		int counter;
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < ship.getLength(); i++) {
				gridboard[row][counter].setOccupied();
				addShipSprite(ship, row, counter, i);
				ship.addPositionGrid(row, counter);
				counter++;
			}
		} else if (ship.getAlignment() == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < ship.getLength(); i++) {
				gridboard[counter][col].setOccupied();
				addShipSprite(ship, counter, col, i);
				ship.addPositionGrid(counter, col);
				counter++;
			}
		}
	}

	private void addShipSprite(Ship ship, int row, int col, int counter) {
		String pre, post;
		if (ship.getAlignment() == Alignment.HORIZONTAL)
			pre = "hor_";
		else
			pre = "ver_";

		if (ship.getLength() > 1) {
			if (counter == 0) {
				post = "front";
			} else if (counter == ship.getLength() - 1) {
				post = "back";
			} else {
				post = "mid";
			}
		} else {
			post = "sub";
		}

		gridboard[row][col]
				.setIcon(new ImageIcon(sprites.getSprite(pre + post)));
	}

	public boolean checkHit(int row, int col) {
		if (!gridboard[row][col].isEmpty()) {
			if (!gridboard[row][col].isHit())
				return true;
		}
		return false;
	}

	public void displayDefeat() {
		defeat = true;
		repaint();
	}

	public void displayVictory() {
		victory = true;
		repaint();
	}

	private void setGridsNotVisisble() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col].setVisible(false);
			}
		}
	}

	public void clear() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				if (!gridboard[row][col].isEmpty()) {
					gridboard[row][col].fastFade();
				}
			}
		}
	}

	public void randomizeShipPlacement(Vector<Ship> ships) {
		ships = new RandomShipPlacer().getRandomShips();
		for (Ship ship : ships) {
			String type = ship.getType();
			System.out.print(type + "[ ");
			for (int i = 0; i < ship.getLength(); i++) {
				int row = ship.getPosition().elementAt(i).getRow();
				int col = ship.getPosition().elementAt(i).getCol();
				System.out.print(row + "," + col + " ");
				gridboard[row][col].setOccupied();
				addShipSprite(ship, row, col, i);
			}
			System.out.print(" ]\n");
		}
	}
}
