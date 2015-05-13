package resources.audio;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class AudioDemo extends JFrame {
	private static final long serialVersionUID = 1L;

	public AudioDemo() {
		super("Audio");
		SoundHolder.initSounds();
		JButton explosion = new JButton("Explosion1");
		explosion.addActionListener(ae -> { play(); });
		add(explosion, FlowLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void play() {
		SoundHolder.getAudio("explosion1").playAudio();
	}
	
	public static void main(String[] args) {
		new AudioDemo();
	}
}