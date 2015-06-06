/**
 * @file Screen.java
 * @author rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import battleship.game.Message;
import battleship.game.Player;
import battleship.gameboard.Gameboard;

/**
 * @class Screen
 * @package battleship.screen
 * @brief Class sets up battleship game gui & components.
 * */
public class Screen {
	private JFrame frame;
	private Gameboard playerBoard, enemyBoard;
	private MainPanel mainPanel;
	private PlayerGUI playergui;
	private MessagePanel msgPanel;
	private ChallengePanel challenge;
	
	/**
	 * Screen constructor
	 * @param Player object, GameBoard palyers gameboard, GameBoard of the enemy.
	 * */
	public Screen(Player player, Gameboard playerBoard, Gameboard enemyBoard) {
		this.playerBoard = playerBoard;
		this.enemyBoard = enemyBoard;
		frame = new JFrame("*** " + player.getName() + " ***");
		mainPanel = new MainPanel();
		playergui = new PlayerGUI(player);
		msgPanel = new MessagePanel();
		challenge = new ChallengePanel(player);
		mainPanel.add(playergui);
		mainPanel.add(playerBoard);
		mainPanel.add(enemyBoard);
		mainPanel.add(challenge);
		mainPanel.add(msgPanel);
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				player.sendMessage(new Message(Message.LOGOUT ,player.getName(), player.getOpponentName(), "Logged out"));
				player.setRunning(false);
				e.getWindow().dispose();
			}
		});
	}
	
	/**
	 * disableRandom
	 * @name disableRandom
	 * @brief Disable's random ship placement button.
	 * */
	public void disableRandom() {
		playergui.disableRandom();
	}
	
	/**
	 * setHits
	 * @name setHits
	 * @param Integer hits set number of hits on gui.
	 * */
	public void setHits(int hits) {
		playergui.getStats().setHits(hits);
	}
	
	/**
	 * setMisses
	 * @name setMisses
	 * @param Integer misses, set number of misses on gui.
	 * */
	public void setMisses(int misses) {
		playergui.getStats().setMisses(misses);
	}
	
	/**
	 * setShips
	 * @name setShips
	 * @param Integer ship, set number of ships on gui.
	 * */
	public void setShips(int ships) {
		playergui.getStats().setShips(ships);
	}
	
	/**
	 * setMessage
	 * @name setMessage
	 * @param String message, to be added to message panel.
	 * */
	public void setMessage(String message) {
		msgPanel.setMessage(message);
	}
	
	/**
	 * updateLobby
	 * @name updateLobby
	 * @brief Update the player names in the lobby.
	 * */
	public void updateLobby() {
		challenge.updateNames();
	}
	
	/**
	 * setInviteEnabled
	 * @name setInviteEnabled
	 * @param setInvite state to boolean.
	 * */
	public void setInviteEnabled(boolean enabled) {
		challenge.setInviteEnabled(enabled);
	}
	
	/**
	 * showGUI
	 * @name showGUI
	 * @brief Shows the screen GUI.
	 * */
	public void showGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * setShipsDeployed
	 * @name setShipsDeployed
	 * @brief setShipsDeployed state, deployed.
	 * */
	public void setShipsDeployed() {
		playergui.setShipsDeployed();
	}
}
