package stsc.frontend.zozka.common.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmType;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.mocks.StockStorageMock;

public class SimulatorSettingsModelTest {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * Generates {@link SimulatorSettingsModel}
	 */
	public static SimulatorSettingsModel generateSimulatorSettingsModel() {
		final SimulatorSettingsModel simulatorSettingsModel = new SimulatorSettingsModel();
		simulatorSettingsModel.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "a1", "Input"));
		simulatorSettingsModel.getModel().get(0).addTextAlgorithm(new TextAlgorithmParameter("e", ParameterType.STRING, "'close'"));
		return simulatorSettingsModel;
	}

	@Test
	public void testSimulatorSettingsModel() throws IOException, ClassNotFoundException, BadParameterException, ParseException {
		final SimulatorSettingsModel model = new SimulatorSettingsModel();
		model.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "a1", ".Input"));
		model.getModel().get(0).addTextAlgorithm(new TextAlgorithmParameter("e", ParameterType.STRING, "'close','open','high','low'"));
		model.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "a1", ".Input"));
		model.getModel().get(1).addTextAlgorithm(new TextAlgorithmParameter("e", ParameterType.STRING, "'open','close','high','low'"));
		model.add(new ExecutionDescription(AlgorithmType.STOCK_VALUE, "b1", ".Diff"));
		model.getModel().get(2).addTextAlgorithm(new TextAlgorithmParameter("p1", ParameterType.SUB_EXECUTION, "'a1'"));
		model.getModel().get(2).addTextAlgorithm(new TextAlgorithmParameter("p2", ParameterType.SUB_EXECUTION, "'a2'"));
		model.add(new ExecutionDescription(AlgorithmType.EOD_VALUE, "c1", ".OpenWhileSignalAlgorithm"));
		model.getModel().get(3).addTextAlgorithm(new TextAlgorithmParameter("p1", ParameterType.SUB_EXECUTION, "'b1'"));
		model.getModel().get(3).addNumberAlgorithm(new NumberAlgorithmParameter("P", ParameterType.DOUBLE, "1000.0", "100.0", "10000.0"));

		final File testFile = testFolder.newFile("simulatorSettingsModelTest");
		try (OutputStream os = new FileOutputStream(testFile)) {
			model.saveToFile(os);
		}
		try (OutputStream os = new FileOutputStream("d://out.txt")) {
			model.saveToFile(os);
		}		
		try (InputStream is = new FileInputStream(testFile)) {
			final SimulatorSettingsModel modelCopy = new SimulatorSettingsModel();
			modelCopy.loadFromFile(is);
			Assert.assertEquals(4, modelCopy.getModel().size());
			Assert.assertEquals(".Input", modelCopy.getModel().get(0).getAlgorithmName());
			Assert.assertEquals("'close', 'open', 'high', 'low'", modelCopy.getModel().get(0).getTextAlgorithms().get(0).domenProperty().getValue());
			Assert.assertEquals(".Input", modelCopy.getModel().get(1).getAlgorithmName());
			Assert.assertEquals("'open', 'close', 'high', 'low'", modelCopy.getModel().get(1).getTextAlgorithms().get(0).domenProperty().getValue());
			Assert.assertEquals(".Diff", modelCopy.getModel().get(2).getAlgorithmName());
			Assert.assertEquals("'a1'", modelCopy.getModel().get(2).getTextAlgorithms().get(0).domenProperty().getValue());
			Assert.assertEquals("'a2'", modelCopy.getModel().get(2).getTextAlgorithms().get(1).domenProperty().getValue());
			Assert.assertEquals(".OpenWhileSignalAlgorithm", modelCopy.getModel().get(3).getAlgorithmName());
			Assert.assertEquals("'b1'", modelCopy.getModel().get(3).getTextAlgorithms().get(0).domenProperty().getValue());
			Assert.assertEquals("1000.0", modelCopy.getModel().get(3).getNumberAlgorithms().get(0).getFrom());
			Assert.assertEquals("100.0", modelCopy.getModel().get(3).getNumberAlgorithms().get(0).getStep());
			Assert.assertEquals("10000.0", modelCopy.getModel().get(3).getNumberAlgorithms().get(0).getTo());

			final SimulatorSettingsGridList ss = modelCopy.generateGridSettings(StockStorageMock.getStockStorage(),
					new FromToPeriod("13-02-1999", "15-09-2007"));
			Assert.assertEquals(1440, ss.size());
			Assert.assertEquals(1, ss.getEodInitializers().size());
			Assert.assertEquals(3, ss.getStockInitializers().size());
			Assert.assertEquals("close", ss.getStockInitializers().get(0).current().getString("e"));
			Assert.assertEquals("close", ss.getStockInitializers().get(1).current().getString("e"));
			Assert.assertEquals("close", ss.getStockInitializers().get(1).next().getString("e"));
			Assert.assertEquals("high", ss.getStockInitializers().get(1).current().getString("e"));
			Assert.assertEquals("a2", ss.getStockInitializers().get(2).current().getSubExecutions().get(1));
		}
	}

}
