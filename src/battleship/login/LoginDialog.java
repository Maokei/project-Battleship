package battleship.login;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.game.Game;
import battleship.network.ClientConnection;
import battleship.player.Player;
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
	private boolean connected;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";
	private Player player;
	private Game game;
	private Screen screen;

	public LoginDialog(Screen screen, Game game) {
		super(screen.getScreen(), true);
		this.screen = screen;
		this.game = game;
		setLayout(new BorderLayout());
		nameInput = new InputPanel("Enter name: ", true);
		avatarChooser = new AvatarPanel();
		buttonPanel = new JPanel();
		cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> { dispose(); });
		clear = new JButton("Clear");
		clear.addActionListener(ae -> { clear(); });
		login = new JButton("Login");
		login.addActionListener(ae -> { login(); } ); 
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		add(nameInput, BorderLayout.NORTH);
		add(avatarChooser, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(200, 300);
		setTitle("Battleship login options");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(screen.getScreen());
		setVisible(true);
	}
	
	public void clear() {
		nameInput.setInput("");
		avatarChooser.reset();
	}
	
	public Avatar getAvatar() {
		return avatarChooser.getAvatar();
	}

	private void login() {
		if (!nameInput.equals("")) {
			connection = new ClientConnection(DEFAULT_ADDRESS, DEFAULT_PORT);
			if (connection.openConnection()) {
				connected = true;
				player = new Player(nameInput.getInput(), connection);
				game.setPlayer(player);
				screen.setAvatar(getAvatar());
				setVisible(false);
				dispose();
			}
		}
		// 
	}
}
