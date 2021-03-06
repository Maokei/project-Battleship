/**
 * @file AvatarPanel.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @class AvatarPanel
 * @extends JPanel
 * @brief Class describes an avatar display panel.
 * */
public class AvatarPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Map<String, BufferedImage> avatars;
	private ArrayList<String> names;
	private JPanel buttonPanel;
	private JButton leftArrow, rightArrow;
	private JLabel avatar, description;
	private JPanel avatarPanel, descriptionPanel;
	private String path = "src/res/sprite/";
	private int index = 0;
	private RadialGradientPaint rgp;
	private Point2D center;
	private float radius;
	private float[] gradientStops = { 0.2f, 0.4f, 0.6f, 0.8f, 1.0f };
	private Color[] colors = { new Color(0, 0, 0), new Color(10, 10, 10),
			new Color(20, 20, 20), new Color(30, 30, 30), new Color(40, 40, 40) };

	/**
	 * @constructor AvatarPanel
	 * */
	public AvatarPanel() {
		super(new BorderLayout());
		setSize(new Dimension(150, 150));
		buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.setBackground(new Color(0, 0, 0));
		leftArrow = new JButton();
		leftArrow.setContentAreaFilled(false);
		leftArrow.setBorderPainted(false);
		leftArrow.addActionListener(new AvatarCycler());
		leftArrow.setEnabled(false);
		rightArrow = new JButton();
		rightArrow.setContentAreaFilled(false);
		rightArrow.setBorderPainted(false);
		rightArrow.addActionListener(new AvatarCycler());

		try {
			leftArrow.setIcon(new ImageIcon(ImageIO.read(new File(path
					+ "arrow_left_50x50.png"))));
			rightArrow.setIcon(new ImageIcon(ImageIO.read(new File(path
					+ "arrow_right_50x50.png"))));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		buttonPanel.add(leftArrow);
		buttonPanel.add(rightArrow);
		add(buttonPanel, BorderLayout.NORTH);
		avatars = new HashMap<String, BufferedImage>();
		readAvatarsFromFile();
		names = new ArrayList<String>();
		avatar = new JLabel();
		avatarPanel = new JPanel(new BorderLayout());
		avatarPanel.setSize(new Dimension(220, 200));
		avatarPanel.add(avatar, BorderLayout.CENTER);
		add(avatar, BorderLayout.CENTER);
		description = new JLabel();
		description.setHorizontalAlignment(SwingConstants.CENTER);
		description.setForeground(new Color(255, 255, 255));
		descriptionPanel = new JPanel();
		descriptionPanel.add(description);
		add(description, BorderLayout.SOUTH);
		setupPaint();
		init();
	}

	/**
	 * init
	 * @name init
	 * @brief Initialize avatar panel.
	 * */
	private void init() {
		readAvatarsFromFile();
		names.add("Pirate Pac");
		names.add("Skull 'n Bones");
		avatar.setIcon(new ImageIcon(avatars.get(names.get(index))));
		description.setText(names.get(index));
	}
	
	/**
	 * setupPaint
	 * @name setupPaint
	 * @brief Setup drawing.
	 * */
	private void setupPaint() {
		center = new Point2D.Double(getWidth() / 2, getHeight() / 2);
		radius = (float) (getWidth() / 2.0);
		rgp = new RadialGradientPaint(center, radius, gradientStops, colors);
	}


	/**
	 * reset
	 * @name reset
	 * @brief Reset's panel.
	 * */
	public void reset() {
		index = 0;
		avatar.setIcon(new ImageIcon(avatars.get(names.get(index))));
		description.setText(names.get(index));
		leftArrow.setEnabled(false);
		rightArrow.setEnabled(true);
	}

	/**
	 * getAvatar
	 * @name getAvatar
	 * @returns Avatar object pointer.
	 * */
	public Avatar getAvatar() {
		return new Avatar(avatars.get(names.get(index)), names.get(index));
	}

	/**
	 * readAvatarFromFile
	 * @name readAvatarFromFile
	 * */
	private void readAvatarsFromFile() {
		try {
			avatars.put("Pirate Pac",
					ImageIO.read(new File(path + "pacman_64x64.png")));
			avatars.put("Skull 'n Bones",
					ImageIO.read(new File(path + "skullnbones_64x64.png")));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @class AvatarCycler
	 * @implements ActionListener
	 * @brief Class describes an Listener that cycles between available avtars.
	 * */
	class AvatarCycler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == leftArrow) {
				moveLeft();
			} else if (e.getSource() == rightArrow) {
				moveRight();
			}

			checkButtons();
		}

		private void moveLeft() {
			if (index > 0) {
				--index;
				avatar.setIcon(new ImageIcon(avatars.get(names.get(index))));
				description.setText(names.get(index));
			}
		}

		private void moveRight() {
			if (index < avatars.size() - 1) {
				++index;
				avatar.setIcon(new ImageIcon(avatars.get(names.get(index))));
				description.setText(names.get(index));
			}
		}

		private void checkButtons() {
			if (index >= 1)
				leftArrow.setEnabled(true);
			else
				leftArrow.setEnabled(false);

			if (index == avatars.size() - 1)
				rightArrow.setEnabled(false);
			else
				rightArrow.setEnabled(true);
		}
	}
	
	/**
	 * paintComponent
	 * @name paintComponent
	 * @param Graphics context.
	 * */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics tmp = g.create();
		Graphics2D g2 = (Graphics2D) tmp;
		g2.setPaint(rgp);
		g2.fillRect(0, 0, getWidth(), getHeight());
		tmp.dispose();
	}

}
