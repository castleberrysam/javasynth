package javasynth;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;

class Delay extends ReadOnlyIntegerPropertyBase implements InvalidationListener {

	IntegerProperty delaySize;
	int[] buffer;
	int index;
	
	Delay(int maxDelaySize) {
		delaySize = new SimpleIntegerProperty(0);
		delaySize.addListener(this);
		
		buffer = new int[maxDelaySize];
	}
	
	void step(int sample) {
		buffer[index++] = sample;
		if(index >= delaySize.get()) {index = 0;}
		
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
	public int get() {
		return buffer[index];
	}

	@Override
	public void invalidated(Observable obs) {
		index = 0;
	}

}
