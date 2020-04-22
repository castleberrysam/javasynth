package javasynth;

import java.util.HashMap;
import java.util.Map;

class Keyboard {
	
	static final Map<String,Double> KEYS;
	
	static {
		KEYS = new HashMap<>();
		KEYS.put("Z", 261.63); // C4
		KEYS.put("S", 277.18); // C#4
		KEYS.put("X", 293.66); // D4
		KEYS.put("D", 311.13); // D#4
		KEYS.put("C", 329.63); // E4
		KEYS.put("V", 349.23); // F4
		KEYS.put("G", 369.99); // F#4
		KEYS.put("B", 392.00); // G4
		KEYS.put("H", 415.30); // G#4
		KEYS.put("N", 440.00); // A4
		KEYS.put("J", 466.16); // A#4
		KEYS.put("M", 493.88); // B4
	}
	
	static double getKeyFreq(String key, int octave) {
		double freq = KEYS.getOrDefault(key.toUpperCase(), 0.0);
		
		int diff = octave - 4;
		for(;diff<0;diff++) {freq /= 2.0;}
		for(;diff>0;diff--) {freq *= 2.0;}
		
		return freq;
	}

}
