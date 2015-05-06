package battleship.network;

public interface Observer {
	public void attach();
	public void detach();
	public void update();
}
