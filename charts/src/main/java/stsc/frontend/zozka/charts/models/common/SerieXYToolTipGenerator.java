package stsc.frontend.zozka.charts.models.common;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

/**
 * Implementation for {@link XYToolTipGenerator} - shows same tooltip for all
 * chart (name that was given in the constructor).
 */
public final class SerieXYToolTipGenerator implements XYToolTipGenerator {

	private final String name;

	public SerieXYToolTipGenerator(String name) {
		this.name = name;
	}

	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		return name;
	}

}
