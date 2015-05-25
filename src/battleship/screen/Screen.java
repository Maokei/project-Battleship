/**
 * @file Screen.java
 * @author rickard, lars
 * */
package battleship.screen;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import battleship.game.ChatPanel;
import battleship.network.Message;
import battleship.player.Board;
import battleship.player.Gameboard;
import battleship.player.Player;

/**
 * @class Screen
 * @package battleship.screen
 * @brief Class sets up battleship game gui & components.
 * */
public class Screen {
	private Player player;
	private JFrame frame;
	private ChatPanel chat;
	private MainPanel mainPanel;
	private Avatar avatar;
	private Gameboard playerGrid, enemyGrid;
	private Board playerBoard, enemyBoard;
	
	public Screen(Gameboard playerGrid, Gameboard enemyGrid, Board playerBoard, Board enemyBoard) {
		this.playerGrid = playerGrid;
		this.enemyGrid = enemyGrid;
		this.playerBoard = playerBoard;
		this.enemyBoard = enemyBoard;
		frame = new JFrame("*** Battleship ***");
		mainPanel = new MainPanel();
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		chat = new ChatPanel(); 
		avatar = new Avatar();
		mainPanel.add(avatar);
		// mainPanel.add(playerGrid);
		// mainPanel.add(enemyGrid);
		mainPanel.add(chat);
		mainPanel.add(playerBoard);
		mainPanel.add(enemyBoard);
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				player.sendMessage(new Message(Message.LOGOUT ,player.getName(), "Logged out"));
				player.setRunning(false);
				e.getWindow().dispose();
			}
		});
	}
	
	public void setAvatar(Avatar av) {
		avatar.setAvatar(av.getImage(), av.getName());
	}
	
	public void setUpChat(Player player) {
		this.player = player;
		chat.setPlayer(player);
	}
	
	public void showGUI() {
		frame.setVisible(true);
	}
	
	public JFrame getScreen() {
		return frame;
	}
}
