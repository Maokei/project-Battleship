package battleship.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import battleship.player.Grid;

public class Screen {
	private JFrame frame;
	private MainPanel mainPanel;
	private Avatar avatar;
	private Grid playerGrid, enemyGrid;
	
	public Screen(Grid playerGrid, Grid enemyGrid) {
		frame = new JFrame("*** Battleship ***");
		mainPanel = new MainPanel();
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		avatar = new Avatar();
		this.playerGrid = playerGrid;
		playerGrid.gridTest();
		this.enemyGrid = enemyGrid;
		enemyGrid.gridTest();
		mainPanel.add(avatar);
		mainPanel.add(playerGrid);
		mainPanel.add(enemyGrid);
		frame.add(mainPanel, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
	}
	
	public void setAvatar(Avatar av) {
		avatar.setAvatar(av.getImage(), av.getName());
	}
	
	public void showGUI() {
		frame.setVisible(true);
	}
	
	public JFrame getScreen() {
		return frame;
	}
}
