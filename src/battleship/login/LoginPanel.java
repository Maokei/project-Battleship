package battleship.login;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import battleship.screen.Avatar;
import battleship.screen.AvatarPanel;
import battleship.screen.InputPanel;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private InputPanel nameInput;
	private AvatarPanel avatarChooser;
	private JPanel buttonPanel;
	
	public LoginPanel() {
		super(new GridLayout(2, 1));
		nameInput = new InputPanel("Enter name: ", true);
		avatarChooser = new AvatarPanel();
		add(nameInput);
		add(avatarChooser);
	}
	
	public void clear() {
		nameInput.setInput("");
		avatarChooser.reset();
	}
	
	public String getName() {
		return nameInput.getInput();
	}
	
	public Avatar getAvatar() {
		return avatarChooser.getAvatar();
	}
	
}
