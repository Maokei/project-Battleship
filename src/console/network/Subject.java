/**
 * @file Subject.java
 * @date 2015-05-05
 * */
package console.network;

/**
 * @package battleship.network
 * @interface Subject
 * */
public interface Subject {
	public void register(Observer obs);
	public void unregister(Observer obs);
	
	public void notifyObservers(Observer obs);
	
}
