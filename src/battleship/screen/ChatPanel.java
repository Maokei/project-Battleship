package battleship.screen;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import battleship.game.GameMode;
import battleship.game.Message;
import battleship.game.Player;
import battleship.screen.InputPanel;

public class ChatPanel extends JPanel {
	private static final long serialVersionUID = 8534267656117643591L;
	private Player player;
	private InputPanel input;
	private JPanel outputPanel;
	private JPanel buttonPanel;
	private JTextArea output;
	private JButton send;

	public ChatPanel() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(150, 100));
		input = new InputPanel("Enter message", true);
		output = new JTextArea(6, 10);
		outputPanel = new JPanel();
		outputPanel.add(new JScrollPane(output));
		send = new JButton("Send");
		send.addActionListener(ae -> {
			sendChatMessage();
		});
		buttonPanel = new JPanel();
		buttonPanel.add(send);
		add(input, BorderLayout.NORTH);
		add(output, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setPlayer(Player player) {
		this.player = player;
		player.getConnection().setOutput(output);
		
		player.sendMessage(new Message(Message.LOGIN, player.getName(), player.getGameMode()));
	}

	private void sendChatMessage() {
		player.sendMessage(new Message(Message.CHAT, player.getName(), input
				.getInput()));
	}

	public void clear() {
		output.setText("");
	}
}
