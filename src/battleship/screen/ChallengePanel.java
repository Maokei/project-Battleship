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

	public ChallengePanel(Player player) {
		this.player = player;
		setLayout(new BorderLayout());
		centerPanel = new JPanel();
		buttonPanel = new JPanel();
		invite = new JButton("Invite");
		invite.addActionListener(new ChallengeListener());
		refresh = new JButton("Refresh");
		refresh.addActionListener(ae -> { updateNames(); });
		buttonPanel.add(invite);
		buttonPanel.add(refresh);
		buttonPanel.setBackground(new Color(10, 10, 10));
		centerPanel.setBackground(new Color(40, 40, 40));
		font = new Font("Monospaced", Font.BOLD, 10);
		setupPlayersList();
		centerPanel.add(players);
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(200, 250);
		setVisible(true);
	}

	/**
	 * updateNames
	 * 
	 * @name updateNames
	 * @brief Set new names into list from names.
	 * */
	public void updateNames() {
		players.clearSelection();
		String[] names = player.getConnectedPlayers().toArray(
				new String[player.getConnectedPlayers().size()]);
		players.setListData(names);
	}

	/**
	 * setupPlayersList
	 * 
	 * @name setupPlayersList
	 * @brief to setup the players JList
	 * */
	private void setupPlayersList() {
		players = new JList<String>(player.getConnectedPlayers().toArray(
				new String[player.getConnectedPlayers().size()]));
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setLayoutOrientation(JList.VERTICAL);
		players.setSelectionBackground(Color.BLACK);
		players.setPreferredSize(new Dimension(100, 220));
		pane = new JScrollPane(players);
	}
	
	/*
	class NameListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JLabel choosen = ((JLabel) e.getSource());
			choosen.setForeground(new Color(0, 255, 0));
			opponent = choosen.getText();
			invite.setEnabled(true);
		}
	}
	*/
	private class ChallengeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!players.isSelectionEmpty()) {
				String sel = (String)players.getSelectedValue();
				//Don't allow player to match himself
				if(sel.equals(player.getName()))
					return;
				//JOptionPane.showMessageDialog(new JFrame(), "Sending battle challange to opponent, " + sel);
				player.sendMessage(new Message(Message.CHALLENGE, player.getName(),
						sel, Challenge_Request));
				players.clearSelection();
			}
		}
	}
}
