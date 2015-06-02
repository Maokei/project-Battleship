package battleship.game;

import java.util.ArrayList;

import battleship.ships.Ship;

public interface BattlePlayer extends PlayerOperations {
	public void setOpponentDeployed();
	public void setPlayerTurn(boolean playerTurn);
	public boolean checkHit(int row, int col);
	public void registerFire(int row, int col);
	public void registerPlayerHit(int row, int col);
	public void registerEnemyHit(Ship ship, int row, int col);
	public void registerPlayerMiss(int row, int col);
	public void registerEnemyMiss(int row, int col);
	public void sinkShip(Ship ship);
	public void placeEnemyShip(Ship ship, int row, int col);
	public void battleLost();
	public void listen();
	
	
}
