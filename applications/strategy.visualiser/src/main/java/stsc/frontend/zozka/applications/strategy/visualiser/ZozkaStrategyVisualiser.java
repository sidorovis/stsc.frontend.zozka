package stsc.frontend.zozka.applications.strategy.visualiser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import stsc.algorithms.Output;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.panes.CurvesViewPane;
import stsc.frontend.zozka.charts.panes.EquityPane;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.components.panes.PeriodAndDatafeedPane;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.trading.TradeProcessorInit;

/**
 * Strategy visualiser is an application that helps debug trading strategy.
 * <br/>
 * Provide access to next trading strategy debug information: <br/>
 * 1. on stock algorithms dependencies and their series (for supported by
 * {@link Output} algorithm type); <br/>
 * 2. eod algorithms with dependencies with output series (for supported by
 * {@link Output} algorithm type); <br/>
 * 3. eod algorithms equity curve result with statistic table.
 */
public final class ZozkaStrategyVisualiser extends Application {

	private Stage owner;
	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();

	private PeriodAndDatafeedPane periodAndDatafeedController;
	private final TextArea textArea = new TextArea();

	private void fillTopPart() throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedPane(owner);
		pane.setTop(periodAndDatafeedController.getGui());
		pane.setCenter(textArea);
		textArea.setText("StockExecutions = a1\na1.loadLine=Input()\n");

		final HBox hbox = new HBox();
		final Button calculateSeries = new Button("Calculate Series");
		calculateSeries.setOnAction(e -> {
			try {
				calculateSeries();
			} catch (InterruptedException exc) {
				new TextAreaDialog(exc);
			}
		});

		final Button calculateOnEodSeries = new Button("Calculate On Eod Series");
		calculateOnEodSeries.setOnAction((buttonAction) -> {
			try {
				calculateOnEodSeries();
			} catch (Exception e) {
				new TextAreaDialog(e);
			}
		});

		final Button calculateEquityButton = new Button("Calculate Equity");
		calculateEquityButton.setOnAction(e -> {
			calculateEquity();
		});

		hbox.getChildren().add(calculateSeries);
		hbox.getChildren().add(calculateOnEodSeries);
		hbox.getChildren().add(calculateEquityButton);

		hbox.setAlignment(Pos.CENTER);
		pane.setBottom(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER);
		splitPane.getItems().add(pane);
	}

	private void fillBottomPart() {
		splitPane.getItems().add(tabPane);
	}

	private void calculateSeries() throws InterruptedException {
		periodAndDatafeedController.loadStockStorage(h -> {
			calculateSeries(periodAndDatafeedController.getStockStorage());
		});
	}

	private void calculateOnEodSeries() throws InterruptedException {
		periodAndDatafeedController.loadStockStorage(h -> {
			calculateOnEodSeries(periodAndDatafeedController.getStockStorage());
		});
	}

	private Optional<String> chooseStock(final StockStorage stockStorage) {
		final Set<String> stockNames = stockStorage.getStockNames();
		final ArrayList<String> stockNamesList = new ArrayList<>();
		stockNamesList.addAll(stockNames);
		Collections.sort(stockNamesList);
		if (stockNamesList.isEmpty()) {
			new TextAreaDialog("No stocks at StockStorage", "For some reason there is no stocks at stock storage. \nPlease check another one.");
			return Optional.empty();
		}
		final ChoiceDialog<String> dialog = new ChoiceDialog<String>(stockNamesList.get(0), stockNamesList);
		dialog.setTitle("Select Prefix for StockStorage");
		dialog.setHeaderText(null);
		return dialog.showAndWait();
	}

	private void calculateSeries(final StockStorage stockStorage) {
		if (stockStorage != null) {
			final Optional<String> stockNamePtr = chooseStock(stockStorage);
			if (stockNamePtr.isPresent()) {
				final Optional<Stock> stockPtr = stockStorage.getStock(stockNamePtr.get());
				if (stockPtr.isPresent()) {
					final Stock stock = stockStorage.getStock(stockNamePtr.get()).get();
					addSeriesForStock(stockStorage, stock);
				}
			}
		}
	}

	private void calculateOnEodSeries(final StockStorage stockStorage) {
		if (stockStorage != null) {
			addSeriesForEod(stockStorage);
		}
	}

	private void addSeriesForStock(StockStorage stockStorage, Stock stock) {
		try {
			final FromToPeriod period = periodAndDatafeedController.getPeriod();

			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, textArea.getText());
			final List<String> executionsName = init.generateOutForStocks();
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Simulator simulator = new Simulator(settings, Sets.newHashSet(stock.getInstrumentName()));
			final SignalsStorage signalsStorage = simulator.getSignalsStorage();

			final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnStockAlgorithm(stock, period, executionsName, signalsStorage);
			final Tab tab = new Tab();
			tab.setText(stock.getInstrumentName());
			tab.setContent(stockViewPane.getMainPane());
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);

		} catch (Exception e) {
			new TextAreaDialog(e);
		}
	}

	private void addSeriesForEod(StockStorage stockStorage) {
		try {
			final FromToPeriod period = periodAndDatafeedController.getPeriod();

			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, textArea.getText());
			final List<String> executionsName = init.generateOutForEods();
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Simulator simulator = new Simulator(settings);
			final SignalsStorage signalsStorage = simulator.getSignalsStorage();

			final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnEodAlgorithm(period, executionsName, signalsStorage);
			final Tab tab = new Tab();
			tab.setText("EC: " + tabPane.getTabs().size());
			tab.setContent(stockViewPane.getMainPane());
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);

		} catch (Exception e) {
			new TextAreaDialog(e);
		}
	}

	private void calculateEquity() {
		periodAndDatafeedController.loadStockStorage(h -> {
			calculateEquity(periodAndDatafeedController.getStockStorage());
		});
	}

	private void calculateEquity(StockStorage stockStorage) {
		if (stockStorage == null) {
			return;
		}
		try {
			final FromToPeriod period = periodAndDatafeedController.getPeriod();

			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, textArea.getText());
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Simulator simulator = new Simulator(settings);

			addEquityPaneTab(simulator, period, simulator.getMetrics());
		} catch (BadAlgorithmException | BadSignalException | IOException e) {
			new TextAreaDialog(e);
		}
	}

	private void addEquityPaneTab(Simulator simulator, FromToPeriod period, Metrics metrics) throws IOException {
		final EquityPane equityPane = new EquityPane(owner, metrics, period);
		final Tab tab = new Tab();
		tab.setText("E:" + tabPane.getTabs().size() + " " + String.format("%.3f", metrics.getDoubleMetric(MetricType.avGain)));
		tab.setContent(equityPane.getMainPane());
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
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
		Application.launch(ZozkaStrategyVisualiser.class, args);
	}

}
