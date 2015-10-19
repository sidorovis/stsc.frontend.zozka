package stsc.frontend.zozka.common.models;

import java.util.function.Function;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;

/**
 * GUI Zozka representation for {@link MpInteger}, {@link MpDouble} algorithm
 * parameters.
 */
public final class NumberAlgorithmParameter implements Function<Void, Boolean> {

	public static final Pattern integerParPattern = Pattern.compile("^-?(\\d)+$");
	public static final Pattern doubleParPattern = Pattern.compile("^-?(\\d)+(\\.(\\d)+)?$");

	private final StringProperty parameterName;
	private final ParameterType type;
	private final Pattern pattern;
	private final StringProperty from;
	private final StringProperty step;
	private final StringProperty to;
	private boolean valid;

	public NumberAlgorithmParameter(String parameterName, ParameterType type, String from, String step, String to) {
		this(parameterName, type, from, step, to, true);
	}

	public NumberAlgorithmParameter(String parameterName, ParameterType type, String from, String step, String to, boolean valid) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = type;
		if (type.equals(ParameterType.INTEGER)) {
			this.pattern = integerParPattern;
		} else {
			this.pattern = doubleParPattern;
		}
		this.from = new SimpleStringProperty(from);
		this.step = new SimpleStringProperty(step);
		this.to = new SimpleStringProperty(to);
		this.valid = valid;
	}

	public StringProperty parameterNameProperty() {
		return parameterName;
	}

	public ParameterType getType() {
		return type;
	}

	public String getFrom() {
		return from.getValue();
	}

	public void setFrom(String value) {
		from.setValue(value);
		validate();
	}

	public String getStep() {
		return step.getValue();
	}

	public void setStep(String value) {
		step.setValue(value);
		validate();
	}

	public String getTo() {
		return to.getValue();
	}

	public void setTo(String value) {
		to.setValue(value);
		validate();
	}

	public boolean isValid() {
		return valid;
	}

	private void validate() {
		try {
			valid = false;

			double fromValue = Double.valueOf(from.getValue());
			Double.valueOf(step.getValue());
			double toValue = Double.valueOf(to.getValue());

			if (fromValue > toValue)
				return;
			if (!pattern.matcher(from.getValue()).matches())
				return;
			if (!pattern.matcher(step.getValue()).matches())
				return;
			if (!pattern.matcher(to.getValue()).matches())
				return;

			valid = true;
		} catch (NumberFormatException e) {
			valid = false;
		}
	}

	@Override
	public Boolean apply(Void notUsedArgument) {
		return isValid();
	}

}
