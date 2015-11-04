package stsc.frontend.zozka.common.panes.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import stsc.frontend.zozka.common.models.StatisticsDescription;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * Implements update mechanism for {@link StatisticsDescription} {@link Observable} model.
 */
final class StatisticsDescriptionObservableModelUpdater implements ListChangeListener<TradingStrategy> {

	private final ObservableList<StatisticsDescription> model;
	private final CostFunction costFunction;

	StatisticsDescriptionObservableModelUpdater(final ObservableList<StatisticsDescription> model, final CostFunction costFunction) {
		this.model = model;
		this.costFunction = costFunction;
	}

	@Override
	public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
		processOnChanged(c);
	}

	private void processOnChanged(final ListChangeListener.Change<? extends TradingStrategy> listChange) {
		synchronized (model) {
			final ListChangeListener.Change<? extends TradingStrategy> change = listChange;
			while (change.next()) {
				if (change.wasAdded()) {
					for (TradingStrategy ts : change.getAddedSubList()) {
						final double tradingStrategyCost = costFunction.calculate(ts.getMetrics());
						Platform.runLater(() -> {
							model.add(new StatisticsDescription(ts, tradingStrategyCost));
						});
					}
				}
				if (change.wasRemoved()) {
					final List<Long> idsToDelete = new ArrayList<Long>();
					for (TradingStrategy tsRemoved : change.getRemoved()) {
						idsToDelete.add(tsRemoved.getSettings().getId());
					}
					Platform.runLater(() -> {
						model.removeIf(p -> {
							return idsToDelete.contains(p.getId());
						});
					});
				}
			}
		}
	}

}
