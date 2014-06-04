package stsc.common.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.signals.Signal;
import stsc.common.signals.StockSignal;
import stsc.common.storage.SignalsStorage;

public class StockAlgorithmInit {

	final String executionName;
	final SignalsStorage signalsStorage;
	final AlgorithmSettings settings;

	final String stockName;

	public StockAlgorithmInit(String executionName, SignalsStorage signalsStorage, String stockName, AlgorithmSettings settings) {
		this.executionName = executionName;
		this.signalsStorage = signalsStorage;
		this.stockName = stockName;
		this.settings = settings;
	}

	final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(stockName, executionName, date, signal);
	}

	final Signal<? extends StockSignal> getSignal(final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	final Signal<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	final Signal<? extends StockSignal> getSignal(final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	final Signal<? extends StockSignal> getSignal(final String executionName, final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	final int getIndexSize() {
		return signalsStorage.getIndexSize(stockName, executionName);
	}

	final int getIndexSize(String stockName) {
		return signalsStorage.getIndexSize(stockName, executionName);
	}

	@Override
	public String toString() {
		return stockName + ": " + executionName + "\n" + settings;
	}

	public AlgorithmSettings getSettings() {
		return settings;
	}

}