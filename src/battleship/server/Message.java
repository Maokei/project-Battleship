package battleship.server;

import java.io.Serializable;


public class Message implements Serializable {
	protected static final long serialVersionUID = 1112122200L;
	static final int LOGOUT = 0, MESSAGE = 1;
	private int type;
	private String name;
	private String message;

	Message(int type, String name, String message) {
		this.type = type;
		this.name = name;
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}