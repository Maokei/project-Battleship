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
 * @package battleship.resources
 * @class Audio class loads audio files from disk and plays them 
 * @brief handles the retrieving and playing of in-game sound
 * */
public class Audio {
	private Clip clip;
	
	/**
	 * loadAudio
	 * 
	 * @name loadAudio
	 * @brief load audio file from disk
	 * @param path the system path used to load file
	 * @param filename the name of the file
	 * */
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
	 * 
	 * @name stop
	 * @brief Stop playing audio.
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
	 * 
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
