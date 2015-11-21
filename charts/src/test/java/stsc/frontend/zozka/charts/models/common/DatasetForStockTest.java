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
		Assert.assertEquals(8792, dfs.getItemCount(0));
		Assert.assertEquals(0.438207, dfs.getOpenValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(0.440112, dfs.getHighValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(0.415344, dfs.getLowValue(0, 1), Settings.doubleEpsilon);
		Assert.assertEquals(0.438207, dfs.getCloseValue(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(117258400.0, dfs.getVolumeValue(0, 0), Settings.doubleEpsilon);
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
