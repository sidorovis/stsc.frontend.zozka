package stsc.frontend.zozka.common.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmType;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.AlgorithmSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;

/**
 * {@link ExecutionDescription} is a GUI version of {@link AlgorithmParameters}
 * with {@link AlgorithmType}, {@link #executionName}, {@link #algorithmName} .
 * <br/>
 */
public final class ExecutionDescription implements Externalizable {

	private static final long serialVersionUID = 1312747786515253819L;

	private AlgorithmType algorithmType;
	private String executionName;
	private String algorithmName;

	private final ObservableList<NumberAlgorithmParameter> numberAlgorithms;
	private final ObservableList<TextAlgorithmParameter> textAlgorithms;

	static ExecutionDescription loadFromFile(ObjectInputStream is) throws ClassNotFoundException, IOException {
		final ExecutionDescription ed = new ExecutionDescription();
		ed.readExternal(is);
		return ed;
	}

	private ExecutionDescription() {
		this.algorithmType = AlgorithmType.STOCK_VALUE;
		this.executionName = "";
		this.algorithmName = "";
		this.numberAlgorithms = FXCollections.observableArrayList();
		this.textAlgorithms = FXCollections.observableArrayList();
	}

	public ExecutionDescription(AlgorithmType algorithmType, String executionName, String algorithmName) {
		this.algorithmType = algorithmType;
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.numberAlgorithms = FXCollections.observableArrayList();
		this.textAlgorithms = FXCollections.observableArrayList();
	}

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public ExecutionDescription addNumberAlgorithm(final NumberAlgorithmParameter numberAlgorithmParameter) {
		numberAlgorithms.add(numberAlgorithmParameter);
		return this;
	}

	public ExecutionDescription addTextAlgorithm(final TextAlgorithmParameter textAlgorithmParameter) {
		textAlgorithms.add(textAlgorithmParameter);
		return this;
	}

	/**
	 * Please do not use it to change {@link ExecutionDescription}.
	 * 
	 * @return {@link ObservableList} of {@link NumberAlgorithmParameter}.
	 */
	public ObservableList<NumberAlgorithmParameter> getNumberAlgorithms() {
		return numberAlgorithms;
	}

	/**
	 * Please do not use it to change {@link ExecutionDescription}.
	 * 
	 * @return {@link ObservableList} of {@link TextAlgorithmParameter}.
	 */
	public ObservableList<TextAlgorithmParameter> getTextAlgorithms() {
		return textAlgorithms;
	}

	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	public void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public GridExecutionInitializer createGridExecution(FromToPeriod period) throws BadParameterException {
		final AlgorithmSettingsGridIterator settings = new AlgorithmSettingsGridIterator(period, false, generateParameters());
		return new GridExecutionInitializer(executionName, algorithmName, settings);
	}

	public GeneticExecutionInitializer createGeneticExecution(FromToPeriod period) throws BadParameterException {
		final AlgorithmSettingsGeneticList settings = new AlgorithmSettingsGeneticList(period, generateParameters());
		return new GeneticExecutionInitializer(executionName, algorithmName, settings);
	}

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	private AlgorithmParameters generateParameters() throws BadParameterException {
		final AlgorithmParameters parameters = new AlgorithmParameters();
		fillNumberParameters(parameters);
		fillTextParameters(parameters);
		return parameters;
	}

	private void fillTextParameters(AlgorithmParameters parameters) throws BadParameterException {
		for (TextAlgorithmParameter p : textAlgorithms) {
			if (p.getType().equals(ParameterType.STRING)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = TextAlgorithmParameter.createDomenRepresentation(p.domenProperty().get());
				parameters.getStrings().add(new MpString(name, domen));
			} else if (p.getType().equals(ParameterType.SUB_EXECUTION)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = TextAlgorithmParameter.createDomenRepresentation(p.domenProperty().get());
				parameters.getSubExecutions().add(new MpSubExecution(name, domen));
			}
		}
	}

	private void fillNumberParameters(AlgorithmParameters parameters) throws BadParameterException {
		for (NumberAlgorithmParameter p : numberAlgorithms) {
			if (p.getType().equals(ParameterType.INTEGER)) {
				final String name = p.parameterNameProperty().get();
				final Integer from = Integer.valueOf(p.getFrom());
				final Integer to = Integer.valueOf(p.getTo());
				final Integer step = Integer.valueOf(p.getStep());
				parameters.getIntegers().add(new MpInteger(name, from, to, step));
			} else if (p.getType().equals(ParameterType.DOUBLE)) {
				final String name = p.parameterNameProperty().get();
				final Double from = Double.valueOf(p.getFrom());
				final Double to = Double.valueOf(p.getTo());
				final Double step = Double.valueOf(p.getStep());
				parameters.getDoubles().add(new MpDouble(name, from, to, step));
			}
		}
	}

	public boolean parameterNameExists(String parameterName) {
		for (NumberAlgorithmParameter p : getNumberAlgorithms()) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		for (TextAlgorithmParameter p : getTextAlgorithms()) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(executionName) + " (" + String.valueOf(algorithmName) + ")";
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		algorithmType = AlgorithmType.valueOf(in.readUTF());
		executionName = in.readUTF();
		algorithmName = in.readUTF();
		int sizeOfNumbers = in.readInt();
		numberAlgorithms.clear();
		for (int i = 0; i < sizeOfNumbers; ++i) {
			String parameterName = in.readUTF();
			String type = in.readUTF();
			String from = in.readUTF();
			String step = in.readUTF();
			String to = in.readUTF();
			boolean valid = in.readBoolean();
			numberAlgorithms.add(new NumberAlgorithmParameter(parameterName, ParameterType.findByName(type), from, step, to, valid));
		}
		int sizeOfText = in.readInt();
		textAlgorithms.clear();
		for (int i = 0; i < sizeOfText; ++i) {
			String parameterName = in.readUTF();
			String type = in.readUTF();
			String domen = in.readUTF();
			textAlgorithms.add(new TextAlgorithmParameter(parameterName, ParameterType.findByName(type), TextAlgorithmParameter.createDomenRepresentation(domen)));
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(algorithmType.name());
		out.writeUTF(executionName);
		out.writeUTF(algorithmName);
		out.writeInt(numberAlgorithms.size());
		for (NumberAlgorithmParameter numberAlgorithmParameter : numberAlgorithms) {
			out.writeUTF(numberAlgorithmParameter.parameterNameProperty().getValue());
			out.writeUTF(numberAlgorithmParameter.getType().getName());
			out.writeUTF(numberAlgorithmParameter.getFrom());
			out.writeUTF(numberAlgorithmParameter.getStep());
			out.writeUTF(numberAlgorithmParameter.getTo());
			out.writeBoolean(numberAlgorithmParameter.isValid());
		}
		out.writeInt(textAlgorithms.size());
		for (TextAlgorithmParameter textAlgorithmParameter : textAlgorithms) {
			out.writeUTF(textAlgorithmParameter.parameterNameProperty().getValue());
			out.writeUTF(textAlgorithmParameter.getType().getName());
			out.writeUTF(textAlgorithmParameter.domenProperty().getValue());
		}
	}

}
