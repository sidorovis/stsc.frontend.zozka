package stsc.frontend.zozka.charts.models;

import java.text.ParseException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.mocks.StockStorageMock;

public class CurveTimeSerieSettingTest {

	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	private FromToPeriod getPeriod() throws ParseException {
		return new FromToPeriod("24-07-1970", "15-09-2015");
	}

	@Test
	public void testCurveTimeSerieSettingForAdjectiveCloseWithNoPeriod() {
		final CurveTimeSerieSetting setting = new CurveTimeSerieSetting("title", stockStorage.getStock("aapl").get(), 0);
		Assert.assertEquals(0, setting.getIndex());
		Assert.assertNotNull(setting.getRenderer());
		Assert.assertNotNull(setting.getTimeSeriesCollection());
	}

	@Test
	public void testCurveTimeSerieSettingForAdjectiveCloseWithPeriod() throws ParseException {
		final CurveTimeSerieSetting setting = new CurveTimeSerieSetting("title", stockStorage.getStock("aapl").get(), 0, getPeriod());
		Assert.assertEquals(0, setting.getIndex());
		Assert.assertNotNull(setting.getRenderer());
		Assert.assertNotNull(setting.getTimeSeriesCollection());
	}

	@Test
	public void testCurveTimeSerieSettingForEodAlgorithm() throws BadAlgorithmException, ParseException, BadSignalException {
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, getPeriod(), "EodExecutions = a1\na1.loadLine = eod.Adln()\n");
		final List<String> outEodNames = init.generateOutForEods();
		final SimulatorSettings settings = new SimulatorSettings(0, init);
		final Simulator simulator = new Simulator(settings);

		final CurveTimeSerieSetting curveTimeSerieSetting = new CurveTimeSerieSetting(true, outEodNames.get(0), 2, simulator.getSignalsStorage());
		Assert.assertEquals(1, curveTimeSerieSetting.getTimeSeriesCollection().getSeries().size());
		Assert.assertEquals(7447, curveTimeSerieSetting.getTimeSeriesCollection().getItemCount(0));
	}

	@Test
	public void testCurveTimeSerieSettingForStockAlgorithm() throws BadAlgorithmException, ParseException, BadSignalException {
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, getPeriod(),
				"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( .Level( f = 0.75d, Diff(.Input(e=close), .Input(e=open)) ) )\n");
		final List<String> outStockNames = init.generateOutForStocks();
		final SimulatorSettings settings = new SimulatorSettings(0, init);
		final Simulator simulator = new Simulator(settings);

		final CurveTimeSerieSetting curveTimeSerieSetting = new CurveTimeSerieSetting(outStockNames.get(0), "aapl", 4, simulator.getSignalsStorage());
		Assert.assertEquals(1, curveTimeSerieSetting.getTimeSeriesCollection().getSeriesCount());
		Assert.assertEquals(7430, curveTimeSerieSetting.getTimeSeriesCollection().getItemCount(0));
	}

}