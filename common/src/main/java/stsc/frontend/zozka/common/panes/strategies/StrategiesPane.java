package stsc.frontend.zozka.common.panes.strategies;

import java.rmi.UnexpectedException;

import org.apache.commons.lang3.Validate;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.MetricsDrawer;
import stsc.frontend.zozka.common.models.ObservableStrategySelector;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;
import stsc.frontend.zozka.common.models.StatisticsDescription;
import stsc.frontend.zozka.common.panes.ProgressWithStopPane;
import stsc.general.simulator.SimulatorFactoryImpl;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticListImpl;
import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcher;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * This is a complicated GUI component for strategy search mechanism. <br/>
 * Currently could work with {@link StrategyGridSearcher} and {@link StrategyGeneticSearcher}. <br/>
 * Represented as table of {@link TradingStrategy} (GUI object: {@link StatisticsDescription}). <br/>
 * Also require {@link MetricsDrawer} GUI component for drawing selected by user {@link TradingStrategy} from table. <br/>
 * Table consist next columns: <br/>
 * <ID> - personal id of the strategy. <br/>
 * <Cost Function> - result double value for CostFunction (could be the same as used at {@link ObservableStrategySelector}) - just to sort strategies in any
 * convenient way. <br/>
 * {@link MetricType}'s into order that was defined at the {@link MetricType} enumeration. <br/>
 * User can see selected by pre-specified selector strategies into convinient table view, observe any strategy details (using one click and double click
 * actions).
 */
public final class StrategiesPane extends BorderPane {

	private final ObservableStrategySelector selector;
	private final MetricsDrawer metricsDrawer;

	private final ObservableList<StatisticsDescription> model = FXCollections.observableArrayList();
	private final ProgressWithStopPane progressPane;
	private final CostFunction costFunction;
	private final int threadAmount;

	private final TableView<StatisticsDescription> table = new TableView<>();

	public static StrategiesPaneBuilder getBuilder() {
		return new StrategiesPaneBuilder();
	}

	StrategiesPane(final StrategiesPaneBuilder spb) throws BadAlgorithmException, UnexpectedException, InterruptedException, BadParameterException {
		Validate.notNull(spb.getObservableStrategySelector());
		Validate.notNull(spb.getMetricsDrawer());
		Validate.notNull(spb.getCreateCostFunction());
		Validate.isTrue(spb.getThreadAmount() > 0, "Thread amount should be bigger then zero.");

		this.selector = spb.getObservableStrategySelector();
		this.metricsDrawer = spb.getMetricsDrawer();
		this.progressPane = new ProgressWithStopPane();
		this.costFunction = spb.getCreateCostFunction();
		this.threadAmount = spb.getThreadAmount();
		createTopElements();
		createEmptyTable();
		setupControlPane(startCalculation(spb));
	}

	private StrategySearcher startCalculation(StrategiesPaneBuilder spb) throws BadAlgorithmException, InterruptedException, BadParameterException {
		return startCalculation(spb.getPeriod(), spb.getSimulatorSettingsModel(), spb.getStockStorage(), spb.getSimulationType());
	}

