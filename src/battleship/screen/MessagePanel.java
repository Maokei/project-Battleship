package battleship.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessagePanel extends JPanel {
	private JLabel label;
	private Font font;
	
	public MessagePanel() {
		super();
		setPreferredSize(new Dimension(1000, 100));
		setBackground(new Color(20, 20, 20));
		label = new JLabel();
		font = new Font("Monospaced", Font.BOLD, 36);
		label.setFont(font);
		label.setForeground(new Color(255, 255, 255));
		add(label);
	}
	
	public void setMessage(String msg) {
		label.setText(msg);
	}
}
