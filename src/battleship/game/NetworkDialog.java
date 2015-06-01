package battleship.game;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.network.ClientConnection;
import battleship.screen.Avatar;
import battleship.screen.InputPanel;
import battleship.screen.Lobby;

public class NetworkDialog extends JDialog {
	private static final long serialVersionUID = -1788732779835707017L;
	private Player player;
	private String name;
	private Avatar avatar;
	private ClientConnection con;
	private GameMode mode;
	private Lobby lobby;
	private InputPanel address;
	private InputPanel port;
	private JPanel centerPanel;
	private JPanel buttonPanel;
	private JButton cancel, clear, login;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";

	public NetworkDialog(Player player, String name, Avatar avatar, ClientConnection con, GameMode mode) {
		super();
		this.name = name;
		this.avatar = avatar;
		this.con = con;
		this.mode = mode;
		setLayout(new BorderLayout());
		address = new InputPanel("Enter IP-address: ", true);
		address.setInput("localhost");
		port = new InputPanel("Enter portnumber: ", true);
		port.setInput("10001");
		cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> {
			close();
		});
		login = new JButton("Login");
		login.addActionListener(ae -> {
			login();
		});
		clear = new JButton("Clear");
		clear.addActionListener(ae -> { clear(); });
		centerPanel = new JPanel();
		centerPanel.add(address);
		centerPanel.add(port);
		buttonPanel = new JPanel();
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(200, 100);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		pack();
	}

	private void clear() {
		address.setInput("");
		port.setInput("");
	}

	private void close() {
		this.dispose();
	}

	private void login() {
		if (!(address.getInput().equals("") && port.getInput().equals(""))) {
			String ipaddress = address.getInput();
			int portNumber = Integer.parseInt(port.getInput());
			con = new ClientConnection(ipaddress, portNumber);
			//player = new Player(name, avatar, con, mode);
			
			if (con.openConnection()) {
				player = new Player(name, avatar, con, mode);
				lobby = new Lobby(player);
				player.init();
				if(player == null) System.exit(0);
				close();
			}
		}
	}
}
