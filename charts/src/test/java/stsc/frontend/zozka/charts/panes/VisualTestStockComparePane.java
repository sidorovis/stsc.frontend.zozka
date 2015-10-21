package stsc.frontend.zozka.charts.panes;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import stsc.common.storage.StockStorage;
import stsc.storage.mocks.StockStorageMock;

public class VisualTestStockComparePane extends Application {

	private static final StockStorage STOCK_STORAGE = StockStorageMock.getStockStorage();

	@Override
	public void start(Stage primaryStage) throws Exception {
		final Alert dialog = new Alert(AlertType.NONE, null, ButtonType.CLOSE);
		final StockComparePane stockComparePane = new StockComparePane();
		stockComparePane.addStockToCompare("Aapl", STOCK_STORAGE.getStock("aapl").get());
		stockComparePane.addStockToCompare("Spy", STOCK_STORAGE.getStock("spy").get());
		stockComparePane.addStockToCompare("Adm", STOCK_STORAGE.getStock("adm").get());
		dialog.getDialogPane().setContent(stockComparePane);
		dialog.setResizable(true);
		dialog.showAndWait();
		primaryStage.close();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockComparePane.class, (java.lang.String[]) null);
	}

}
