package stsc.frontend.zozka.components.models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.GeneticList;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public final class SimulationsDescription {

	private Path datafeedPath = Paths.get("./");
	private StockStorage stockStorage;
	private FromToPeriod period;

	private ObservableList<ExecutionDescription> executionDescriptions = FXCollections.observableArrayList();

	public ObservableList<ExecutionDescription> getExecutionDescriptions() {
		return executionDescriptions;
	}

	public Path getDatafeedPath() {
		return datafeedPath;
	}

	public void setDatafeedPath(Path datafeed) {
		datafeedPath = datafeed;
	}

	public void setPeriod(Date from, Date to) {
		period = new FromToPeriod(from, to);
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public void setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public SimulatorSettingsGridList getGrid() throws BadParameterException {
		final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
		for (ExecutionDescription ed : executionDescriptions) {
			GridExecutionInitializer ei = ed.createGridExecution(period);
			if (ed.getAlgorithmType().isStock()) {
				factory.addStock(ei);
			} else {
				factory.addEod(ei);
			}
		}
		return factory.getList();
	}

	public GeneticList getGenetic() throws BadParameterException {
		final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
		for (ExecutionDescription ed : executionDescriptions) {
			GeneticExecutionInitializer ei = ed.createGeneticExecution(period);
			if (ed.getAlgorithmType().isStock()) {
				factory.addStock(ei);
			} else {
				factory.addEod(ei);
			}
		}
		return factory.getList();
	}
}
