package stsc.frontend.zozka.charts.panes;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.general.trading.TradeProcessorInit;

public class CurvesViewPaneTest {

	private static VisualTestCurvesViewPane visualTestCurvesViewPane = new VisualTestCurvesViewPane();

	@Test
	public void testCurvesViewPaneCreatePaneForAdjectiveClose() throws Exception {
		final JFXPanel jfxPanel = new JFXPanel();
		final Stock aapl = visualTestCurvesViewPane.getStock();
		final CurvesViewPane curvesViewPane = CurvesViewPane.createPaneForAdjectiveClose(aapl);
		Assert.assertEquals(2, curvesViewPane.getTableModel().size());
		Assert.assertEquals(1, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getSeriesCount());
		Assert.assertEquals(8792, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getItemCount(0));
		jfxPanel.setEnabled(false);
	}

	@Test
	public void testCurvesViewPaneCreatePaneForAdjectiveCloseWithPeriod() throws Exception {
		final JFXPanel jfxPanel = new JFXPanel();
		final Stock aapl = visualTestCurvesViewPane.getStock();
		final CurvesViewPane curvesViewPane = CurvesViewPane.createPaneForAdjectiveClose(aapl, visualTestCurvesViewPane.getPeriod());
		Assert.assertEquals(2, curvesViewPane.getTableModel().size());
		Assert.assertEquals(1, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getSeriesCount());
		Assert.assertEquals(4028, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getItemCount(0));
		jfxPanel.setEnabled(false);
	}

	@Test
	public void testCurvesViewPaneCreatePaneForOnStockAlgorithm() throws Exception {
		final JFXPanel jfxPanel = new JFXPanel();
		final Stock aapl = visualTestCurvesViewPane.getStock();
		final TradeProcessorInit tpInit = visualTestCurvesViewPane.getTradeProcessorInitForOnStockAlgorithm();
		final List<String> executionsName = tpInit.generateOutForStocks();
		final SignalsStorage signalsStorage = visualTestCurvesViewPane.getSignalsStorage(tpInit);

		final CurvesViewPane curvesViewPane = CurvesViewPane.createPaneForOnStockAlgorithm(aapl, visualTestCurvesViewPane.getPeriod(), executionsName, signalsStorage);
		Assert.assertEquals(4, curvesViewPane.getTableModel().size());
		Assert.assertEquals(1, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getSeriesCount());
		Assert.assertEquals(4030, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getItemCount(0));
		Assert.assertEquals(4030, curvesViewPane.getTableModel().get(1).getTimeSeriesCollection().getItemCount(0));
		Assert.assertEquals(4030, curvesViewPane.getTableModel().get(2).getTimeSeriesCollection().getItemCount(0));
		Assert.assertEquals(51, curvesViewPane.getTableModel().get(3).getTimeSeriesCollection().getItemCount(0));
		jfxPanel.setEnabled(false);
	}

	@Test
	public void testCurvesViewPaneCreatePaneForOnEodAlgorithm() throws Exception {
		final JFXPanel jfxPanel = new JFXPanel();
		final TradeProcessorInit tpInit = visualTestCurvesViewPane.getTradeProcessorInitForOnEodAlgorithm();
		final List<String> executionsName = tpInit.generateOutForEods();
		final SignalsStorage signalsStorage = visualTestCurvesViewPane.getSignalsStorage(tpInit);

		final CurvesViewPane curvesViewPane = CurvesViewPane.createPaneForOnEodAlgorithm(visualTestCurvesViewPane.getPeriod(), executionsName, signalsStorage);
		Assert.assertEquals(1, curvesViewPane.getTableModel().size());
		Assert.assertEquals(1, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getSeriesCount());
		Assert.assertEquals(4030, curvesViewPane.getTableModel().get(0).getTimeSeriesCollection().getItemCount(0));

		jfxPanel.setEnabled(false);
	}

}
