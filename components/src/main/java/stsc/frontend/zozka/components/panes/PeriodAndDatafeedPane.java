package stsc.frontend.zozka.components.panes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.components.DatafeedLoader;

/**
 * GUI Pane that provide possibility to setup datafeed path and period.
 */
public final class PeriodAndDatafeedPane extends Pane {

	private final Stage owner;
	private final Parent gui;

	/**
	 * On the start should be empty (shows that path was never selected). <br/>
	 * Should be {@link String} type to have possibility to compare with
	 * {@link #datafeedPath} field text value.
	 */
	private Optional<String> datafeed = Optional.empty();
	private StockStorage stockStorage;

	@FXML
	private Label datafeedPath;
	@FXML
	private DatePicker fromPeriod;
	@FXML
	private DatePicker toPeriod;

	public PeriodAndDatafeedPane(final Stage owner) throws IOException {
		this.owner = owner;
		final URL location = getClass().getResource("period_and_datafeed_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		initialize();
	}

	public Parent getGui() {
		return gui;
	}

	private void initialize() {
		validateGui();
		fromPeriod.setValue(LocalDate.of(1990, 1, 1));
		toPeriod.setValue(LocalDate.of(2020, 12, 31));
	}

	private void validateGui() {
		assert datafeedPath != null : "fx:id=\"datafeedPath\" was not injected: check your FXML file.";
		assert fromPeriod != null : "fx:id=\"fromPeriod\" was not injected: check your FXML file.";
		assert toPeriod != null : "fx:id=\"toPeriod\" was not injected: check your FXML file.";
	}

	@FXML
	private void datafeedEdit(final MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
			final String path = datafeedPath.getText();
			final File f = new File(path);

			final Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to change datafeed path?", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Datafeed Path");
			alert.setHeaderText(null);
			final Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get().equals(ButtonType.YES)) {
				final DirectoryChooser dc = new DirectoryChooser();
				if (f.exists()) {
					dc.setInitialDirectory(f);
				}
				final File choosedDatafeedFolder = dc.showDialog(owner);
				if (choosedDatafeedFolder != null && choosedDatafeedFolder.isDirectory()) {
					datafeedPath.setText(choosedDatafeedFolder.getAbsolutePath());
				}
			}
		}
	}

	private Date createDate(LocalDate date) {
		return new Date(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public boolean loadStockStorage(EventHandler<WorkerStateEvent> loadStockHandler) {
		try {
			if (!datafeed.isPresent() || !datafeed.get().equals(datafeedPath.getText())) {
				startLoadStockStorage(loadStockHandler);
			} else {
				loadStockHandler.handle(new WorkerStateEvent(null, WorkerStateEvent.WORKER_STATE_SUCCEEDED));
			}
		} catch (Exception e) {
			new TextAreaDialog(e);
			return false;
		}
		return true;
	}

	private void startLoadStockStorage(final EventHandler<WorkerStateEvent> hander) {
		try {
			datafeed = Optional.of(datafeedPath.getText());
			final DatafeedLoader loader = new DatafeedLoader(Paths.get(datafeed.get()));
			loader.startLoad(successHandler -> {
				try {
					stockStorage = loader.getStockStorage();
					hander.handle(successHandler);
				} catch (Exception e) {
					new TextAreaDialog(e);
				}
			} , exitHandler -> {
				stockStorage = null;
				new TextAreaDialog("Datafeed load failed", "Error: " + exitHandler.toString());
				hander.handle(exitHandler);
			});
		} catch (Exception e) {
			new TextAreaDialog(e);
		}
	}

	public FromToPeriod getPeriod() {
		return new FromToPeriod(createDate(fromPeriod.getValue()), createDate(toPeriod.getValue()));
	}
}
