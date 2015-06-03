package battleship.game;

import java.awt.BorderLayout;
import java.awt.Color;
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
	
	
	public MultiPlayerDialog(LoginDialog loginDialog, boolean b, Player player) {
		super(loginDialog, true);
		setLayout(new BorderLayout());
		centerPanel = new JPanel();
		buttonPanel = new JPanel();
		cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> { this.dispose(); });
		ok = new JButton("OK");
		ok.addActionListener(ae -> { setOpponent(player); });
		ok.setEnabled(false);
		buttonPanel.add(cancel);
		buttonPanel.add(ok);
		buttonPanel.setBackground(new Color(10, 10, 10));
		centerPanel.setBackground(new Color(40, 40, 40));
		font = new Font("Monospaced", Font.BOLD, 14);
		players = player.getConnectedPlayers();
		playerLabels = new ArrayList<JLabel>(players.size());
		if(players.size() > 0) {
			centerPanel.setLayout(new GridLayout(players.size(), players.size()));
			for(String name : players) {
				JLabel nameLabel = new JLabel("Name: ");
				nameLabel.setFont(font);
				nameLabel.setBackground(new Color(40, 40, 40));
				nameLabel.setForeground(new Color(255, 255, 255));
				centerPanel.add(nameLabel);
				JLabel tmp = new JLabel(name);
				tmp.setFont(font);
				tmp.setBackground(new Color(20, 20, 20));
				tmp.setForeground(new Color(255, 255, 255));
				tmp.addMouseListener(new NameListener());
				playerLabels.add(tmp);
				centerPanel.add(tmp);
			}
		} else {
			centerPanel.setLayout(new BorderLayout());
			JLabel noPlayersLabel = new JLabel(noPlayers);
			noPlayersLabel.setFont(font);
			noPlayersLabel.setForeground(new Color(255, 255, 255));
			centerPanel.add(noPlayersLabel, BorderLayout.CENTER);
		}
		
		add(new JScrollPane(centerPanel), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(150, 250);
		setLocationRelativeTo(loginDialog);
		setVisible(true);
	}
	
	private void setOpponent(Player player) {
		player.setOpponent(opponent);
		this.dispose();
	}

	class NameListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JLabel choosen = ((JLabel) e.getSource());
			choosen.setForeground(new Color(0, 255, 0));
			opponent = choosen.getText();
			ok.setEnabled(true);
		}
	}
	
	
}
