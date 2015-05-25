/**
 * @authors rickard, lars
 * @file Battle.java
 * */
package battleship.network;

import battleship.network.PlayerProxy;

public class Battle extends Thread {
	//For communicating with server
	private Server server;
	//Player connections
	private PlayerProxy bluePlayer; //p1
	private PlayerProxy redPlayer; //p2
	//copy of player grids
	//plater turn switch, true == blue player false == red player
	boolean turn; 
	
	public Battle(Server server, PlayerProxy p1, PlayerProxy p2) {
		bluePlayer = p1;
		redPlayer = p2;
		this.server = server;
	}
	
	public void run() {
		//Match loop
		while(true) {
			//ship placement
			
			//battle begins
			
			//announce winner
			announceWinner();
		}
	}
	
	public void shipPlacementPhase() {
		while(true){
			
		}
	}
	
	private void announceWinner() {
		//notify server to take back players and add them to the players list
	}
}
