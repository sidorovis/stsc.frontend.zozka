package stsc.frontend.zozka.components.panes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;

public class VisualTestPeriodAndDatafeedPane extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final PeriodAndDatafeedPane periodAndDatafeedPane = new PeriodAndDatafeedPane(stage);
		BorderPane pane = new BorderPane();
		pane.setCenter(periodAndDatafeedPane.getGui());
		final Button save = new Button("Test Save");
		pane.setBottom(save);
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
		save.setOnAction(eh -> {
			periodAndDatafeedPane.loadStockStorage(loadStockHandler -> {
				new TextAreaDialog("Load stock storage test results",
						"Results are: \n" + loadStockHandler.getEventType() + "\n" + periodAndDatafeedPane.getStockStorage().getStockNames().toString());
			});
		});

	}

	public static void main(String[] args) {
		Application.launch(VisualTestPeriodAndDatafeedPane.class, args);
	}

}
