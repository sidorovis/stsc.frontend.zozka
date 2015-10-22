package stsc.frontend.zozka.common.dialogs;

import java.util.Optional;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.common.stocks.MemoryStock;
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
				dialog.addStockDescription(new StockDescription(index++, stock.get()));
			}
		}
		dialog.setOnMouseDoubleClicked(stockDescription -> {
			if (stockDescription.getStock().getInstrumentName().equals("aapl")) {
				dialog.deleteStock(stockDescription.getStock().getInstrumentName());
			} else {
				final MemoryStock newStock = new MemoryStock(stockDescription.getStock().getInstrumentName());
				newStock.getDays().addAll(ss.getStock("aapl").get().getDays());
				dialog.updateStock(newStock);
			}
			new TextAreaDialog("Temp Dialog", stockDescription.toString()).showAndWait();
			return Optional.empty();
		});
		dialog.show();
	}

	public static void main(final String[] args) {
		Application.launch(VisualTestStockListDialog.class, args);
	}
}
