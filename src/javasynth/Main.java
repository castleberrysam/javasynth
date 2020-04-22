package javasynth;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application implements Runnable, EventHandler<KeyEvent> {
	
	static final int SAMPLE_RATE = 44100;

	public static void main(String[] args) {
		launch(args);
	}
	
	VoicePane voicePane;
	Delay delay;
	boolean runSampleThread = true;
	
	int genSample() {
		voicePane.voice.step();
		int sample = voicePane.voice.get();
		delay.step(sample);
		return sample + (int) (delay.get() * delayVolume.valueProperty().get());
	}
	
	@Override
	public void run() {
		AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
		SourceDataLine line;
		try {
			line = AudioSystem.getSourceDataLine(format);
			line.open(format, 4096);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
		line.start();
		
		byte[] buf = new byte[4096];
		while(runSampleThread) {
			for(int i=0;i<4096;i+=2) {
				int sample = genSample();
				buf[i] = (byte) (sample & 0xff);
				buf[i+1] = (byte) (sample >>> 8);
			}
			
			line.write(buf, 0, 4096);
		}
		
		line.close();
	}

	SimpleSlider delayVolume;
	SimpleSlider delaySize;
	
	int curOctave = 4;
	double lastFreq;
	
	@Override
	public void start(Stage stage) {
		voicePane = new VoicePane();
		
		delayVolume = new SimpleSlider("Delay Volume", 1.0, 0.0, 0.1);
		delaySize = new SimpleSlider("Delay Time", 1.0, 0.0, 0.1);
		delay = new Delay(Main.SAMPLE_RATE);
		delay.delaySize.bind(delaySize.valueProperty().multiply(Main.SAMPLE_RATE));
		
		Scene scene = new Scene(new VBox(voicePane, delayVolume, delaySize));
		scene.setOnKeyPressed(this);
		scene.setOnKeyReleased(this);
		
		stage.setScene(scene);
		stage.setTitle("Javasynth");
		stage.setMinWidth(800);
		stage.setMinHeight(800);
		stage.show();
		
		new Thread(this).start();
	}
	
	@Override
	public void stop() {
		runSampleThread = false;
	}

	@Override
	public void handle(KeyEvent event) {
		String text = event.getText();
		double freq = Keyboard.getKeyFreq(text, curOctave);

		if(event.getEventType() == KeyEvent.KEY_PRESSED) {
			if(text.equals(",")) {
				if(curOctave > 0) {curOctave--;}
			} else if(text.equals(".")) {
				if(curOctave < 8) {curOctave++;}
			} else if(freq != 0.0 && (!voicePane.voice.trigger.get() || freq != lastFreq)) {
				voicePane.voice.mainOsc.frequency.set(freq);
				voicePane.voice.trigger.set(true);
					
				lastFreq = freq;
			}
		} else if(freq == lastFreq) {
			voicePane.voice.trigger.set(false);
		}
	}

}
