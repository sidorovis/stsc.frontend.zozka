package stsc.frontend.zozka.charts.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.AbstractXYDataset;

/**
 * Abstract settings class for different charts. <br/>
 * Regulates visibility of one chart unit (curve / candlestick ...).
 */
public abstract class CurveChartSetting {

	private final BooleanProperty showAlgorithm;
	private final StringProperty title;

	protected CurveChartSetting(boolean showAlgo, String title) {
		this.showAlgorithm = new SimpleBooleanProperty(showAlgo);
		this.title = new SimpleStringProperty(title);
	}

	public BooleanProperty showAlgorithmProperty() {
		return showAlgorithm;
	}

	public void setShowAlgorithm(boolean value) {
		this.showAlgorithm.setValue(value);
	}

	public StringProperty propertyTitle() {
		return title;
	}

	protected void addListenerToShowAlgorithm(ChangeListener<Boolean> listener) {
		showAlgorithm.addListener(listener);
	}

	public abstract XYItemRenderer getRenderer();

	public abstract int getIndex();

	public abstract AbstractXYDataset getTimeSeriesCollection();
}