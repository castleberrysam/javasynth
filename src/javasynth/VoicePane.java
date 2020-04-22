package javasynth;

import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class VoicePane extends VBox {
	
	static class BorderLabel extends Label {
		
		static final Border BORDER = new Border(
				new BorderStroke(null, BorderStrokeStyle.SOLID, null, null));
		
		BorderLabel(String text) {
			super(text);
			setBorder(BORDER);
		}
		
	}
	
	static class VoicePaneVBox extends VBox {
		
		VoicePaneVBox(String label, Node... children) {
			List<Node> childList = getChildren();
			childList.add(new BorderLabel(label));
			childList.addAll(Arrays.asList(children));
			setSpacing(10);
			setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(this, Priority.ALWAYS);
		}
		
	}
	
	Voice voice;
	
	SimpleSlider volume;
	WaveSelectPane mainWave;
	SimpleSlider mainFreq;
	
	SimpleSlider pwmFreq;
	SimpleSlider pwmInitial;
	SimpleSlider pwmMagnitude;
	
	SimpleSlider hpCutoffBase;
	SimpleSlider hpResonance;
	SimpleSlider lpCutoffBase;
	SimpleSlider lpResonance;
	
	SimpleSlider cutoffInitialLevel;
	SimpleSlider cutoffAttackLevel;
	SimpleSlider cutoffAttack;
	SimpleSlider cutoffDecay;
	SimpleSlider cutoffRelease;
	
	SimpleSlider amplitudeAttack;
	SimpleSlider amplitudeDecay;
	SimpleSlider amplitudeSustain;
	SimpleSlider amplitudeRelease;
	
	VBox vboxMain;
	VBox vboxPwm;
	VBox vboxHp;
	VBox vboxLp;
	VBox vboxCutoff;
	VBox vboxAmplitude;
	
	HBox hbox0;
	HBox hbox1;
	HBox hbox2;
	
	VoicePane() {
		voice = new Voice(Oscillator.Type.SINE);
		
		volume = new SimpleSlider("Volume", 1.0, 1.0, 0.1);
		voice.volume.bind(volume.valueProperty());

		mainWave = new WaveSelectPane(voice.mainOsc);
		mainFreq = new SimpleSlider("Frequency", 8000, 440, 500);
		voice.mainOsc.frequency.bindBidirectional(mainFreq.valueProperty());
		
		pwmFreq = new SimpleSlider("Frequency", 300, 0, 50);
		pwmInitial = new SimpleSlider("Base Width", 1.0, 0.5, 0.1);
		pwmMagnitude = new SimpleSlider("Magnitude", 1.0, 0.0, 0.1);
		voice.pwmOsc.frequency.bind(pwmFreq.valueProperty());
		voice.pwmInitial.bind(pwmInitial.valueProperty());
		voice.pwmMagnitude.bind(pwmMagnitude.valueProperty());
				
		hpCutoffBase = new SimpleSlider("Cutoff Base", 8000, 1, 500);
		hpResonance = new SimpleSlider("Resonance", 10, 0.7071, 1);
		lpCutoffBase = new SimpleSlider("Cutoff Base", 8000, 8000, 500);
		lpResonance = new SimpleSlider("Resonance", 10, 0.7071, 1);
		voice.hpCutoffBase.bind(hpCutoffBase.valueProperty());
		voice.hpFilter.resonance.bind(hpResonance.valueProperty());
		voice.lpCutoffBase.bind(lpCutoffBase.valueProperty());
		voice.lpFilter.resonance.bind(lpResonance.valueProperty());
		
		cutoffInitialLevel = new SimpleSlider("Initial Level", -8000, 0, 0, 500);
		cutoffAttackLevel = new SimpleSlider("Attack Level", 8000, 0, 500);
		cutoffAttack = new SimpleSlider("Attack", 5, 0, 1);
		cutoffDecay = new SimpleSlider("Decay", 5, 0, 1);
		cutoffRelease = new SimpleSlider("Release", 5, 0, 1);
		voice.cutoffADSR.initialLevel.bind(cutoffInitialLevel.valueProperty());
		voice.cutoffADSR.attackLevel.bind(cutoffAttackLevel.valueProperty());
		voice.cutoffADSR.attackTime.bind(cutoffAttack.valueProperty().multiply(Main.SAMPLE_RATE));
		voice.cutoffADSR.decayTime.bind(cutoffDecay.valueProperty().multiply(Main.SAMPLE_RATE));
		voice.cutoffADSR.releaseTime.bind(cutoffRelease.valueProperty().multiply(Main.SAMPLE_RATE));
		
		amplitudeAttack = new SimpleSlider("Attack", 5, 0, 1);
		amplitudeDecay = new SimpleSlider("Decay", 5, 0, 1);
		amplitudeSustain = new SimpleSlider("Sustain", 1.0, 1.0, 0.1);
		amplitudeRelease = new SimpleSlider("Release", 5, 0, 1);
		voice.amplitudeADSR.attackTime.bind(amplitudeAttack.valueProperty().multiply(Main.SAMPLE_RATE));
		voice.amplitudeADSR.decayTime.bind(amplitudeDecay.valueProperty().multiply(Main.SAMPLE_RATE));
		voice.amplitudeADSR.sustainLevel.bind(amplitudeSustain.valueProperty());
		voice.amplitudeADSR.releaseTime.bind(amplitudeRelease.valueProperty().multiply(Main.SAMPLE_RATE));
		
		vboxMain = new VoicePaneVBox("Main Oscillator", volume, mainWave, mainFreq);
		vboxPwm = new VoicePaneVBox("Pulse Width Modulation", pwmFreq, pwmInitial, pwmMagnitude);
		vboxHp = new VoicePaneVBox("Highpass Filter", hpCutoffBase, hpResonance);
		vboxLp = new VoicePaneVBox("Lowpass Filter", lpCutoffBase, lpResonance);
		vboxCutoff = new VoicePaneVBox("Cutoff Envelope", cutoffInitialLevel, cutoffAttackLevel, cutoffAttack, cutoffDecay, cutoffRelease);
		vboxAmplitude = new VoicePaneVBox("Amplitude Envelope", amplitudeAttack, amplitudeDecay, amplitudeSustain, amplitudeRelease);
		
		hbox0 = new HBox(vboxMain, vboxPwm);
		hbox0.setSpacing(10);
		hbox1 = new HBox(vboxHp, vboxLp);
		hbox1.setSpacing(10);
		hbox2 = new HBox(vboxCutoff, vboxAmplitude);
		hbox2.setSpacing(10);
		
		getChildren().addAll(hbox0, hbox1, hbox2);
		setSpacing(10);
	}
	
}
