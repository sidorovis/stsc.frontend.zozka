package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.storage.mocks.StockStorageMock;
import stsc.yahoo.liquiditator.StockFilter;

public class VisualTestZozkaDatafeedCheckerStockCorrectnessHelper extends Application {

	private static final ZozkaDatafeedCheckerStockCorrectnessHelper helper = new ZozkaDatafeedCheckerStockCorrectnessHelper(new StockFilter());
	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Override
	public void start(Stage primaryStage) throws Exception {
		makeUserSelectEitherHeLikeCurrentStockState(primaryStage);
		makeUserSelectEitherHeWantsToSaveNewStockData(primaryStage);
	}

	private void makeUserSelectEitherHeLikeCurrentStockState(Stage primaryStage) throws IOException {
		final boolean resave = helper.makeUserSelectEitherHeLikeToRedownloadCurrentStockState(primaryStage, stockStorage.getStock("aapl"), stockStorage.getStock("adm"));
		new TextAreaDialog("User selected: ", "" + resave);
		helper.makeUserSelectEitherHeLikeToRedownloadCurrentStockState(primaryStage, stockStorage.getStock("aapl"), stockStorage.getStock("adm"));
	}

	private void makeUserSelectEitherHeWantsToSaveNewStockData(Stage primaryStage) {
		final boolean userWants = helper.makeUserSelectEitherHeWantsToSaveNewStockData(stockStorage.getStock("aapl").get());
		new TextAreaDialog("User selected: ", "" + userWants);
	}

	public static void main(String[] args) {
		Application.launch(VisualTestZozkaDatafeedCheckerStockCorrectnessHelper.class, (java.lang.String[]) null);
	}

}
