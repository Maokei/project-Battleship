/**
 *@file Lobby.java
 *@author rickard, lars 
 **/
package battleship.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import battleship.game.Message;
import battleship.game.Player;
import battleship.network.Server;

public class Lobby extends JFrame{
	private static final long serialVersionUID = 611848419874984812L;
	private JPanel bottomButtons;
	private ChatPanel chatPanel;
	private JList players;
	private JScrollPane pane;
	private JTextArea chat;
	private JTextField chatInput;
	private JButton chaBtn;
	private JButton playAi;
	private Player player;
	
	public Lobby() {
		super("*** Battleship lobby ***");
		setupGUI();
	}
	
	private void setupGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//setup panels
		bottomButtons = new JPanel();
		//bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
		bottomButtons.setLayout(new BorderLayout());
		bottomButtons.add(chaBtn = new JButton("Challange"),BorderLayout.WEST);
		bottomButtons.add(playAi = new JButton("Play vs AI"), BorderLayout.EAST);
		chaBtn.setMargin(new Insets(25, 25, 25, 25));
		playAi.setMargin(new Insets(25, 25, 25, 25));
		//list
		setupPlayersList();
		//Chat panel
		chatPanel = new ChatPanel();
		chatPanel.setPreferredSize(new Dimension(900, 400));
		//add components
		add(pane, BorderLayout.WEST);
		add(chatPanel, BorderLayout.EAST);
		add(bottomButtons, BorderLayout.SOUTH);
		chaBtn.addActionListener(new ChallangeListener());
		//
		setSize(1200, 600);
		setVisible(true);
	}
	
	//listener class
	class ChallangeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!players.isSelectionEmpty()) {
				String sel = (String)players.getSelectedValue();
				JOptionPane.showMessageDialog(new JFrame(), "Sending battle challange to opponent, " + sel);
				player.sendMessage(new Message(Message.CHALLENGE, player.getName(), sel));
				players.clearSelection();
			}
		}
		
	}
	
	private void setupPlayersList() {
		String test[] = {"bosse", "olle", "gustav"};
		players = new JList(test);
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setSelectionBackground(Color.BLACK);
		players.setPreferredSize(new Dimension(300, 300));
		pane = new JScrollPane(players);
	}
	
	public static void main(String[] args) {
		new Lobby();
	}
}