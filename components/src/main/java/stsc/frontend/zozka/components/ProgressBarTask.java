package stsc.frontend.zozka.components;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import stsc.common.system.BackgroundProcess;

/**
 * {@link ProgressBarTask} is an {@link Task} implementation with updating of GUI {@link ProgressBar} with selected period of time. <br/>
 * It should be started in parallel thread.
 */
final class ProgressBarTask extends Task<Integer> {

	private final int sleepTimeBetweenUpdates;
	private final BackgroundProcess<?, ?> backgroundProcess;
	private final ProgressBar progressBar;
	private final int initialSize;

	private ProgressBarTask(final Builder builder) throws NullPointerException {
		if (builder.progressBar == null) {
			throw new NullPointerException("setProgressBar() should be called before build()");
		}
		if (builder.backgroundProcess == null) {
			throw new NullPointerException("setBackgroundProcess() should be called before build()");
		}
		this.sleepTimeBetweenUpdates = builder.sleepTimeBetweenUpdates;
		this.backgroundProcess = builder.backgroundProcess;
		this.progressBar = builder.progressBar;
		this.initialSize = queueSize();
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
			Thread.sleep(sleepTimeBetweenUpdates);
		}
		return iterations;
	}

	private int queueSize() {
		return backgroundProcess.amountToProcess();
	}

	public static class Builder {

		private int sleepTimeBetweenUpdates = 300;
		private BackgroundProcess<?, ?> backgroundProcess;
		private ProgressBar progressBar;

		public Builder() {
		}

		Builder setSleepTimeBetweenUpdates(int sleepTime) {
			this.sleepTimeBetweenUpdates = sleepTime;
			return this;
		}

		Builder setProgressBar(final ProgressBar progressBar) {
			this.progressBar = progressBar;
			return this;
		}

		Builder setBackgroundProcess(final BackgroundProcess<?, ?> backgroundProcess) {
			this.backgroundProcess = backgroundProcess;
			return this;
		}

		ProgressBarTask build() {
			return new ProgressBarTask(this);
		}

	}

	public static Builder getBuilder() {
		return new Builder();
	}

}