package battleship.game;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.network.ClientConnection;
import battleship.screen.Avatar;
import battleship.screen.InputPanel;

public class NetworkDialog extends JDialog {
	private static final long serialVersionUID = -1788732779835707017L;
	private Player player;
	private Avatar avatar;
	private GameMode mode;
	private InputPanel name;
	private InputPanel address;
	private InputPanel port;
	private JPanel centerPanel;
	private JPanel buttonPanel;
	private JButton cancel, clear, login;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";

	public NetworkDialog(Player player) {
		super();
		setLayout(new BorderLayout());
		name = new InputPanel("Enter name: ", true);
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
		centerPanel = new JPanel(new GridLayout(3, 1));
		centerPanel.add(name);
		centerPanel.add(address);
		centerPanel.add(port);
		buttonPanel = new JPanel();
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(200, 120);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		pack();
	}

	private void clear() {
		name.setInput("");
		address.setInput("");
		port.setInput("");
	}

	private void close() {
		this.dispose();
	}

	private void login() {
		if (!(name.getInput().equals("") && address.getInput().equals("") && port.getInput().equals(""))) {
			String playerName = name.getInput();
			String ipaddress = address.getInput();
			int portNumber = Integer.parseInt(port.getInput());
			ClientConnection con = new ClientConnection(ipaddress, portNumber);
			if (con.openConnection()) {
				player = new Player(playerName, con);
				if(player == null) System.exit(0);
				
				close();
			}
			// JOptionPane.showOptionDialog(null, "Waiting for player","Waiting", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
			//lobby = new Lobby(player);
		}
	}
}
