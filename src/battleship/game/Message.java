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
	private String reciever;
	public static final int LOGIN = 0, MESSAGE = 1,
			CHAT = 2, LOGOUT = 3, DEPLOYED = 4, TURN = 5,
			AIMATCH = 6, CHALLENGE = 7, LOST = 8, RETRIEVE = 9;

	
	public Message(int type, String sender, String reciever,  String msg) {
		this.type = type;
		this.sender = sender;
		this.setReciever(reciever);
		this.msg = msg;
	}
	/*
	public Message(int type, String name, String msg, String receiver) {
		this.type = type;
		this.name = name;
		this.msg = msg;
		this.receiver = receiver;
	}
	*/
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
	public String getName() {
		return sender;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}
	
	/**
	 * getName
	 * @name getRecevier
	 * @return return the receiver name as a string player name that sent the message or server
	 * */
	/*
	public String getReeiver() {
		return receiver;
	}
	*/
}
