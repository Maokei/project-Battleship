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
import javax.swing.SwingConstants;

public class Avatar extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private String name;
	private JLabel avatarLabel, nameLabel;
	private String path = "src/res/sprite/";
	private RadialGradientPaint rgp;
	private Point2D center;
	private float radius;
	private float[] gradientStops = { 0.2f, 0.4f, 0.6f, 0.8f, 1.0f };
	private Color[] colors = { new Color(0, 0, 0), new Color(10, 10, 10),
			new Color(20, 20, 20), new Color(30, 30, 30), new Color(40, 40, 40) };
	

	public Avatar() {
		super(new GridLayout(2, 1, 10, 10));
		setSize(150, 150);
		setupPaint();
		avatarLabel = new JLabel();
		try {
			img = ImageIO.read(new File(path + "pacman_64x64.png"));
			avatarLabel.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		nameLabel = new JLabel("Pirate Pac");
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setForeground(new Color(255, 255, 255));
		add(avatarLabel);
		add(nameLabel);
	}
	
	private void setupPaint() {
		center = new Point2D.Double(getWidth() / 2, getHeight() / 2);
		radius = (float) (getWidth() / 2.0);
		rgp = new RadialGradientPaint(center, radius, gradientStops, colors);
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
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics tmp = g.create();
		Graphics2D g2 = (Graphics2D) tmp;
		g2.setPaint(rgp);
		g2.fillRect(0, 0, getWidth(), getHeight());
		tmp.dispose();
	}

	
}