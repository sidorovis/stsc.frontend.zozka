package stsc.frontend.zozka.charts.panes;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.OHLCDataset;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import stsc.common.FromToPeriod;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.frontend.zozka.charts.models.CandleSticksChartDataset;
import stsc.frontend.zozka.charts.models.CurveChartSetting;
import stsc.frontend.zozka.charts.models.CurveTimeSerieSetting;
import stsc.frontend.zozka.charts.models.common.DatasetForStock;

/**
 * Common container for drawing curves on GUI. Could be used to draw next curves
 * with possibility hide them using table view algorithms representations.<br/>
 * 1. {@link #createPaneForAdjectiveClose(Stock)} /
 * {@link #createPaneForAdjectiveClose(Stock, FromToPeriod)} - draws
 * {@link CandleSticksChartDataset} based chart. <br/>
 * 2.
 * {@link #createPaneForOnStockAlgorithm(Stock, FromToPeriod, List, SignalsStorage)}
 * - draws candlesticks chart for stock and execution results on that stock.
 * <br/>
 * 3. {@link #createPaneForOnEodAlgorithm(FromToPeriod, List, SignalsStorage)} -
 * draws candlesticks chart for end of day's executions.
 */
public final class CurvesViewPane {

	private final CurveChartSetting chartDataset;
	private final Parent gui;

	private final ObservableList<CurveChartSetting> tableModel = FXCollections.observableArrayList();
	@FXML
	private TableView<CurveChartSetting> configurationTable;
	@FXML
	private TableColumn<CurveChartSetting, Boolean> showAlgorithmColumn;
	@FXML
	private TableColumn<CurveChartSetting, String> titleColumn;
	@FXML
	private BorderPane chartPane;

	public static CurvesViewPane createPaneForAdjectiveClose(Stock stock) throws IOException {
		final CandleSticksChartDataset chartDataset = new CandleSticksChartDataset(new DatasetForStock(stock));
		final CurvesViewPane result = new CurvesViewPane(stock, chartDataset);
		result.getTableModel().add(new CurveTimeSerieSetting("Adjective Close", stock, 1));
		result.addChartForStock(chartDataset.getTimeSeriesCollection());
		return result;
	}

	public static CurvesViewPane createPaneForAdjectiveClose(Stock stock, FromToPeriod period) throws IOException {
		final CandleSticksChartDataset chartDataset = new CandleSticksChartDataset(new DatasetForStock(stock, period));
		final CurvesViewPane result = new CurvesViewPane(stock, chartDataset);
		result.getTableModel().add(new CurveTimeSerieSetting("Adjective Close", stock, 1, period));
		result.addChartForStock(chartDataset.getTimeSeriesCollection());
		return result;
	}

	public static CurvesViewPane createPaneForOnStockAlgorithm(Stock stock, FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage) throws IOException {
		final CandleSticksChartDataset chartDataset = new CandleSticksChartDataset(new DatasetForStock(stock, period));
		final CurvesViewPane result = new CurvesViewPane(stock, chartDataset, period);
		result.loadTableModel(stock.getInstrumentName(), executionsName, signalsStorage);
		result.addChartForStock(chartDataset.getTimeSeriesCollection());
		return result;
	}

	public static CurvesViewPane createPaneForOnEodAlgorithm(FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage) throws IOException {
		final CurvesViewPane result = new CurvesViewPane(period, signalsStorage);
		result.loadTableModel(executionsName, signalsStorage);
		result.addChartForEod();
		return result;
	}

	private CurvesViewPane(Stock stock, CandleSticksChartDataset chartDataset) throws IOException {
		this.chartDataset = chartDataset;
		this.gui = getGui();
		getTableModel().add(chartDataset);
	}

	private CurvesViewPane(Stock stock, CandleSticksChartDataset chartDataset, FromToPeriod period) throws IOException {
		this.chartDataset = chartDataset;
		this.gui = getGui();
	}

	private CurvesViewPane(FromToPeriod period, SignalsStorage signalsStorage) throws IOException {
		this.chartDataset = new CurveTimeSerieSetting(false, "", 0, signalsStorage);
		this.gui = getGui();
	}

	private Parent getGui() throws IOException {
		final URL location = CurvesViewPane.class.getResource("stock_view_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent result = loader.load();
		initialize();
		return result;
	}

	private void initialize() {
		validateGui();
		configurationTable.setItems(getTableModel());
		showAlgorithmColumn.setCellValueFactory(cellData -> cellData.getValue().showAlgorithmProperty());
		showAlgorithmColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showAlgorithmColumn));
		showAlgorithmColumn.setOnEditCommit(e -> e.getRowValue().setShowAlgorithm(e.getNewValue()));
		showAlgorithmColumn.setEditable(true);
		configurationTable.setEditable(true);
		titleColumn.setCellValueFactory(cellData -> cellData.getValue().propertyTitle());
	}

	private void validateGui() {
		assert configurationTable != null : "fx:id=\"configurationTable\" was not injected: check your FXML file.";
		assert showAlgorithmColumn != null : "fx:id=\"showAlgorithmColumn\" was not injected: check your FXML file.";
		assert titleColumn != null : "fx:id=\"titleColumn\" was not injected: check your FXML file.";
		assert chartPane != null : "fx:id=\"chartPane\" was not injected: check your FXML file.";
	}

	private void loadTableModel(final String stockName, final List<String> executionsName, SignalsStorage signalsStorage) {
		int index = 1;
		for (String executionName : executionsName) {
			getTableModel().add(new CurveTimeSerieSetting(executionName, stockName, index, signalsStorage));
			index += 1;
		}
	}

	private void loadTableModel(final List<String> executionsName, SignalsStorage signalsStorage) {
		int index = 1;
		for (String executionName : executionsName) {
			getTableModel().add(new CurveTimeSerieSetting(true, executionName, index, signalsStorage));
			index += 1;
		}
	}

	private void addChartForStock(OHLCDataset ohlcDataset) {
		final JFreeChart chart = ChartFactory.createCandlestickChart("Price", "", "", ohlcDataset, true);
		chart.getXYPlot().setRenderer(0, getChartDataset().getRenderer());
		for (CurveChartSetting serie : getTableModel()) {
			final int index = serie.getIndex();
			chart.getXYPlot().setDataset(index, serie.getTimeSeriesCollection());
			chart.getXYPlot().setRenderer(index, serie.getRenderer());
			chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		final SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);
	}

	private void addChartForEod() {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", dataset, true, true, false);
		chart.getXYPlot().setRenderer(0, getChartDataset().getRenderer());
		for (CurveChartSetting serie : getTableModel()) {
			final int index = serie.getIndex();
			chart.getXYPlot().setDataset(index, serie.getTimeSeriesCollection());
			chart.getXYPlot().setRenderer(index, serie.getRenderer());
			chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		final SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);
	}

	public Parent getMainPane() {
		return gui;
	}

	private CurveChartSetting getChartDataset() {
		return chartDataset;
	}

	/**
	 * protected for tests purposes
	 */
	ObservableList<CurveChartSetting> getTableModel() {
		return tableModel;
	}

}
