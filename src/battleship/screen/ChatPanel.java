/**
 * @file ChatPanel.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
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

/**
 * @class ChatPanel
 * @extends JPanel
 * @brief Class describes a chat panel java swing component.
 * */
public class ChatPanel extends JPanel {
	private static final long serialVersionUID = 8534267656117643591L;
	private Player player;
	private InputPanel input;
	private JPanel outputPanel;
	private JPanel buttonPanel;
	private JTextArea output;
	private JButton send;

	/**
	 * Constructor
	 * */
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

	/**
	 * setPlayer
	 * @name setPlayer
	 * @param Player to be set a user of chat panel.
	 * */
	public void setPlayer(Player player) {
		this.player = player;
		player.getConnection().setOutput(output);
	}

	/**
	 * sendChatMessage
	 * @name sendChatMessage
	 * @brief Grab message in input field and send as a CHAT Message.
	 * */
	private void sendChatMessage() {
		player.sendMessage(new Message(Message.CHAT, player.getName(), player.getOpponentName(), input
				.getInput()));
	}
	
	/**
	 * clear
	 * @name clear 
	 * @brief Clear input field.
	 * */
	public void clear() {
		output.setText("");
	}
}
