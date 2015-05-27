
package battleship.game;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.game.Game;
import battleship.network.ClientConnection;
import battleship.screen.Avatar;
import battleship.screen.AvatarPanel;
import battleship.screen.InputPanel;
import battleship.screen.Screen;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private InputPanel nameInput;
	private AvatarPanel avatarChooser;
	private JPanel buttonPanel;
	private JButton cancel, clear, login;
	private ClientConnection connection;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";
	private Player player;

	public LoginDialog(Player player) {
		super();
		this.player = player;
		setLayout(new BorderLayout());
		nameInput = new InputPanel("Enter name: ", true);
		avatarChooser = new AvatarPanel();
		buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(0, 0, 0));
		cancel = new JButton("Cancel");
		cancel.setBorderPainted(false);
		cancel.setBackground(new Color(255, 60, 60));
		cancel.setForeground(new Color(255, 255, 255));
		cancel.addActionListener(ae -> { dispose(); });
		clear = new JButton("Clear");
		clear.setBorderPainted(false);
		clear.setBackground(new Color(60, 255, 255));
		clear.setForeground(new Color(255, 255, 255));
		clear.addActionListener(ae -> { clear(); });
		login = new JButton("Login");
		login.setBorderPainted(false);
		login.setBackground(new Color(60, 255, 60));
		login.setForeground(new Color(255, 255, 255));
		login.addActionListener(ae -> { login(); } ); 
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		add(nameInput, BorderLayout.NORTH);
		add(avatarChooser, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(250, 300);
		setTitle("Battleship login options");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void clear() {
		nameInput.setInput("");
		avatarChooser.reset();
	}
	
	private void close() {
		setVisible(false);
		dispose();
	}

	private void login() {
		if (!nameInput.equals("")) {
			connection = new ClientConnection(DEFAULT_ADDRESS, DEFAULT_PORT);
			if (connection.openConnection()) {
				player = new Player(nameInput.getInput(), avatarChooser.getAvatar(), connection);
				if(player == null) System.exit(0);
				close();
			}
		}
	}
}
