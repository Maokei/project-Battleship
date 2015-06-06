/**
 * @file ChallangePanel.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import static battleship.game.Constants.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import battleship.game.LoginDialog;
import battleship.game.Message;
import battleship.game.Player;

/**
 * @class ChallangePanel
 * @extends JPanel
 * @brief Class describes a challange panel.
 * */
public class ChallengePanel extends JPanel {
	private static final long serialVersionUID = 2283968572833866258L;
	private Player player;
	private JList<String> players;
	private JButton invite, refresh;;
	private JPanel centerPanel, buttonPanel;
	private JScrollPane pane;
	private Font font;
	private String opponent;
	private final String noPlayers = "No available players.";

	/**
	 *  @constructor ChallangePanel
	 *  @param Takes a Player pointer.
	 * */
	public ChallengePanel(Player player) {
		this.player = player;
		setLayout(new BorderLayout());
		font = new Font("Monospaced", Font.BOLD, 12);
		centerPanel = new JPanel();
		buttonPanel = new JPanel();
		invite = new JButton("Invite");
		invite.setBorderPainted(false);
		invite.setBackground(new Color(62, 60, 250));
		invite.setForeground(new Color(255, 255, 255));
		invite.setFont(font);
		invite.addActionListener(new ChallengeListener());
		refresh = new JButton("Refresh");
		refresh.setBorderPainted(false);
		refresh.setBackground(new Color(90, 191, 7));
		refresh.setForeground(new Color(255, 255, 255));
		refresh.setFont(font);
		refresh.addActionListener(ae -> { updateNames(); });
		buttonPanel.add(invite);
		buttonPanel.add(refresh);
		buttonPanel.setBackground(new Color(80, 80, 80));
		centerPanel.setBackground(new Color(80, 80, 80));
		setupPlayersList();
		centerPanel.add(players);
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(100, 140);
		setVisible(true);
	}
	
	/**
	 * setInviteEnabled
	 * @name setInviteEnabled
	 * @param Boolean to set enabled state.
	 * */
	public void setInviteEnabled(boolean enabled) {
		invite.setEnabled(enabled);
	}

	/**
	 * updateNames
	 * 
	 * @name updateNames
	 * @brief Set new names into list from names.
	 * */
	public void updateNames() {
		players.clearSelection();
		String[] names = player.getConnectedPlayers().keySet().toArray(
				new String[player.getConnectedPlayers().size()]);
		String[] available = player.getConnectedPlayers().values().toArray(
				new String[player.getConnectedPlayers().size()]);
		ArrayList<String> availableNames = new ArrayList<String>();
		
		for(Entry<String, String> entry : player.getConnectedPlayers().entrySet()) {
			availableNames.add(entry.getKey() + " " + entry.getValue());
		}
		
		String[] display = availableNames.toArray(new String[player.getConnectedPlayers().size()]);
		players.setListData(display);
	}

	/**
	 * setupPlayersList
	 * 
	 * @name setupPlayersList
	 * @brief to setup the players JList
	 * */
	private void setupPlayersList() {
		players = new JList<String>(player.getConnectedPlayers().keySet().toArray(
				new String[player.getConnectedPlayers().size()]));
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setLayoutOrientation(JList.VERTICAL);
		players.setBackground(new Color(30, 30, 30));
		players.setForeground(new Color(255, 255, 255));
		players.setFont(font);
		players.setSelectionBackground(new Color(116, 255, 150));
		players.setPreferredSize(new Dimension(120, 140));
		pane = new JScrollPane(players);
	}
	
	/**
	 * @class ChallangeListener
	 * @implements ActionListener
	 * @brief Class describes a button listener for the Challenge panel.
	 * */
	private class ChallengeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!players.isSelectionEmpty()) {
				String sel = (String)players.getSelectedValue();
				String[] values = sel.split(" ");
				String name = values[0];
				String available = values[1];
				//Don't allow player to match himself
				if(name.equals(player.getName()))
					return;
				if(available.trim().equalsIgnoreCase("Playing")) {
					players.setSelectionBackground(new Color(212, 97, 93));
				} else {
					player.sendMessage(new Message(Message.CHALLENGE, player.getName(),
							name, Challenge_Request));
				}
				//JOptionPane.showMessageDialog(new JFrame(), "Sending battle challange to opponent, " + sel);
			}
		}
	}
}