	private StrategySearcher startCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage,
			SimulationType simulationType) throws BadAlgorithmException, InterruptedException, BadParameterException {
		switch (simulationType) {
		case GENETIC:
			return startGeneticCalculation(period, settingsModel, stockStorage);
		default:
		}
		return startGridCalculation(period, settingsModel, stockStorage);
	}

	private void setupControlPane(final StrategySearcher strategySearcher) throws UnexpectedException {
		progressPane.setOnStopButtonAction(() -> {
			strategySearcher.stopSearch();
		});
		strategySearcher.addIndicatorProgress(new IndicatorProgressListener() {
			@Override
			public void processed(double percent) {
				Platform.runLater(() -> {
					if (Double.compare(1.0, percent) <= 0) {
						progressPane.hide();
					} else {
						progressPane.setIndicatorProgress(percent);
					}
				});
			}
		});
	}

	private void createTopElements() {
		this.setTop(progressPane);
	}

	private void createEmptyTable() {
		{
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> new SimpleIntegerProperty((int) cellData.getValue().getId()));
			column.setText("ID");
			column.setEditable(false);
			table.getColumns().add(column);
		}
		{
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> cellData.getValue().getCostFunctionResult());
			column.setText("<Cost Function>");
			column.setEditable(false);
			table.getColumns().add(column);
		}
		for (MetricType mt : MetricType.values()) {
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> cellData.getValue().getProperty(mt));
			column.setText(mt.name());
			column.setEditable(false);
			table.getColumns().add(column);
		}

		setCenter(table);
		table.setItems(model);
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.getSelectionModel().setCellSelectionEnabled(false);
		table.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
			@Override
			public void onChanged(final ListChangeListener.Change<? extends Integer> c) {
				tableSelectionChanged(c.getList());
			}
		});
		table.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				showTextField();
			}
		});
	}

	private void tableSelectionChanged(ObservableList<? extends Integer> observableList) {
		final int selected = table.getSelectionModel().getSelectedIndex();
		synchronized (model) {
			if (selected >= 0 && selected < model.size()) {
				final StatisticsDescription statisticsDescription = model.get(selected);
				metricsDrawer.drawMetric(statisticsDescription.getId(), statisticsDescription.getMetrics());
			}
		}
	}

	private void showTextField() {
		final StatisticsDescription sd = table.getSelectionModel().getSelectedItem();
		if (sd != null) {
			final long id = sd.getId();
			new TextAreaDialog("Strategy: " + String.valueOf(id), sd.toString());
		}
	}

	private StrategySearcher startGridCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException, BadParameterException {
		final SimulatorSettingsGridList list = settingsModel.generateGridSettings(stockStorage, period);
		checkThatMaxPossibleSizeCorrect(list.size());
		addListenerOnChanged(selector.getObservableStrategyList());

		return StrategyGridSearcher.getBuilder(). //
				setSimulatorSettingsGridList(list). //
				setSelector(selector). //
				setThreadAmount(threadAmount). //
				build();
	}

	private StrategySearcher startGeneticCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException, InterruptedException, BadParameterException {
		final SimulatorSettingsGeneticListImpl list = settingsModel.generateGeneticSettings(stockStorage, period);
		checkThatMaxPossibleSizeCorrect(selector.maxPossibleAmount());
		addListenerOnChanged(selector.getObservableStrategyList());

		final StrategyGeneticSearcher sgs = createStrategyGeneticSearcher(list, selector);

		// TODO fix this thread creating process, it is very unstable (but
		// OK for now).
		new Thread(() -> {
			try {
				sgs.waitAndGetSelector();
			} catch (Exception e) {
				Platform.runLater(() -> {
					new TextAreaDialog(e);
				});
			}
		}).start();
		return sgs;
	}

	private StrategyGeneticSearcher createStrategyGeneticSearcher(SimulatorSettingsGeneticListImpl list, ObservableStrategySelector selector) {
		return StrategyGeneticSearcher.getBuilder(). //
				withThreadAmount(threadAmount). //
				withGeneticList(list). //
				withStrategySelector(selector).//
				withPopulationCostFunction(costFunction). //
				withPopulationSize(300). //
				withSimulatorFactory(new SimulatorFactoryImpl()). //
				build();
	}

	private void addListenerOnChanged(ObservableList<TradingStrategy> strategyList) {
		strategyList.addListener(new StatisticsDescriptionObservableModelUpdater(model, costFunction));
	}

	private void checkThatMaxPossibleSizeCorrect(long maxPossibleSize) throws BadAlgorithmException {
		if (maxPossibleSize == 0) {
			throw new BadAlgorithmException("Simulation Settings Grid size equal to Zero.");
		}
	}
}
