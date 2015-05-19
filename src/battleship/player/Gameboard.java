/**
 * @file Grid.java
 * @date 2015-05-05
 * @authors Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import resources.image.SpriteLoader;

import java.util.Arrays;

/**
 * @package battleship.gameboad
 * @class Grid
 * @brief Class describes a battleship grid. Grid size: 10 * 10 as give by
 *        project specification.
 * 
 *        water tile 'o' ship tiles submarine 's' destroyer 'd' carrier 'c' ship
 *        hit tiles 'x'
 * */
public class Gameboard extends JPanel {
	private static final long serialVersionUID = 13847238970823704L;
	private static Color color = Color.BLUE;
	private static final int size = 10;
	private static final int SPRITE_SIZE = 32;
	private char grid[][];
	private int row;
	private int col;
	private JLabel[][] lgrid;
	private SpriteLoader sprites;

	public Gameboard(Integer... newS) {
		Integer nonDefault;
		if (newS.length > 1) {
			nonDefault = newS[0];
			if (nonDefault > size) {
				row = nonDefault;
				col = nonDefault;
			}
		} else {
			row = size;
			col = size;
		}

		grid = new char[row][col];
		lgrid = new JLabel[size][size];
		// fill grid
		/*
		 * for(int i = 0; i < row; i++) for(int l = 0; l<col; l++) grid[i][l] =
		 * 'o';
		 */

		// fill grid
		for (char[] row : grid)
			Arrays.fill(row, 'o');
		
		grid[5][5] = 's';

		sprites = SpriteLoader.getInstance(32, 32, 8, 8, 12);
		sprites.loadSprites("src/res/sprite/spritesheet_battleship.png");
		setLayout(new GridLayout(size, size));
		for (int i = 0; i < size; i++) {
			for (int l = 0; l < size; l++) {
				lgrid[i][l] = new JLabel();
				if (grid[i][l] == 'o') {
					lgrid[i][l].setIcon(new ImageIcon(sprites
							.getSprite("water")));
				} else if (grid[i][l] == 's') {
					lgrid[i][l].setIcon(new ImageIcon(sprites
							.getSprite("hor_sub")));
				}
				// add to panel
				this.add(lgrid[i][l]);
			}
		}
	}

	/*
	 * public void gridTest() { //sub test tile //initiate setLayout(new
	 * GridLayout(size, size)); for(int i = 0; i < size; i++){ for(int l = 0; l
	 * < size; l++){ lgrid[i][l] = new JLabel(); if(grid[i][l] == 'o') {
	 * lgrid[i][l].setIcon(new ImageIcon(sprites.getSprite("water"))); }else
	 * if(grid[i][l] == 's'){ lgrid[i][l].setIcon(new
	 * ImageIcon(sprites.getSprite("hor_sub"))); } //add to panel
	 * this.add(lgrid[i][l]); } } }
	 */
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
	
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
	
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	private Icon testImg() {
		BufferedImage image = new BufferedImage(32, 32,
				BufferedImage.TYPE_INT_RGB);

		try {
			image = ImageIO.read(new File("src/res/sprite/water_sprite.jpg"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		Graphics g = image.getGraphics();
		g.setColor(color);
		// g.fillRect(0, 0, 32, 32);
		g.dispose();
		return new ImageIcon(image);
	}

	/**
	 * isEmpty
	 * 
	 * @param Interger
	 *            x position, Integer y position, Alignment a for ship, Integer
	 *            length for ship
	 * @return true or false if cells are empty or occupied
	 * */

	public boolean isEmpty(int x, int y, Alignment a, int length) {
		int x1, y1;

		if (a == Alignment.HORIZONTAL) {
			x1 = x + length;
			y1 = y;
		} else {
			y1 = y + length;
			x1 = x;
		}

		if (x1 < 11 && y1 < 11)
			return false;

		for (int i = x; i < x1; x++) {
			for (int l = y; l < y1; y++) {
				if (grid[i][l] != 'o')
					return false;
			}
		}

		return true;
	}

	/**
	 * PlaceShipOnGrid
	 * 
	 * @brief Places valid ship tiles on grid
	 * @param Takes
	 *            a ship object
	 * @return void
	 * */

	public void placeShipOnGrid(Ship ship) {
		int l = 0;
		int x1 = ship.getX1();
		int y1 = ship.getY1();
		// direction to count
		if (ship.getAlignment() == Alignment.VERTICAL) {
			for (int i = ship.getY(); i <= y1; i++) {
				placeShipTile(ship.getX(), ship.getY(), ship.getType());
			}
		} else { // Horizontal
			for (int i = ship.getX(); i <= x1; i++) {
				placeShipTile(ship.getX(), ship.getY(), ship.getType());
			}
		}
	}
	
	public void fire(int row, int col) {
		lgrid[row][col].setIcon(new ImageIcon(sprites.getSprite("hit")));
	}

	public void placeShip(Ship ship, int row, int col) {
		switch (ship.alignment) {
		case VERTICAL:
			placeVertical(ship, row, col); break;
		case HORIZONTAL:
			placeHorizontal(ship, row, col); break;
		}
	}

	private void placeHorizontal(Ship ship, int row, int col) {
		int counter = col;
		if (ship.length > 1) {
			for (int i = 0; i < ship.length; i++) {
				if(i == 0) {
					lgrid[row][counter++].setIcon(new ImageIcon(sprites
						.getSprite("hor_front")));
				} else if(i == ship.length - 1) {
					lgrid[row][counter++].setIcon(new ImageIcon(sprites
							.getSprite("hor_back")));
				} else {
					lgrid[row][counter++].setIcon(new ImageIcon(sprites
							.getSprite("hor_mid")));
				}
			}
		} else {
			lgrid[row][col].setIcon(new ImageIcon(sprites
					.getSprite("hor_sub")));
		}
		repaint();
	}
	

	private void placeVertical(Ship ship, int row, int col) {
		int counter = row;
		if (ship.length > 1) {
			for (int i = 0; i < ship.length; i++) {
				if(i == 0) {
					lgrid[counter++][col].setIcon(new ImageIcon(sprites
						.getSprite("ver_front")));
				} else if(i == ship.length - 1) {
					lgrid[counter++][col].setIcon(new ImageIcon(sprites
							.getSprite("ver_back")));
				} else {
					lgrid[counter++][col].setIcon(new ImageIcon(sprites
							.getSprite("ver_mid")));
				}
			}
		} else {
			lgrid[row][col].setIcon(new ImageIcon(sprites
					.getSprite("ver_sub")));
		}
	}

	/**
	 * PlaceShipTile
	 * 
	 * @brief help function to place a ship tile in grid
	 * @param
	 * @return void
	 * */
	private void placeShipTile(int x, int y, String type) {
		switch (type) {
		case "Submarine":
			grid[x][y] = 's';
			break;
		case "Destroyer":
			grid[x][y] = 'd';
			break;
		case "Carrier":
			grid[x][y] = 'c';
			break;

		}
	}

	/**
	 * registerHit
	 * 
	 * @brief Update the grid with a hit
	 * @param X
	 *            coordinate , Y coordinate
	 * @return void
	 * */
	public void registerHit(int x, int y) {
		grid[x][y] = 'x';
	}

	public Color getColor() {
		return color;
	}

	public int getGridSize() {
		return size;
	}
}
