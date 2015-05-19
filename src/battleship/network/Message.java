package battleship.network;
/**
 * @file Message.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */

import battleship.player.Alignment;
import battleship.player.ShipType;
import java.io.Serializable;

/**
 * @package console.network
 * @class Message
 * @brief Class describes a message from a user to to another or everyone. 
 * */
public abstract class Message {
	private static final long serialVersionUID = 7526476844562776147L;
	private String sender;
	private String receiver;
	static final int LOGOUT = 0, MESSAGE = 1;
	private int type;
	
	public Message(String sender, String receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public String getMessageType() {
		return "message";
	}
	
	public int getType() {
		return type;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
}

/**
 * @package console.network
 * @class ChatMessage
 * @brief Class describes a ChatMessage from a user to to another or everyone. 
 * */
class ChatMessage extends Message {
	private String message;
	
	public ChatMessage(String sender, String receiver, String message) {
		super(sender, receiver);
		this.message = message;
	}
	
	public ChatMessage(String sender, String message) {
		super(sender, "");
		this.message = message;
	}
	
	public int getType() {
		return 1;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "ChatMessage: " + getSender() + " : " + getReceiver();
	}
}


/**
 * @package console.network
 * @class ShotMessage
 * @brief Class describes a battle message containing information about a shot fired
 * */
class PositionShipMessage extends Message {
	private int posX;
	private int posY;
	private ShipType type;
	private Alignment shipAlignment;
	
	public PositionShipMessage(String sender, String receiver, int x, int y, Alignment a) {
		super(sender, receiver);
		posX = x;
		posY = y;
		shipAlignment = a;
	}
	
	public int getX() {return posX;}
	public int getY() {return posY;}
	public ShipType getShipType() {return type;}
	public Alignment getAlignment() {return shipAlignment;}
	
	@Override
	public String toString() {
		return "PositionShipMessage: " + getSender() + " : " + getReceiver() + " : " + shipAlignment.toString() + " : " + type.toString();
	}
}

/**
 * @package console.network
 * @class ValidMoveMessage
 * @brief Message class that describes the validation of a move
 * */
class AnswerMessage extends Message {
	private boolean answer;
	
	public AnswerMessage(String sender, String receiver, boolean v) {
		super(sender, receiver);
		answer = v;
	}
	
	public boolean getAnswer() {return answer;}
}