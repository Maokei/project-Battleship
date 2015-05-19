/**
 * @file Game.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import battleship.login.LoginDialog;
import battleship.network.ClientConnection;
import battleship.player.Grid;
import battleship.player.Player;
import battleship.screen.Avatar;
import battleship.screen.Screen;

public class Game {
	private Player player;
	private Screen screen;
	private LoginDialog login;
	private ClientConnection con;
	private Grid playerGrid, enemyGrid;
	
	public Game() {
		playerGrid = new Grid();
		enemyGrid = new Grid();
		screen = new Screen(playerGrid, enemyGrid);
		login = new LoginDialog(screen, this);
		player = login.getPlayer();
		player.setGrid(playerGrid);
		login.dispose();
		screen.showGUI();
		player.init();
		player.listen();
	}
	
	public static void main(String[] args) {
		Game game = new Game();
	}
}
