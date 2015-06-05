/**
 * @file Message.java
 * @authors Rickard, Lars
 * @date 2015-05-19
 * @brief File contains a class Message describing a message.
 * */
package battleship.game;

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
	private String sender;
	private String receiver;
	public static final int LOGIN = 0, MESSAGE = 1,
			CHAT = 2, LOGOUT = 3, DEPLOYED = 4, TURN = 5,
			AIMATCH = 6, CHALLENGE = 7, LOST = 8, MODE = 9,
			VALID = 10;
	

	public Message(int type, String sender, String receiver, String msg) {
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
		this.msg = msg;
	}
	
	/**
	 * getType
	 * @name getType
	 * @param None
	 * @return returns constant integer to denote the type of message for parsing
	 * */
	public int getType() {
		return type;
	}

	/**
	 * getMessage
	 * @name getMessage
	 * @param none
	 * @return String containing the actual message
	 * */
	public String getMessage() {
		return msg;
	}
	
	/**
	 * getName
	 * @name getName
	 * @return return the name as a string player name that sent the message or server
	 * */
	public String getSender() {
		return sender;
	}
	
	/**
	 * getReceiver 
	 * @name getReceiver
	 * @return String name of the receiver.
	 * */
	public String getReceiver() {
		return receiver;
	}
}
