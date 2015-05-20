package battleship.game;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import battleship.network.ClientConnection;
import battleship.network.Message;
import battleship.player.Player;
import battleship.screen.InputPanel;

public class Chat {
	private JFrame frame;
	private String name;
	private ClientConnection con;
	private boolean connected = false;
	private Player player;
	private InputPanel input;
	private JPanel outputPanel;
	private JPanel buttonPanel;
	private JTextArea output;
	private JButton login, send;

	public Chat(String name) {
		this.name = name;
		frame = new JFrame("Battleship Chat");
		input = new InputPanel("Enter message", true);
		output = new JTextArea(10, 20);
		outputPanel = new JPanel();
		outputPanel.add(new JScrollPane(output));
		login = new JButton("Log In");
		login.addActionListener(ae -> {
			login();
		});
		send = new JButton("Send");
		send.setEnabled(false);
		send.addActionListener(ae -> {
			sendChatMessage();
		});
		buttonPanel = new JPanel();
		buttonPanel.add(login);
		buttonPanel.add(send);

		frame.add(input, BorderLayout.NORTH);
		frame.add(outputPanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void showChat() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(250, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void sendChatMessage() {
		if (connected)
			con.sendMessage(new Message(Message.CHAT, player.getName(), input
					.getInput()));
	}

	private void login() {
		con = new ClientConnection("localhost", 10001);
		if (con.openConnection()) {
			connected = true;
			con.setOutput(output);
			login.setEnabled(false);
			send.setEnabled(true);
			player = new Player(name, con);
			con.setPlayer(player);
			player.sendMessage(new Message(Message.LOGIN, player.getName(), ""));
			new Thread(con).start();
		}
	}

	public static void main(String[] args) {
		String name = JOptionPane.showInputDialog("Enter name");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Chat(name).showChat();
			}
		});
	}
}
