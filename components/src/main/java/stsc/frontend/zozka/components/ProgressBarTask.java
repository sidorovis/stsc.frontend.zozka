package stsc.frontend.zozka.components;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import stsc.common.system.BackgroundProcess;

public final class ProgressBarTask extends Task<Integer> {

	private final int initialSize;
	private final BackgroundProcess<?> stockStorage;
	private final ProgressBar progressBar;

	public ProgressBarTask(final BackgroundProcess<?> stockStorage, ProgressBar progressBar) {
		this.stockStorage = stockStorage;
		this.progressBar = progressBar;
		initialSize = queueSize();
	}

	@Override
	protected Integer call() throws Exception {
		int currentQueueSize = queueSize();
		int iterations = initialSize - currentQueueSize;
		while (currentQueueSize != 0) {
			updateProgress(iterations, initialSize);
			progressBar.setProgress((double) iterations / initialSize);
			currentQueueSize = queueSize();
			iterations = initialSize - currentQueueSize;
			Thread.sleep(300);
		}
		return iterations;
	}

	private int queueSize() {
		return stockStorage.amountToProcess();
	}
}