package stsc.frontend.zozka.components.simulation.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.common.models.NumberAlgorithmParameter;
import stsc.frontend.zozka.common.models.ParameterType;
import stsc.frontend.zozka.common.models.TextAlgorithmParameter;

/**
 * This class contain helper methods for
 * {@link CreateExecutionDescriptionDialog}
 */
final class CreateExecutionDescriptionDialogHelper {

	private static final String DOUBLE_DEFAULT_FROM = "0.0";
	private static final String DOUBLE_DEFAULT_STEP = "2.0";
	private static final String DOUBLE_DEFAULT_TO = "50.0";

	private static final String INTEGER_DEFAULT_FROM = "0";
	private static final String INTEGER_DEFAULT_STEP = "1";
	private static final String INTEGER_DEFAULT_TO = "22";

	static final Pattern PARAMETER_NAME_PATTERN = Pattern.compile("^_?[a-zA-Z]([\\w_])*$");

	static final class NumberParameters {

		private boolean valid = true;

		Optional<String> from = Optional.empty();
		Optional<String> step = Optional.empty();
		Optional<String> to = Optional.empty();

		public boolean isValid() {
			return valid;
		}
	}

	public void processAddParameterAction(final ExecutionDescription model) {
		final Optional<String> parameterName = getParameterName(model);
		if (!parameterName.isPresent()) {
			return;
		}
		final Optional<ParameterType> parameterType = getParameterType();
		if (!parameterType.isPresent()) {
			return;
		}
		switch (parameterType.get()) {
		case DOUBLE:
			addDoubleParameter(parameterName.get(), model);
			break;
		case INTEGER:
			addIntegerParameter(parameterName.get(), model);
			break;
		case STRING:
			addStringParameter(parameterName.get(), model);
			break;
		case SUB_EXECUTION:
			addSubExecutionParameter(parameterName.get(), model);
			break;
		default:
			break;
		}
	}

	private Optional<String> getParameterName(final ExecutionDescription model) {
		final Optional<String> parameterName = getParameterName();

		if (parameterName.isPresent() && model.parameterNameExists(parameterName.get())) {
			new TextAreaDialog("Parameter name does not match pattern", "You could add only one parameter (one for both for number or test tables).");
			return Optional.empty();
		}
		return parameterName;
	}

