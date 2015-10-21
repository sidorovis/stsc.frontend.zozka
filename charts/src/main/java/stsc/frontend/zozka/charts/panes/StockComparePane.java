package stsc.frontend.zozka.charts.panes;

import java.io.IOException;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
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

	public void addStockToCompare(final String title, final Stock stock) throws IOException {
		final BorderPane borderPane = new BorderPane();
		final Label titleLabel = new Label(title);
		titleLabel.setTextAlignment(TextAlignment.CENTER);
		BorderPane.setAlignment(titleLabel, Pos.CENTER);
		borderPane.setTop(titleLabel);
		final CurvesViewPane dataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(stock);
		borderPane.setCenter(dataStockViewPane.getMainPane());
		splitPane.getItems().add(borderPane);
	}
}
