/**
 * @file Audio.java
 * @authors rickard, lars
 * @date 2015-05-25
 * */
package battleship.resources;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @class Audio
 * @brief Class describes the playing of an audio event.
 * */
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
	
	/**
	 * stop
	 * @name stop
	 * @brief Stop's playing.
	 * */
	public void stop() {
		if(clip.isRunning()) {
			clip.stop();
		}
	}
	
	/**
	 * reset
	 * @name reset
	 * @brief Reset playing.
	 * */
	public void reset() {
		stop();
		clip.setFramePosition(0);
	}
	
	/**
	 * playAudio
	 * @name playAudio
	 * @brief Play audio clip from the start.
	 * */
	public void playAudio() {
		reset();
		clip.start();
	}
	
	/**
	 * setLoop
	 * @name setLoop 
	 * @param Set loop state for audio clip.
	 * */
	public Audio setLoop(boolean loop) {
		if(loop) {
			clip.setLoopPoints(0, -1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		return this;
	}
	
}
