package stsc.frontend.zozka.common.panes;

import java.rmi.UnexpectedException;

import org.jfree.chart.JFreeChart;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.models.ObservableStrategySelector;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;

/**
 * Builder class for {@link StrategiesPane}. <br/>
 * All getters are package private - so only {@link StrategiesPane} coudl get
 * them.
 */
public final class StrategiesPaneBuilder {

	private FromToPeriod period;
	private SimulatorSettingsModel simulatorSettingsModel;
	private StockStorage stockStorage;
	// TODO should be changed on some interface for chart displaying
	private JFreeChart jFreeChart;
	private SimulationType simulationType;

	private ObservableStrategySelector observableStrategySelector;

	/**
	 * could be created only from {@link StrategiesPane} static method
	 */
	StrategiesPaneBuilder() {
	}

	FromToPeriod getPeriod() {
		return period;
	}

	public StrategiesPane build() throws UnexpectedException, BadAlgorithmException, InterruptedException {
		return new StrategiesPane(this);
	}

	public StrategiesPaneBuilder setPeriod(FromToPeriod period) {
		this.period = period;
		return this;
	}

	SimulatorSettingsModel getSimulatorSettingsModel() {
		return simulatorSettingsModel;
	}

	public StrategiesPaneBuilder setSimulatorSettingsModel(SimulatorSettingsModel simulatorSettingsModel) {
		this.simulatorSettingsModel = simulatorSettingsModel;
		return this;
	}

	StockStorage getStockStorage() {
		return stockStorage;
	}

	public StrategiesPaneBuilder setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
		return this;
	}

	JFreeChart getjFreeChart() {
		return jFreeChart;
	}

	public StrategiesPaneBuilder setjFreeChart(JFreeChart jFreeChart) {
		this.jFreeChart = jFreeChart;
		return this;
	}

	SimulationType getSimulationType() {
		return simulationType;
	}

	public StrategiesPaneBuilder setSimulationType(SimulationType simulationType) {
		this.simulationType = simulationType;
		return this;
	}

	ObservableStrategySelector getObservableStrategySelector() {
		return observableStrategySelector;
	}

	public StrategiesPaneBuilder setObservableStrategySelector(ObservableStrategySelector observableStrategySelector) {
		this.observableStrategySelector = observableStrategySelector;
		return this;
	}

}
