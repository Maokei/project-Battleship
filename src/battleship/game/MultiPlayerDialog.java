package battleship.game;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MultiPlayerDialog extends JDialog {
	private static final long serialVersionUID = -7081113365953129588L;
	private ArrayList<String> players;
	private ArrayList<JLabel> playerLabels;
	private JButton cancel, ok;
	private JPanel centerPanel, buttonPanel;
	private Font font;
	private String opponent;
	private final String noPlayers = "There are currently no other players connected";
	
	
	public MultiPlayerDialog(Player player) {
		super();
		setLayout(new BorderLayout());
		centerPanel = new JPanel();
		buttonPanel = new JPanel();
		cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> { this.dispose(); });
		ok = new JButton("OK");
		ok.addActionListener(ae -> { setOpponent(player); });
		ok.setEnabled(false);
		font = new Font("Monospaced", Font.PLAIN, 14);
		players = player.getConnectedPlayers();
		playerLabels = new ArrayList<JLabel>(players.size());
		if(players.size() > 0) {
			centerPanel.setLayout(new GridLayout(players.size(), players.size()));
			for(String name : players) {
				centerPanel.add(new JLabel("Name: "));
				JLabel tmp = new JLabel(name);
				tmp.addMouseListener(new NameListener());
				playerLabels.add(tmp);
				centerPanel.add(tmp);
			}
		} else {
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(new JLabel(noPlayers), BorderLayout.CENTER);
		}
		
		add(new JScrollPane(centerPanel), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(200, 300);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void setOpponent(Player player) {
		player.setOpponent(opponent);
		this.dispose();
	}

	class NameListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			opponent = ((JLabel) e.getSource()).getText();
			ok.setEnabled(true);
		}
	}
	
	
}
