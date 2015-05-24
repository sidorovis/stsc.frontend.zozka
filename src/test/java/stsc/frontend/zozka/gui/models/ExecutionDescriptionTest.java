package stsc.frontend.zozka.gui.models;

import org.junit.Assert;
import org.junit.Test;

public class ExecutionDescriptionTest {

	@Test
	public void testExecutionDescription() {
		final ExecutionDescription from = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "testExecution", "testAlgorithm");
		Assert.assertFalse(from.parameterNameExists("notexists"));
	}
}
