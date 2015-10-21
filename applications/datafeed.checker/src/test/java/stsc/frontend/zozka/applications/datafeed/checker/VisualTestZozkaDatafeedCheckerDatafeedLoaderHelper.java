package stsc.frontend.zozka.applications.datafeed.checker;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.yahoo.liquiditator.StockFilter;

public class VisualTestZozkaDatafeedCheckerDatafeedLoaderHelper extends Application {

	private final static ZozkaDatafeedCheckerDatafeedLoaderHelper helper = new ZozkaDatafeedCheckerDatafeedLoaderHelper(new StockFilter());

	@Override
	public void start(Stage primaryStage) throws Exception {
		makeUserChooseFolderTest(primaryStage);
		makeUserChooseStockNamePrefix(primaryStage);
	}

	private void makeUserChooseFolderTest(Stage primaryStage) {
		final Label label = new Label("__before__");
		final boolean result = helper.makeUserChooseFolder(primaryStage, label);
		new TextAreaDialog("New label value is", label.getText() + "\nFolder was changed: " + result).showAndWait();
	}

	private void makeUserChooseStockNamePrefix(Stage primaryStage) {
		final Optional<String> result = helper.makeUserChooseStockNamePrefix();
		new TextAreaDialog("Choosed value", (!result.isPresent() ? "VALUE NOT CHOOSED" : "'" + result.get() + "'")).showAndWait();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestZozkaDatafeedCheckerDatafeedLoaderHelper.class, (java.lang.String[]) null);
	}

}
