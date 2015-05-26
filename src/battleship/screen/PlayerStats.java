package battleship.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import battleship.player.Player;

public class PlayerStats extends JPanel {
	
	private JLabel hitsLabel;
	private JLabel hitsValue;
	private JLabel missLabel;
	private JLabel missValue;
	private JLabel shipsLabel;
	private JLabel shipsValue;
	private GridBagConstraints gc;
	private Color textColor = new Color(255, 255, 255);
	private Color labelColor = new Color(15, 15, 15);
	private Color valueColor = new Color(25, 25, 25);
	private Font font = new Font("Monospaced", Font.PLAIN, 14);
	
	public PlayerStats() {
		super(new GridBagLayout());
		setPreferredSize(new Dimension(200, 300));
		setBackground(labelColor);
		font = new Font("Monospaced", Font.PLAIN, 14);
		hitsLabel = new JLabel("Hits: ");
		hitsLabel.setFont(font);
		hitsLabel.setBackground(labelColor);
		hitsLabel.setForeground(textColor);
		hitsValue = new JLabel("0");
		hitsValue.setBackground(valueColor);
		hitsValue.setForeground(textColor);
		missLabel = new JLabel("Misses: ");
		missLabel.setFont(font);
		missLabel.setBackground(labelColor);
		missLabel.setForeground(textColor);
		missValue = new JLabel("0");
		missValue.setBackground(valueColor);
		missValue.setForeground(textColor);
		shipsLabel = new JLabel("Ships: ");
		shipsLabel.setFont(font);
		shipsLabel.setBackground(labelColor);
		shipsLabel.setForeground(textColor);
		shipsValue = new JLabel("9");
		shipsValue.setBackground(valueColor);
		shipsValue.setForeground(textColor);
		gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		add(hitsLabel, gc);
		
		gc.gridx = 1;
		add(hitsValue, gc);
		
		gc.gridx = 0;
		gc.gridy = 2;
		add(missLabel, gc);
		
		gc.gridx = 1;
		add(missValue, gc);
		
		gc.gridx = 0;
		gc.gridy = 3;
		add(shipsLabel, gc);
		
		gc.gridx = 1;
		add(shipsValue, gc);
	}
	
	public void setHits(int hits) {
		hitsValue.setText(Integer.toString(hits));
	}
	
	public void setMisses(int misses) {
		missValue.setText(Integer.toString(misses));
	}
	
	public void setShips(int ships) {
		shipsValue.setText(Integer.toString(ships));
	}
}
