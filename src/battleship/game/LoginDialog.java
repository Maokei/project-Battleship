/**
 * @file LoginDialog.java
 * @author Rickard(rijo1001), Lars(lama1203)
 * @date 2015-05-05
 * */
package battleship.game;

import static battleship.game.Constants.DEFAULT_ADDRESS;
import static battleship.game.Constants.DEFAULT_PORT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import battleship.network.ClientConnection;
import battleship.screen.AvatarPanel;
import battleship.screen.InputPanel;

/**
 * @class LoginDialog
 * @package battleship.game
 * @brief A dialog to choose a name, avatar, and GameMode
 * */
public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private Player player;
	private InputPanel nameInput;
	private AvatarPanel avatarChooser;
	private JPanel buttonPanel;
	private JPanel radioPanel;
	private JPanel centerPanel;
	private JRadioButton single, multi;
	private ButtonGroup group;
	private JButton cancel, clear, login;
	private ClientConnection connection;
	private Font font;
	private GameMode mode;
	private String ip;
	private int port;

	/**
	 * LoginDialog
	 * 
	 * @name LoginDialog
	 * @brief One argument constructor sets up interface of dialog
	 * @param player the player to instantiate based on user choice
	 * */
	public LoginDialog(Player player) {
		super();
		this.player = player;
		mode = GameMode.SinglePlayer;
		ip = DEFAULT_ADDRESS;
		port = DEFAULT_PORT;
		setLayout(new BorderLayout());
		font = new Font("Monospaced", Font.PLAIN, 12);
		nameInput = new InputPanel("Enter name: ", true);
		radioPanel = new JPanel(new GridLayout(1, 2));
		radioPanel.setBorder(BorderFactory.createTitledBorder(null, "Single- or multiplayer",TitledBorder.CENTER, TitledBorder.TOP, font, new Color(255, 255, 255)));
		radioPanel.setBackground(new Color(120, 100, 120));
		single = new JRadioButton("Select");
		single.setFont(font);
		single.setForeground(new Color(255, 255, 255));
		single.setBackground(new Color(90, 191, 7));
		single.setSelected(true);
		single.addActionListener(new PlayerModeListener());
		multi = new JRadioButton("Select");
		multi.setFont(font);
		multi.setForeground(new Color(255, 255, 255));
		multi.setBackground(new Color(80, 80, 80));
		multi.addActionListener(new PlayerModeListener());
		try {
			single.setIcon(new ImageIcon(ImageIO.read(new File("src/res/sprite/singleplayer.png"))));
			multi.setIcon(new ImageIcon(ImageIO.read(new File("src/res/sprite/multiplayer.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		group = new ButtonGroup();
		group.add(single); group.add(multi);
		radioPanel.add(single);
		radioPanel.add(multi);
		radioPanel.setBackground(new Color(0, 0, 0));
		avatarChooser = new AvatarPanel();
		buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(0, 0, 0));
		cancel = new JButton("Cancel");
		cancel.setBorderPainted(false);
		cancel.setBackground(new Color(194, 17, 22));
		cancel.setForeground(new Color(255, 255, 255));
		cancel.addActionListener(ae -> { dispose(); });
		clear = new JButton("Clear");
		clear.setBorderPainted(false);
		clear.setBackground(new Color(62, 60, 250));
		clear.setForeground(new Color(255, 255, 255));
		clear.addActionListener(ae -> { clear(); });
		login = new JButton("Login");
		login.setBorderPainted(false);
		login.setBackground(new Color(90, 191, 7));
		login.setForeground(new Color(255, 255, 255));
		login.addActionListener(ae -> { login(); } ); 
		buttonPanel.add(cancel);
		buttonPanel.add(clear);
		buttonPanel.add(login);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(new Color(0,0,0));
		centerPanel.add(radioPanel, BorderLayout.NORTH);
		centerPanel.add(avatarChooser, BorderLayout.CENTER);
		add(nameInput, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(230, 350);
		setTitle("Battleship login options");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		pack();
		
	}
	
	/**
	 * clear
	 * 
	 * @name clear
	 * @brief clears name input field and resets avatar
	 * */
	private void clear() {
		nameInput.setInput("");
		avatarChooser.reset();
	}
	
	/**
	 * close
	 * 
	 * @name close
	 * @brief set visibility false and disposes the dialog
	 * */
	private void close() {
		setVisible(false);
		dispose();
	}
	
	/**
	 * getIpAndPort
	 * 
	 * @name getIpAndPort
	 * @brief prompts user for input on ip-address and port
	 * */
	private void getIpAndPort() {
		ip = (String)JOptionPane.showInputDialog(
                this,
                "Enter ip",
                "Server Ip",
                JOptionPane.PLAIN_MESSAGE,
                null,
               null,
                DEFAULT_ADDRESS);
		String temp = (String)JOptionPane.showInputDialog(
                this,
                "Enter port",
                "Server Port",
                JOptionPane.PLAIN_MESSAGE,
                null,
               null,
                DEFAULT_PORT);
		port = Integer.parseInt(temp);
	}
	
	/**
	 * login
	 * 
	 * @name login
	 * @brief instantiate the player based on options of the user
	 * */
	private void login() {
		if (!nameInput.equals("")) {
			if(mode == GameMode.SinglePlayer) {
				connection = new ClientConnection(DEFAULT_ADDRESS, DEFAULT_PORT);
				if (connection.openConnection()) {
					player = new Player(nameInput.getInput(), avatarChooser.getAvatar(), connection, mode);
					player.init();
					if(player == null) System.exit(0);
					close();
				}
				System.out.println("You choose the single player mode");
			} else if (mode == GameMode.MultiPlayer) {
				getIpAndPort();
				connection = new ClientConnection(ip, port);
				if (connection.openConnection()) {
					player = new Player(nameInput.getInput(), avatarChooser.getAvatar(), connection, mode);
					player.init();
					if(player == null) System.exit(0);
					close();
				}
			}
		}
	}
	
	/**
	 * PlayerModeListener
	 * 
	 * @class PlayerModeListener
	 * @name PlayerModeListener
	 * @implements ActionListener
	 * @brief inner class that sets the GameMode and changes color to reflect the choice
	 * */
	class PlayerModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == single) {
				single.setBackground(new Color(90, 191, 7));
				multi.setBackground(new Color(80, 80, 80));
				mode = GameMode.SinglePlayer;
			} else if (e.getSource() == multi) {
				multi.setBackground(new Color(90, 191, 7));
				single.setBackground(new Color(80, 80, 80));
				mode = GameMode.MultiPlayer;
			}
		}
	}
}
