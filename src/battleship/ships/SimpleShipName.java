/**
 * @file SimpleShipName.java
 * @authors
 * */
package battleship.ships;

import java.util.Random;

/**
 * @class SimpleShipName
 * @brief Class generates names for ships.
 * */
public class SimpleShipName {
	private String names [] = {"Ghost", "Bosse", "Yamato", "Wolf", "R. Reagan", "Gustav", "Erika", "Samurai", "Olle", "Pippi", "Xaorui", "Janne", "Warchief", "Assasinator", "Zoee", "Garlic"};
	private String nick [] = {"pain delivery system", "dominator", "ship", "dismantler", "harvester", "scream", "soul harvester", "hulk", "bonemarrow gatherer", "pirate ship", "scarling", "buttercup", "pile of rust", "powder cloud", "blood gutter", "red sail"};
	private Random r;
	private static SimpleShipName instance = null;
	
	private SimpleShipName() {
		r = new Random();
	}
	
	/**
	 * generateName
	 * @name generateName
	 * @return returns a randomized ship name.
	 * */
	public String generateName() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(names[r.nextInt(names.length)]);
		sb.append(" the ");
		sb.append(r.nextInt(nick.length));
		
		return sb.toString();
	}
	
	/**
	 * getInstance
	 * @name getInstance
	 * @returns singleton instance.
	 * */
	public static SimpleShipName getInstance() {
	      if(instance == null) {
	         instance = new SimpleShipName();
	      }
	      return instance;
	}
}
