package stsc.frontend.zozka.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;

/**
 * This is Visual Test for {@link DatafeedLoader} component.
 */
public final class VisualTestDatafeedLoader extends Application {

	private void load(final Stage stage, final Path path) throws Exception {
		final DatafeedLoader loader = new DatafeedLoader(path);
		loader.startLoad(successHandler -> {
			StockStorage stockStorage;
			try {
				stockStorage = loader.getStockStorage();
				new TextAreaDialog(successHandler.toString(), stockStorage.getStockNames().toString());
			} catch (Exception e) {
				new TextAreaDialog(e);
			}
		} , exitHandler -> {
			new TextAreaDialog("", exitHandler.toString());
		});
	}

	@Override
	public void start(Stage stage) throws Exception {
		final Path path = Paths.get(new File(getClass().getResource("./").toURI()).getAbsolutePath());
		load(stage, path);
	}

	public static void main(String[] args) throws IOException {
		Application.launch(VisualTestDatafeedLoader.class, args);
	}

}
