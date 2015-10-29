package stsc.frontend.zozka.common.models;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Ordering;

import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.AlgorithmType;
import stsc.common.algorithms.MutatingAlgorithmSettings;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;

public class ExecutionDescriptionTest {

	@Test
	public void testExecutionDescriptionForDouble() throws BadParameterException {
		final ExecutionDescription from = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "testExecution", "testAlgorithm");
		Assert.assertFalse(from.parameterNameExists("notexists"));
		from.addNumberAlgorithm(new NumberAlgorithmParameter("pName", ParameterType.DOUBLE, "10.0", "1.5", "20.0"));
		final GeneticExecutionInitializer gei = from.createGeneticExecution(new FromToPeriod(new Date(), new Date()));
		Assert.assertEquals("testAlgorithm", gei.getAlgorithmName());
		Assert.assertEquals("testExecution", gei.getExecutionName());
		for (int i = 0; i < 1000; ++i) {
			final MutatingAlgorithmSettings aSettings = gei.generateRandom();
			Assert.assertTrue(10.0 <= aSettings.getDouble("pName").doubleValue());
			Assert.assertTrue(20.0 >= aSettings.getDouble("pName").doubleValue());
			Assert.assertEquals(1.5 * ((int) ((aSettings.getDouble("pName").doubleValue() - 10.0) / 1.5)) + 10.0, aSettings.getDouble("pName").doubleValue(),
					Settings.doubleEpsilon);
		}
	}

	@Test
	public void testExecutionDescriptionForInteger() throws BadParameterException {
		final ExecutionDescription from = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "testExecution", "testAlgorithm");
		Assert.assertFalse(from.parameterNameExists("notexists"));
		from.addNumberAlgorithm(new NumberAlgorithmParameter("pName", ParameterType.INTEGER, "10", "3", "22"));
		final GeneticExecutionInitializer gei = from.createGeneticExecution(new FromToPeriod(new Date(), new Date()));
		Assert.assertEquals("testAlgorithm", gei.getAlgorithmName());
		Assert.assertEquals("testExecution", gei.getExecutionName());
		for (int i = 0; i < 1000; ++i) {
			final MutatingAlgorithmSettings aSettings = gei.generateRandom();
			Assert.assertTrue(10 <= aSettings.getInteger("pName").intValue());
			Assert.assertTrue(22 >= aSettings.getInteger("pName").intValue());
			Assert.assertEquals(3 * ((int) ((aSettings.getInteger("pName").doubleValue() - 10.0) / 3.0)) + 10.0, aSettings.getInteger("pName").doubleValue(),
					Settings.doubleEpsilon);
		}
	}

	@Test
	public void testExecutionDescriptionForString() throws BadParameterException {
		final ExecutionDescription from = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "testExecution", "testAlgorithm");
		Assert.assertFalse(from.parameterNameExists("notexists"));
		from.addTextAlgorithm(new TextAlgorithmParameter("pName", ParameterType.STRING, Arrays.asList("asd", "cvb", "tyu")));
		final GeneticExecutionInitializer gei = from.createGeneticExecution(new FromToPeriod(new Date(), new Date()));
		Assert.assertEquals("testAlgorithm", gei.getAlgorithmName());
		Assert.assertEquals("testExecution", gei.getExecutionName());
		for (int i = 0; i < 1000; ++i) {
			final MutatingAlgorithmSettings aSettings = gei.generateRandom();
			Assert.assertTrue(Arrays.asList("asd", "cvb", "tyu").contains(aSettings.getString("pName")));
		}
	}

	@Test
	public void testExecutionDescriptionForSubExecutions() throws BadParameterException {
		final ExecutionDescription from = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "testExecution", "testAlgorithm");
		Assert.assertFalse(from.parameterNameExists("notexists"));
		from.addTextAlgorithm(new TextAlgorithmParameter("pName", ParameterType.SUB_EXECUTION, Arrays.asList("vfe", "oru", "lkj")));
		final GeneticExecutionInitializer gei = from.createGeneticExecution(new FromToPeriod(new Date(), new Date()));
		Assert.assertEquals("testAlgorithm", gei.getAlgorithmName());
		Assert.assertEquals("testExecution", gei.getExecutionName());
		for (int i = 0; i < 1000; ++i) {
			final AlgorithmSettings aSettings = gei.generateRandom();
			aSettings.getSubExecutions().sort(Ordering.natural());
			Assert.assertTrue(Arrays.asList("lkj", "vfe", "oru").contains(aSettings.getSubExecutions().get(0)));
		}
	}

}
