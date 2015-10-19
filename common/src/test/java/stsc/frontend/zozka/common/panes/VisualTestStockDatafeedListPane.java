package stsc.frontend.zozka.common.panes;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
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

	@Override
	public void start(Stage parent) throws Exception {
		StockDatafeedListPane mainPane = new StockDatafeedListPane("<datafeed title>");
		mainPane.setPrefHeight(600);
		final Scene scene = new Scene(mainPane);
		parent.setScene(scene);
		parent.show();
		mainPane.loadDatafeed( //
				Paths.get(new File(getClass().getResource("./").toURI()).getAbsolutePath()), //
				f -> {
					Alert alert = new Alert(AlertType.INFORMATION, "Download Finished", ButtonType.OK);
					alert.showAndWait();
					return Optional.empty();
				} , //
				Optional.empty());
		mainPane.setOnMouseDoubleClick(new Function<StockDescription, Optional<Void>>() {
			@Override
			public Optional<Void> apply(StockDescription sd) {
				new TextAreaDialog(sd.getStock().getInstrumentName(), sd.toString()).show();
				return Optional.empty();
			}
		});
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockDatafeedListPane.class, (java.lang.String[]) null);
	}

}
