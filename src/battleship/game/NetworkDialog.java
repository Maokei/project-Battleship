package battleship.game;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import battleship.network.ClientConnection;
import battleship.screen.InputPanel;

public class NetworkDialog extends JDialog {
	private static final long serialVersionUID = -1788732779835707017L;
	private ClientConnection con;
	private InputPanel address;
	private InputPanel port;
	private JPanel centerPanel;
	private JPanel buttonPanel;
	private JButton cancel, login;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";

	public NetworkDialog(ClientConnection con) {
		super();
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
		centerPanel = new JPanel();
		centerPanel.add(address);
		centerPanel.add(port);
		buttonPanel = new JPanel();
		buttonPanel.add(cancel);
		buttonPanel.add(login);
		
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(200, 100);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		pack();
	}

	private void close() {
		this.dispose();
	}

	private void login() {
		if (!(address.getInput().equals("") && port.getInput().equals(""))) {
			String ipaddress = address.getInput();
			int portNumber = Integer.parseInt(port.getInput());
			con = new ClientConnection(ipaddress, portNumber);
			// add functionality here
		}
	}
}
