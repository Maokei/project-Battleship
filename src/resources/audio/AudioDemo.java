package resources.audio;

import java.awt.FlowLayout;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JFrame;

public class AudioDemo extends JFrame {
	private static final long serialVersionUID = 1L;

	public AudioDemo() {
		super("Audio");
		SoundHolder.initSounds();
		JButton explosion = new JButton("Explosion1");
		explosion.addActionListener(ae -> { play(); });
		this.add(explosion);
		this.setSize(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		this.pack();
	}
	
	public void play() {
		SoundHolder.getAudio("explosion1").playAudio();
	}
	
	public static void main(String[] args) {
		new AudioDemo();
		
	//path test,
	//Audio a = new Audio();
	//a.loadAudio("src/res/audio/", "tilt.wav");
	//a.playAudio();
	
	}
}