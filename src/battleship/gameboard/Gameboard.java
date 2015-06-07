/**
 * @file Gameboard.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
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

import battleship.resources.SpriteLoader;
import battleship.ships.Alignment;
import battleship.ships.Ship;

/**
 * Gameboard
 * @class Gameboard
 * @extends JPanel
 * @brief Class describes a battleship gameboard.
 * */
public class Gameboard extends JPanel {
	private static final long serialVersionUID = 1L;
	private Grid[][] gridboard;
	private SpriteLoader sprites;
	private BufferedImage background;
	private boolean defeat = false;
	private boolean victory = false;

	/**
	 * Gameboard
	 * 
	 * @name GameBoard
	 * @constructor Gameboard
	 * @brief instantiates and initiates the Gameboard grids
	 * */
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

	/**
	 * initGrid
	 * @name initGrid
	 * @brief Function initiates a player grid.
	 * */
	private void initGrid() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col] = new Grid(row, col);
				add(gridboard[row][col]);
			}
		}
	}

	/**
	 * addHit
	 * @name addHit
	 * @brief Add a hit on a player grid.
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * */
	public void addHit(int row, int col) {
		gridboard[row][col].setIcon(new ImageIcon(sprites.getSprite("hit")));
		gridboard[row][col].setHit();
	}

	/**
	 * addMiss
	 * @name addMiss
	 * @brief Add a miss on a player grid.
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * */
	public void addMiss(int row, int col) {
		gridboard[row][col].setIcon(new ImageIcon(sprites.getSprite("miss")));
		gridboard[row][col].setMiss();
	}

	/**
	 * fadeGridOut
	 * @name fadeGridOut
	 * @brief fade out effect on grid.
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * */
	public void fadeGridOut(int row, int col) {
		gridboard[row][col].fadeOut();
	}

	/**
	 * fadeGridIn
	 * @name fadeGridIn
	 * @brief fade in effect on grid.
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * */
	public void fadeGridIn(int row, int col) {
		gridboard[row][col].fadeIn();
	}

	/**
	 * paintComponent
	 * @name paintComponent
	 * @param Graphics g to draw
	 * */
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

	
	/**
	 * checkShipPlacement
	 * 
	 * @name checkShipPlacement
	 * @brief Check if ship placement is possible, given position and alignment in ship object.
	 * @param ship the ship used for alignment check and length
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * @return boolean value if ship placement is possible otherwise false will be returned.
	 * */
	public boolean checkShipPlacement(Ship ship, int row, int col) {
		if (ship.getAlignment() == Alignment.HORIZONTAL) {
			int width = ship.getLength() + 2;
			int height = 3;
			if ((col + ship.getLength() - 1) < SIZE) {
				if (col > 0) {
					--col;
				} else {
					--width;
				}
				if(!((col + ship.getLength()) < SIZE - 1)) {
					--width;
				}
				if (!(row < (SIZE - 1))) {
					--height;
				}
				
				if (row > 0) {
					--row;
				} else {
					--height;
				}
				
				int rowCounter = row;
				int colCounter = col;
				for(int i = 0; i < height; i++) {
					colCounter = col;
					for(int j = 0; j < width; j++) {
						if(!gridboard[rowCounter][colCounter++].isEmpty()) {
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
				} else {
					--height;
				}
				if(!(row + ship.getLength() < SIZE - 1)) {
					--height;
				}
				if (!(col < (SIZE - 1))) {
					--width;
				}
				if (col > 0) {
					--col;
				} else {
					--width;
				}
				
				int rowCounter = row;
				int colCounter = col;
				for(int i = 0; i < width; i++) {
					rowCounter = row;
					for(int j = 0; j < height; j++) {
						if(!gridboard[rowCounter++][colCounter].isEmpty()) {
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
	 * placeShip
	 * @name placeShip
	 * @brief Place ship on the playing grid, and fill points on grid belonging to ship length and alignment.
	 * @param ship the ship used for alignment check and length
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * */
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

	/**
	 * addShipSprite
	 * 
	 * @name addShipSprite
	 * @brief Add appropriate sprite to draw.
	 * @param ship the ship used for alignment check and length
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * @param counter the counter used to get the correct sprite
	 * */
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

	/**
	 * checkHit
	 * @name checkHit
	 * @brief Help function to look for a hit on given position.
	 * @param row the gridboard row coordinate
	 * @param row the gridboard col coordinate
	 * @return If target has been false is returned, false for miss or empty.
	 * */
	public boolean checkHit(int row, int col) {
		if (gridboard[row][col].getOccupied()) {
				return true;
		}
		return false;
	}

	/**
	 * displayDefeat
	 * @name displayDefeat
	 * @brief Set defeat state and repaint.
	 * */
	public void displayDefeat() {
		defeat = true;
		repaint();
	}

	/**
	 * displayVictory
	 * @name displayVictory
	 * @brief Set victory state and repaint. 
	 * */
	public void displayVictory() {
		victory = true;
		repaint();
	}

	/**
	 * setGridsNotVisible
	 * @name setGridNotVisible
	 * @brief Make set each grid cell not visible.
	 * */
	private void setGridsNotVisisble() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col].setVisible(false);
			}
		}
	}

	/**
	 * randmizeShipPlacement
	 * 
	 * @name randomizeShipPlacement 
	 * @brief Takes a vector of ships to randomized onto grid.
	 * @param ships Vector of Ship to be placed 
	 * */
	public void randomizeShipPlacement(Vector<Ship> ships) {
		for (Ship ship : ships) {
			for (int i = 0; i < ship.getLength(); i++) {
				int row = ship.getPosition().elementAt(i).getRow();
				int col = ship.getPosition().elementAt(i).getCol();
				gridboard[row][col].setOccupied();
				addShipSprite(ship, row, col, i);
			}
		}
	}

	/**
	 * checkFire
	 * 
	 * @name checkFire
	 * @brief checks whether specified grid is not hit or missed
	 * @param integer row and integer column where fire will be checked.
	 * @return boolean true if is hit or is, empty false
	 * */
	public boolean checkFire(int row, int col) {
		if (!(gridboard[row][col].isHit() || gridboard[row][col].isMiss())) {
				return true;
		}
		return false;
	}
}
