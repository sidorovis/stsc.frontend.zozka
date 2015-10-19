package stsc.frontend.zozka.common.models;

import org.junit.Assert;
import org.junit.Test;

import stsc.frontend.zozka.common.models.StockDescription;
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
