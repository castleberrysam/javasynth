package javasynth;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class SimpleSlider extends HBox {
	
	Slider slider;
	
	SimpleSlider(String label, double range, double def, double interval) {
		this(label, 0.0, range, def, interval);
	}

	SimpleSlider(String label, double start, double end, double def, double interval) {
		slider = new Slider(start, end, def);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(interval);
		HBox.setHgrow(slider, Priority.ALWAYS);
		getChildren().addAll(new Label(label), slider);
		setSpacing(10);
	}
	
	DoubleProperty valueProperty() {
		return slider.valueProperty();
	}

}
