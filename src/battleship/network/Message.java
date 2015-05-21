/**
 * @file Message.java
 * @authors Rickard, Lars
 * @date 2015-05-19
 * */
package battleship.network;

import java.io.Serializable;

/**
 * @class Message
 * @package battleship.network
 * @implements Serializable
 * @brief Class describes a message to be send between server and clients.
 * */
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
