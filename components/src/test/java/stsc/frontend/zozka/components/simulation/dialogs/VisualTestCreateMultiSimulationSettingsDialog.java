package stsc.frontend.zozka.components.simulation.dialogs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;

public class VisualTestCreateMultiSimulationSettingsDialog extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final CreateMultiSimulationSettingsDialog controller = new CreateMultiSimulationSettingsDialog(stage);
		new TextAreaDialog("Controller", controller.getSimulationType().toString()).showAndWait();
		BorderPane pane = new BorderPane();
		pane.setCenter(controller.getGui());
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCreateMultiSimulationSettingsDialog.class, args);
	}
}
