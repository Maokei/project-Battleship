/**
 * @file MessagePanel.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * MessagePanel
 * @name MessagePanel
 * @extends JPanel
 * @brief Class describes a message panel GUI component. 
 * */
public class MessagePanel extends JPanel {
	private static final long serialVersionUID = 9121864476651955903L;
	private JLabel label;
	private Font font;
	
	/**
	 * @constructor MessagePanel
	 * */
	public MessagePanel() {
		super();
		setPreferredSize(new Dimension(1000, 100));
		setBackground(new Color(20, 20, 20));
		label = new JLabel("Deploy your ships");
		font = new Font("Monospaced", Font.BOLD, 36);
		label.setFont(font);
		label.setForeground(new Color(255, 255, 255));
		add(label);
	}
	
	/**
	 * setMessage
	 * @name setMessage 
	 * @param Set message in panel.
	 * */
	public void setMessage(String msg) {
		label.setText(msg);
	}
}
