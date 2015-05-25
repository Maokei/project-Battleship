package resources.audio;

import java.util.HashMap;
import java.util.Map;

public class SoundHolder {
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
	public static void initSounds() {
		for(int i = 0; i < filenames.length; i++) {
			Audio audio = new Audio();
			audio.loadAudio(path, filenames[i]);
			mappedAudio.put(mappedNames[i], audio);
		}
	}
	
	public static Audio getAudio(String name) {
		return mappedAudio.get(name);
	}
	
	
}
