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

public class InputPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private JLabel textLabel;
	private JTextField textField;
	private boolean editable;
	private Color color;
	private GridBagConstraints gc;
	
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

	public void setInput(String input) {
		textField.setText(input);
	}

	public String getInput() {
		return textField.getText();
	}

	public void setLabelText(String text) {
		textLabel.setText(text);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	class UserInputListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			setInputColor(new Color(255, 255, 170));
		}

		@Override
		public void focusLost(FocusEvent e) {
			setInputColor(new Color(255, 255, 255));
		}
		
		private void setInputColor(Color color) {
			textField.setBackground(color);
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics tmp = g.create();
		tmp.setColor(new Color(18, 28, 26));
        tmp.fillRect(0, 0, getWidth(), getHeight());
        tmp.dispose();
	}
}