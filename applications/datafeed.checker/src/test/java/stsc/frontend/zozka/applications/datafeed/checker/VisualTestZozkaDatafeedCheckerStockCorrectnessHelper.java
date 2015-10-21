package stsc.frontend.zozka.applications.datafeed.checker;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.storage.mocks.StockStorageMock;
import stsc.yahoo.liquiditator.StockFilter;

public class VisualTestZozkaDatafeedCheckerStockCorrectnessHelper extends Application {

	private static final ZozkaDatafeedCheckerStockCorrectnessHelper helper = new ZozkaDatafeedCheckerStockCorrectnessHelper(new StockFilter());

	@Override
	public void start(Stage primaryStage) throws Exception {
		makeUserSelectEitherHeLikeCurrentStockState(primaryStage);
		makeUserSelectEitherHeWantsToSaveNewStockData(primaryStage);
	}

	private void makeUserSelectEitherHeLikeCurrentStockState(Stage primaryStage) {
		final boolean resave = helper.makeUserSelectEitherHeLikeCurrentStockState(primaryStage, StockStorageMock.getStockStorage().getStock("aapl").get(),
				StockStorageMock.getStockStorage().getStock("adm").get(), true);
		new TextAreaDialog("User selected: ", "" + resave).showAndWait();
		helper.makeUserSelectEitherHeLikeCurrentStockState(primaryStage, StockStorageMock.getStockStorage().getStock("aapl").get(),
				StockStorageMock.getStockStorage().getStock("adm").get(), false);
	}

	private void makeUserSelectEitherHeWantsToSaveNewStockData(Stage primaryStage) {
		final boolean userWants = helper.makeUserSelectEitherHeWantsToSaveNewStockData(StockStorageMock.getStockStorage().getStock("aapl").get());
		new TextAreaDialog("User selected: ", "" + userWants).showAndWait();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestZozkaDatafeedCheckerStockCorrectnessHelper.class, (java.lang.String[]) null);
	}

}
