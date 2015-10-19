package stsc.frontend.zozka.charts.pane;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;

public class EquityPaneTest {

	/**
	 * Please change this test with {@link VisualTestEquityPane} only.
	 */

	@Test
	public void testEquityPane() throws ParseException, BadAlgorithmException, BadSignalException {
		final Simulator simulator = new VisualTestEquityPane().getSimulator();
		Assert.assertNotNull(simulator.getMetrics());
	}

}
