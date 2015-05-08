/**
 * @file Grid.java
 * @date 2015-05-05
 * @authors Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.gameboard;

import java.awt.Color;
import java.util.Arrays;

/**
 * @package battleship.gameboad
 * @class Grid
 * @brief Class describes a battleship grid.
 * Grid size: 10 * 10 as give by project specification.
 * X  = hit 
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
	
	public Color getColor() {
		return color;
	}
	
	public int getGridSize() {
		return size;
	}

}
