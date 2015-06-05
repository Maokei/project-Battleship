/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.game;

//include constants
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
 * @brief Class represent a player human battleship player, class also contains
 *        many of the core gameplay mechanics.
 * @implements BattlePlayer interface
 * */
public class Player {
	private String name;
	private Avatar avatar;
	private Screen screen;
	private ClientConnection con;
	private GameMode mode;
	private Gameboard playerBoard, enemyBoard;
	private Vector<Ship> playerShips;
	private Alignment alignment = Alignment.HORIZONTAL;
	private Toolkit toolkit;
	private Image cursorImg;
	private Cursor cursor;
	private int shipPlacementIndex;
	private int remainingShips;
	private int hits, misses;
	private boolean opponentDeployed = false;
	private boolean deployed = false;
	private boolean playerTurn = false;
	private boolean hasOpponent = false;
	private String opponentName = "";

	/**
	 * Player
	 * 
	 * @brief Player constructor
	 * @param String
	 *            player name, Avatar object player picture, ClientConnection
	 *            for talk to server.
	 * */
	public Player(String name, Avatar avatar, ClientConnection con,
			GameMode mode) {
		this.name = name;
		this.avatar = avatar;
		this.con = con;
		this.mode = mode;
		con.setPlayer(this);
	}

	/**
	 * init
	 * 
	 * @name init
	 * @brief Player class initiation function.
	 * @return void function
	 * */
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
		sendMessage(new Message(Message.LOGIN, name, "", getGameMode()));
	}

	/**
	 * listen
	 * 
	 * @name listen
	 * @brief Start a new thread to listen for incoming events with
	 *        ClientConnection.
	 * */

	public void listen() {
		new Thread(con).start();
	}

	/**
	 * sendMessage
	 * 
	 * @name sendMessage
	 * @param Message
	 *            object to be sent to server.
	 * @return void function.
	 * */
	public void sendMessage(Message message) {
		con.sendMessage(message);
	}

	/**
	 * getConnection
	 * 
	 * @name getConnection
	 * @return ClientConnection pointer.
	 * */
	public ClientConnection getConnection() {
		return con;
	}

	/**
	 * getName
	 * 
	 * @name getName
	 * @return String object player name.
	 **/
	public String getName() {
		return name;
	}

	/**
	 * getAvatar
	 * 
	 * @name getAvatar
	 * @return avatar pointer used by player.
	 * */
	public Avatar getAvatar() {
		return avatar;
	}

	/**
	 * getGameMode
	 * 
	 * @name getGameMode
	 * @return GameMode enum.
	 * */
	public String getGameMode() {
		return mode.getMode();
	}

	/**
	 * getConnectedPlayers
	 * 
	 * @name getConnectedPlayers
	 * @param ArrayList
	 *            <String> of connected players.
	 * */
	public ArrayList<String> getConnectedPlayers() {
		return con.getConnectedPlayers();
	}

	/**
	 * checkHit
	 * 
	 * @name checkHit
	 * @brief Check for a hit in given position.
	 * @param Integer
	 *            row and integer column in grid.
	 * @true True for hit false for no hit.
	 * */
	public boolean checkHit(int row, int col) {
		return playerBoard.checkHit(row, col);
	}

	/**
	 * setOpponentName
	 * 
	 * @name setOpponent
	 * @param Takes
	 *            an opponent as a string name
	 * */
	public void setOpponentName(String opponent) {
		this.opponentName = opponent;
	}

	/**
	 * getOpponentName
	 * 
	 * @name getOpponentName
	 * @return returns opponent name as a string.
	 * */
	public String getOpponentName() {
		return opponentName;
	}

	public void setHasOpponent(boolean hasOpponent) {
		this.hasOpponent = hasOpponent;
	}

	public boolean getHasOpponent() {
		return hasOpponent;
	}

	public void checkFire(int row, int col) {
		if (checkHit(row, col)) {
			sendMessage(new Message(Message.VALID, name, "", "HIT "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		} else {
			sendMessage(new Message(Message.VALID, name, "", "MISS "
					+ Integer.toString(row) + " " + Integer.toString(col)));
		}
	}

	/**
	 * registerFire
	 * 
	 * @name registerFire
	 * @brief register a shot on the grid.
	 * @param intger
	 *            row and integer column in a grid.
	 * */
	/*
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
	*/
	/**
	 * registerPlayerHit
	 * 
	 * @name registerPlayerHit
	 * @param integer
	 *            row and integer column in grid.
	 * */
	public void registerPlayerHit(int row, int col) {
		AudioLoader.getAudio("explosion1").playAudio();
		enemyBoard.addHit(row, col);
		screen.setHits(++hits);
		playerTurn = true;
	}

	/**
	 * registerEnemyHit
	 * 
	 * @name registerEnemyHit
	 * @brief Function register and enemy hit on a grid.
	 * @param Ship
	 *            object, integer row and integer column.
	 * @return void function.
	 * */
	public void registerEnemyHit(int row, int col) {
		for (Ship ship : playerShips) {
			if (ship.isAlive() && ship.checkHit(row, col)) {
				AudioLoader.getAudio("explosion1").playAudio();
				playerBoard.addHit(row, col);
				if (mode == GameMode.MultiPlayer) {
					sendMessage(new Message(Message.MESSAGE, getName(),
							getOpponentName(), "HIT " + Integer.toString(row) + " "
									+ Integer.toString(col)));
				}
				
				ship.hit();
				if (!ship.isAlive()) {
					sinkShip(ship);
					screen.setShips(--remainingShips);
					if (remainingShips == 0)
						battleLost();
				}

				if (mode == GameMode.SinglePlayer) {
					sendMessage(new Message(Message.MESSAGE, getName(),
							getOpponentName(), "HIT " + Integer.toString(row) + " "
									+ Integer.toString(col)));
				}
			}
		}
	}

	/**
	 * sinkShip
	 * 
	 * @name sinkShip
	 * @param Takes
	 *            a ship pointer for a ship to be sunk.
	 * */
	public void sinkShip(Ship ship) {
		AudioLoader.getAudio("tilt").playAudio();
		int row = ship.getStartPosition().getRow();
		int col = ship.getStartPosition().getCol();

		sendMessage(new Message(Message.MESSAGE, getName(), getOpponentName(),
				"SHIP_DOWN " + ship.getType() + " " + ship.getAlignment() + " "
						+ Integer.toString(row) + " " + Integer.toString(col)));

		playerBoard.placeShip(ship, row, col);
		for (Grid pos : ship.getPosition()) {
			playerBoard.fadeGridOut(pos.getRow(), pos.getCol());
		}
	}

	/**
	 * registerPlayerMiss
	 * 
	 * @name registerPlayerMiss
	 * @brief register a miss by player.
	 * @param interger
	 *            row and integer column.
	 * */
	public void registerPlayerMiss(int row, int col) {
		AudioLoader.getAudio("splash1").playAudio();
		sendMessage(new Message(Message.TURN, getName(), getOpponentName(), ""));
		screen.setMessage("Wait for your turn");
		enemyBoard.addMiss(row, col);
		screen.setMisses(++misses);
		playerTurn = false;
	}

	/**
	 * registerEnemyMiss
	 * 
	 * @name registerEnemyMiss
	 * @brief registers a miss by the enemy.
	 * @param integer
	 *            row and integer column on grid.
	 * */
	public void registerEnemyMiss(int row, int col) {
		AudioLoader.getAudio("splash1").playAudio();
		sendMessage(new Message(Message.MESSAGE, getName(), getOpponentName(),
				"MISS " + Integer.toString(row) + " " + Integer.toString(col)));
		playerBoard.addMiss(row, col);

	}

	/**
	 * placeEnemyShip
	 * 
	 * @name placeEnemyShip
	 * @brief register an enemy ship on enemy grid to be drawn.
	 * @param Ship
	 *            object, integer row and integer column.
	 * */
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

	/**
	 * randomizeShipPlacement
	 * 
	 * @name randomizeShipPlacement
	 * @brief meta function to start randomization of ships
	 * @return void function
	 **/
	public void randomizeShipPlacement() {
		shipPlacementIndex = playerShips.size();
		screen.setShipsDeployed();
		screen.setMessage("Press Ready Button");
		playerShips = new RandomShipPlacer().getRandomShips();
		playerBoard.randomizeShipPlacement(playerShips);
	}

	/**
	 * setRunning
	 * 
	 * @name setRunning
	 * @param set
	 *            running state clientConnection takes boolean value.
	 * */
	public void setRunning(boolean running) {
		con.setRunning(running);
	}

	/**
	 * setOpponentDeployed
	 * 
	 * @name setOpponentDeployed
	 * @brief set opponent deployed state true.
	 * */
	public void setOpponentDeployed() {
		opponentDeployed = true;
	}

	/**
	 * setDeployed
	 * 
	 * @name setDeployed
	 * @brief setDeployed state true and appropriate screen message for player.
	 * */
	public void setDeployed() {
		deployed = true;
		if (!(opponentDeployed || playerTurn)) {
			screen.setMessage("Wait for opponent");
		} else {
			screen.setMessage("Fire at will!!");
		}
	}

	/**
	 * setPlayerTurn
	 * 
	 * @name setPlayerTurn
	 * @brief set turn state and appropriate screen message.
	 * @param boolean value to set player turn state.
	 * */
	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
		screen.setMessage("Fire at will!!");
	}

	/**
	 * battleWon
	 * 
	 * @name battleWon
	 * @brief stop turns and display victory message.
	 * */
	public void battleWon() {
		playerTurn = false;
		screen.setMessage("You are Victorious!!!");
		playerBoard.displayVictory();
	}

	/**
	 * battleLost
	 * 
	 * @name battleLost
	 * @brief player lost sound and stop turn, display battle lost message.
	 * */
	public void battleLost() {
		AudioLoader.getAudio("march").setLoop(true).playAudio();
		sendMessage(new Message(Message.LOST, getName(), getOpponentName(), ""));
		screen.setMessage("You sir, are a DISGRACE!!");
		playerBoard.displayDefeat();
		playerTurn = false;
	}

	/**
	 * GameTimer
	 * 
	 * @class GameTimer
	 * @brief And inner help class to help with timing of events.
	 * @param integer
	 *            seconds and integer amount of delay.
	 * */
	class GameTimer {
		private Timer t;
		private int seconds;
		private final int delay;

		public GameTimer(int seconds, int delay) {
			this.seconds = seconds;
			this.delay = delay;
		}

		/**
		 * run
		 * 
		 * @name run
		 * @brief start's thread
		 * */
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

		/**
		 * checkTime
		 * 
		 * @name checkTime
		 * @brief if no time left stop.
		 * */
		private void checkTime() {
			if (seconds <= 0) {
				t.stop();
				t = null;
			}
		}
	}

	public void placePlayerShip(int row, int col) {
		screen.setMessage("Deploy your ships");
		if (shipPlacementIndex == 0) {
			screen.disableRandom();
		}
		Ship ship = playerShips.elementAt(shipPlacementIndex);
		ship.setAlignment(alignment);

		if (playerBoard.checkShipPlacement(ship, row, col)) {
			playerBoard.placeShip(ship, row, col);
			shipPlacementIndex++;

			if (shipPlacementIndex == playerShips.size()) {
				screen.setShipsDeployed();
				screen.setMessage("Press Ready Button");
			}
		}

	}

	/**
	 * BoardListener
	 * 
	 * @class BoardListener
	 * @extends MouseAdapter
	 * @brief Event listener class for a player board.
	 * */
	class BoardListener extends MouseAdapter {

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
				} else {
					if (shipPlacementIndex < playerShips.size()) {
						Ship ship = playerShips.elementAt(shipPlacementIndex);
						ship.setAlignment(alignment);

						sendMessage(new Message(Message.VALID, name,
								getOpponentName(), "PLACING " + ship.getType()
										+ " " + ship.getAlignment() + " "
										+ Integer.toString(row) + " "
										+ Integer.toString(col)));
					}
					// placePlayerShip(row, col);
				}
			} else if (e.getComponent() == enemyBoard) {
				if(enemyBoard.checkFire(row, col))
					fire(row, col);
			}
		}

		/**
		 * fire
		 * 
		 * @name fire
		 * @param Integer
		 *            row and integer column, sends out fire message.
		 * */
		private void fire(int row, int col) {
			if (checkDeployed() && playerTurn) {
				sendMessage(new Message(Message.MESSAGE, getName(),
						getOpponentName(), "FIRE " + Integer.toString(row)
								+ " " + Integer.toString(col)));
				playerTurn = false;
			}
		}

		private boolean checkDeployed() {
			if (mode == GameMode.MultiPlayer) {
				return (deployed && opponentDeployed);
			} else {
				return deployed;
			}
		}
	}

	/**
	 * handleChallange
	 * 
	 * @name handleChallange
	 * @param handle
	 *            a challange message given String sender name and String
	 *            message.
	 * */
	public void handleChallenge(Message msg) {
		String title = "", msgText = "";
		int reply = -1;
		if (msg.getMessage().equalsIgnoreCase(Challenge_Request)) {
			msgText = msg.getSender()
					+ " has sent a game invitation to you\n\nDo you accept the challenge?";
			title = "CHALLENGE REQUEST";
			reply = JOptionPane.showConfirmDialog(null, msgText, title,
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				opponentName = msg.getSender();
				hasOpponent = true;
				sendMessage(new Message(Message.CHALLENGE, getName(),
						getOpponentName(), Challenge_Accept));
			} else {
				sendMessage(new Message(Message.CHALLENGE, getName(),
						msg.getSender(), Challenge_Deny));
			}
		} else if (msg.getMessage().equalsIgnoreCase(Challenge_Accept)) {
			msgText = msg.getSender()
					+ " has accepted your your invitation\n\nGood Luck!.";
			title = "CHALLENGE ACCEPT";
			JOptionPane.showMessageDialog(null, msgText, title,
					JOptionPane.INFORMATION_MESSAGE);
			opponentName = msg.getSender();
		} else if (msg.getMessage().equalsIgnoreCase(Challenge_Deny)) {
			msgText = msg.getSender()
					+ " has denied your request.\n\nMaybe he is busy.";
			title = "CHALLENGE DENIED";
			JOptionPane.showMessageDialog(null, msgText, title,
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	/**
	 * handleAIMatch
	 * 
	 * @name handleAIMatch
	 * @brief JOptionPane query player about playing against server instead of
	 *        waiting for a player.
	 * */
	public void handleAIMatch() {
		String msgText = "There are no available players at this time\n\nDo you want to play singleplayer?";
		String title = "No Available Players";
		int reply = JOptionPane.showConfirmDialog(null, msgText, title,
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			sendMessage(new Message(Message.MODE, getName(), getOpponentName(),
					"SinglePlayer"));
		} else {
			sendMessage(new Message(Message.CHALLENGE, getName(),
					getOpponentName(), Challenge_Deny));
		}
	}

	public void handleNonValidMove(Message msg) {
		System.out.println("Handling NonValidMove");
		String[] tokens = msg.getMessage().split(" ");
		int row, col;
		switch (tokens[0].toUpperCase()) {
		case "HIT":
			row = Integer.parseInt(tokens[1]);
			col = Integer.parseInt(tokens[2]);
			screen.setMessage("Enemy Missed an illegal shot at " + row + "," + col);
			break;
		case "MISS":
			row = Integer.parseInt(tokens[1]);
			col = Integer.parseInt(tokens[2]);
			screen.setMessage("Enemy Missed an illegal shot at " + row + "," + col);
			break;
		case "PLACING": 
			screen.setMessage("It's a little crowded, don't ya think!!");
			break;
		}
	}
}
