package stsc.frontend.zozka.dialogs;

import java.util.Optional;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.StockListDialog;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.storage.mocks.StockStorageMock;

public final class VisualTestStockListDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final StockStorage ss = StockStorageMock.getStockStorage();
		final StockListDialog dialog = new StockListDialog(parent, "StockList");
		int index = 0;
		for (String stockName : ss.getStockNames()) {
			final Optional<Stock> stock = ss.getStock(stockName);
			if (stock.isPresent()) {
				dialog.getModel().add(new StockDescription(index++, stock.get()));
			}
		}
		dialog.setOnMouseDoubleClicked(stockDescription -> {
			new TextAreaDialog("Temp Dialog", stockDescription.toString()).show();
			return Optional.empty();
		});
		dialog.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockListDialog.class, (java.lang.String[]) null);
	}
}
