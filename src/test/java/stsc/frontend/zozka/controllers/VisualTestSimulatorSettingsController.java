package stsc.frontend.zozka.controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VisualTestSimulatorSettingsController extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final SimulatorSettingsController controller = new SimulatorSettingsController(stage);
		BorderPane pane = new BorderPane();
		pane.setCenter(controller.getGui());
		pane.setBottom(new Button("Test Save"));
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestSimulatorSettingsController.class, args);
	}
}
