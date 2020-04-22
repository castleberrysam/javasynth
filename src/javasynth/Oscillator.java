package javasynth;

import java.util.Random;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;

class Oscillator extends ReadOnlyIntegerPropertyBase implements InvalidationListener {
	
	enum Type {
		SINE,
		SQUARE,
		SAW_UP,
		SAW_DOWN,
		TRIANGLE,
		NOISE,
	}
	
	Type type;
	Random rand; // for NOISE waveform

	DoubleProperty frequency; // Input, 0 to SAMPLE_RATE/2 Hz
	DoubleProperty phase; // Input, 0 to 4095
	
	DoubleProperty pulseWidth; // Input, 0 to 1, for SQUARE waveform
	
	DoubleProperty angle; // Output, 0 to 4095
	
	Oscillator syncOsc;
	
	Oscillator(Type type) {
		this(type, 440.0, 0.0);
	}
	
	Oscillator(Type type, double frequency, double phase) {
		setType(type);
		rand = new Random();
		
		this.frequency = new SimpleDoubleProperty(frequency);
		this.phase = new SimpleDoubleProperty(phase);
		pulseWidth = new SimpleDoubleProperty(0.5);
		angle = new SimpleDoubleProperty(0);
		
		this.phase.addListener(this);
		angle.addListener(this);
	}
	
	void setType(Type type) {
		this.type = type;
	}
	
	void step() {
		double angleVal = angle.get();
		angleVal += (frequency.get() * (4096.0 / Main.SAMPLE_RATE));
		if(angleVal >= 4096.0) {
			angleVal -= 4096.0;
			if(syncOsc != null) {syncOsc.angle.set(0.0);}
		}
		
		angle.set(angleVal);
	}
	
	void setSync(Oscillator syncOsc) {
		this.syncOsc = syncOsc;
	}
	
	@Override
	public int get() {
		int index = (int) (angle.get() + phase.get());
		if(index >= 4096) {index -= 4096;}
		switch(type) {
		case SINE:
			return Wavetable.sineSigned(index);
		case SQUARE:
			return (index < 4096 * pulseWidth.get()) ? 32767 : -32768;
		case SAW_UP:
			return (index - 2048) << 4;
		case SAW_DOWN:
			return (2047 - index) << 4;
		case TRIANGLE:
			if(index < 1024) {return index << 5;}
			if(index < 3072) {return (2048 - index) << 5;}
			return (index - 4096) << 5;
		case NOISE:
			return rand.nextInt(65536) - 32768;
		default:
			return 0;
		}
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void invalidated(Observable obs) {
		fireValueChangedEvent();
	}

}
