package battleship.player;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import resources.image.SpriteLoader;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	private final int SIZE = 10;
	private final int SPRITE_SIZE = 32;
	private Grid[][] gridboard;
	private SpriteLoader sprites;
	private BufferedImage background;

	public Board() {
		super();
		setLayout(new GridLayout(SIZE, SIZE));
		setSize(new Dimension(SIZE * SPRITE_SIZE, SIZE * SPRITE_SIZE));
		
		gridboard = new Grid[SIZE][SIZE];
		sprites = SpriteLoader.getInstance(32, 32, 8, 8, 13);
		sprites.loadSprites("src/res/sprite/spritesheet_battleship.png");
		background = sprites.getSprite("water");
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
	
	public void fadeGrid(int row, int col) {
		gridboard[row][col].fadeDown();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int row = 0; row <= SIZE; row++) {
			int y = row * SPRITE_SIZE;
			for (int col = 0; col <= SIZE; col++) {
				int x = col * SPRITE_SIZE;
				g.drawLine(0, y, SIZE * SPRITE_SIZE, y);
				g.drawLine(x, 0, x, SIZE * SPRITE_SIZE);
				g.drawImage(background, x, y, null);
			}
		}
	}

	public boolean checkShipPlacement(Ship ship, int row, int col) {
		int length = ship.getLength();
		int counter;
		if (ship.alignment == Alignment.HORIZONTAL) {
			if ((col + length - 1) < SIZE) {
				counter = col;
				for (int i = 0; i < length; i++) {
					if (!gridboard[row][counter].isEmpty()) {
						return false;
					}
					counter++;
				}
			} else {
				return false;
			}
				
		} else if (ship.alignment == Alignment.VERTICAL) {
			if ((row + length - 1) < SIZE) {
				counter = row;
				for (int i = 0; i < length; i++) {
					if (!gridboard[counter][col].isEmpty()) {
						return false;
					}
					counter++;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public void placeShip(Ship ship, int row, int col) {
		int counter;
		System.out.print("Occupied: [ ");
		if (ship.alignment == Alignment.HORIZONTAL) {
			counter = col;
			for (int i = 0; i < ship.getLength(); i++) {
				gridboard[row][counter].setOccupied();
				System.out.print(row + "," + counter + " ");
				addShipSprite(ship, row, counter, i);
				ship.addPositionGrid(row, counter);
				counter++;
			}
		} else if (ship.alignment == Alignment.VERTICAL) {
			counter = row;
			for (int i = 0; i < ship.getLength(); i++) {
				gridboard[counter][col].setOccupied();
				System.out.print(counter + "," + col + " ");
				addShipSprite(ship, counter, col, i);
				ship.addPositionGrid(counter, col);
				counter++;
			}
		}
		System.out.print("]\n");
		printOccupied();
		
	}
	
	private void printOccupied() {
		System.out.print("gridboard occupied: [");
		for(int row = 0; row < SIZE; row++) {
			for(int col = 0; col < SIZE; col++) {
				if(!gridboard[row][col].isEmpty()) {
					System.out.print(row + "," + col + " ");
				}
			}
		}
		System.out.print("]\n");
	}

	private void addShipSprite(Ship ship, int row, int col, int counter) {
		String pre, post;
		if (ship.alignment == Alignment.HORIZONTAL)
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
		if(!gridboard[row][col].isEmpty()) {
			if(!gridboard[row][col].isHit())
				return true;
		}
		return false;
	}
}
