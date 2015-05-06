/**
 * @file ChatMessage.java
 * @date 2015-05-06
 * @author rickard(rijo1001), lars(lama1205)
 * */
package console.network;

/**
 * @package console.network
 * @class ChatMessage
 * @brief Class describes a chat message from a user to to another or everyone. 
 * */
public class ChatMessage {
	private static final long serialVersionUID = 7526471154562776147L;
	private String sender,
	receiver,
	message;
	
	public ChatMessage(String sender, String receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getRecevier() {
		return receiver;
	}
}
