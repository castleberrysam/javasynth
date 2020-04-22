package javasynth;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

class ADSR extends ReadOnlyDoublePropertyBase implements InvalidationListener {
	
	DoubleProperty initialLevel;
	DoubleProperty attackLevel;
	DoubleProperty sustainLevel;
	
	IntegerProperty attackTime;
	IntegerProperty decayTime;
	IntegerProperty releaseTime;
	
	BooleanProperty trigger;
	
	double invAttack;
	double invDecay;
	double invRelease;
	
	boolean pressed;
	boolean released;
	double magnitude;
	int time;
	
	ADSR() {
		initialLevel = new SimpleDoubleProperty(0.0);
		attackLevel = new SimpleDoubleProperty(1.0);
		sustainLevel = new SimpleDoubleProperty(1.0);
		initialLevel.addListener(this);
		attackLevel.addListener(this);
		sustainLevel.addListener(this);
		
		attackTime = new SimpleIntegerProperty(1);
		decayTime = new SimpleIntegerProperty(1);
		releaseTime = new SimpleIntegerProperty(1);
		attackTime.addListener(this);
		decayTime.addListener(this);
		releaseTime.addListener(this);
		
		trigger = new SimpleBooleanProperty(false);
		trigger.addListener(this);
		
		updateParams();
	}
	
	private void updateParams() {
		pressed = false;
		released = true;
		magnitude = initialLevel.get();
		time = releaseTime.get();
		
		invAttack = (attackLevel.get() - initialLevel.get()) / attackTime.get();
		invDecay = (sustainLevel.get() - attackLevel.get()) / decayTime.get();
		invRelease = (initialLevel.get() - sustainLevel.get()) / releaseTime.get();
	}
	
	boolean isActive() {
		return pressed || (time < releaseTime.get());
	}
	
	void step() {
		if(pressed) {
			if(time < attackTime.get()) {
				magnitude += invAttack;
				time++;
			} else if(time < attackTime.get() + decayTime.get()) {
				magnitude += invDecay;
				time++;
			} else {
				magnitude = sustainLevel.get();
				if(released) {
					pressed = false;
					time = 0;
				}
			}
		} else if(time < releaseTime.get()) {
			magnitude += invRelease;
			time++;
		} else {
			magnitude = initialLevel.get();
		}
		
		fireValueChangedEvent();
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
	public double get() {
		return magnitude;
	}

	@Override
	public void invalidated(Observable obs) {
		if(obs == trigger) {
			if(trigger.get()) {
				pressed = true;
				released = false;
				magnitude = initialLevel.get();
				time = 0;
			} else {
				released = true;
				if(time >= attackTime.get() + decayTime.get()) {
					pressed = false;
					time = 0;
				}
			}
		} else {
			updateParams();
		}
	}

}
