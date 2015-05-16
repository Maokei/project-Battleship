package resources.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteLoader {
	private int width, height;
	private int rows, cols;
	private int numOfSprites;
	private Map<String, BufferedImage> sprites;
	private List<String> names;

	public SpriteLoader(int width, int height, int rows, int cols, int numOfSprites) {
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;
		this.numOfSprites = numOfSprites;
		sprites = new HashMap<String, BufferedImage>(numOfSprites);
		names = new ArrayList<String>(numOfSprites);
		initNames();
	}

	private void initNames() {
		names.add("hor_front"); names.add("hor_mid"); names.add("hor_back");
		names.add("ver_front"); names.add("ver_mid"); names.add("ver_back");
		names.add("hor_sub"); names.add("ver_sub"); names.add("water1");
		names.add("water2");
	}

	public boolean loadSprites(String filename) {
		try {
			BufferedImage spritesheet = ImageIO.read(new File(filename));
			int spriteCounter = 0;

			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					BufferedImage sprite = spritesheet.getSubimage(col * width,
							row * height, width, height);
					if (names.size() < sprites.size()) {
						sprites.put("", sprite);
					} else {
						sprites.put(names.get(spriteCounter), sprite);
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

	public void addName(String name) {
		names.add(name);
	}

	public BufferedImage getSprite(String name) {
		return sprites.get(name);
	}
}
