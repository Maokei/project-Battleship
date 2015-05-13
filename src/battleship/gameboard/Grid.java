/**
 * @file Grid.java
 * @date 2015-05-05
 * @authors Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.gameboard;

import java.awt.Color;
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
public class Grid {
	private static Color color = Color.BLUE;
	private static final int size = 10;
	private char grid[][];
	private int row;
	private int col;
	
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
		
		//fill grid
		/*for(int i = 0; i < row; i++)
			for(int l = 0; l<col; l++)
				grid[i][l] = 'o';*/
		
		//fill grid
		for(char[] row: grid)
			Arrays.fill(row, 'o');
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

}
