package stsc.frontend.zozka.common.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import stsc.common.algorithms.AlgorithmType;

public class SimulatorSettingsModelTest {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * Generates {@link SimulatorSettingsModel}
	 */
	public static SimulatorSettingsModel generateSimulatorSettingsModel() {
		final SimulatorSettingsModel simulatorSettingsModel = new SimulatorSettingsModel();
		simulatorSettingsModel.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "a1", "Input"));
		simulatorSettingsModel.getModel().get(0).addTextAlgorithm(new TextAlgorithmParameter("e", ParameterType.STRING, "close"));
		return simulatorSettingsModel;
	}

	@Test
	public void testSimulatorSettingsModel() throws IOException, ClassNotFoundException {
		final SimulatorSettingsModel model = new SimulatorSettingsModel();
		model.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "a1", "Input"));
		model.getModel().get(0).addTextAlgorithm(new TextAlgorithmParameter("e", ParameterType.STRING, "close"));
		final File testFile = testFolder.newFile("simulatorSettingsModelTest");
		try (OutputStream os = new FileOutputStream(testFile)) {
			model.saveToFile(os);
		}
		try (InputStream is = new FileInputStream(testFile)) {
			final SimulatorSettingsModel modelCopy = new SimulatorSettingsModel();
			modelCopy.loadFromFile(is);
			Assert.assertEquals(1, modelCopy.getModel().size());
			Assert.assertEquals("Input", modelCopy.getModel().get(0).getAlgorithmName());
		}
	}

}
