package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.ExecutionInitializer;
import stsc.general.simulator.SimulatorSettings;

public class SimulatorSettingsGridCopyList extends SimulatorSettingsGridList {

	SimulatorSettingsGridCopyList(StockStorage stockStorage, FromToPeriod period, List<ExecutionInitializer> stocks,
			List<ExecutionInitializer> eods, boolean finished) {
		super(stockStorage, period, stocks, eods, finished);
	}

	@Override
	public Iterator<SimulatorSettings> iterator() {
		final List<ExecutionInitializer> stocks = new ArrayList<>(stockInitializers.size());
		for (ExecutionInitializer executionInitializer : stockInitializers) {
			stocks.add(executionInitializer.clone());
		}
		final List<ExecutionInitializer> eods = new ArrayList<>(eodInitializers.size());
		for (ExecutionInitializer executionInitializer : eodInitializers) {
			eods.add(executionInitializer.clone());
		}

		return new SimulatorSettingsGridIterator(stockStorage, period, stocks, eods, finished);
	}

}