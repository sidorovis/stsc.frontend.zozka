package stsc.frontend.zozka.charts.panes;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorConfiguration;
import stsc.general.simulator.SimulatorConfigurationImpl;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestCurvesViewPane extends Application {

	final static StockStorage stockStorage = StockStorageMock.getStockStorage();

	final Stock getStock() throws Exception {
		final Optional<Stock> stockPtr = stockStorage.getStock("aapl");
		if (!stockPtr.isPresent()) {
			throw new Exception("aapl stock not available test should fail");
		}
		return stockPtr.get();
	}

	final FromToPeriod getPeriod() throws ParseException {
		return new FromToPeriod("01-01-1993", "31-12-2008");
	}

	@Override
	public void start(Stage parent) throws Exception {
		testCreatePaneForAdjectiveClose();
		testCreatePaneForAdjectiveCloseWithPeriod();
		testCreatePaneForOnStockAlgorithm();
		testCreatePaneForOnEodAlgorithm();
	}

	private void testCreatePaneForAdjectiveClose() throws Exception {
		final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
		dialog.setTitle("createPaneForAdjectiveClose");
		final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForAdjectiveClose(getStock());
		dialog.getDialogPane().setContent(stockViewPane.getMainPane());
		dialog.setResizable(true);
		dialog.showAndWait();
	}

	private void testCreatePaneForAdjectiveCloseWithPeriod() throws Exception {
		final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
		dialog.setTitle("createPaneForAdjectiveClose");
		final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForAdjectiveClose(getStock(), getPeriod());
		dialog.getDialogPane().setContent(stockViewPane.getMainPane());
		dialog.setResizable(true);
		dialog.showAndWait();
	}

	/**
	 * for test purpose
	 */
	TradeProcessorInit getTradeProcessorInitForOnStockAlgorithm() throws BadAlgorithmException, ParseException {
		return new TradeProcessorInit(stockStorage, getPeriod(),
				"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( .Level( f = 0.75d, Diff(.Input(e=close), .Input(e=open)) ) )\n");
	}

	/**
	 * for test purpose
	 */
	SignalsStorage getSignalsStorage(TradeProcessorInit init) throws Exception {
		final SimulatorConfiguration settings = new SimulatorConfigurationImpl(0, init, new HashSet<String>(Arrays.asList(new String[] { getStock().getInstrumentName() })));
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(settings);
		return simulator.getSignalsStorage();

	}

	private void testCreatePaneForOnStockAlgorithm() throws Exception {
		final TradeProcessorInit init = getTradeProcessorInitForOnStockAlgorithm();
		final List<String> executionsName = init.generateOutForStocks();
		final SignalsStorage signalsStorage = getSignalsStorage(init);

		final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
		dialog.setTitle("createPaneForOnStockAlgorithm");
		final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnStockAlgorithm(getStock(), getPeriod(), executionsName, signalsStorage);
		dialog.getDialogPane().setContent(stockViewPane.getMainPane());
		dialog.setResizable(true);
		dialog.showAndWait();
	}

	/**
	 * for test purpose
	 */
	TradeProcessorInit getTradeProcessorInitForOnEodAlgorithm() throws BadAlgorithmException, ParseException {
		return new TradeProcessorInit(stockStorage, getPeriod(), "EodExecutions = a1\na1.loadLine = eod.AdlAdl()\n");
	}

	private void testCreatePaneForOnEodAlgorithm() throws Exception {
		final TradeProcessorInit init = getTradeProcessorInitForOnEodAlgorithm();
		final List<String> executionsName = init.generateOutForEods();
		final SignalsStorage signalsStorage = getSignalsStorage(init);

		final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
		dialog.setTitle("createPaneForOnEodAlgorithm");
		final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnEodAlgorithm(getPeriod(), executionsName, signalsStorage);
		dialog.getDialogPane().setContent(stockViewPane.getMainPane());
		dialog.setResizable(true);
		dialog.showAndWait();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCurvesViewPane.class, (java.lang.String[]) null);
	}

}
