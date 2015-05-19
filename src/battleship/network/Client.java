/*
package battleship.network;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client {
	private String address;
	private int portNumber;
	private String name;
	private JFrame frame;
	private JLabel nameLabel;
	private JTextField nameInput;
	private JLabel messageLabel;
	private JTextField messageInput;
	private JButton logOn, logOff;
	private JTextArea output;
	private ClientConnection connection;
	private boolean connected;
	public static final int DEFAULT_PORT = 10001;
	public static final String DEFAULT_ADDRESS = "localhost";

	public Client(String address, int portNumber) {
		this.address = address;
		this.portNumber = portNumber;
		name = "Anonymous";
		frame = new JFrame("Player");
		nameLabel = new JLabel("Enter your name");
		nameInput = new JTextField(15);
		nameLabel.setLabelFor(nameInput);
		JPanel namePanel = new JPanel();
		namePanel.add(nameLabel);
		namePanel.add(nameInput);

		messageLabel = new JLabel("Enter message");
		messageInput = new JTextField(15);
		messageInput.setEditable(false);
		messageInput.addActionListener(ae -> {
			sendMessageEvent();
		});
		messageLabel.setLabelFor(messageInput);
		JPanel messagePanel = new JPanel();
		messagePanel.add(messageLabel);
		messagePanel.add(messageInput);
		JPanel northPanel = new JPanel(new GridLayout(2, 1));
		northPanel.add(namePanel);
		northPanel.add(messagePanel);
		frame.add(northPanel, BorderLayout.NORTH);

		output = new JTextArea(15, 15);
		output.setEditable(false);
		JPanel centerPanel = new JPanel();
		centerPanel.add(output);
		frame.add(centerPanel);

		logOn = new JButton("Log On");
		logOn.addActionListener(ae -> {
			logOnEvent();
		});

		logOff = new JButton("Log Off");
		logOff.addActionListener(ae -> {
			logOffEvent();
		});
		logOff.setEnabled(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(logOn);
		buttonPanel.add(logOff);
		frame.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void sendMessageEvent() {
		if (connected) {
			connection.sendMessage(
					new ChatMessage(Message.MESSAGE, name, messageInput.getText()));
		}
	}

	private void logOnEvent() {
		if (!nameInput.getText().equals("")) {
			name = nameInput.getText();
		}

		connection = new ClientConnection(address, portNumber, this);
		if (connection.openConnection()) {
			connected = true;
			messageInput.setEditable(true);
			logOn.setEnabled(false);
			logOff.setEnabled(true);
		}
		
		new Thread(connection).start();
		connection.sendMessage(new Message(Message.MESSAGE, name, "Logged in"));
	}

	private void logOffEvent() {
		connection.sendMessage(new Message(Message.LOGOUT, name, "Logged out"));
	}

	void reset() {
		connection = null;
		nameInput.setText("");
		name = "Anonymous";
		output.setText("");
		logOn.setEnabled(true);
		logOff.setEnabled(false);
	}

	void clearOutput() {
		output.setText("");
	}

	void setText(String text) {
		output.setText(text);
	}

	void appendText(String text) {
		output.append(text);
	}
	
	void fire(int x, int y) {
		
	}

	public void showGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		nameInput.requestFocus();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Client(DEFAULT_ADDRESS, DEFAULT_PORT).showGUI();
			}
		});
	}

}
*/