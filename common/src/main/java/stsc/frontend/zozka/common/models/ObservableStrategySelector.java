package stsc.frontend.zozka.common.models;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.BorderedStrategySelector;
import stsc.general.strategy.selector.StrategySelector;

/**
 * This class provide adapter for {@link StrategySelector} to JavaFx {@link ObservableList}.
 */
public final class ObservableStrategySelector extends BorderedStrategySelector {

	final private StrategySelector selector;
	final private ObservableList<TradingStrategy> strategyList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	public ObservableStrategySelector(BorderedStrategySelector selector) {
		super(selector.maxPossibleAmount());
		this.selector = selector;
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(final TradingStrategy newStrategy) {
		final List<TradingStrategy> deletedStrategies = selector.addStrategy(newStrategy);
		boolean newStrategyWasAdded = true;
		if (!deletedStrategies.isEmpty()) {
			for (TradingStrategy i : deletedStrategies) {
				if (i.equals(newStrategy)) {
					newStrategyWasAdded = false;
				}
				strategyList.remove(i);
			}
			if (newStrategyWasAdded) {
				strategyList.add(newStrategy);
			}
		} else {
			strategyList.add(newStrategy);
		}
		return deletedStrategies;
	}

	@Override
	public synchronized boolean removeStrategy(TradingStrategy strategy) {
		return strategyList.remove(strategy);
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		return selector.getStrategies();
	}

	@Override
	public synchronized int currentStrategiesAmount() {
		return selector.currentStrategiesAmount();
	}

	/**
	 * JavaFx {@link ObservableList} adapter for GUI.
	 * 
	 * @return observable list of {@link TradingStrategy}.
	 */
	public ObservableList<TradingStrategy> getObservableStrategyList() {
		return strategyList;
	}

}