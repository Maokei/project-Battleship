/**
 * @file Game.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import battleship.network.Server;
import battleship.resources.AudioLoader;

public class Game {
	private Player player;
	private LoginDialog login;
	private String ip;
	private int port;
	public static final String DEFAULT_ADDRESS = "localhost";
	public static final int DEFAULT_PORT = 10001;
	
	public Game() {
		AudioLoader.initSounds();
		getIpAndPort();
		// login = new LoginDialog(player);
	}
	
	private void getIpAndPort() {
		new NetworkDialog(player);
		/*
		ip = (String)JOptionPane.showInputDialog(
                "Enter ip",
                "Server Ip",
                JOptionPane.PLAIN_MESSAGE,
                DEFAULT_ADDRESS);
		//port
		String temp = (String)JOptionPane.showInputDialog(
                this,
                "Enter port",
                "Server Port",
                JOptionPane.PLAIN_MESSAGE,
                null,
               null,
                DEFAULT_PORT);
		port = Integer.parseInt(temp);
		*/
	}
	
	
	
	private void waitingDialog() {
		JOptionPane optionPane = new JOptionPane("Waiting for players!"); 
		JDialog wait = optionPane.createDialog("Waiting");
		wait.setModal(false);
		wait.setVisible(true);
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

