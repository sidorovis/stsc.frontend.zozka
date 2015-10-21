package stsc.frontend.zozka.charts.panes;

import java.io.IOException;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import stsc.common.stocks.Stock;

/**
 * This Pane shows {@link Stock} as bunch of charts on adjective close (depends
 * on configuration).
 */
public final class StockComparePane extends BorderPane {

	private final SplitPane splitPane = new SplitPane();

	public StockComparePane() {
		splitPane.setOrientation(Orientation.VERTICAL);
		this.setCenter(splitPane);
	}

	public void addStockToCompare(final Stock stock) throws IOException {
		final BorderPane borderPane = new BorderPane();
		final CurvesViewPane dataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(stock);
		borderPane.setCenter(dataStockViewPane.getMainPane());
		splitPane.getItems().add(borderPane);
	}
}
