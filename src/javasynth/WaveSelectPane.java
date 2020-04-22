package javasynth;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

class WaveSelectPane extends HBox implements EventHandler<ActionEvent> {
	
	static class WaveSelectButton extends RadioButton {
		
		Oscillator.Type type;
		
		WaveSelectButton(String label, Oscillator.Type type) {
			super(label);
			this.type = type;
		}
		
	}
	
	Oscillator osc;
	ToggleGroup group;
	List<WaveSelectButton> buttons;
	
	void addButton(String label, Oscillator.Type type) {
		WaveSelectButton button = new WaveSelectButton(label, type);
		button.setOnAction(this);
		button.setToggleGroup(group);
		button.setFocusTraversable(false);
		buttons.add(button);
	}
	
	WaveSelectPane(Oscillator osc) {
		this.osc = osc;
		group = new ToggleGroup();
		buttons = new ArrayList<WaveSelectButton>(8);

		addButton("Sine", Oscillator.Type.SINE);
		addButton("Square", Oscillator.Type.SQUARE);
		addButton("Saw (up)", Oscillator.Type.SAW_UP);
		addButton("Saw (down)", Oscillator.Type.SAW_DOWN);
		addButton("Triangle", Oscillator.Type.TRIANGLE);
		addButton("Noise", Oscillator.Type.NOISE);
		
		buttons.get(0).setSelected(true);
		getChildren().addAll(buttons);
	}
	
	@Override
	public void handle(ActionEvent event) {
		WaveSelectButton button = (WaveSelectButton) event.getSource();
		osc.setType(button.type);
	}

}
