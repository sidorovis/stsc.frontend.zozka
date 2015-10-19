package stsc.frontend.zozka.common.panes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import stsc.common.storage.StockStorage;

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
		final StockStorage ss = pane.getStockStorage();
		Assert.assertEquals(9, ss.getStockNames().size());
		jfxPanel.setEnabled(false);
	}

}
