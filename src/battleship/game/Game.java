/**
 * @file Game.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import static battleship.game.Constants.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import battleship.network.Server;
import battleship.resources.AudioLoader;

/**
 * @package battleship.game
 * @Class game
 * @brief Class prompts user for launching a Game or Server instance. 
 * */
public class Game {
	private Player player;
	private LoginDialog login;
	
	/**
	 * Game
	 * 
	 * @name Game
	 * @brief Game constructor initiates game sound instantiates a LoginDialog
	 * */
	public Game() {
		AudioLoader.initSounds();
		login = new LoginDialog(player);
	}
	/**
	 * main
	 * 
	 * @name main
	 * @brief main method
	 * */
	public static void main(String[] args) {
		Object[] ob = { "Client", "Server","Cancel" };
		int choice = optionPane("Start client or server?","Battleship", ob);
		
		switch(choice) {
		case 0: new Game(); break;
		case 1: new Server(DEFAULT_PORT).listen(); break;
		default:
		}
	}
	
	/**
	 * optionPane
	 * 
	 * @name optionPane
	 * @brief displays a dialog prompting to launch instance of Game or Server
	 * @param question the question displayed
	 * @param title the title of the dialog
	 * @param options Object array of the options to be displayed
	 * @return an integer representing the user choice
	 * */
	public static int optionPane(String question, String title, Object[] options) {
		return JOptionPane.showOptionDialog(
				new JFrame(), question, title,
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[2]);
	}
}

