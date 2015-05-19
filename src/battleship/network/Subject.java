/**
 * @file Subject.java
 * @date 2015-05-05
 * */
package battleship.network;


/**
 * @package battleship.network
 * @interface Subject
 * */
public interface Subject {
	public void register(Observer obs , String playerName);
	public void unregister(Observer obs, String playerName);
	public void notifyObservers();
	public void receiveMessage(Message message);
}
