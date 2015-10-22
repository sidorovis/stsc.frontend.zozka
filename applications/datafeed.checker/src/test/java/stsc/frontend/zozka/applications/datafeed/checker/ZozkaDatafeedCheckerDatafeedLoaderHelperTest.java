package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.liquiditator.StockFilter;

public final class ZozkaDatafeedCheckerDatafeedLoaderHelperTest {

	@Test
	public void testZozkaDatafeedCheckerDatafeedLoaderHelperGeneratePrefixForNames() {
		final List<String> result = new ZozkaDatafeedCheckerDatafeedLoaderHelper(new StockFilter()).generatePrefixForNames();
		Assert.assertEquals(27, result.size());
		Assert.assertEquals("", result.get(0));
		Assert.assertEquals("a", result.get(1));
		Assert.assertEquals("b", result.get(2));
		Assert.assertEquals("z", result.get(26));
	}

	@Test
	public void testZozkaDatafeedCheckerDatafeedLoaderHelperFindDifferenceByDaysSizeAndStockFilter() throws IOException, InterruptedException, URISyntaxException {
		final Path p = Paths.get(new File(getClass().getResource(YahooDatafeedSettings.DATA_FOLDER).toURI()).getAbsolutePath());
		final YahooFileStockStorage all = new YahooFileStockStorage(new YahooDatafeedSettings(p, p), false);
		all.removeIf((s) -> {
			return s.startsWith("aa");
		});
		all.startInBackground();
		final YahooFileStockStorage filtered = new YahooFileStockStorage(new YahooDatafeedSettings(p, p), false);
		filtered.removeIf((s) -> {
			return s.startsWith("spy");
		});
		filtered.startInBackground();
		all.waitForBackgroundProcess();
		filtered.waitForBackgroundProcess();
		final Set<String> diff = new ZozkaDatafeedCheckerDatafeedLoaderHelper(new StockFilter()).findDifferenceByDaysSizeAndStockFilter(all, filtered);
		Assert.assertEquals(1, diff.size());
		Assert.assertEquals("aapl", diff.iterator().next());
	}

	@Test
	public void testZozkaDatafeedCheckerDatafeedLoaderHelperFindDifferenceByDaysSize() throws IOException, InterruptedException, URISyntaxException {
		final Path dataPath = Paths.get(new File(getClass().getResource(YahooDatafeedSettings.DATA_FOLDER).toURI()).getAbsolutePath());
		final YahooFileStockStorage data = new YahooFileStockStorage(new YahooDatafeedSettings(dataPath, dataPath), true);
		final Path filteredPath = Paths.get(new File(getClass().getResource(YahooDatafeedSettings.FILTER_DATA_FOLDER).toURI()).getAbsolutePath());
		final YahooFileStockStorage filtered = new YahooFileStockStorage(new YahooDatafeedSettings(filteredPath, filteredPath), true);
		data.waitForBackgroundProcess();
		filtered.waitForBackgroundProcess();
		final Set<String> diff = new ZozkaDatafeedCheckerDatafeedLoaderHelper(new StockFilter()).findDifferenceByDaysSizeAndStockFilter(data, filtered);
		Assert.assertEquals(2, diff.size());
	}

}
