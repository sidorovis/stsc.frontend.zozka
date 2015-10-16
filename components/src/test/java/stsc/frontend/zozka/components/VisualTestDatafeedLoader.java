package stsc.frontend.zozka.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.common.storage.StockStorage;

public class VisualTestDatafeedLoader extends Application {

	private void load(final Stage stage, final Path path) throws Exception {
		final DatafeedLoader loader = new DatafeedLoader(path);
		loader.startLoad(rh -> {
			StockStorage stockStorage;
			try {
				stockStorage = loader.getStockStorage();
				System.out.println(rh);
				System.out.println(stockStorage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} , eh -> {
			System.out.println(eh);
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
