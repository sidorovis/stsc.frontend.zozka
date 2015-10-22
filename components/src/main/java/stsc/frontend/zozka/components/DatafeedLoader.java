package stsc.frontend.zozka.components;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
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

	public DatafeedLoader(final Path datafeed) throws IOException {
		this.yahooFileStockStorage = new YahooFileStockStorage(new YahooDatafeedSettings(datafeed), false);
	}

	public void startLoad(EventHandler<WorkerStateEvent> successHandler, EventHandler<WorkerStateEvent> exitHandler)
			throws ClassNotFoundException, IOException, InterruptedException {
		final ProgressBar progressBar = new ProgressBar(0.0);
		final ProgressBarTask task = ProgressBarTask.getBuilder(). //
				setBackgroundProcess(yahooFileStockStorage). //
				setProgressBar(progressBar). //
				build();
		this.loadThread = new Thread(task);
		this.loadThread.start();

		final Alert loadDialog = new Alert(AlertType.INFORMATION, "Wait for datafeed load...", ButtonType.CLOSE);
		loadDialog.setTitle("Datafeed loading process");
		loadDialog.setHeaderText(null);
		loadDialog.getDialogPane().setContent(progressBar);
		loadDialog.show();
		task.setOnSucceeded(eh -> Platform.runLater(() -> {
			loadDialog.hide();
			try {
				yahooFileStockStorage.waitForBackgroundProcess();
			} catch (Exception e) {
				new TextAreaDialog(e);
			}
			successHandler.handle(eh);
		}));
		task.setOnFailed(exitHandler);
		task.setOnCancelled(exitHandler);
		yahooFileStockStorage.startInBackground();
	}

	public StockStorage getStockStorage() {
		return yahooFileStockStorage;
	}

}
