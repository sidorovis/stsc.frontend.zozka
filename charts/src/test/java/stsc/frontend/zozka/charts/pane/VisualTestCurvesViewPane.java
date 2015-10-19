package stsc.frontend.zozka.charts.pane;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.panes.CurvesViewPane;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestCurvesViewPane extends Application {

	private final static StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Override
	public void start(Stage parent) throws Exception {
		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2015");

		final Optional<Stock> stockPtr = stockStorage.getStock("aapl");
		if (!stockPtr.isPresent()) {
			throw new Exception("aapl stock not available test should fail");
		}
		final Stock aapl = stockPtr.get();

		{
			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period,
					"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( .Level( f = 0.75d, Diff(.Input(e=close), .Input(e=open)) ) )\n");
			final List<String> executionsName = init.generateOutForStocks();
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Set<String> stockNames = new HashSet<String>(Arrays.asList(new String[] { "aapl" }));
			final Simulator simulator = new Simulator(settings, stockNames);
			final SignalsStorage signalsStorage = simulator.getSignalsStorage();

			final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
			dialog.setTitle("createPaneForOnStockAlgorithm");
			final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnStockAlgorithm(parent, aapl, period, executionsName, signalsStorage);
			dialog.getDialogPane().setContent(stockViewPane.getMainPane());
			dialog.showAndWait();
		}
		{
			final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
			dialog.setTitle("createPaneForAdjectiveClose");
			final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForAdjectiveClose(parent, aapl, period);
			dialog.getDialogPane().setContent(stockViewPane.getMainPane());
			dialog.showAndWait();
		}
		{
			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, "EodExecutions = a1\na1.loadLine = eod.AdlAdl()\n");
			final List<String> executionsName = init.generateOutForEods();
			final SimulatorSettings settings = new SimulatorSettings(0, init);
			final Simulator simulator = new Simulator(settings);
			final SignalsStorage signalsStorage = simulator.getSignalsStorage();

			final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
			dialog.setTitle("createPaneForOnEodAlgorithm");
			final CurvesViewPane stockViewPane = CurvesViewPane.createPaneForOnEodAlgorithm(parent, period, executionsName, signalsStorage);
			dialog.getDialogPane().setContent(stockViewPane.getMainPane());
			dialog.showAndWait();
		}
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCurvesViewPane.class, (java.lang.String[]) null);
	}

}
