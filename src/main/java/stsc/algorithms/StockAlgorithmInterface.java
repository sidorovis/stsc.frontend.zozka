package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public interface StockAlgorithmInterface {
	
	public abstract void setExecutionName(String executionName);

	public abstract void setSignalsStorage(SignalsStorage signalsStorage);

	public abstract Class<? extends StockSignal> registerSignalsClass();

	public abstract void process(String stockName, Day day) throws BadSignalException;

}
