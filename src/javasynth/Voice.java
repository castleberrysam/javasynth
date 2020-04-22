package javasynth;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

class Voice extends ReadOnlyIntegerPropertyBase {
	
	Oscillator mainOsc;
	
	Oscillator pwmOsc;
	DoubleProperty pwmInitial; // Input, 0 to 1
	DoubleProperty pwmMagnitude; // Input, 0 to 1
	
	SVF hpFilter;
	SVF lpFilter;
	DoubleProperty hpCutoffBase;
	DoubleProperty lpCutoffBase;
	
	ADSR cutoffADSR;
	ADSR amplitudeADSR;
	BooleanProperty trigger;

	DoubleProperty volume; // Input, 0 to 1
	
	Voice(Oscillator.Type type) {
		mainOsc = new Oscillator(type);
		
		pwmOsc = new Oscillator(Oscillator.Type.SINE);
		pwmInitial = new SimpleDoubleProperty(0.5);
		pwmMagnitude = new SimpleDoubleProperty(0.4);
		
		pwmOsc.frequency.set(0.0);
		mainOsc.pulseWidth.bind(pwmInitial.add(pwmOsc.divide(32768.0).multiply(pwmMagnitude)));
		
		hpFilter = new SVF();
		lpFilter = new SVF();
		hpCutoffBase = new SimpleDoubleProperty(0.0);
		lpCutoffBase = new SimpleDoubleProperty(20000.0);
		
		hpFilter.input.bind(mainOsc);
		lpFilter.input.bind(hpFilter.outputHP);
		
		cutoffADSR = new ADSR();
		amplitudeADSR = new ADSR();
		trigger = new SimpleBooleanProperty(false);
		
		cutoffADSR.trigger.bind(trigger);
		amplitudeADSR.trigger.bind(trigger);
		
		cutoffADSR.sustainLevel.set(0.0);
		hpFilter.cutoff.bind(hpCutoffBase.add(cutoffADSR));
		lpFilter.cutoff.bind(lpCutoffBase.add(cutoffADSR));
		
		volume = new SimpleDoubleProperty(1.0);
	}
	
	void step() {
		cutoffADSR.step();
		amplitudeADSR.step();
		pwmOsc.step();
		mainOsc.step();
	}

	@Override
	public int get() {
		return (int) (lpFilter.outputLP.get() * amplitudeADSR.get() * volume.get());
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}
	
}
