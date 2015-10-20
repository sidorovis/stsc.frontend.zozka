package stsc.frontend.zozka.common.panes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.models.ObservableStrategySelector;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;
import stsc.frontend.zozka.common.models.SimulatorSettingsModelTest;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsWithDistanceSelector;
import stsc.general.strategy.selector.StrategyFilteringSelector;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestStrategiesPane extends Application {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	final SplitPane chartPane = new SplitPane();

	private JFreeChart addChartPane() {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", null, true, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.getItems().add(sn);
		return chart;
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
		final StatisticsWithDistanceSelector selectorBase = new StatisticsWithDistanceSelector(10, 20, costFunction);
		selectorBase.withDistanceParameter(MetricType.winProb, 0.75);
		selectorBase.withDistanceParameter(MetricType.avGain, 0.075);
		selectorBase.withDistanceParameter(MetricType.avWin, 0.075);
		selectorBase.withDistanceParameter(MetricType.startMonthMax, 0.45);
		selectorBase.withDistanceParameter(MetricType.avLoss, 0.7);
		final StrategyFilteringSelector filteringSelector = new StrategyFilteringSelector(selectorBase);
		// filteringSelector.withDoubleMinFilter(MetricType.freq, 0.01);
		// filteringSelector.withDoubleMinFilter(MetricType.winProb, 0.2);
		final ObservableStrategySelector selector = new ObservableStrategySelector(filteringSelector);
		return selector;
	}

	@Override
	public void start(Stage parent) throws Exception {
		chartPane.setOrientation(Orientation.VERTICAL);
		chartPane.setDividerPosition(0, 0.5);
		final JFreeChart chart = addChartPane();
		final Scene scene = new Scene(chartPane);

		final StockStorage stockStorage = StockStorageMock.getStockStorage();

		final SimulatorSettingsModel simulatorSettingsModel = SimulatorSettingsModelTest.createSimulatorSettingsModel();
		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2010");

		final StrategiesPane sp = StrategiesPane.getBuilder(). //
				setPeriod(period). //
				setSimulatorSettingsModel(simulatorSettingsModel). //
				setStockStorage(stockStorage). //
				setjFreeChart(chart). //
				setSimulationType(SimulationType.GRID). //
				setObservableStrategySelector(createSelector()). //
				build();

		chartPane.getItems().add(sp);
		parent.setScene(scene);
		parent.setMinHeight(800);
		parent.setMinWidth(800);
		parent.setWidth(800);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStrategiesPane.class, (java.lang.String[]) null);
	}
}
