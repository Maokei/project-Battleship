package battleship.network;

import java.io.Serializable;


public class Message implements Serializable {
	private static final long serialVersionUID = -1162931686882215029L;
	private int type;
	private String msg;
	private String name;
	public static final int LOGIN = 0, MESSAGE = 1,
			CHAT = 2, LOGOUT = 3;

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
