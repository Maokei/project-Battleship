/**
 * @file SpriteLoader.java
 * @authors rijo1001, lama1203
 * @date 2015-05-20
 * */
package battleship.resources;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @class SpriteLoader
 * @brief Describes a class that reads images form file to create BufferedImages. 
 * */
public class SpriteLoader {
	private int width, height;
	private int rows, cols;
	private int numOfSprites;
	private Map<String, BufferedImage> sprites;
	private List<String> names;
	private static SpriteLoader instance = null;

	/**
	 * @constructor SpriteLoader
	 * */
	private SpriteLoader(int width, int height, int rows, int cols, int numOfSprites) {
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;
		this.numOfSprites = numOfSprites;
		sprites = new HashMap<String, BufferedImage>(numOfSprites);
		names = new ArrayList<String>(numOfSprites);
		initNames();
	}

	/**
	 * initNames
	 * @name initNames
	 * @brief Initializes sprite's with pre-defined names.
	 * */
	private void initNames() {
		names.add("hor_front"); names.add("hor_mid"); names.add("hor_back");
		names.add("ver_front"); names.add("ver_mid"); names.add("ver_back");
		names.add("hor_sub"); names.add("ver_sub"); names.add("water1");
		names.add("water2"); names.add("water"); names.add("hit");
		names.add("miss");
	}

	/**
	 * loadSprites
	 *  @name loadSprites 
	 *  @param String filename of sprite to load.
	 *  @return Was successful or not.
	 * */
	public boolean loadSprites(String filename) {
		try {
			BufferedImage spritesheet = getTransparentImage(ImageIO.read(new File(filename)));
			int spriteCounter = 0;

			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					BufferedImage sprite = spritesheet.getSubimage(col * width,
							row * height, width, height);
					if (names.size() < sprites.size()) {
						sprites.put("", sprite);
					} else {
						sprites.put(names.get(spriteCounter), getTransparentImage(sprite));
					}
					spriteCounter++;
					if(spriteCounter == numOfSprites)
						return true;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * getTransparentImage
	 * @name getTransparentImage
	 * @param Image img, creates a transparent image with image.
	 * @return BufferedImage with transparency.
	 * */
	public static BufferedImage getTransparentImage(Image img) {
		/*
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		*/
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

	/**
	 * addName
	 * @name addName
	 * */
	public void addName(String name) {
		names.add(name);
	}

	/**
	 * getSprite
	 * @name getSprite
	 * @param Request sprite by String name.
	 * @return BufferedImage requested.
	 * */
	public BufferedImage getSprite(String name) {
		return sprites.get(name);
	}
	
	/**
	 * getInstance
	 * @name getInstance
	 * @return SpriteLoader instance.
	 * */
	public static SpriteLoader getInstance(int width, int height, int rows, int cols, int numOfSprites) {
		if(instance == null)
			instance = new SpriteLoader(width, height, rows, cols, numOfSprites);
		return instance;
	}
}
