package stsc.frontend.zozka.charts.models;

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmNameGenerator;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.frontend.zozka.charts.models.common.SerieXYToolTipGenerator;
import stsc.signals.DoubleSignal;

/**
 * Time serie curve chart settings. <br/>
 * Configure visibility of time serie by GUI interface requirements.
 */
public final class CurveTimeSerieSetting extends CurveChartSetting {

	private final int index;
	private final TimeSeriesCollection timeSeriesCollection;
	private final XYItemRenderer seriesRenderer;

	public CurveTimeSerieSetting(boolean showAlgo, String title, Stock stock, int index) {
		this(showAlgo, title, stock, index, 0, stock.getDays().size());
	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, Stock stock, int index, FromToPeriod period) {
		this(showAlgo, title, stock, index, stock.findDayIndex(period.getFrom()), stock.findDayIndex(period.getTo()) - 1);
	}

	private CurveTimeSerieSetting(boolean showAlgo, String title, Stock stock, int index, int fromIndex, int toIndex) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = new TimeSeries(title);

		for (int i = fromIndex; i < toIndex; ++i) {
			stsc.common.Day day = stock.getDays().get(i);
			timeSeries.addOrUpdate(new Day(day.getDate()), day.getAdjClose());
		}

		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});

	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, int index, SignalsStorage signalsStorage) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = createOnEodTimeSeries(title, signalsStorage);
		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});
	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, String stockName, int index, SignalsStorage signalsStorage) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = createOnStockTimeSeries(title, stockName, signalsStorage);
		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});
	}

	private TimeSeries createOnEodTimeSeries(String title, SignalsStorage signalsStorage) {
		final TimeSeries timeSeries = new TimeSeries(title);
		final String outName = AlgorithmNameGenerator.generateOutAlgorithmName(title);
		final int size = signalsStorage.getIndexSize(outName);
		for (int i = 0; i < size; ++i) {
			final SignalContainer<? extends SerieSignal> s = signalsStorage.getEodSignal(outName, i);
			if (s != null) {
				timeSeries.add(new Day(s.getDate()), s.getContent(DoubleSignal.class).getValue());
			}
		}
		return timeSeries;
	}

	private TimeSeries createOnStockTimeSeries(String title, String stockName, SignalsStorage signalsStorage) {
		final TimeSeries timeSeries = new TimeSeries(title);
		final String outName = AlgorithmNameGenerator.generateOutAlgorithmName(title);
		final int size = signalsStorage.getIndexSize(stockName, outName);
		for (int i = 0; i < size; ++i) {
			final SignalContainer<? extends SerieSignal> s = signalsStorage.getStockSignal(stockName, outName, i);
			if (s != null) {
				timeSeries.add(new Day(s.getDate()), s.getContent(DoubleSignal.class).getValue());
			}
		}
		return timeSeries;
	}

	public TimeSeriesCollection getTimeSeriesCollection() {
		return timeSeriesCollection;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public XYItemRenderer getRenderer() {
		return seriesRenderer;
	}
}