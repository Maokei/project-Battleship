/**
 * @file Grid.java
 * @date 2015-05-05
 * @authors Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.gameboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.Arrays;

import battleship.entity.Alignment;

/**
 * @package battleship.gameboad
 * @class Grid
 * @brief Class describes a battleship grid.
 * Grid size: 10 * 10 as give by project specification.
 * 
 * water tile
 * 'o'
 *ship tiles
 * submarine 's'
 * destroyer 'd'
 * carrier 'c'
 *ship hit tiles
 *	'x'
 * */
public class Grid extends JPanel {
	private static final long serialVersionUID = 13847238970823704L;
	private static Color color = Color.BLUE;
	private static final int size = 10;
	private char grid[][];
	private int row;
	private int col;
	private JLabel[][] lgrid;
	
	public Grid(Integer... newS) {
		Integer nonDefault;
		if(newS.length > 1) {
			nonDefault = newS[0];
			if(nonDefault > size) {
				row = nonDefault;
				col = nonDefault;
			}
		}else{
			row = size;
			col = size;
		}
		
		grid = new char[row][col];
		lgrid = new JLabel[size][size];
		//fill grid
		/*for(int i = 0; i < row; i++)
			for(int l = 0; l<col; l++)
				grid[i][l] = 'o';*/
		
		//fill grid
		for(char[] row: grid)
			Arrays.fill(row, 'o');
	}
	
	void gridTest() {
		//initiate
		setLayout(new GridLayout(size, size));
		for(int i = 0; i < size; i++){
			for(int l = 0; l < size; l++){
				lgrid[i][l] = new JLabel();
				lgrid[i][l].setIcon(testImg());
				//add to panel
				this.add(lgrid[i][l]);
			}
		}
	}
	
	private Icon testImg() {
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		
		try{
			image = ImageIO.read(new File("res/sprite/test_sprite.jpg"));
		}catch(Exception e){
			
		}
		Graphics g = image.getGraphics();
	      g.setColor(color);
	      g.fillRect(0, 0, 32, 32);
	      g.dispose();
		return new ImageIcon(image);
	}
	
	/**
	 * isEmpty
	 * @param Interger x position, Integer y position, Alignment a for ship, Integer length for ship 
	 * @return true or false if cells are empty or occupied
	 * */
	boolean isEmpty(int x, int y, Alignment a, int length) {
		int x1, y1;
		
		if(a == Alignment.HORIZONTAL) {
			x1 = x + length;
			y1 = y;
		}else {
			y1 = y + length;
			x1 = x;
		}
			
		if(x1 < 11 && y1 < 11)
			return false;
		
		for(int i = x;i < x1; x++) {
			for(int l = y; l<y1; y++) {
				if(grid[i][l] != 'o')
					return false;
			}
		}
		
		return true;
	}
	
	public void registerHit(int x, int y) {
		grid[x][y] = 'x';
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getGridSize() {
		return size;
	}
	
	private static void createAndShowGui() {
	      Grid mainPanel = new Grid();
	      mainPanel.gridTest();
	      JFrame frame = new JFrame("GridExample");
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.getContentPane().add(mainPanel);
	      frame.setSize(400, 400);
	      frame.setLocationByPlatform(true);
	      frame.setVisible(true);
	   }
	
	public static void main(String[] args) {
		createAndShowGui();
	}
}
