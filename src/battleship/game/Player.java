/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.game;

import static battleship.game.Constants.*;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import battleship.gameboard.Gameboard;
import battleship.gameboard.Grid;
import battleship.network.ClientConnection;
import battleship.network.RandomShipPlacer;
import battleship.resources.AudioLoader;
import battleship.screen.Avatar;
import battleship.screen.Lobby;
import battleship.screen.Screen;
import battleship.ships.Alignment;
import battleship.ships.Ship;
import battleship.ships.ShipBuilder;

/**
 * @package battleship.entity
 * @Class Player
 * @brief Class represent a player human or non-human,
 * */
public class Player implements BattlePlayer {
	private String name;
	private Avatar avatar;
	private Screen screen;
	private ClientConnection con;
	private GameMode mode;
	private Gameboard playerBoard, enemyBoard;
	private Vector<Ship> playerShips;
	private Toolkit toolkit;
	private Image cursorImg;
	private Cursor cursor;
	private int shipPlacementIndex;
	private int remainingShips;
	private int hits, misses;
	private boolean opponentDeployed = false;
	private boolean deployed = false;
	private boolean playerTurn = false;
	private String opponent;

	public Player(String name, Avatar avatar, ClientConnection con,
			GameMode mode) {
		this.name = name;
		this.avatar = avatar;
		this.con = con;
		this.mode = mode;
		con.setBattlePlayer(this);
	}

	public void init() {
		hits = misses = shipPlacementIndex = 0;
		playerBoard = new Gameboard();
		enemyBoard = new Gameboard();
		playerBoard.addMouseListener(new BoardListener());
		enemyBoard.addMouseListener(new BoardListener());

		// toolkit = Toolkit.getDefaultToolkit();
		// cursorImg = toolkit.getImage("src/res/sprite/crosshair.png");
		// cursor = toolkit.createCustomCursor(cursorImg, new Point(0, 0), "");
		// enemyBoard.setCursor(cursor);
		screen = new Screen(this, playerBoard, enemyBoard);
		playerShips = ShipBuilder.buildShips();
		remainingShips = 9;
		screen.showGUI();
		AudioLoader.getAudio("ocean1").setLoop(true).playAudio();
		listen();
	}

	@Override
	public void listen() {
		new Thread(con).start();
	}

	public void sendMessage(Message message) {
		con.sendMessage(message);
	}

	public ClientConnection getConnection() {
		return con;
	}

	public String getName() {
		return name;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public String getGameMode() {
		return mode.getMode();
	}

	public ArrayList<String> getConnectedPlayers() {
		return con.getConnectedPlayers();
	}

	public boolean checkHit(int row, int col) {
		return playerBoard.checkHit(row, col);
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public String getOpponent() {
		return opponent;
	}

	/**
	 * register a shot on the grid
	 * */
	public void registerFire(int row, int col) {
		if (checkHit(row, col)) {
			for (Ship ship : playerShips) {
				if (ship.isAlive() && ship.checkHit(row, col)) {
					registerEnemyHit(ship, row, col);
				}
			}
		} else {
			registerEnemyMiss(row, col);
		}
	}

	public void registerPlayerHit(int row, int col) {
		AudioLoader.getAudio("explosion1").playAudio();
		enemyBoard.addHit(row, col);
		screen.setHits(++hits);
		playerTurn = true;
	}

	public void registerEnemyHit(Ship ship, int row, int col) {
		AudioLoader.getAudio("explosion1").playAudio();
		playerBoard.addHit(row, col);
		ship.hit();
		if (!ship.isAlive()) {
			sinkShip(ship);
			screen.setShips(--remainingShips);
		}
		sendMessage(new Message(Message.MESSAGE, name, "HIT "
				+ Integer.toString(row) + " " + Integer.toString(col)));
		if (remainingShips == 0)
			battleLost();
	}

	public void sinkShip(Ship ship) {
		AudioLoader.getAudio("tilt").playAudio();
		int row = ship.getStartPosition().getRow();
		int col = ship.getStartPosition().getCol();

		sendMessage(new Message(Message.MESSAGE, name, "SHIP_DOWN "
				+ ship.getType() + " " + ship.getAlignment() + " "
				+ Integer.toString(row) + " " + Integer.toString(col)));

		playerBoard.placeShip(ship, row, col);
		for (Grid pos : ship.getPosition()) {
			playerBoard.fadeGridOut(pos.getRow(), pos.getCol());
		}
	}

	public void registerPlayerMiss(int row, int col) {
		AudioLoader.getAudio("splash1").playAudio();
		sendMessage(new Message(Message.TURN, name, ""));
		screen.setMessage("Wait for your turn");
		enemyBoard.addMiss(row, col);
		screen.setMisses(++misses);
		playerTurn = false;
	}

	public void registerEnemyMiss(int row, int col) {
		AudioLoader.getAudio("splash1").playAudio();
		sendMessage(new Message(Message.MESSAGE, name, "MISS "
				+ Integer.toString(row) + " " + Integer.toString(col)));
		playerBoard.addMiss(row, col);
	}

	public void placeEnemyShip(Ship ship, int row, int col) {
		AudioLoader.getAudio("ship_down").playAudio();
		System.out.println("Placing enemy ship:\n");
		System.out.println(ship.getType() + " " + ship.getAlignment()
				+ " Grid[" + row + ", " + col + "]");
		enemyBoard.placeShip(ship, row, col);
		for (Grid pos : ship.getPosition()) {
			enemyBoard.fadeGridIn(pos.getRow(), pos.getCol());
		}
	}

	public void randomizeShipPlacement() {
		shipPlacementIndex = playerShips.size();
		screen.setShipsDeployed();
		screen.setMessage("Press Ready Button");
		playerShips = new RandomShipPlacer().getRandomShips();
		playerBoard.randomizeShipPlacement(playerShips);
	}

	public void setRunning(boolean running) {
		con.setRunning(running);
	}

	public void setOpponentDeployed() {
		opponentDeployed = true;
	}

	public void setDeployed() {
		deployed = true;
		if (!(opponentDeployed || playerTurn)) {
			screen.setMessage("Wait for opponent");
		} else {
			screen.setMessage("Fire at will!!");
		}
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
		screen.setMessage("Fire at will!!");
	}

	public void battleWon() {
		playerTurn = false;
		screen.setMessage("You are Victorious!!!");
		playerBoard.displayVictory();
	}

	public void battleLost() {
		AudioLoader.getAudio("march").setLoop(true).playAudio();
		sendMessage(new Message(Message.LOST, name, ""));
		screen.setMessage("You sir, are a DISGRACE!!");
		playerBoard.displayDefeat();
		playerTurn = false;
	}

	class GameTimer {
		private Timer t;
		private int seconds;
		private final int delay;

		public GameTimer(int seconds, int delay) {
			this.seconds = seconds;
			this.delay = delay;
		}

		public void run() {
			t = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					--seconds;
					screen.setMessage("AI incoming projectile in " + seconds);
					checkTime();
				}
			});
			t.start();
		}

