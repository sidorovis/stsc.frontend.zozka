package stsc.frontend.zozka.panes;

import java.awt.Color;
import java.rmi.UnexpectedException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.dialogs.TextAreaDialog;
import stsc.frontend.zozka.gui.models.ObservableStrategySelector;
import stsc.frontend.zozka.gui.models.SerieXYToolTipGenerator;
import stsc.frontend.zozka.gui.models.SimulationType;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcher;
import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcherBuilder;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StatisticsWithDistanceSelector;
import stsc.general.strategy.selector.StrategyFilteringSelector;

public class StrategiesPane extends BorderPane {

	private static Metrics METRICS = new Metrics(Metrics.getBuilder());

	public static class StatisticsDescription {
		private static DecimalFormat df = new DecimalFormat("0.0000");

		private final TradingStrategy tradingStrategy;

		public StatisticsDescription(TradingStrategy tradingStrategy) {
			this.tradingStrategy = tradingStrategy;
		}

		public long getId() {
			return tradingStrategy.getSettings().getId();
		}

		public SimpleDoubleProperty getProperty(MetricType metricType) {
			final Double value = tradingStrategy.getMetrics().getMetric(metricType);
			df.format(value);
			return new SimpleDoubleProperty(Double.valueOf(df.format(value)));
		}

		@Override
		public String toString() {
			return tradingStrategy.getSettings().toString();
		}
	}

	private final Stage owner;
	private final ObservableList<StatisticsDescription> model = FXCollections.observableArrayList();
	private final ProgressWithStopPane controlPane;
	private final TableView<StatisticsDescription> table = new TableView<>();
	private final JFreeChart chart;

	public StrategiesPane(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage, JFreeChart chart,
			SimulationType simulationType) throws BadAlgorithmException, UnexpectedException, InterruptedException {
		this.owner = owner;
		this.chart = chart;
		this.controlPane = new ProgressWithStopPane();
		createTopElements();
		createEmptyTable();
		setupControlPane(startCalculation(period, model, stockStorage, simulationType));
	}

	private void setupControlPane(Optional<StrategySearcher> ss) throws UnexpectedException {
		if (!ss.isPresent()) {
			throw new UnexpectedException("Calculations are not started, problem on StrategySearch creation phaze.");
		}
		controlPane.setOnStopButtonAction(() -> {
			if (ss.isPresent()) {
				ss.get().stopSearch();
			}
		});
		ss.get().addIndicatorProgress(new IndicatorProgressListener() {
			@Override
			public void processed(double percent) {
				Platform.runLater(() -> {
					if (Double.compare(1.0, percent) <= 0) {
						controlPane.hide();
					} else {
						controlPane.setIndicatorProgress(percent);
					}
				});
			}
		});
	}

	private void createTopElements() {
		this.setTop(controlPane);
	}

	private void createEmptyTable() {
		{
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> new SimpleIntegerProperty((int) cellData.getValue().getId()));
			column.setText("Id");
			column.setEditable(false);
			table.getColumns().add(column);
		}

