package stsc.frontend.zozka.components.simulation.dialogs;

import java.util.Optional;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.components.simulation.dialogs.CreateExecutionDescriptionDialog;

public class VisualTestCreateAlgorithmDialogApplication extends Application {

	public VisualTestCreateAlgorithmDialogApplication() {
	}

	@Override
	public void start(Stage parent) throws Exception {
		CreateExecutionDescriptionDialog controller = new CreateExecutionDescriptionDialog(parent);
		final Optional<ExecutionDescription> ed = controller.getExecutionDescription();
		if (ed.isPresent()) {
			controller = new CreateExecutionDescriptionDialog(parent, ed.get());
			controller.getExecutionDescription();
		}
		parent.close();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCreateAlgorithmDialogApplication.class, (java.lang.String[]) null);

	}
}
