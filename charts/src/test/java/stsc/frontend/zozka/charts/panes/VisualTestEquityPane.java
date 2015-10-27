package stsc.frontend.zozka.charts.panes;

import java.text.ParseException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.panes.EquityPane;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.SimulatorSettingsImpl;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestEquityPane extends Application {

	final Simulator getSimulator() throws BadAlgorithmException, ParseException, BadSignalException {
		final StockStorage yfss = StockStorageMock.getStockStorage();
		final FromToPeriod period = getPeriod();
		final TradeProcessorInit init = new TradeProcessorInit(yfss, period,
				"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( .Level( f = 0.75d, Diff(.Input(e=close), .Input(e=open)) ) )\n");
		final SimulatorSettings settings = new SimulatorSettingsImpl(0, init);
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(settings);
		return simulator;
	}

	@Override
	public void start(Stage parent) throws Exception {
		final FromToPeriod period = getPeriod();
		final Simulator simulator = getSimulator();
		final EquityPane equityPane = new EquityPane(parent, simulator.getMetrics(), period);
		final Scene scene = new Scene(equityPane.getMainPane());
		parent.setScene(scene);
		parent.show();
	}

	private FromToPeriod getPeriod() throws ParseException {
		return new FromToPeriod("01-01-1990", "31-12-2010");
	}

	public static void main(String[] args) {
		Application.launch(VisualTestEquityPane.class, (java.lang.String[]) null);
	}

}
