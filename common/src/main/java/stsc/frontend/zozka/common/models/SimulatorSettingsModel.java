package stsc.frontend.zozka.common.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

/**
 * This class store GUI version of {@link SimulatorSettings} (+period field and
 * etc.).
 */
public final class SimulatorSettingsModel {

	private final ObservableList<ExecutionDescription> model;

	public SimulatorSettingsModel() {
		model = FXCollections.observableArrayList();
	}

	public ObservableList<ExecutionDescription> getModel() {
		return model;
	}

	public boolean isEmpty() {
		return model.isEmpty();
	}

	public int size() {
		return model.size();
	}

	public void clear() {
		model.clear();
	}

	public void add(ExecutionDescription ed) {
		model.add(ed);
	}

	public void saveToFile(OutputStream os) throws FileNotFoundException, IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
			oos.writeInt(model.size());
			for (ExecutionDescription executionDescription : model) {
				executionDescription.writeExternal(oos);
			}
		}
	}

	public void set(int index, ExecutionDescription newEd) {
		model.set(index, newEd);
	}

	public SimulatorSettingsModel loadFromFile(final InputStream is) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(is)) {
			final int size = ois.readInt();
			model.clear();
			for (int i = 0; i < size; ++i) {
				final ExecutionDescription ed = ExecutionDescription.loadFromFile(ois);
				model.add(ed);
			}
		}
		return this;
	}

	public SimulatorSettingsGridList generateGridSettings(StockStorage stockStorage, FromToPeriod period) throws BadParameterException {
		final List<GridExecutionInitializer> stocks = new ArrayList<>();
		final List<GridExecutionInitializer> eods = new ArrayList<>();
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				stocks.add(executionDescription.createGridExecution(period));
			} else {
				eods.add(executionDescription.createGridExecution(period));
			}
		}
		return new SimulatorSettingsGridList(stockStorage, period, stocks, eods, false);
	}

	public SimulatorSettingsGeneticList generateGeneticSettings(StockStorage stockStorage, FromToPeriod period) throws BadParameterException {
		final List<GeneticExecutionInitializer> stocks = new ArrayList<>();
		final List<GeneticExecutionInitializer> eods = new ArrayList<>();
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				stocks.add(executionDescription.createGeneticExecution(period));
			} else {
				eods.add(executionDescription.createGeneticExecution(period));
			}
		}
		return new SimulatorSettingsGeneticList(stockStorage, period, stocks, eods);
	}
}
