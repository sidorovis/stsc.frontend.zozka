package stsc.frontend.zozka.charts.models;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.models.common.DatasetForStock;
import stsc.storage.mocks.StockStorageMock;

public class CandleSticksChartDatasetTest {

	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Test
	public void testCandleSticksChartDataset() {
		final DatasetForStock dfs = new DatasetForStock(stockStorage.getStock("aapl").get());
		final CandleSticksChartDataset candleSticksChartDataset = new CandleSticksChartDataset(dfs);
		Assert.assertEquals(0, candleSticksChartDataset.getIndex());
		Assert.assertEquals(dfs, candleSticksChartDataset.getTimeSeriesCollection());
	}

}