		for (Map.Entry<MetricType, Integer> e : METRICS.getIntegerMetrics().entrySet()) {
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> cellData.getValue().getProperty(e.getKey()));
			column.setText(e.getKey().name());
			column.setEditable(false);
			table.getColumns().add(column);
		}

		for (Map.Entry<MetricType, Double> e : METRICS.getDoubleMetrics().entrySet()) {
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> cellData.getValue().getProperty(e.getKey()));
			column.setText(e.getKey().name());
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
				final StatisticsDescription sd = model.get(selected);
				drawStatistics(sd.tradingStrategy.getSettings().getId(), sd.tradingStrategy.getMetrics());
			}
		}
	}

	private void showTextField() {
		final StatisticsDescription sd = table.getSelectionModel().getSelectedItem();
		if (sd != null) {
			final long id = sd.getId();
			new TextAreaDialog(owner, "Strategy: " + String.valueOf(id), sd.toString()).show();
		}
	}

	private void drawStatistics(long id, Metrics metrics) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve:" + String.valueOf(id));

		final EquityCurve equityCurveInMoney = metrics.getEquityCurveInMoney();

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final EquityCurve.Element e = equityCurveInMoney.get(i);
			ts.add(new Day(e.date), e.value);
		}
		dataset.addSeries(ts);

		chart.getXYPlot().setDataset(dataset);
		final XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(String.valueOf(id)));
		renderer.setSeriesPaint(0, Color.RED);
		chart.getXYPlot().setRenderer(renderer);
		chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}

	private Optional<StrategySearcher> startCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage,
			SimulationType simulationType) throws BadAlgorithmException, InterruptedException {
		if (simulationType.equals(SimulationType.GRID)) {
			return startGridCalculation(period, settingsModel, stockStorage);
		} else {
			return startGeneticCalculation(period, settingsModel, stockStorage);
		}
	}

	private Optional<StrategySearcher> startGridCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException {
		try {
			final SimulatorSettingsGridList list = settingsModel.generateGridSettings(stockStorage, period);
			checkThatMaxPossibleSizeCorrect(list.size());
			final ObservableStrategySelector selector = createSelector();

			addListenerOnChanged(selector.getObservableStrategyList());
			return Optional.of(new StrategyGridSearcher(list, selector, 4));
		} catch (BadParameterException e1) {
			Dialogs.create().owner(owner).showException(e1);
		}
		return Optional.empty();
	}

	private Optional<StrategySearcher> startGeneticCalculation(FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException, InterruptedException {
		try {
			final SimulatorSettingsGeneticList list = settingsModel.generateGeneticSettings(stockStorage, period);
			final ObservableStrategySelector selector = createSelector();
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
						Dialogs.create().owner(owner).showException(e);
					});
				}
			}).start();
			return Optional.of(sgs);
		} catch (BadParameterException badParameterException) {
			Dialogs.create().owner(owner).showException(badParameterException);
		}
		return Optional.empty();
	}

	private StrategyGeneticSearcher createStrategyGeneticSearcher(SimulatorSettingsGeneticList list, ObservableStrategySelector selector) {
		final StrategyGeneticSearcherBuilder builder = StrategyGeneticSearcher.getBuilder();
		builder.withThreadAmount(4).withSimulatorSettings(list);
		builder.withStrategySelector(selector);
		builder.withPopulationCostFunction(new CostWeightedProductFunction());
		builder.withPopulationSize(300);
		return builder.build();
	}

	private ObservableStrategySelector createSelector() {
		final CostWeightedSumFunction costFunction = new CostWeightedSumFunction();
		costFunction.withParameter(MetricType.winProb, 4.0);
		costFunction.withParameter(MetricType.ddValueAvGain, -1.0);
		costFunction.withParameter(MetricType.avGain, 1.0);
		costFunction.withParameter(MetricType.kelly, 1.0);
		costFunction.withParameter(MetricType.avWin, 1.0);
		costFunction.withParameter(MetricType.avLoss, -1.0);
		costFunction.withParameter(MetricType.freq, 1.0);
		costFunction.withParameter(MetricType.maxLoss, -1.0);
		final StatisticsWithDistanceSelector selectorBase = new StatisticsWithDistanceSelector(100, 3, costFunction);
		selectorBase.withDistanceParameter(MetricType.winProb, 0.75);
		selectorBase.withDistanceParameter(MetricType.avGain, 0.075);
		selectorBase.withDistanceParameter(MetricType.avWin, 0.075);
		selectorBase.withDistanceParameter(MetricType.startMonthMax, 0.45);
		selectorBase.withDistanceParameter(MetricType.avLoss, 0.7);
		final StrategyFilteringSelector filteringSelector = new StrategyFilteringSelector(selectorBase);
		filteringSelector.withDoubleMinFilter(MetricType.freq, 0.01);
		filteringSelector.withDoubleMinFilter(MetricType.winProb, 0.2);
		final ObservableStrategySelector selector = new ObservableStrategySelector(filteringSelector);
		return selector;
	}

	private void addListenerOnChanged(ObservableList<TradingStrategy> strategyList) {
		strategyList.addListener(new ListChangeListener<TradingStrategy>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
				processOnChanged(c);
			}
		});
	}

	private void checkThatMaxPossibleSizeCorrect(long maxPossibleSize) throws BadAlgorithmException {
		if (maxPossibleSize == 0) {
			throw new BadAlgorithmException("Simulation Settings Grid size equal to Zero.");
		}
	}

	protected void processOnChanged(ListChangeListener.Change<? extends TradingStrategy> listChange) {
		final ListChangeListener.Change<? extends TradingStrategy> change = listChange;
		synchronized (model) {
			while (change.next()) {
				if (change.wasAdded()) {
					for (TradingStrategy ts : change.getAddedSubList()) {
						Platform.runLater(() -> {
							model.add(new StatisticsDescription(ts));
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
