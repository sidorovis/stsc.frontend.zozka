package stsc.frontend.zozka.common.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettingsImpl;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticListImpl;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

/**
 * This class store GUI version of {@link SimulatorSettingsImpl} (+period field and etc.).
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
		final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				factory.addStock(executionDescription.createGridExecution(period));
			} else {
				factory.addEod(executionDescription.createGridExecution(period));
			}
		}
		return factory.getList();
	}

	public SimulatorSettingsGeneticListImpl generateGeneticSettings(StockStorage stockStorage, FromToPeriod period) throws BadParameterException {
		final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
		for (ExecutionDescription executionDescription : model) {
			if (executionDescription.getAlgorithmType().isStock()) {
				factory.addStock(executionDescription.createGeneticExecution(period));
			} else {
				factory.addEod(executionDescription.createGeneticExecution(period));
			}
		}
		return factory.getList();
	}
}
