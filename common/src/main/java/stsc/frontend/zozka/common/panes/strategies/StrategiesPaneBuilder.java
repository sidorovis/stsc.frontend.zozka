package stsc.frontend.zozka.common.panes.strategies;

import java.rmi.UnexpectedException;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.models.MetricsDrawer;
import stsc.frontend.zozka.common.models.ObservableStrategySelector;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.statistic.cost.function.CostFunction;

/**
 * Builder class for {@link StrategiesPane}. <br/>
 * All getters are package private - so only {@link StrategiesPane} coudl get them.
 */
public final class StrategiesPaneBuilder {

	private FromToPeriod period;
	private SimulatorSettingsModel simulatorSettingsModel;
	private StockStorage stockStorage;
	private MetricsDrawer metricsDrawer;
	private SimulationType simulationType;

	private ObservableStrategySelector observableStrategySelector;
	private CostFunction createCostFunction;

	private int threadAmount = 4;

	/**
	 * could be created only from {@link StrategiesPane} static method
	 */
	StrategiesPaneBuilder() {
	}

	FromToPeriod getPeriod() {
		return period;
	}

	public StrategiesPane build() throws UnexpectedException, BadAlgorithmException, InterruptedException, BadParameterException {
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

	MetricsDrawer getMetricsDrawer() {
		return metricsDrawer;
	}

	public StrategiesPaneBuilder setMetricsDrawer(MetricsDrawer metricsDrawer) {
		this.metricsDrawer = metricsDrawer;
		return this;
	}

	public CostFunction getCreateCostFunction() {
		return createCostFunction;
	}

	public StrategiesPaneBuilder setCostFunction(CostFunction createCostFunction) {
		this.createCostFunction = createCostFunction;
		return this;
	}

	public int getThreadAmount() {
		return threadAmount;
	}

	public StrategiesPaneBuilder setThreadAmount(int threadAmount) {
		this.threadAmount = threadAmount;
		return this;
	}

}
