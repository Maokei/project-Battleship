package battleship.screen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import battleship.network.Server;

public class Lobby extends JFrame{
	private static final long serialVersionUID = 611848419874984812L;
	JPanel bottomButtons;
	MessagePanel mesPanel;
	JList players;
	JTextArea chat;
	JTextField chatInput;
	JButton chaBtn;
	JButton playAi;
	
	public Lobby() {
		super("*** Battleship lobby ***");
		setupGUI();
	}
	
	private void setupGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//setup panels
		bottomButtons = new JPanel();
		//bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
		bottomButtons.setLayout(new BorderLayout());
		bottomButtons.add(chaBtn = new JButton("Challange"),BorderLayout.WEST);
		bottomButtons.add(playAi = new JButton("Play vs AI"), BorderLayout.EAST);
		chaBtn.setMargin(new Insets(25, 25, 25, 25));
		playAi.setMargin(new Insets(25, 25, 25, 25));
		//list
		String test[] = {"bosse", "olle", "gustav"};
		players = new JList(test);
		players.setPreferredSize(new Dimension(300, 300));
		JScrollPane pane = new JScrollPane(players);
		mesPanel = new MessagePanel();
		//add components
		add(pane, BorderLayout.WEST);
		add(mesPanel, BorderLayout.EAST);
		add(bottomButtons, BorderLayout.SOUTH);
		//
		setSize(1200, 600);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Lobby();
	}
}
