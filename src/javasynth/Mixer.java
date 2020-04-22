package javasynth;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Mixer extends ReadOnlyIntegerPropertyBase implements InvalidationListener {
	
	enum Mode {
		ADD,
		MULTIPLY,
	}
	
	Mode mode;
	
	IntegerProperty input0; // Input, 16-bit signed PCM
	IntegerProperty input1; // Input, 16-bit signed PCM
	
	DoubleProperty vol0; // Input, 0 to 1
	DoubleProperty vol1; // Input, 0 to 1
	
	Mixer(Mode mode) {
		setMode(mode);
		input0 = new SimpleIntegerProperty(0);
		input1 = new SimpleIntegerProperty(0);
		vol0 = new SimpleDoubleProperty(1);
		vol1 = new SimpleDoubleProperty(1);
		
		input0.addListener(this);
		input1.addListener(this);
	}
	
	void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public int get() {
		int sample0 = (int) (input0.get() * vol0.get());
		int sample1 = (int) (input1.get() * vol1.get());
		
		int result;
		switch(mode) {
		case ADD:
			result = sample0 + sample1;
			break;
		case MULTIPLY:
			result = (sample0 * sample1) >> 16;
			break;
		default:
			result = 0;
			break;
		}
		
		if(result > 32767) {result = 32767;}
		if(result < -32768) {result = -32768;}

		return result;
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
