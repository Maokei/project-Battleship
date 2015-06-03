/**
 * @file Game.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import battleship.network.Server;
import battleship.resources.AudioLoader;

public class Game {
	private Player player;
	private LoginDialog login;
	public static final int DEFAULT_PORT = 10001;
	
	public Game() {
		AudioLoader.initSounds();
		
		login = new LoginDialog(player);
	}
	
	public static void main(String[] args) {
		Object[] ob = { "Client", "Server","Cancel" };
		int choice = optionPane("Start client or server?","Battleship", ob);
		
		switch(choice) {
		case 0: new Game(); break;
		case 1: new Server(DEFAULT_PORT).listen(); break;
		default:
		}
	}
	
	public static int optionPane(String question, String title, Object[] options) {
		return JOptionPane.showOptionDialog(
				new JFrame(), question, title,
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[2]);
	}
}

