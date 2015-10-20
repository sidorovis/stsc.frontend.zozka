package stsc.frontend.zozka.components.simulation.dialogs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.frontend.zozka.components.simulation.dialogs.CreateSimulatorSettingsDialog;

public class VisualTestCreateSimulatorSettingsDialog extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final CreateSimulatorSettingsDialog controller = new CreateSimulatorSettingsDialog(stage);
		BorderPane pane = new BorderPane();
		pane.setCenter(controller.getGui());
		pane.setBottom(new Button("Test Save"));
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCreateSimulatorSettingsDialog.class, args);
	}
}
