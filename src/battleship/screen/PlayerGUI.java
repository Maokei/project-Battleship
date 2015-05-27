package battleship.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import battleship.game.Message;
import battleship.game.Player;

public class PlayerGUI extends JPanel {
	private static final long serialVersionUID = 3152977875044360557L;
	private Player player;
	private JLabel nameLabel;
	private PlayerStats stats;
	private Avatar avatar;
	private ChatPanel chat;
	private JPanel buttonPanel;
	private JLabel buttonLabel;
	private JButton ready;
	private GridBagConstraints gc;
	
	public PlayerGUI(Player player) {
		super(new GridBagLayout());
		this.player = player;
		setPreferredSize(new Dimension(280, 320));
		setBackground(new Color(50, 50, 50));
		nameLabel = new JLabel(player.getName());
		nameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
		nameLabel.setForeground(new Color(255, 255, 255));
		stats = new PlayerStats();
		chat = new ChatPanel();
		chat.setPlayer(player);
		avatar = player.getAvatar();
		buttonPanel = new JPanel();
		buttonLabel = new JLabel("All ships placed");
		ready = new JButton("Ready");
		ready.setEnabled(false);
		ready.addActionListener(ae -> { shipsPlaced(); });
		gc = new GridBagConstraints();
		buttonPanel.add(buttonLabel);
		buttonPanel.add(ready);
		
		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.insets = new Insets(10, 40, 10, 20);
		add(nameLabel);
		gc.insets = new Insets(10, 20, 10, 20);
		gc.gridy = 1;
		add(buttonPanel, gc);
		
		gc.gridwidth = 1;
		gc.insets = new Insets(0, 5, 0, 0);
		gc.gridy = 2;
		gc.ipadx = 100;
		gc.ipady = 70;
		add(chat, gc);
		
		gc.insets = new Insets(0, 0, 0, 5);
		gc.gridx = 1;
		gc.ipadx = 0;
		gc.ipady = 14;
		add(avatar, gc);
		
		gc.gridx = 0;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(10, 50, 10, 50);
		gc.gridy = 3;
		gc.gridwidth = 2;
		add(stats, gc);
	}
	
	private void shipsPlaced() {
		player.sendMessage(new Message(Message.DEPLOYED, player.getName(), ""));
		player.setDeployed();
	}
	
	public void setShipsDeployed() {
		ready.setEnabled(true);
	}
	
	public PlayerStats getStats() {
		return stats;
	}
}
