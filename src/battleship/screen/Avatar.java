package battleship.screen;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Avatar extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private String name;
	private JLabel avatarLabel, nameLabel;
	
	
	public Avatar(BufferedImage img, String name) {
		super(new GridLayout(2, 1));
		avatarLabel = new JLabel();
		avatarLabel.setIcon(new ImageIcon(img));
		nameLabel = new JLabel(name);
		add(avatarLabel);
		add(nameLabel);
	}
}