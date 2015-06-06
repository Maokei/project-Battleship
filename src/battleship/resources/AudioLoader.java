/**
 * @file AudioLoader.java
 * @authors rickard, lars
 * @date 2015-05-25 
 **/

package battleship.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * @class AudioLoader
 * @brief Describes a class responsible for loading audio clips from file and creating Audio objects. 
 **/
public class AudioLoader {
	private static String path = "src/res/audio/";
	private static Map<String, Audio> mappedAudio = new HashMap<String, Audio>();
	private static String[] filenames = {"ambient_melodic_drums.wav", "ambient_ocean1.wav",
			"ambient_ocean2.wav", "ambient_rocky.wav", "ambient_techno.wav",
			 "intro_dreamy.wav",
			"intro_military_march.wav", "soundeffect_explosion1.wav","tilt.wav",
			"water_splash1.wav", "water_splash2.wav", "sinking.wav", "ship_down.wav" };
	
	private static String[] mappedNames = {"drums", "ocean1", "ocean2",
			"rocky", "techno", "dreamy",
			"march", "explosion1", "tilt", "splash1", "splash2", "sinking", "ship_down" };
	/**
	 * initSounds
	 * @name initSounds
	 * @brief Initiate sounds to Audio objects.
	 * */
	public static void initSounds() {
		for(int i = 0; i < filenames.length; i++) {
			Audio audio = new Audio();
			audio.loadAudio(path, filenames[i]);
			mappedAudio.put(mappedNames[i], audio);
		}
	}
	
	/**
	 * getAudio
	 * @name getAudio
	 * @param Name of audio clip to be returned as Audio pointer.
	 * */
	public static Audio getAudio(String name) {
		return mappedAudio.get(name);
	}
	
	
}
