package javasynth;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

class SVF implements InvalidationListener {
	
	static abstract class SVFProperty extends ReadOnlyIntegerPropertyBase implements InvalidationListener {
		
		SVFProperty(ReadOnlyIntegerProperty input) {
			input.addListener(this);
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
	
	IntegerProperty input;

	DoubleProperty cutoff;
	DoubleProperty resonance;
			
	SVFProperty outputLP;
	SVFProperty outputBP;
	SVFProperty outputHP;
	
	double ic1eq;
	double ic2eq;
	double g;
	double k;
	double a1;
	double a2;
	double a3;

	SVF() {
		this(3000, 0.707);
	}
	
	SVF(double cutoff, double resonance) {
		input = new SimpleIntegerProperty(0);
		this.cutoff = new SimpleDoubleProperty(cutoff);
		this.resonance = new SimpleDoubleProperty(resonance);
		
		outputLP = new SVFProperty(input) {
			@Override
			public int get() {return getLP();}
		};
		
		outputBP = new SVFProperty(input) {
			@Override
			public int get() {return getBP();}
		};
		
		outputHP = new SVFProperty(input) {
			@Override
			public int get() {return getHP();}
		};

		this.cutoff.addListener(this);
		this.resonance.addListener(this);
	}
	
	@Override
	public void invalidated(Observable obs) {
		double cutoffVal = cutoff.get();
		double resonanceVal = resonance.get();
		
		if(cutoffVal < 0.0) {cutoffVal = 0.0;}
		if(cutoffVal > Main.SAMPLE_RATE/2.0) {cutoffVal = Main.SAMPLE_RATE/2.0;}
		if(resonanceVal < 0.001) {resonanceVal = 0.001;}
		
		int angle = (int) (cutoffVal * (2048.0 / Main.SAMPLE_RATE));
		int sine = Wavetable.sineSigned(angle);
		int cosine = Wavetable.sineSigned(angle + 1024);
		
		g = ((float) sine) / ((float) cosine);
		k = 1.0 / resonanceVal;
		a1 = 1.0 / (1.0 + (g * (g + k)));
		a2 = g * a1;
		a3 = g * a2;
	}
	
	int[] getAll() {
		double sample = input.get() / 32768.0;
		double v3 = sample - ic2eq;
		double v1 = (a1 * ic1eq) + (a2 * v3);
		double v2 = ic2eq + (a2 * ic1eq) + (a3 * v3);
		
		ic1eq = (2.0f * v1) - ic1eq;
		ic2eq = (2.0f * v2) - ic2eq;
		
		double lp = v2;
		double bp = v1;
		double hp = sample - (k * bp) - lp;
		
		int[] ret = new int[3];
		ret[0] = (int) (lp * 32768);
		ret[1] = (int) (bp * 32768);
		ret[2] = (int) (hp * 32768);
		return ret;
	}
	
	int getLP() {return getAll()[0];}
	int getBP() {return getAll()[1];}
	int getHP() {return getAll()[2];}

}