		private void checkTime() {
			if (seconds <= 0) {
				t.stop();
				t = null;
			}
		}
	}

	class BoardListener extends MouseAdapter {
		Alignment alignment = Alignment.HORIZONTAL;

		@Override
		public void mousePressed(MouseEvent e) {
			int row = e.getY() / GRID_SIZE;
			int col = e.getX() / GRID_SIZE;

			if (e.getComponent() == playerBoard) {
				if (SwingUtilities.isRightMouseButton(e)) { // change alignment
					if (alignment == Alignment.HORIZONTAL)
						alignment = Alignment.VERTICAL;
					else
						alignment = Alignment.HORIZONTAL;
				} else { // place ship

					placePlayerShip(row, col);
				}
			} else if (e.getComponent() == enemyBoard) {
				fire(row, col);
			}
		}

		private void placePlayerShip(int row, int col) {
			if (shipPlacementIndex == 0) {
				screen.disableRandom();
			}

			if (shipPlacementIndex < playerShips.size()) {
				Ship ship = playerShips.elementAt(shipPlacementIndex);
				ship.setAlignment(alignment);

				sendMessage(new Message(Message.MESSAGE, name, "PLACING "
						+ ship.getType() + " " + ship.getAlignment() + " "
						+ Integer.toString(row) + " " + Integer.toString(col)));

				if (playerBoard.checkShipPlacement(ship, row, col)) {
					playerBoard.placeShip(ship, row, col);
					shipPlacementIndex++;

					if (shipPlacementIndex == playerShips.size()) {
						screen.setShipsDeployed();
						screen.setMessage("Press Ready Button");
					}
				}
			}
		}

		private void fire(int row, int col) {
			if (deployed && opponentDeployed && playerTurn) {
				if(enemyBoard.checkFire(row, col)) {
				sendMessage(new Message(Message.MESSAGE, name, "FIRE "
						+ Integer.toString(row) + " " + Integer.toString(col)));
				playerTurn = false;
				}
			}
		}
	}

	@Override
	public void handleChallenge(String sender, String message) {
		String title = "", msgText = "";
		int reply = -1;
		if (message.equalsIgnoreCase(Challenge_Request)) {
			msgText = sender
					+ " want's to battle with you\n\nDo you accept the challenge?";
			title = "CHALLENGE REQUEST";
			reply = JOptionPane.showConfirmDialog(null, msgText, title,
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				opponent = sender;
				sendMessage(new Message(Message.CHALLENGE, name + " "
						+ opponent, Challenge_Accept));
			} else {
				sendMessage(new Message(Message.CHALLENGE, name + " "
						+ opponent, Challenge_Deny));
			}

		} else if (message.equalsIgnoreCase(Challenge_Accept)) {
			msgText = sender + " has accepted your request.";
			title = "CHALLENGE ACCEPT";
			JOptionPane.showMessageDialog(null, msgText, title,
					JOptionPane.INFORMATION_MESSAGE);
			opponent = sender;
		} else if (message.equalsIgnoreCase(Challenge_Deny)) {
			msgText = sender + " has denied your request.";
			title = "CHALLENGE ACCEPT";
			JOptionPane.showMessageDialog(null, msgText, title,
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	@Override
	public void handleAIMatch() {
		String msgText = "There are no available players at this time\n\nDo you want to play singleplayer?";
		String title = "No Available Players";
		int reply = JOptionPane.showConfirmDialog(null, msgText, title,
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			sendMessage(new Message(Message.MODE, name, "SinglePlayer"));
		} else {
			sendMessage(new Message(Message.CHALLENGE, name + " " + opponent,
					Challenge_Deny));
		}
	}

}
