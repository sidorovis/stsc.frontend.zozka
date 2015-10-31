package stsc.frontend.zozka.components.panes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;

/**
 * This is Visual Test for {@link PeriodAndDatafeedPane} GUI component. <br/>
 * Usage: please select some folder and dates at the dialog with pane then click 'Test Save' button. <br/>
 * At the result it should try to load Yahoo Datafeed from selected folder and show loaded stock names at the {@link TextAreaDialog}.
 */
public final class VisualTestPeriodAndDatafeedPane extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final PeriodAndDatafeedPane periodAndDatafeedPane = new PeriodAndDatafeedPane(stage);
		final BorderPane pane = new BorderPane();
		pane.setCenter(periodAndDatafeedPane.getGui());
		final Button save = new Button("Test Save");
		pane.setBottom(save);
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
		save.setOnAction(eh -> {
			periodAndDatafeedPane.loadStockStorage(loadStockHandler -> {
				new TextAreaDialog("Load stock storage test results", //
						"Results are: \n" + loadStockHandler.getEventType() + "\n" + //
								"Dates: " + periodAndDatafeedPane.getPeriod().getFrom() + " -> " + periodAndDatafeedPane.getPeriod().getTo() + "\n" + //
								periodAndDatafeedPane.getStockStorage().getStockNames().toString());
			});
		});
	}

	public static void main(String[] args) {
		Application.launch(VisualTestPeriodAndDatafeedPane.class, args);
	}

}
