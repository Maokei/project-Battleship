/**
 * @file Game.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import battleship.login.LoginDialog;
import battleship.network.ClientConnection;
import battleship.network.Server;
import battleship.player.Grid;
import battleship.player.Player;
import battleship.screen.Avatar;
import battleship.screen.Screen;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game {
	private Player player;
	private Screen screen;
	private LoginDialog login;
	private ClientConnection con;
	private Grid playerGrid, enemyGrid;
	public static final int DEFAULT_PORT = 10001;
	
	public Game() {
		playerGrid = new Grid();
		enemyGrid = new Grid();
		screen = new Screen(playerGrid, enemyGrid);
		login = new LoginDialog(screen, this);		
		player.setGrid(playerGrid);
		screen.showGUI();
		player.init();
		player.listen();
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public static void main(String[] args) {
		Object[] ob = {"Client", "Server","Cancel"};
		int choice = optionPane("Start client or server?","Battleship",ob);
		switch(choice) {
		case 0:
			Game game = new Game();
			break;
		case 1:
			Server server = new Server(DEFAULT_PORT);
			server.listen();
			break;
		case 2:
			//do nothing
			break;
		
		}
	}
	
	public static int optionPane(String question, String title, Object[] options) {
		int n = 0;
		JFrame frame = new JFrame();
		n = JOptionPane.showOptionDialog(frame, question, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		return n;
	}
}

