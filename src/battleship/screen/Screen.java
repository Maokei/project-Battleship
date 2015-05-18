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
	private InputPanel namePanel;
	private InputPanel avatarpanel;
	private JLabel position;
	private JPanel positionPanel;
	private static FireButton fireButton;
	private Grid playerGrid, enemyGrid;
	
	public Screen() {
		frame = new JFrame("*** Battleship ***");
		mainPanel = new MainPanel();
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		mainPanel.addMouseListener(new GridListener());
		namePanel = new InputPanel("Enter name", true);
		avatarpanel = new InputPanel("choose avatar", true);
		position = new JLabel("Click on screen to get position");
		position.setForeground(new Color(255, 255, 255));
		positionPanel = new JPanel();
		positionPanel.setSize(new Dimension(100, 120));
		positionPanel.setBackground(new Color(18, 28, 26));
		positionPanel.add(position);
		mainPanel.add(namePanel);
		mainPanel.add(positionPanel);
		mainPanel.add(avatarpanel);
		fireButton = new FireButton("Fire Missile");
		mainPanel.add(fireButton);
		mainPanel.add(new AvatarPanel());
		playerGrid = new Grid();
		playerGrid.gridTest();
		enemyGrid = new Grid();
		enemyGrid.gridTest();
		mainPanel.add(playerGrid);
		mainPanel.add(enemyGrid);
		frame.add(mainPanel, BorderLayout.CENTER);
	}
	
	public void showGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Screen().showGUI();
			}
		});
	}
	
	class GridListener extends MouseAdapter {
	    @Override
	    public void mousePressed(MouseEvent e) {
	    	position.setText("You fired at Position: " + e.getX() + " " + e.getY());
	    }
	}
	
}
