package resources.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Audio {
	private Clip clip;
	
	public void loadAudio(String path, String filename) {
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(new File(path + filename));
			clip = AudioSystem.getClip();
			clip.open(in);
		} catch (UnsupportedAudioFileException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			System.out.println(filename);
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if(clip.isRunning()) {
			clip.stop();
		}
	}
	
	public void reset() {
		stop();
		clip.setFramePosition(0);
	}
	
	public void playAudio() {
		reset();
		clip.start();
	}
	
	public Audio setLoop(boolean loop) {
		if(loop) {
			clip.setLoopPoints(0, -1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		return this;
	}
	
}
