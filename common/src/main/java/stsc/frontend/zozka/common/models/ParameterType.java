package stsc.frontend.zozka.common.models;

import java.util.HashMap;
import java.util.Map;

import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;

/**
 * Type of algorithm parameter. Created as part of GUI Zozka representation
 * models.
 */
public enum ParameterType {

	INTEGER("Integer", Integer.class, MpInteger.class), //
	DOUBLE("Double", Double.class, MpDouble.class), //
	STRING("String", String.class, MpString.class), //
	SUB_EXECUTION("Sub Execution", String.class, MpSubExecution.class); //

	private static class NameToParameterType {
		public static final Map<String, ParameterType> values = new HashMap<>();
	}

	private final String name;
	private final Class<?> classType;
	private final Class<? extends MpIterator<?, ?>> iteratorType;

	private <T> ParameterType(String name, Class<T> classType, Class<? extends MpIterator<T, ?>> iteratorType) {
		this.name = name;
		this.classType = classType;
		this.iteratorType = iteratorType;
		NameToParameterType.values.put(name, this);
	}

	public String getName() {
		return name;
	}

	public Class<?> getClassType() {
		return classType;
	}

	public Class<? extends MpIterator<?, ?>> getIteratorType() {
		return iteratorType;
	}

	public boolean isInteger() {
		return this.equals(INTEGER);
	}

	public boolean isDouble() {
		return this.equals(DOUBLE);
	}

	public boolean isString() {
		return this.equals(STRING);
	}

	public boolean isSubString() {
		return this.equals(SUB_EXECUTION);
	}

	public static ParameterType findByName(String name) {
		return NameToParameterType.values.get(name);
	}
}
