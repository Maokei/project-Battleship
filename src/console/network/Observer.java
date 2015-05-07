/**
 * @file Observer.java
 * @date 2015-05-05
 * */
package console.network;

/**
 * @package battleship.network
 * @interface Observer
 * @brief 
 * */
public interface Observer {
	public void update();
	public void update(ChatMessage message);
	public void setSubject(Subject sub);
	public abstract String getName();
}
