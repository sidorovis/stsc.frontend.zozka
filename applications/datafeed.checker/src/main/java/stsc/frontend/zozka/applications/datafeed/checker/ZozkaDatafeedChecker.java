package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.common.dialogs.StockListDialog;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.frontend.zozka.common.panes.StockDatafeedListPane;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.liquiditator.StockFilter;

public final class ZozkaDatafeedChecker extends Application {

	private static final StockFilter stockFilter = new StockFilter();
	private static final ZozkaDatafeedCheckerDatafeedLoaderHelper datafeedLoaderHelper = new ZozkaDatafeedCheckerDatafeedLoaderHelper(stockFilter);
	private static final ZozkaDatafeedCheckerStockCorrectnessHelper stockCorrectnessHelper = new ZozkaDatafeedCheckerStockCorrectnessHelper(stockFilter);

	private Stage owner;

	@FXML
	private BorderPane borderPane;
	@FXML
	private final Label datafeedPathLabel = new Label("<datafeed path>");
	private String datafeedPath;
	private String datafeedPrefixLetter;

	private StockDatafeedListPane dataStockList;
	private StockDatafeedListPane filteredStockDataList;

	public ZozkaDatafeedChecker() {
		this.datafeedPath = "";
		this.datafeedPrefixLetter = "";
	}

	@Override
	public void start(final Stage owner) throws Exception {
		this.owner = owner;
		owner.setScene(initializeGui());
		owner.setMinHeight(500);
		owner.setMinWidth(830);
		owner.setWidth(830);
		owner.show();
		connectDatafeedChange();
	}

	private void connectDatafeedChange() {
		datafeedPathLabel.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				chooseFolder();
			}
		});
	}

	private void chooseFolder() {
		if (datafeedLoaderHelper.makeUserChooseFolder(owner, datafeedPathLabel)) {
			try {
				loadDatafeed();
			} catch (IOException e) {
				new TextAreaDialog("Exception", e).showAndWait();
			}
		}
	}

	private Scene initializeGui() throws IOException {
		borderPane = new BorderPane();
		borderPane.setTop(datafeedPathLabel);
		final Scene scene = new Scene(borderPane);
		final SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.HORIZONTAL);
		dataStockList = new StockDatafeedListPane("Data");
		addAndSetupOnDoubleClickTableAction(splitPane, dataStockList);
		filteredStockDataList = new StockDatafeedListPane("Filtered data");
		addAndSetupOnDoubleClickTableAction(splitPane, filteredStockDataList);
		borderPane.setCenter(splitPane);
		return scene;
	}

	private void addAndSetupOnDoubleClickTableAction(SplitPane splitPane, StockDatafeedListPane listPane) {
		splitPane.getItems().add(listPane);
		setOnDoubleClickTableAction(listPane);
	}

	private void setOnDoubleClickTableAction(final StockDatafeedListPane listPane) {
		listPane.setOnMouseDoubleClick(sd -> {
			processStockDescription(sd);
			return Optional.empty();
		});
	}

	// TODO delete it or not
	// private void datafeedEdit(final MouseEvent mouseEvent) {
	// if (mouseEvent.getButton() == MouseButton.PRIMARY &&
	// mouseEvent.getClickCount() == 2) {
	// chooseFolder();
	// }
	// }

	private void loadDatafeed() throws IOException {
		final Optional<String> selectedPrefix = datafeedLoaderHelper.makeUserChooseStockNamePrefix();
		if (selectedPrefix.isPresent() && (datafeedPrefixLetter != selectedPrefix.get() || datafeedPath != datafeedPathLabel.getText())) {
			datafeedPath = datafeedPathLabel.getText();
			datafeedPrefixLetter = selectedPrefix.get();
			runLoadDatafeed(selectedPrefix.get());
		}
	}

	private void runLoadDatafeed(final String selectedPrefix) throws IOException {
		final Optional<Predicate<String>> predicate = Optional.of((p) -> {
			return !p.startsWith(selectedPrefix);
		});
		final YahooDatafeedSettings yahooDatafeedSettings = new YahooDatafeedSettings(Paths.get(datafeedPath));

		dataStockList.loadDatafeed( //
				yahooDatafeedSettings.getDataFolder(), //
				onDataEnd -> {
					filteredStockDataList.loadDatafeed( //
							yahooDatafeedSettings.getFilteredDataFolder(), //
							onFilterEnd -> {
						checkThatStocksAreEqual();
						return Optional.empty();
					} , predicate);
					return Optional.empty();
				} , predicate);
	}

	private void checkThatStocksAreEqual() {
		final StockStorage dataStockStorage = dataStockList.getStockStorage();
		final StockStorage filteredDataStockStorage = filteredStockDataList.getStockStorage();
		final Set<String> notEqualStockList = datafeedLoaderHelper.findDifferenceByDaysSizeAndStockFilter(dataStockStorage, filteredDataStockStorage);
		if (!notEqualStockList.isEmpty()) {
			runShowListDialog(dataStockStorage, filteredDataStockStorage, notEqualStockList);
		}
	}

	private void runShowListDialog(final StockStorage dataStockStorage, final StockStorage filteredDataStockStorage, final Set<String> notEqualStockList) {
		final StockListDialog stockListDialog = new StockListDialog(owner, "List of Stocks which have different days size at data and filtered data.");
		stockListDialog.setOnMouseDoubleClicked(sd -> {
			processStockDescription(sd, dataStockStorage, filteredDataStockStorage, stockListDialog.getModel());
			return Optional.empty();
		});
		int index = 0;
		for (String stockName : notEqualStockList) {
			final Optional<Stock> stockPtr = dataStockStorage.getStock(stockName);
			if (!stockPtr.isPresent()) {
				stockListDialog.getModel().add(new StockDescription(index++, stockPtr.get()));
			}
		}
		stockListDialog.show();
	}

	private void processStockDescription(final StockDescription sd) {
		try {
			final String stockName = sd.getStock().getInstrumentName();
			final Optional<Stock> data = dataStockList.getStockStorage().getStock(stockName);
			final Optional<Stock> filtered = filteredStockDataList.getStockStorage().getStock(stockName);

			final ZozkaDatafeedCheckerTempHelper helper = new ZozkaDatafeedCheckerTempHelper(new YahooDatafeedSettings(Paths.get(datafeedPath)), dataStockList,
					filteredStockDataList, model);
			if (data.isPresent() && filtered.isPresent()) {
				helper.checkStockAndAskForUser(sd.getStock(), data.get(), filtered.get(), owner);
			}
		} catch (Exception e) {
			new TextAreaDialog("Exception", e).showAndWait();
		}
	}

	public static void main(String[] args) {
		Application.launch(ZozkaDatafeedChecker.class, args);
	}
}
