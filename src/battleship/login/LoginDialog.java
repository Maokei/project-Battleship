package battleship.login;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.game.Game;
import battleship.network.ClientConnection;
import battleship.player.Player;
import battleship.screen.Screen;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private LoginPanel loginPanel;
	private JPanel buttonPanel;
	private JButton cancel, clear, login;
	private ClientConnection connection;
	private boolean connected;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";
	private Player player;

	public LoginDialog(Screen screen, Game game) {
		super(screen.getScreen(), true);
		setLayout(new GridLayout(2, 1));
		setSize(300, 600);
		setTitle("Battleship login options");
		
		loginPanel = new LoginPanel();
		buttonPanel = new JPanel();
		cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> {
			dispose();
		});
		clear = new JButton("Clear");
		clear.addActionListener(ae -> {
			loginPanel.clear();
		});
		login = new JButton("Login");
		login.addActionListener(ae -> { login(); } ); 
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		add(loginPanel);
		add(buttonPanel);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(screen.getScreen());
		setVisible(true);
	}

	private void login() {
		if (!loginPanel.getName().equals("")) {
			connection = new ClientConnection(DEFAULT_ADDRESS, DEFAULT_PORT);
			if (connection.openConnection()) {
				connected = true;
				player = new Player(loginPanel.getName(), connection);
			}
		}
		// 
	}
	
	public Player getPlayer() {
		return player;
	}
}
