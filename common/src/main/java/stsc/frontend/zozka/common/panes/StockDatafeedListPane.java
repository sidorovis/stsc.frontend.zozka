package stsc.frontend.zozka.common.panes;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.common.system.BackgroundProcess;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.YahooFileStockStorage;

/**
 * GUI Pane with Stock Datafeed List and {@link YahooFileStockStorage} (loader
 * for Yahoo stock datafeed). Load stocks in foreground.
 */
public final class StockDatafeedListPane extends BorderPane {

	private YahooFileStockStorage stockStorage;

	@FXML
	private Label label;

	private ObservableList<StockDescription> model = FXCollections.observableArrayList();
	@FXML
	private TableView<StockDescription> table;
	@FXML
	private TableColumn<StockDescription, Number> idColumn;
	@FXML
	private TableColumn<StockDescription, String> stockColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> liquidColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> validColumn;

	private final ProgressWithStopPane progressWithStopPane = new ProgressWithStopPane();

	public StockDatafeedListPane(final String title) throws IOException {
		final URL location = StockDatafeedListPane.class.getResource("stock_datafeed_list_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		setCenter(gui);

		initialize();
		label.setText(title);
	}

	private void initialize() {
		validateGui();
		table.setItems(model);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		stockColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		liquidColumn.setCellValueFactory(cellData -> cellData.getValue().liquidProperty());
		liquidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(liquidColumn));
		validColumn.setCellValueFactory(cellData -> cellData.getValue().validProperty());
		validColumn.setCellFactory(CheckBoxTableCell.forTableColumn(validColumn));
		setBottom(null);
	}

	private void validateGui() {
		assert label != null : "fx:id=\"label\" was not injected: check your FXML file.";
		assert table != null : "fx:id=\"table\" was not injected: check your FXML file.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file.";
		assert stockColumn != null : "fx:id=\"stockColumn\" was not injected: check your FXML file.";
		assert liquidColumn != null : "fx:id=\"liquidColumn\" was not injected: check your FXML file.";
		assert validColumn != null : "fx:id=\"validColumn\" was not injected: check your FXML file.";
	}

	public void loadDatafeed(final Path datafeedPath, Function<Set<String>, Optional<Void>> onFinish, Optional<Predicate<String>> filter) {
		model.clear();
		try {
			setBottom(progressWithStopPane);
			final YahooFileStockStorage ss = new YahooFileStockStorage(new YahooDatafeedSettings(datafeedPath, datafeedPath), false);
			if (filter.isPresent()) {
				ss.removeIf(filter.get());
			}
			postLoadDatafeedActions(onFinish, ss);
		} catch (IOException e) {
			new TextAreaDialog(e);
		}
	}

	private void postLoadDatafeedActions(Function<Set<String>, Optional<Void>> onFinish, final YahooFileStockStorage stoskStorage) throws IOException {
		setStockStorage(stoskStorage);
		setUpdateModel(stoskStorage);
		startLoadIndicatorUpdates(stoskStorage, onFinish);
		setProgressStopButton(stoskStorage);
		stoskStorage.startInBackground();
	}

	private void setStockStorage(YahooFileStockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	private void setUpdateModel(final YahooFileStockStorage ss) {
		final AtomicInteger index = new AtomicInteger(0);
		ss.addReceiver(newStock -> Platform.runLater(() -> {
			synchronized (model) {
				model.add(new StockDescription(index.getAndIncrement(), newStock));
			}
		}));
	}

	private void startLoadIndicatorUpdates(final YahooFileStockStorage stockStorage, final Function<Set<String>, Optional<Void>> onFinish) {
		final Thread t = new Thread(() -> {
			try {
				updateIndicatorValue(stockStorage);
				Platform.runLater(() -> {
					progressWithStopPane.setIndicatorProgress(100.0);
					setBottom(null);
					if (onFinish != null) {
						onFinish.apply(null);
					}
				});
			} catch (Exception e) {
				new TextAreaDialog(e);
			}
		});
		t.start();
	}

	private void updateIndicatorValue(BackgroundProcess<?, ?> bp) throws InterruptedException {
		final int allSize = bp.amountToProcess();
		int currentSize = bp.amountToProcess();
		while (currentSize != 0) {
			final int value = allSize - currentSize;
			currentSize = bp.amountToProcess();
			Platform.runLater(() -> {
				progressWithStopPane.setIndicatorProgress((double) value / allSize);
			});
			Thread.sleep(300);
		}
	}

	private void setProgressStopButton(final BackgroundProcess<?, ?> ss) {
		progressWithStopPane.setOnStopButtonAction(() -> {
			try {
				ss.stopBackgroundProcess();
				ss.waitForBackgroundProcess();
			} catch (Exception e) {
				new TextAreaDialog(e);
			}
		});
	}

	public void setOnMouseDoubleClick(final Function<StockDescription, Optional<Void>> function) {
		table.setOnMouseClicked(eh -> {
			if (eh.getButton() == MouseButton.PRIMARY && eh.getClickCount() == 2) {
				final StockDescription sd = table.getSelectionModel().getSelectedItem();
				if (sd != null) {
					function.apply(sd);
				}
			}
		});
	}

	public void updateStock(Stock newStockData) {
		stockStorage.updateStock(newStockData);
		model.forEach((sd) -> {
			if (sd.getStock().getInstrumentName().equals(newStockData.getInstrumentName())) {
				sd.setStock(newStockData);
			}
		});
		table.setItems(model);
	}

	/**
	 * @return loaded {@link StockStorage} or null otherwise.
	 */
	public StockStorage getStockStorage() {
		return stockStorage;
	}

}
