package stsc.frontend.zozka.panes;

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

public class EquityPane {

	private static class StatisticElement {
		private final StringProperty name;
		private final StringProperty value;

		public StatisticElement(String name, String value) {
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

	private JFreeChart chart;

	private final Parent gui;
	@FXML
	private BorderPane chartPane;

	private final ObservableList<StatisticElement> statisticsTableModel = FXCollections.observableArrayList();
	@FXML
	private TableView<StatisticElement> statisticsTable;
	@FXML
	private TableColumn<StatisticElement, String> statisticName;
	@FXML
	private TableColumn<StatisticElement, String> statisticValue;

	public EquityPane(final Stage owner, Metrics metrics, FromToPeriod period) throws IOException {
		final URL location = EquityPane.class.getResource("04_equity_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();

		initialize();
		loadStatisticsTableModel(metrics);
		setChartPane(metrics);
	}

	private void initialize() {
		validateGui();
		statisticsTable.setItems(statisticsTableModel);
		statisticName.setCellValueFactory(cellData -> cellData.getValue().propertyName());
		statisticValue.setCellValueFactory(cellData -> cellData.getValue().propertyValue());
	}

	private void loadStatisticsTableModel(Metrics metrics) {
		for (Map.Entry<MetricType, Integer> e : metrics.getIntegerMetrics().entrySet()) {
			statisticsTableModel.add(new StatisticElement(e.getKey().name(), e.getValue().toString()));
		}
		for (Map.Entry<MetricType, Double> e : metrics.getDoubleMetrics().entrySet()) {
			statisticsTableModel.add(new StatisticElement(e.getKey().name(), e.getValue().toString()));
		}
	}

	private void validateGui() {
		assert chartPane != null : "fx:id=\"chartPane\" was not injected: check your FXML file.";
		assert statisticsTable != null : "fx:id=\"statisticsTable\" was not injected: check your FXML file.";
		assert statisticName != null : "fx:id=\"statisticName\" was not injected: check your FXML file.";
		assert statisticValue != null : "fx:id=\"statisticValue\" was not injected: check your FXML file.";
	}

	private void setChartPane(Metrics metrics) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve");

		final EquityCurve equityCurveInMoney = metrics.getEquityCurveInMoney();

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final EquityCurve.Element e = equityCurveInMoney.get(i);
			ts.add(new Day(e.date), e.value);
		}
		dataset.addSeries(ts);

		this.chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", dataset, false, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);
	}

	public Parent getMainPane() {
		return gui;
	}
}
