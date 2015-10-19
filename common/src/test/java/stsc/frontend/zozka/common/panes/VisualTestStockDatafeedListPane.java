package stsc.frontend.zozka.common.panes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.frontend.zozka.common.panes.StockDatafeedListPane;

public class VisualTestStockDatafeedListPane extends Application {

	StockDatafeedListPane downloadDatafeedOnInterface(Function<Set<String>, Optional<Void>> onFinish) throws IOException, URISyntaxException {
		final StockDatafeedListPane mainPane = new StockDatafeedListPane("<datafeed title>");
		mainPane.setPrefHeight(600);
		mainPane.loadDatafeed( //
				Paths.get(new File(getClass().getResource("./").toURI()).getAbsolutePath()), //
				onFinish, //
				Optional.empty());
		mainPane.setOnMouseDoubleClick(new Function<StockDescription, Optional<Void>>() {
			@Override
			public Optional<Void> apply(StockDescription sd) {
				new TextAreaDialog(sd.getStock().getInstrumentName(), sd.toString()).show();
				return Optional.empty();
			}
		});
		return mainPane;
	}

	@Override
	public void start(Stage parent) throws Exception {
		StockDatafeedListPane mainPane = downloadDatafeedOnInterface(f -> {
			Alert alert = new Alert(AlertType.INFORMATION, "Download Finished", ButtonType.OK);
			alert.showAndWait();
			return Optional.empty();
		});
		final Scene scene = new Scene(mainPane);
		parent.setScene(scene);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockDatafeedListPane.class, (java.lang.String[]) null);
	}

}
