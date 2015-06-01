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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;

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
import battleship.network.ClientConnection;

/**
 * Lobby
 * @class Lobby 
 * @brief describes a player lobby where players can chat and send battle challenges to each other.
 * @extends JFrame
 * @param Player , takes a player object
 * */
public class Lobby extends JFrame {
	private static final long serialVersionUID = 611848419874984812L;
	private Player player;
	private String name;
	private Avatar avatar;
	private ClientConnection con;
	private JPanel bottomButtons;
	private ChatPanel chatPanel;
	private ArrayList<String> names;
	private JList players;
	private JScrollPane pane;
	private JTextArea chat;
	private JTextField chatInput;
	private JButton chaBtn;
	private JButton playAi;
	private JButton refresh;
	
	public Lobby(Player player) {
		super("*** Battleship lobby ***");
		this.player = player;
		names = new ArrayList<String>();
		names.add(player.getName());
		setupGUI();
		
	}
	
	/**
	 * Constructor
	 * */
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
		bottomButtons.add(refresh = new JButton("Refresh"), BorderLayout.CENTER);
		refresh.setMargin(new Insets(25, 25, 25, 25));
		chaBtn.setMargin(new Insets(25, 25, 25, 25));
		playAi.setMargin(new Insets(25, 25, 25, 25));
		//list
		setupPlayersList();
		//Chat panel
		chatPanel = new ChatPanel();
		chatPanel.setPlayer(player);
		chatPanel.setPreferredSize(new Dimension(900, 400));
		//add components
		add(pane, BorderLayout.WEST);
		add(chatPanel, BorderLayout.EAST);
		add(bottomButtons, BorderLayout.SOUTH);
		chaBtn.addActionListener(new ChallangeListener());
		refresh.addActionListener(new RefreshListener());
		//
		setSize(1200, 600);
		setVisible(true);
	}
	
	//challange listener class
	private class ChallangeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!players.isSelectionEmpty()) {
				String sel = (String)players.getSelectedValue();
				//Don't allow player to match himself
				if(sel.equals(player.getName()))
					return;
				JOptionPane.showMessageDialog(new JFrame(), "Sending battle challange to opponent, " + sel);
				player.sendMessage(new Message(Message.CHALLENGE, player.getName(), sel));
				players.clearSelection();
			}
		}
		
	}
	
	//Ai challange button listener class
	private class AiChallangeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			player.sendMessage(new Message(Message.AIMATCH, player.getName(), ""));
		}
	}
	
	//Refresh button
	private class RefreshListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		}
	
	/**
	 * setNames
	 * @name setName
	 * @param Takes an ArrayList containing strings, player names
	 * */
	public void setNames(ArrayList<String> n) {
		names = n;
	}
	
	/**
	 * setNames
	 * @name setNames
	 * @param String takes a string of names in the format "name1 name2 name3 name4" sets them 
	 * to names J-list
	 * */
	public void setNames(String namess) {
		try {
			String temp[] = namess.split("\\s+");
		    Collection<? extends String> n = Arrays.asList(temp);
		    //clear list
			names.clear();
			names.addAll(n);
		} catch (PatternSyntaxException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * updateNames
	 * @name updateNames
	 * @brief Set new names into list from names.
	 * */
	public void updateNames() {
		//players.clearSelection();
		players.setListData(names.toArray());
	}
	
	/**
	 * setupPlayersList
	 * @name setupPlayersList
	 * @brief to setup the players JList
	 * */
	private void setupPlayersList() {
		players = new JList(names.toArray());
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setSelectionBackground(Color.BLACK);
		players.setPreferredSize(new Dimension(300, 300));
		pane = new JScrollPane(players);
	}
}
