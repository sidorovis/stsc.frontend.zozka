package stsc.frontend.zozka.components.simulation.dialogs;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.NumberAlgorithmParameter;

/**
 * This class contain helper methods for
 * {@link CreateExecutionDescriptionDialog}
 */
final class CreateExecutionDescriptionDialogHelper {

	private static final String defaultFrom = "0.0";
	private static final String defaultStep = "1.0";
	private static final String defaultTo = "22.0";

	static final class NumberParameters {

		private boolean valid = true;

		Optional<String> from = Optional.empty();
		Optional<String> step = Optional.empty();
		Optional<String> to = Optional.empty();

		public boolean isValid() {
			return valid;
		}
	}

	NumberParameters readAllDoubleParameters() {
		final String errorMessage = "Double is a number (-)?([0-9])+(.[0-9]+)?";
		final NumberParameters result = new NumberParameters();
		result.from = readDoubleParameter(defaultFrom, "Double Parameter", "Enter From", "From: ", errorMessage);
		if (!result.from.isPresent()) {
			result.valid = false;
			return result;
		}
		result.step = readDoubleParameter(defaultStep, "Double Parameter", "Enter Step", "Step: ", errorMessage);
		if (!result.step.isPresent()) {
			result.valid = false;
			return result;
		}
		result.to = readDoubleParameter(defaultTo, "Double Parameter", "Enter To", "To: ", errorMessage);
		if (!result.to.isPresent()) {
			result.valid = false;
			return result;
		}
		return result;
	}

	Optional<String> readDoubleParameter(final String defaultValue, String title, String masthead, String message, String errorMessage) {
		final TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle(title);
		dialog.setHeaderText(masthead);
		dialog.setContentText(message);
		final Optional<String> doubleParameter = dialog.showAndWait();
		if (!doubleParameter.isPresent() || !validateDouble(doubleParameter.get())) {
			new TextAreaDialog("Double value is incorrect", errorMessage).showAndWait();
			return Optional.empty();
		}
		return Optional.of(doubleParameter.get());
	}

	public boolean validateDouble(final String doubleValue) {
		return NumberAlgorithmParameter.doubleParPattern.matcher(doubleValue).matches();
	}
}
