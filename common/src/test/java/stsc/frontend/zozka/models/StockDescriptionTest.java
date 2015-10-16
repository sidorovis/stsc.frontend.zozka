package stsc.frontend.zozka.models;

import org.junit.Assert;
import org.junit.Test;

import stsc.storage.mocks.StockStorageMock;

public class StockDescriptionTest {

	@Test
	public void testStockDescription() {
		final StockDescription sd = new StockDescription(3, StockStorageMock.getStockStorage().getStock("aapl").get());
		Assert.assertEquals("aapl", sd.nameProperty().get());
		Assert.assertNotNull(sd.liquidProperty());
		Assert.assertNotNull(sd.validProperty());
	}

}
