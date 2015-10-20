package stsc.frontend.zozka.components.simulation.dialogs;

import java.util.Optional;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.components.simulation.dialogs.CreateExecutionDescriptionDialog;

public class VisualTestCreateExecutionDescriptionDialog extends Application {

	public VisualTestCreateExecutionDescriptionDialog() {
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
		Application.launch(VisualTestCreateExecutionDescriptionDialog.class, (java.lang.String[]) null);

	}
}
