package stsc.frontend.zozka.applications.strategy.selector;

import java.io.IOException;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ObservableStrategySelector;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.panes.strategies.MetricsDrawerImpl;
import stsc.frontend.zozka.common.panes.strategies.StrategiesPane;
import stsc.frontend.zozka.components.panes.PeriodAndDatafeedPane;
import stsc.frontend.zozka.components.simulation.dialogs.CreateSimulatorSettingsDialog;
import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsIntervalImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsWithSettingsClusterDistanceSelector;
import stsc.general.strategy.selector.StrategyFilteringSelector;

public class ZozkaStrategySelector extends Application {

	private Stage owner;
	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();
	private final BorderPane chartPane = new BorderPane();
	private JFreeChart chart;

	private PeriodAndDatafeedPane periodAndDatafeedController;
	private CreateSimulatorSettingsDialog simulatorSettingsController;

	private void fillTopPart() throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedPane(owner);
		simulatorSettingsController = new CreateSimulatorSettingsDialog(owner);
		pane.setTop(periodAndDatafeedController.getGui());
		final SplitPane centerSplitPane = new SplitPane();
		centerSplitPane.getItems().add(simulatorSettingsController.getGui());
		centerSplitPane.getItems().add(chartPane);
		addChartPane();
		pane.setCenter(centerSplitPane);

		final HBox hbox = new HBox();
		addButtons(hbox);
		hbox.setAlignment(Pos.CENTER);
		pane.setBottom(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER);
		splitPane.getItems().add(pane);
	}

	private void addButtons(HBox hbox) {
		final Button localGridSearchButton = new Button("Local Grid Search");
		localGridSearchButton.setOnAction(e -> {
			runLocalGridSearch();
		});

		final Button localGeneticSearchButton = new Button("Local Genetic Search");
		localGeneticSearchButton.setOnAction(e -> {
			runLocalGeneticSearch();
		});

		final Button distributedGridSearchButton = new Button("Distributed Grid Search");
		distributedGridSearchButton.setOnAction(e -> {
			new TextAreaDialog(new Exception("Distributed Grid Search Not Implemented Yet"));
		});
		final Button distributedGeneticSearchButton = new Button("Distributed Genetic Search");
		distributedGeneticSearchButton.setOnAction(e -> {
			new TextAreaDialog(new Exception("Distributed Genetic Search Not Implemented Yet"));
		});

		hbox.getChildren().add(localGridSearchButton);
		hbox.getChildren().add(localGeneticSearchButton);
		hbox.getChildren().add(distributedGridSearchButton);
		hbox.getChildren().add(distributedGeneticSearchButton);
	}

	private void addChartPane() {
		this.chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", null, true, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		final SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);
	}

	private void runLocalGridSearch() {
		periodAndDatafeedController.loadStockStorage(eh -> Platform.runLater(() -> {
			localSearch(periodAndDatafeedController.getStockStorage(), SimulationType.GRID, "Grid");
		}));
	}

	private void runLocalGeneticSearch() {
		periodAndDatafeedController.loadStockStorage(eh -> Platform.runLater(() -> {
			localSearch(periodAndDatafeedController.getStockStorage(), SimulationType.GENETIC, "Genetic");
		}));
	}

	private void localSearch(StockStorage stockStorage, SimulationType simulationType, String tabName) {
		if (stockStorage == null)
			return;
		final FromToPeriod period = periodAndDatafeedController.getPeriod();
		try {
			final StrategiesPane pane = StrategiesPane.getBuilder(). //
					setPeriod(period). //
					setStockStorage(stockStorage). //
					setSimulationType(simulationType). //
					setSimulatorSettingsModel(simulatorSettingsController.getModel()). //
					setMetricsDrawer(new MetricsDrawerImpl(chart)). //
					setObservableStrategySelector(createSelector()). //
					build();
			final Tab tab = new Tab(tabName + "(" + (new Date()) + ")");
			tab.setContent(pane);
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);
		} catch (Exception e) {
			new TextAreaDialog(e);
		}
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
		final StatisticsWithSettingsClusterDistanceSelector selectorBase = new StatisticsWithSettingsClusterDistanceSelector(50, 20, new SimulatorSettingsIntervalImpl(),
				costFunction).setEpsilon(25.0);
		// selectorBase.withDistanceParameter(MetricType.winProb, 0.75);
		// selectorBase.withDistanceParameter(MetricType.avGain, 0.075);
		// selectorBase.withDistanceParameter(MetricType.avWin, 0.075);
		// selectorBase.withDistanceParameter(MetricType.startMonthMax, 0.45);
		// selectorBase.withDistanceParameter(MetricType.avLoss, 0.7);
		final StrategyFilteringSelector filteringSelector = new StrategyFilteringSelector(selectorBase);
		filteringSelector.withDoubleMinFilter(MetricType.freq, 0.01);
		filteringSelector.withDoubleMinFilter(MetricType.winProb, 0.1);
		final ObservableStrategySelector selector = new ObservableStrategySelector(filteringSelector);
		return selector;
	}

	private void fillBottomPart() {
		splitPane.getItems().add(tabPane);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.owner = stage;
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.1f);
		stage.setMinWidth(1200);
		stage.setMinHeight(800);
		fillTopPart();
		fillBottomPart();

		final Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(ZozkaStrategySelector.class, args);
	}

}
