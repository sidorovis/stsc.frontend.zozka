package stsc.frontend.zozka.common.panes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestStrategiesPane extends Application {

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

	@Override
	public void start(Stage parent) throws Exception {
		chartPane.setOrientation(Orientation.VERTICAL);
		chartPane.setDividerPosition(0, 0.5);
		final JFreeChart chart = addChartPane();
		final Scene scene = new Scene(chartPane);

		final StockStorage yfss = StockStorageMock.getStockStorage();

		SimulatorSettingsModel model = new SimulatorSettingsModel();
		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2010");
		model.loadFromFile(getClass().getResourceAsStream("strategy_selector/size_2280"));

		final StrategiesPane sp = new StrategiesPane(period, model, yfss, chart, SimulationType.GENETIC);
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
