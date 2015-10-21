package stsc.frontend.zozka.common.panes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import stsc.common.storage.StockStorage;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.YahooFileStockStorage;

public class StockDatafeedListPaneTest {

	@Test
	public void testStockDatafeedListPane() throws IOException, URISyntaxException, InterruptedException {
		final JFXPanel jfxPanel = new JFXPanel();
		final Lock lock = new ReentrantLock();
		final StockDatafeedListPane pane = new VisualTestStockDatafeedListPane().downloadDatafeedOnInterface(f -> {
			synchronized (lock) {
				lock.notify();
			}
			return Optional.empty();
		});
		synchronized (lock) {
			lock.wait();
		}
		final StockStorage stockStorage = pane.getStockStorage();
		Assert.assertEquals(2, stockStorage.getStockNames().size());
		jfxPanel.setEnabled(false);
	}

	@Test
	public void testStockDatafeedListPaneCheckThatFilterWorks() throws IOException, URISyntaxException, InterruptedException {
		final Path datafeedPath = new VisualTestStockDatafeedListPane().getDatafeedPath();
		final YahooFileStockStorage stockStorage = new YahooFileStockStorage(new YahooDatafeedSettings(datafeedPath, datafeedPath), true).waitForBackgroundProcess();
		Assert.assertEquals(10, stockStorage.getStockNames().size());
	}

}
