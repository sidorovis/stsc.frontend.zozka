package stsc.frontend.zozka.components;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBarBuilder;
import javafx.stage.Window;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.YahooFileStockStorage;

/**
 * {@link YahooFileStockStorage} loader with GUI representation. Created as
 * progress bar.
 */
public final class DatafeedLoader {

	private Thread loadThread;
	private final YahooFileStockStorage yahooFileStockStorage;

	public DatafeedLoader(final Path datafeed) throws Exception {
		final YahooDatafeedSettings yahooDatafeedSettings = new YahooDatafeedSettings(datafeed);
		this.yahooFileStockStorage = new YahooFileStockStorage(yahooDatafeedSettings, false);
	}

	public void startLoad(EventHandler<WorkerStateEvent> successHandler, EventHandler<WorkerStateEvent> exitHandler)
			throws ClassNotFoundException, IOException, InterruptedException {
		final ProgressBar progressBar = new ProgressBar(0.0);
		final ProgressBarTask task = new ProgressBarTask(yahooFileStockStorage, progressBar);
		this.loadThread = new Thread(task);
		this.loadThread.start();

		final Alert loadDialog = new Alert(AlertType.INFORMATION, "Wait for datafeed load...", ButtonType.CLOSE);
		loadDialog.getDialogPane().setContent(progressBar);
		loadDialog.show();
		task.setOnSucceeded(eh -> Platform.runLater(() -> {
			loadDialog.hide();
			try {
				yahooFileStockStorage.waitForBackgroundProcess();
			} catch (Exception e) {
				final PipedOutputStream pis = new PipedOutputStream();
				final PrintWriter pw = new PrintWriter(pis);
				e.printStackTrace(pw);
				new TextAreaDialog("Exception arrived while loading yahoo file stock storage datafeed.", pis.toString()).showAndWait();
			}
			successHandler.handle(eh);
		}));
		task.setOnFailed(exitHandler);
		task.setOnCancelled(exitHandler);
		yahooFileStockStorage.startInBackground();
	}

	public StockStorage getStockStorage() throws InterruptedException {
		return yahooFileStockStorage;
	}

}