	private Optional<ParameterType> getParameterType() {
		final Alert alert = new Alert(AlertType.NONE, "Choose type for parameter");
		for (ParameterType pt : ParameterType.values()) {
			final ButtonType buttonType = new ButtonType(pt.getName());
			alert.getButtonTypes().add(buttonType);
		}
		final Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			return Optional.ofNullable(ParameterType.findByName(result.get().getText()));
		}
		return Optional.empty();
	}

	private void addIntegerParameter(final String parameterName, final ExecutionDescription model) {
		CreateExecutionDescriptionDialogHelper.NumberParameters numberParameters = readAllIntegerParameters();
		if (numberParameters.isValid()) {
			model.addNumberAlgorithm(
					new NumberAlgorithmParameter(parameterName, ParameterType.INTEGER, numberParameters.from.get(), numberParameters.step.get(), numberParameters.to.get()));
		}
	}

	private void addDoubleParameter(final String parameterName, final ExecutionDescription model) {
		CreateExecutionDescriptionDialogHelper.NumberParameters numberParameters = readAllDoubleParameters();
		if (numberParameters.isValid()) {
			model.addNumberAlgorithm(
					new NumberAlgorithmParameter(parameterName, ParameterType.DOUBLE, numberParameters.from.get(), numberParameters.step.get(), numberParameters.to.get()));
		}
	}

	private void addStringParameter(final String parameterName, final ExecutionDescription model) {
		final List<String> values = getStringDomen("String Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		model.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.STRING, domen));
	}

	private void addSubExecutionParameter(final String parameterName, final ExecutionDescription model) {
		final List<String> values = getStringDomen("SubExecution Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		model.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.SUB_EXECUTION, domen));
	}

	/**
	 * ParameterName
	 */
	private Optional<String> getParameterName() {
		final TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Parameter Name");
		dialog.setHeaderText("Parameter name:");
		dialog.setContentText("Enter: ");
		final Optional<String> parameterName = dialog.showAndWait();

		if (parameterName.isPresent() && !PARAMETER_NAME_PATTERN.matcher(parameterName.get()).matches()) {
			new TextAreaDialog("Parameter name does not match pattern", "Parameter name should contain only letters, numbers and '_' symbol.");
			return Optional.empty();
		}
		return parameterName;
	}

	/**
	 * For {@link ParameterType#INTEGER}.
	 */

	private NumberParameters readAllIntegerParameters() {
		final String errorMessage = "Integer is a number (-)?([0-9])+";
		final NumberParameters result = new NumberParameters();
		result.from = readIntegerParameter(INTEGER_DEFAULT_FROM, "Enter From", "From: ", errorMessage);
		if (!result.from.isPresent()) {
			result.valid = false;
			return result;
		}
		result.step = readIntegerParameter(INTEGER_DEFAULT_STEP, "Enter Step", "Step: ", errorMessage);
		if (!result.step.isPresent()) {
			result.valid = false;
			return result;
		}
		result.to = readIntegerParameter(INTEGER_DEFAULT_TO, "Enter To", "To: ", errorMessage);
		if (!result.to.isPresent()) {
			result.valid = false;
			return result;
		}
		return result;
	}

	private Optional<String> readIntegerParameter(final String defaultValue, String masthead, String message, String errorMessage) {
		final TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle("Integer Parameter");
		dialog.setHeaderText(masthead);
		dialog.setContentText(message);
		final Optional<String> integerParameter = dialog.showAndWait();
		if (!integerParameter.isPresent() || !validateInteger(integerParameter.get())) {
			new TextAreaDialog("Integer value is incorrect", errorMessage);
			return Optional.empty();
		}
		return Optional.of(integerParameter.get());
	}

	boolean validateInteger(final String integerValue) {
		return NumberAlgorithmParameter.integerParPattern.matcher(integerValue).matches();
	}

	/**
	 * For {@link ParameterType#DOUBLE}.
	 */

	private NumberParameters readAllDoubleParameters() {
		final String errorMessage = "Double is a number (-)?([0-9])+(.[0-9]+)?";
		final NumberParameters result = new NumberParameters();
		result.from = readDoubleParameter(DOUBLE_DEFAULT_FROM, "Double Parameter", "Enter From", "From: ", errorMessage);
		if (!result.from.isPresent()) {
			result.valid = false;
			return result;
		}
		result.step = readDoubleParameter(DOUBLE_DEFAULT_STEP, "Double Parameter", "Enter Step", "Step: ", errorMessage);
		if (!result.step.isPresent()) {
			result.valid = false;
			return result;
		}
		result.to = readDoubleParameter(DOUBLE_DEFAULT_TO, "Double Parameter", "Enter To", "To: ", errorMessage);
		if (!result.to.isPresent()) {
			result.valid = false;
			return result;
		}
		return result;
	}

	private Optional<String> readDoubleParameter(final String defaultValue, String title, String masthead, String message, String errorMessage) {
		final TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle(title);
		dialog.setHeaderText(masthead);
		dialog.setContentText(message);
		final Optional<String> doubleParameter = dialog.showAndWait();
		if (!doubleParameter.isPresent() || !validateDouble(doubleParameter.get())) {
			new TextAreaDialog("Double value is incorrect", errorMessage);
			return Optional.empty();
		}
		return Optional.of(doubleParameter.get());
	}

	boolean validateDouble(final String doubleValue) {
		return NumberAlgorithmParameter.doubleParPattern.matcher(doubleValue).matches();
	}

	/**
	 * For {@link ParameterType#STRING} {@link ParameterType#SUB_EXECUTION}.
	 */

	private List<String> getStringDomen(String title) {
		final ArrayList<String> values = new ArrayList<>();
		while (true) {
			final TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle(title);
			dialog.setContentText("Hack: add several divided by ','.\nPress 'Cancel' to finish enter.");
			final Optional<String> stringValue = dialog.showAndWait();
			if (stringValue.isPresent()) {
				values.add(stringValue.get());
			} else {
				break;
			}
		}
		return values;
	}
}
