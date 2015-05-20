package battleship.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Avatar extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private String name;
	private JLabel avatarLabel, nameLabel;
	private String path = "src/res/sprite/";
	

	public Avatar() {
		super(new GridLayout(2, 1, 10, 10));
		setSize(160, 160);
		avatarLabel = new JLabel();
		try {
			img = ImageIO.read(new File(path + "pacman_150x125.png"));
			avatarLabel.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		nameLabel = new JLabel("Pirate Pac");
		add(avatarLabel);
		add(nameLabel);
	}
	
	
	public Avatar(BufferedImage img, String name) {
		super(new GridLayout(2, 1, 10, 10));
		this.img = img;
		this.name = name;
		avatarLabel = new JLabel();
		avatarLabel.setIcon(new ImageIcon(img));
		nameLabel = new JLabel(name);
		add(avatarLabel);
		add(nameLabel);
	}

	public void setAvatar(BufferedImage img, String name) {
		this.img = img;
		this.name = name;
		avatarLabel.setIcon(new ImageIcon(img));
		nameLabel.setText(name);
	}

	public BufferedImage getImage() {
		return img;
	}

	public String getName() {
		return name;
	}

	
}