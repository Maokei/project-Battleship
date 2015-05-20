package battleship.network;

public class Message {
	private int type;
	private String msg;
	private String name;
	public static final int LOGIN = 0, MESSAGE = 1, LOGOUT = 2;
	
	public Message(int type, String name, String msg) {
		this.type = type;
		this.name = name;
		this.msg = msg;
	}
	
	public int getType() {
		return type;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public String getName() {
		return name;
	}
	
}
