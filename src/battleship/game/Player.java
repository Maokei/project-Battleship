/**
 * @file Player.java
 * @date 2015-05-05
 * @author Rickard(rijo1001), Lars(lama1203)
 * */
package battleship.game;


import static battleship.game.Constants.*;


//import constants

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import battleship.gameboard.Gameboard;
import battleship.gameboard.Grid;
import battleship.network.AIPlayer;
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
 * @brief Class represent a player human battleship player, class also contains many of the core gameplay mechanics.
 * @implements BattlePlayer interface
 * */
public class Player implements BattlePlayer{
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
	private String opponent, server;
	private ArrayList<String> playersConnected;
	private boolean challengeAccepted;

	/**
	 * Player
	 * @brief Player constructor
	 * @param String player name, Avatar object player picture, ClientConnection for talk to server.
	 * */
	public Player(String name, Avatar avatar, ClientConnection con,
			GameMode mode) {
		this.name = name;
		this.avatar = avatar;
		this.con = con;
		this.mode = mode;
		con.setBattlePlayer(this);
	}
	
	/**
	 * Player
	 * @brief Player constructor
	 * @param String player name, ClientConnection to talk to server and handle messages. 
	 * */
	public Player(String name, ClientConnection con) {
		this.name = name;
		this.con = con;
		this.con.setBattlePlayer(this);
		playersConnected = new ArrayList<String>();
		server = "Server";
		listen();
		sendMessage(new Message(Message.LOGIN, name, server, ""));
		new LoginDialog(this);
	}

	/**
	 * init
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
		server = "Server";
		//toolkit = Toolkit.getDefaultToolkit();
		//cursorImg = toolkit.getImage("src/res/sprite/crosshair.png");
		//cursor = toolkit.createCustomCursor(cursorImg, new Point(0, 0), "");
		//enemyBoard.setCursor(cursor);
		screen = new Screen(this, playerBoard, enemyBoard);
		playerShips = ShipBuilder.buildShips();
		remainingShips = 9;
		screen.showGUI();
		AudioLoader.getAudio("ocean1").setLoop(true).playAudio();
		listen();
	}
	
	/**
	 * listen
	 * @name listen
	 * @brief Start a new thread to listen for incoming events with ClientConnection.
	 * */
	@Override
	public void listen() {
		new Thread(con).start();
	}

	/**
	 * sendMessage
	 * @name sendMessage
	 * @param Message object to be sent to server.
	 * @return void function.
	 * */
	public void sendMessage(Message message) {
		con.sendMessage(message);
	}
	
	/**
	 * setConnection
	 * @name setConnection
	 * @param ClientConnection object, to set clientConnection.
	 * */
	public void setConnection(ClientConnection con) {
		this.con = con;
	}

	
	/**
	 * getConnection
	 * @name getConnection
	 * @return ClientConnection pointer.
	 * */
	public ClientConnection getConnection() {
		return con;
	}

	/**
	 * getName
	 * @name getName
	 * @return String object player name.
	 **/
	public String getName() {
		return name;
	}
	
	/**
	 * getopponent
	 * @name getOpponent
	 * @return String object opponent name;
	 * */
	public String getOpponent() {
		return opponent;
	}

	/**
	 * getAvatar 
	 * @name getAvatar
	 * @return avatar pointer used by player.
	 * */
	public Avatar getAvatar() {
		return avatar;
	}

	/**
	 * getGameMode
	 * @name getGameMode
	 * @return GameMode enum.
	 * */
	public String getGameMode() {
		return mode.getMode();
	}

	/**
	 * checkHit
	 * @name checkHit
	 * @brief Check for a hit in given position.
	 * @param Integer row and integer column in grid.
	 * @true True for hit false for no hit.
	 * */
	public boolean checkHit(int row, int col) {
		return playerBoard.checkHit(row, col);
	}
	
	/**
	 * setPlatersConnected 
	 * @name setPlayersConnected
	 * @param Array of strings of players connected to the server.
	 * @return void function.
	 * */
	public void setPlayersConnected(ArrayList<String> playersConnected) {
		this.playersConnected = playersConnected;
	}
	
	/**
	 * getConnectedPlayers
	 * @name getConnectedPlayers
	 * @param ArrayList<String> of connected players.
	 * */
	public ArrayList<String> getConnectedPlayers() {
		return playersConnected;
	}

	/**
	 * registerFire
	 * @name registerFire
	 * @brief register a shot on the grid.
	 * @param intger row and integer column in a grid.
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

	/**
	 * registerPlayerHit
	 * @name registerPlayerHit
	 * @param integer row and integer column in grid.
	 * */
	public void registerPlayerHit(int row, int col) {
		AudioLoader.getAudio("explosion1").playAudio();
		enemyBoard.addHit(row, col);
		screen.setHits(++hits);
	}

	/**
	 * registerEnemyHit
	 * @name registerEnemyHit
	 * @brief Function register and enemy hit on a grid.
	 * @param Ship object, integer row and integer column.
	 * @return void function.
	 * */
	public void registerEnemyHit(Ship ship, int row, int col) {
		AudioLoader.getAudio("explosion1").playAudio();
		playerBoard.addHit(row, col);
		ship.hit();
		if (!ship.isAlive()) {
			sinkShip(ship);
			screen.setShips(--remainingShips);
		}
		sendMessage(new Message(Message.MESSAGE, name, opponent, "HIT "
				+ Integer.toString(row) + " " + Integer.toString(col)));
		if (remainingShips == 0)
			battleLost();
	}

