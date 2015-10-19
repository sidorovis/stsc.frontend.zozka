package stsc.frontend.zozka.charts.panes;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import stsc.common.FromToPeriod;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * {@link EquityPane} is a pane that contain two parts: <br/>
 * 1. chart for trading strategy equity on
 * {@link Metrics#getEquityCurveInMoney()}. <br/>
 * 2. table with {@link Metrics} (mapped metric name - metric value).
 */
public final class EquityPane {

	private final static class MetricsElement {

		private final StringProperty name;
		private final StringProperty value;

		public MetricsElement(final String name, final String value) {
			this.name = new SimpleStringProperty(name);
			this.value = new SimpleStringProperty(value);
		}

		public StringProperty propertyName() {
			return name;
		}

		public StringProperty propertyValue() {
			return value;
		}
	}

	private final Parent gui;
	@FXML
	private BorderPane chartPane;

	private final ObservableList<MetricsElement> metricsTableModel = FXCollections.observableArrayList();
	@FXML
	private TableView<MetricsElement> metricsTable;
	@FXML
	private TableColumn<MetricsElement, String> metricsName;
	@FXML
	private TableColumn<MetricsElement, String> metricsValue;

	public EquityPane(final Stage owner, Metrics metrics, FromToPeriod period) throws IOException {
		final URL location = EquityPane.class.getResource("equity_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();

		initialize();
		loadStatisticsTableModel(metrics);
		setChartPane(metrics);
	}

	private void initialize() {
		validateGui();
		metricsTable.setItems(metricsTableModel);
		metricsName.setCellValueFactory(cellData -> cellData.getValue().propertyName());
		metricsValue.setCellValueFactory(cellData -> cellData.getValue().propertyValue());
	}

	private void loadStatisticsTableModel(Metrics metrics) {
		for (Map.Entry<MetricType, Integer> e : metrics.getIntegerMetrics().entrySet()) {
			metricsTableModel.add(new MetricsElement(e.getKey().name(), e.getValue().toString()));
		}
		for (Map.Entry<MetricType, Double> e : metrics.getDoubleMetrics().entrySet()) {
			metricsTableModel.add(new MetricsElement(e.getKey().name(), e.getValue().toString()));
		}
	}

	private void validateGui() {
		assert chartPane != null : "fx:id=\"chartPane\" was not injected: check your FXML file.";
		assert metricsTable != null : "fx:id=\"metricsTable\" was not injected: check your FXML file.";
		assert metricsName != null : "fx:id=\"metricsName\" was not injected: check your FXML file.";
		assert metricsValue != null : "fx:id=\"metricsValue\" was not injected: check your FXML file.";
	}

	private JFreeChart setChartPane(Metrics metrics) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve");

		final EquityCurve equityCurveInMoney = metrics.getEquityCurveInMoney();

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final EquityCurve.Element e = equityCurveInMoney.get(i);
			ts.add(new Day(e.date), e.value);
		}
		dataset.addSeries(ts);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", dataset, false, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);

		return chart;
	}

	public Parent getMainPane() {
		return gui;
	}
}
