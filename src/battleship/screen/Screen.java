/**
 * @file Screen.java
 * @author rickard, lars
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
	private MainPanel mainPanel;
	private PlayerGUI playergui;
	private MessagePanel msgPanel;
	
	public Screen(Player player, Gameboard playerBoard, Gameboard enemyBoard) {
		frame = new JFrame("*** " + player.getName() + " ***");
		mainPanel = new MainPanel();
		playergui = new PlayerGUI(player);
		msgPanel = new MessagePanel();
		mainPanel.add(playergui);
		mainPanel.add(playerBoard);
		mainPanel.add(enemyBoard);
		
		mainPanel.add(msgPanel);
		frame.add(mainPanel, BorderLayout.CENTER);
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
	
	public void setHits(int hits) {
		playergui.getStats().setHits(hits);
	}
	
	public void setMisses(int misses) {
		playergui.getStats().setMisses(misses);
	}
	
	public void setShips(int ships) {
		playergui.getStats().setShips(ships);
	}
	
	public void setMessage(String message) {
		msgPanel.setMessage(message);
	}
	
	public void showGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setShipsDeployed() {
		playergui.setShipsDeployed();
	}
}
