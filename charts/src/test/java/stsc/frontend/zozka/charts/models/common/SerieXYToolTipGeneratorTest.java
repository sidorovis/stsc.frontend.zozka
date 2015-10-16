package stsc.frontend.zozka.charts.models.common;

import org.junit.Assert;
import org.junit.Test;

public class SerieXYToolTipGeneratorTest {

	@Test
	public void testSerieXYToolTipGenerator() {
		SerieXYToolTipGenerator s = new SerieXYToolTipGenerator("hello world");
		Assert.assertEquals("hello world", s.generateToolTip(null, 0, 0));
	}

}
