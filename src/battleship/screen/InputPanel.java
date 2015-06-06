/**
 * @file InputPanel.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @class InputPanel
 * @extends JPanel
 * */
public class InputPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private JLabel textLabel;
	private JTextField textField;
	private boolean editable;
	private GridBagConstraints gc;
	
	/**
	 * @constructor InputPanel
	 * @param String text , boolean editable state.
	 * */
	public InputPanel(String text, boolean editable) {
		super(new GridBagLayout());
		textLabel = new JLabel(text);
		textLabel.setForeground(Color.WHITE);
		textField = new JTextField(8);
		textField.setEditable(editable);
		textField.addFocusListener(new UserInputListener());
		textLabel.setLabelFor(textField);
		
		gc = new GridBagConstraints();
		gc.weightx = 0.3;
		gc.insets = new Insets(5, 5, 5, 5);
		gc.anchor = GridBagConstraints.BASELINE_LEADING;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		add(textLabel, gc);
		
		gc.weightx = 0.7;
		gc.gridx = 1;
		add(textField, gc);
	}

	/**
	 * setInput
	 * @name setInput
	 * @param String to input;
	 * */
	public void setInput(String input) {
		textField.setText(input);
	}

	/**
	 * getInput
	 * @name getInput
	 * @return returns input as String.
	 * */
	public String getInput() {
		return textField.getText();
	}

	/**
	 * setLabelTest
	 * @name setLabelTest
	 * @param String label to set.
	 * */
	public void setLabelText(String text) {
		textLabel.setText(text);
	}

	/**
	 * isEditable
	 * @name isEditable
	 * @return editable state as boolean.
	 * */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * setEditable
	 * @name setEditable
	 * @param Boolean set editable state.
	 * */
	public void setEditable(boolean editable) {
		setEditable(editable);
	}
	
	/**
	 * @class UserUnpitListener
	 * @implements FocusListener
	 * */
	class UserInputListener implements FocusListener {

		/**
		 * focusGained
		 * @name focusGained
		 * @param FocusEvent.
		 * */
		@Override
		public void focusGained(FocusEvent e) {
			setInputColor(new Color(250, 248, 200));
		}

		/**
		 * focusLost
		 * @name focusLost
		 * @param FocusEvent.
		 * */
		@Override
		public void focusLost(FocusEvent e) {
			setInputColor(new Color(255, 255, 255));
		}
		
		/**
		 * setInputColor
		 * @name setInputColor
		 * @param Color to set.
		 * */
		private void setInputColor(Color color) {
			textField.setBackground(color);
		}
	}
	
	/**
	 * painComponent
	 * @name paintComponent
	 * @param Graphics context.
	 * */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics tmp = g.create();
		tmp.setColor(new Color(18, 28, 26));
        tmp.fillRect(0, 0, getWidth(), getHeight());
        tmp.dispose();
	}
}