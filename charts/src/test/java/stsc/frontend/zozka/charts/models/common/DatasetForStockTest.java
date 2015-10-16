package stsc.frontend.zozka.charts.models.common;

import org.junit.Test;

import org.junit.Assert;

import stsc.common.Settings;
import stsc.common.storage.StockStorage;
import stsc.storage.mocks.StockStorageMock;

public class DatasetForStockTest {

	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Test
	public void testDatasetForStock() {
		final DatasetForStock dfs = new DatasetForStock(stockStorage.getStock("aapl").get());
		Assert.assertEquals(1, dfs.getSeriesCount());
		Assert.assertEquals(2.91, dfs.getOpenValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(2.95063, dfs.getHighValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(2.882547, dfs.getLowValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(2.91, dfs.getCloseValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(2981600.0, dfs.getVolumeValue(0, 0), Settings.doubleEpsilon);
		for (int i = 0; i <= dfs.getItemCount(0); ++i) {
			Assert.assertNotNull(dfs.getOpen(0, i));
			Assert.assertNotNull(dfs.getHigh(0, i));
			Assert.assertNotNull(dfs.getLow(0, i));
			Assert.assertNotNull(dfs.getClose(0, i));
			Assert.assertNotNull(dfs.getVolume(0, i));
			Assert.assertNotNull(dfs.getX(0, i));
			Assert.assertNotNull(dfs.getY(0, i));
		}
	}

}
