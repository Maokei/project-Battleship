/**
 * @file PlayerGUI.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import battleship.game.Message;
import battleship.game.Player;

/**
 * @class PlayerGUI
 * @extends JPanel
 * @package battleship.screen
 * */
public class PlayerGUI extends JPanel {
	private static final long serialVersionUID = 3152977875044360557L;
	private Player player;
	private JLabel nameLabel;
	private PlayerStats stats;
	private Avatar avatar;
	private ChatPanel chat;
	private JPanel buttonPanel;
	private JLabel readyLabel;
	private JLabel randomLabel;
	private ReadyButton ready;
	private JButton random;
	private GridBagConstraints gc;
	private boolean deployed = false;

	/**
	 * PlayerGUI Constructor
	 * @param Takes a player pointer to adapt GUI for.
	 * */
	public PlayerGUI(Player player) {
		super(new GridBagLayout());
		this.player = player;
		setPreferredSize(new Dimension(280, 360));
		setBackground(new Color(50, 50, 50));
		nameLabel = new JLabel(player.getName());
		nameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
		nameLabel.setForeground(new Color(255, 255, 255));
		stats = new PlayerStats();
		chat = new ChatPanel();
		chat.setPlayer(player);
		avatar = player.getAvatar();
		buttonPanel = new JPanel(new GridLayout(2, 2));
		buttonPanel.setBackground(new Color(30, 30, 30));
		readyLabel = new JLabel("All ships placed");
		readyLabel.setForeground(new Color(255, 255, 255));
		ready = new ReadyButton();
		ready.setBackground(new Color(15, 15, 15));
		ready.setEnabled(false);
		ready.addActionListener(ae -> {
			shipsPlaced();
		});

		randomLabel = new JLabel("Randomize ships");
		randomLabel.setForeground(new Color(255, 255, 255));
		random = new JButton("Random");
		random.setBackground(new Color(15, 15, 15));
		random.addActionListener(ae -> {
			randomizeShipPlacement();
		});

		gc = new GridBagConstraints();
		buttonPanel.add(readyLabel);
		buttonPanel.add(ready);
		buttonPanel.add(randomLabel);
		buttonPanel.add(random);

		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.insets = new Insets(10, 40, 10, 20);
		add(nameLabel);
		gc.insets = new Insets(10, 20, 10, 20);
		gc.gridy = 1;
		add(buttonPanel, gc);

		gc.gridwidth = 1;
		gc.insets = new Insets(0, 5, 0, 0);
		gc.gridy = 2;
		gc.ipadx = 100;
		gc.ipady = 70;
		add(chat, gc);

		gc.insets = new Insets(0, 0, 0, 5);
		gc.gridx = 1;
		gc.ipadx = 0;
		gc.ipady = 14;
		add(avatar, gc);

		gc.gridx = 0;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(10, 50, 10, 50);
		gc.gridy = 3;
		gc.gridwidth = 2;
		add(stats, gc);
	}

	/**
	 * randomizeShipPlacement
	 * @name randomizeShipPlacement
	 * @brief Initiate randomization of player ships.
	 * */
	private void randomizeShipPlacement() {
		player.randomizeShipPlacement();
		random.setEnabled(false);
	}

	/**
	 * shipsPlaced
	 * @name shipsPlaced
	 * @brief Set ship deployed state and sendout deployed message.
	 * */
	private void shipsPlaced() {
		if (!deployed) {
			player.sendMessage(new Message(Message.DEPLOYED, player.getName(), player.getOpponentName(),
					""));
			player.setDeployed();
			deployed = true;
			ready.stop();
		}
	}

	/**
	 * setShipsDeployed
	 * @name setShipsDeployed
	 * @brief set deployed state.
	 * */
	public void setShipsDeployed() {
		random.setEnabled(false);
		ready.setEnabled(true);
		ready.blink();
	}
	
	/**
	 * disableRandom
	 * @name disableRandom
	 * @brief set enabled state.
	 * */
	public void disableRandom() {
		random.setEnabled(false);
	}

	/**
	 * getStats
	 * @name getStats
	 * @return Returns player stats.
	 * */
	public PlayerStats getStats() {
		return stats;
	}

	/**
	 * @class ReadyButton
	 * @extends JButton
	 * @brief Class describes a ready button.
	 * */
	class ReadyButton extends JButton {
		private static final long serialVersionUID = 1L;
		private Blinker blink;
		BufferedImage img = null;
		private float alpha;
		private float level;
		private int delay;

		public ReadyButton() {
			super();
			setOpaque(false);
			alpha = 1.0f;
			level = -0.1f;
			delay = 100;
			try {
				img = ImageIO.read(new File("src/res/sprite/ready_40x20.png"));
			} catch (IOException e) {
				img = null;
			}
			setIcon(new ImageIcon(img));
			setBorderPainted(false);
			setContentAreaFilled(false);
			setBackground(new Color(15, 15, 15));
		}
		
		/**
		 * blink
		 * @name blink
		 * @brief Engage button blinking.
		 * */
		public void blink() {
			blink = new Blinker();
			blink.fadeInAndOut();
		}

		/**
		 * stop
		 * @name stop
		 * @brief Stops the button from blinking.
		 * */
		public void stop() {
			blink.stop();
			alpha = 1.0f;
			repaint();
		}

		/**
		 * pain
		 * @name paint
		 * @param Graphics context
		 * */
		@Override
		public void paint(Graphics g) {
			if (img == null || img.getWidth() != getWidth()
					|| img.getHeight() != getHeight()) {
				img = getGraphicsConfiguration().createCompatibleImage(
						getWidth(), getHeight());
			}
			Graphics g_img = img.getGraphics();
			g_img.setClip(g.getClip());
			super.paint(g_img);

			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
		}

		/**
		 * @class Blinker
		 * @brief Class blinks.
		 * */
		class Blinker {
			private Timer t;

			public void fadeInAndOut() {
				t = new Timer(delay, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						alpha += level;
						if (!checkBounds()) {
							changeDirection();
						}
						repaint();
					}
				});
				t.setCoalesce(true);
				t.start();
			}

			/**
			 * checkBounds
			 * @name checkBounds 
			 * @return check alpha bound true if bounds are valid.
			 * */
			private boolean checkBounds() {
				if (alpha < 0.0f) {
					alpha = 0.0f;
					return false;
				} else if (alpha > 1.0f) {
					alpha = 1.0f;
					return false;
				}
				return true;
			}
			
			/**
			 * changeDirection
			 * @name changeDirection
			 * */
			private void changeDirection() {
				level = -level;
			}
			
			/**
			 * stop
			 * @name stop 
			 * @brief Stops blinking.
			 * */
			public void stop() {
				t.stop();
				t = null;
			}
		}

	}
}