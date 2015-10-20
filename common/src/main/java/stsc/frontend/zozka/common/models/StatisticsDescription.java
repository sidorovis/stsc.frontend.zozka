package stsc.frontend.zozka.common.models;

import javafx.beans.property.SimpleDoubleProperty;
import stsc.frontend.zozka.common.panes.StrategiesPane;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

/**
 * This is Zozka GUI model for {@link StrategiesPane} table. Used to create
 * Trading Strategy description table with selected {@link Metrics}.
 */
public final class StatisticsDescription {

	private final TradingStrategy tradingStrategy;

	public StatisticsDescription(final TradingStrategy tradingStrategy) {
		this.tradingStrategy = tradingStrategy;
	}

	public long getId() {
		return tradingStrategy.getSettings().getId();
	}

	public SimpleDoubleProperty getProperty(final MetricType metricType) {
		return new SimpleDoubleProperty(tradingStrategy.getMetrics().getMetric(metricType));
	}

	@Override
	public String toString() {
		return tradingStrategy.getSettings().toString();
	}

	public Metrics getMetrics() {
		return tradingStrategy.getMetrics();
	}
}