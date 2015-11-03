package stsc.frontend.zozka.common.models;

import javafx.beans.property.SimpleDoubleProperty;
import stsc.frontend.zozka.common.panes.strategies.StrategiesPane;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

/**
 * This is Zozka GUI model for {@link StrategiesPane} table. Used to create Trading Strategy description table with selected {@link Metrics}.
 */
public final class StatisticsDescription {

	private final TradingStrategy tradingStrategy;
	private final Double costFunctionResult;

	public StatisticsDescription(final TradingStrategy tradingStrategy, final Double costFunctionResult) {
		this.tradingStrategy = tradingStrategy;
		this.costFunctionResult = costFunctionResult;
	}

	public long getId() {
		return tradingStrategy.getSettings().getId();
	}

	public SimpleDoubleProperty getCostFunctionResult() {
		return new SimpleDoubleProperty(trimDouble(costFunctionResult));
	}

	public SimpleDoubleProperty getProperty(final MetricType metricType) {
		return new SimpleDoubleProperty(trimDouble(tradingStrategy.getMetrics().getMetric(metricType)));
	}

	private double trimDouble(double value) {
		return Math.ceil(value * 1000000.0) / 1000000.0;
	}

	@Override
	public String toString() {
		return tradingStrategy.getSettings().toString();
	}

	public Metrics getMetrics() {
		return tradingStrategy.getMetrics();
	}
}