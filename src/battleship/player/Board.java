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
		System.out.println("I'm in Board Ctor");
		setLayout(new GridLayout(SIZE, SIZE));
		gridboard = new Grid[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				gridboard[row][col] = new Grid(row, col);
				add(gridboard[row][col]);
			}
		}
		setSize(new Dimension(SIZE * SPRITE_SIZE, SIZE * SPRITE_SIZE));
		sprites = SpriteLoader.getInstance(32, 32, 8, 8, 12);
		sprites.loadSprites("src/res/sprite/spritesheet_battleship.png");
		background = sprites.getSprite("water");
		repaint();
	}

	public void addHit(int row, int col) {
		gridboard[row][col].setIcon(new ImageIcon(sprites.getSprite("hit")));
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
}
