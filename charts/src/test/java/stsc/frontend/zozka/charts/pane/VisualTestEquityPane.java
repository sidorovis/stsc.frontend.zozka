package stsc.frontend.zozka.charts.pane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.panes.EquityPane;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestEquityPane extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final StockStorage yfss = StockStorageMock.getStockStorage();
		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2010");
		final TradeProcessorInit init = new TradeProcessorInit(yfss, period,
				"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( .Level( f = 0.75d, Diff(.Input(e=close), .Input(e=open)) ) )\n");
		final SimulatorSettings settings = new SimulatorSettings(0, init);
		final Simulator simulator = new Simulator(settings);
		final EquityPane equityPane = new EquityPane(parent, simulator.getMetrics(), period);
		final Scene scene = new Scene(equityPane.getMainPane());
		parent.setScene(scene);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestEquityPane.class, (java.lang.String[]) null);
	}

}
