package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.liquiditator.StockFilter;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockDatafeedListPane {

	private static final StockFilter stockFilter = new StockFilter();

	private final Stage owner;
	private final Parent gui;

	private StockStorage stockStorage;

	@FXML
	private Label label;
	@FXML
	private BorderPane borderPane;

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

	private ProgressWithStopPane progressWithStopPane = new ProgressWithStopPane();

	public StockDatafeedListPane(final Stage owner, final String title) throws IOException {
		this.owner = owner;
		final URL location = EquityPane.class.getResource("04_stock_datafeed_list_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();

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
		borderPane.setBottom(null);
	}

	private void validateGui() {
		assert label != null : "fx:id=\"label\" was not injected: check your FXML file.";
		assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file.";
		assert table != null : "fx:id=\"table\" was not injected: check your FXML file.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file.";
		assert stockColumn != null : "fx:id=\"stockColumn\" was not injected: check your FXML file.";
		assert liquidColumn != null : "fx:id=\"liquidColumn\" was not injected: check your FXML file.";
		assert validColumn != null : "fx:id=\"validColumn\" was not injected: check your FXML file.";
	}

	public Parent getGui() {
		return gui;
	}

	public void loadDatafeed(final String string) {
		model.clear();
		try {
			borderPane.setBottom(progressWithStopPane);
			final YahooFileStockStorage ss = new YahooFileStockStorage(string, string, false);
			stockStorage = ss;
			setUpdateModel(ss);
			startLoadIndicatorUpdates(ss);
			ss.startLoadStocks();
			setProgressStopButton(ss);
		} catch (ClassNotFoundException | IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void setUpdateModel(final YahooFileStockStorage ss) {
		final AtomicInteger index = new AtomicInteger(0);
		ss.addReceiver(newStock -> Platform.runLater(() -> {
			final boolean liquid = stockFilter.test(newStock) == null;
			synchronized (model) {
				model.add(new StockDescription(index.getAndIncrement(), newStock, liquid));
			}
		}));
	}

	private void startLoadIndicatorUpdates(final YahooFileStockStorage ss) {
		final Queue<String> queue = ss.getTasks();

		final Thread t = new Thread(() -> {
			try {
				updateIndicatorValue(queue);
				Platform.runLater(() -> {
					progressWithStopPane.setIndicatorProgress(100.0);
					borderPane.setBottom(null);
				});
			} catch (Exception e) {
				Dialogs.create().owner(owner).showException(e);
			}
		});
		t.start();
	}

	private void updateIndicatorValue(final Queue<String> queue) throws InterruptedException {
		final int allSize = queue.size();
		while (!queue.isEmpty()) {
			final int value = allSize - queue.size();
			Platform.runLater(() -> {
				progressWithStopPane.setIndicatorProgress((double) value / allSize);
			});
			Thread.sleep(300);
		}
	}

	private void setProgressStopButton(final YahooFileStockStorage ss) {
		progressWithStopPane.setOnStopButtonAction(() -> {
			try {
				ss.stopLoadStocks();
				ss.waitForLoad();
			} catch (Exception e) {
				Dialogs.create().owner(owner).showException(e);
			}
		});
	}
}
