package stsc.frontend.zozka.common.models;

import stsc.frontend.zozka.common.panes.strategies.StrategiesPane;
import stsc.general.statistic.Metrics;

/**
 * This is an interface for Metrics Drawer. {@link StrategiesPane} require GUI
 * component that will draw metrics somehow when user select one of the Trading
 * Strategy at the table.
 */
public interface MetricsDrawer {

	void drawMetric(long id, final Metrics metrics);

}