	/**
	 * sikShip
	 * @name sinkShip
	 * @param Takes a ship pointer for a ship to be sunk.
	 * */
	public void sinkShip(Ship ship) {
		AudioLoader.getAudio("tilt").playAudio();
		int row = ship.getStartPosition().getRow();
		int col = ship.getStartPosition().getCol();

		sendMessage(new Message(Message.MESSAGE, name, opponent, "SHIP_DOWN "
				+ ship.getType() + " " + ship.getAlignment() + " "
				+ Integer.toString(row) + " " + Integer.toString(col)));

		playerBoard.placeShip(ship, row, col);
		for (Grid pos : ship.getPosition()) {
			playerBoard.fadeGridOut(pos.getRow(), pos.getCol());
		}
	}

	/**
	 * registerPlayerMiss
	 * @name registerPlayerMiss
	 * @brief register a miss by player.
	 * @param interger row and integer column.
	 * */
	public void registerPlayerMiss(int row, int col) {
		//play sound
		AudioLoader.getAudio("splash1").playAudio();
		//sends out message
		sendMessage(new Message(Message.TURN, opponent, name, ""));
		screen.setMessage("Wait for your turn");
		enemyBoard.addMiss(row, col);
		screen.setMisses(++misses);
		playerTurn = false;
	}

	/**
	 * registerEnemyMiss
	 * @name registerEnemyMiss
	 * @brief registers a miss by the enemy.
	 * @param integer row and integer column on grid.
	 * */
	public void registerEnemyMiss(int row, int col) {
		AudioLoader.getAudio("splash1").playAudio();
		sendMessage(new Message(Message.MESSAGE, name, opponent, "MISS "
				+ Integer.toString(row) + " " + Integer.toString(col)));
		playerBoard.addMiss(row, col);
	}

	/**
	 * placeEnemyShip
	 * @name placeEnemyShip
	 * @brief register an enemy ship on enemy grid to be drawn.
	 * @param Ship object, integer row and integer column.
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
	 * @name randomizeShipPlacement
	 * @brief  meta function to start randomization of ships
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
	 * @name setRunning
	 * @param set running state clientConnection takes boolean value.
	 * */
	public void setRunning(boolean running) {
		con.setRunning(running);
	}

	/**
	 * setOpponentDeployed
	 * @name setOpponentDeployed
	 * @brief set opponent deployed state true.
	 * */
	public void setOpponentDeployed() {
		opponentDeployed = true;
	}

	/**
	 * setDeployed
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
	 * @name battleLost
	 * @brief player lost sound and stop turn, display battle lost message.
	 * */
	public void battleLost() {
		AudioLoader.getAudio("march").setLoop(true).playAudio();
		sendMessage(new Message(Message.LOST, opponent, name, ""));
		screen.setMessage("You sir, are a DISGRACE!!");
		playerBoard.displayDefeat();
		playerTurn = false;
	}
	
	/**
	 * GameTimer
	 * @class GameTimer
	 * @brief And inner help class to help with timing of events.
	 * @param integer seconds and integer amount of delay.
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
		 * @name run
		 * @brief start's thread
		 * */
		public void run() {
			t = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					--seconds;
					screen.setMessage("AI incoming projectile in "+ seconds);
					checkTime();
				}
			});
			t.start();
		}

		/**
		 * checkTime
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

	/**
	 * BoardListener
	 * @class BoardListener
	 * @extends MouseAdapter
	 * @brief Event listener class for a player board.
	 * */
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
			if(shipPlacementIndex == 0) { screen.disableRandom(); }
			
			if (shipPlacementIndex < playerShips.size()) {
				Ship ship = playerShips.elementAt(shipPlacementIndex);
				ship.setAlignment(alignment);

				sendMessage(new Message(Message.MESSAGE, name, opponent, "PLACING "
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
				System.out.println("Firing");
				sendMessage(new Message(Message.MESSAGE, name, opponent, "FIRE "
						+ Integer.toString(row) + " " + Integer.toString(col)));
			}
		}
	}

	/**
	 * setAvatar 
	 * @name setAvatar
	 * @param Avatr pointer ,set avatar to be used.
	 * */
	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	/**
	 * setMode
	 * @name setMode
	 * @param GameMode to be played.
	 * */
	public void setMode(GameMode mode) {
		this.mode = mode;
	}

	/**
	 * startGame
	 * @name startGame
	 * @brief start a single player match.
	 * */
	public void startGame() {
		if(mode == GameMode.SinglePlayer) {
			sendMessage(new Message(Message.MODE, name, "Server", "SinglePlayer"));
			initPlayer();
		} else if(mode == GameMode.MultiPlayer) {
			sendMessage(new Message(Message.MODE, name, opponent, Challenge_Request));
			initPlayer();
		}
	}
	
	/**
	 * initPlayer
	 * @name initPlayer
	 * @brief initialization function for player class. Add listeners and show gui.
	 * */
	private void initPlayer() {
		System.out.println("In initPlayer");
		hits = misses = shipPlacementIndex = 0;
		playerBoard = new Gameboard();
		enemyBoard = new Gameboard();
		playerBoard.addMouseListener(new BoardListener());
		enemyBoard.addMouseListener(new BoardListener());
		server = "Server";
		challengeAccepted = false;
		//toolkit = Toolkit.getDefaultToolkit();
		//cursorImg = toolkit.getImage("src/res/sprite/crosshair.png");
		//cursor = toolkit.createCustomCursor(cursorImg, new Point(0, 0), "");
		//enemyBoard.setCursor(cursor);S
		screen = new Screen(this, playerBoard, enemyBoard);
		playerShips = ShipBuilder.buildShips();
		remainingShips = 9;
		screen.showGUI();
		AudioLoader.getAudio("ocean1").setLoop(true).playAudio();
	}

	/**
	 * setOpponent
	 * @name setOpponent
	 * */
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public void setChallengeAccepted(boolean accepted) {
		challengeAccepted = accepted;
	}
}
