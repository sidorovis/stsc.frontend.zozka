package stsc.frontend.zozka.applications;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatHelper;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.charts.panes.CurvesViewPane;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.frontend.zozka.common.panes.StockDatafeedListPane;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.downloader.YahooDownloadHelper;
import stsc.yahoo.liquiditator.StockFilter;

public class ZozkaDatafeedCheckerHelper {

	private static final YahooDownloadHelper YAHOO_DOWNLOAD_HELPER = new YahooDownloadHelper();
	private static final StockFilter stockFilter = new StockFilter();

	private final YahooDatafeedSettings yahooDatafeedSettings;
	private final StockDatafeedListPane dataStockList;
	private final StockDatafeedListPane filteredStockDataList;

	private ObservableList<StockDescription> dialogModel;

	public ZozkaDatafeedCheckerHelper(YahooDatafeedSettings yahooDatafeedSettings, StockDatafeedListPane dataStockList,
			StockDatafeedListPane filteredStockDataList, ObservableList<StockDescription> dialogModel) {
		this.yahooDatafeedSettings = yahooDatafeedSettings;
		this.dataStockList = dataStockList;
		this.filteredStockDataList = filteredStockDataList;
		this.dialogModel = dialogModel;
	}

	public static boolean isLiquid(Stock s) {
		return stockFilter.isLiquid(s);
	}

	public static boolean isValid(Stock s) {
		return stockFilter.isValid(s);
	}

	private boolean showStockRepresentation(Stage owner, Stock data, Stock filtered, boolean askForSave) {
		try {
			final String stockRepresentationTitle = generateRepresentationTitle(askForSave);
			final Alert alert = new Alert(AlertType.INFORMATION, stockRepresentationTitle, ButtonType.CLOSE);
			final DialogPane borderPane = new DialogPane();
			SplitPane splitPane = new SplitPane();
			splitPane.setOrientation(Orientation.VERTICAL);

			if (data != null) {
				final CurvesViewPane dataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(data);
				splitPane.getItems().add(dataStockViewPane.getMainPane());
			}
			if (filtered != null) {
				final CurvesViewPane filteredDataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(filtered);
				splitPane.getItems().add(filteredDataStockViewPane.getMainPane());
			}
			borderPane.setContent(splitPane);
			alert.setDialogPane(borderPane);
			if (askForSave) {
				final String error = createErrorMessage(data);
				final Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, error, ButtonType.YES, ButtonType.NO);
				final Optional<ButtonType> result = confirmationAlert.showAndWait();
				if (result.isPresent() && result.get().equals(ButtonType.YES)) {
					return true;
				}
			}
			alert.getDialogPane().setPrefHeight(640);
			alert.getDialogPane().setPrefWidth(800);
			return false;
		} catch (IOException e) {
			new TextAreaDialog("Exception", e).showAndWait();
		}
		return false;
	}

	private String createErrorMessage(Stock s) {
		final String liquid = stockFilter.isLiquidTestWithError(s);
		final String valid = stockFilter.isValidWithError(s);
		String error = "";
		error += (liquid != null) ? liquid : "";
		error += (valid != null) ? valid : "";
		if (error.isEmpty()) {
			error = "Liquid and Valid test passed";
		}
		return error;
	}

	private String generateRepresentationTitle(boolean askForSave) {
		if (askForSave) {
			return "ForAdjectiveClose - do you want to save it?";
		} else {
			return "ForAdjectiveClose";
		}
	}

	public void checkStockAndAskForUser(Stock toTest, Stock data, Stock filtered, Stage owner) {
		if (isLiquid(toTest) && isValid(toTest)) {
			showStockRepresentation(owner, data, filtered, false);
		} else {
			if (checkLiquidityAndValidityAndRedownload(owner, toTest)) {
				showStockRepresentation(owner, data, filtered, false);
			}
		}
	}

	/**
	 * @return true it download was cancelled
	 */
	private boolean checkLiquidityAndValidityAndRedownload(Stage owner, Stock stock) {
		boolean downloadCancelled = checkLiquidutyAndRedownload(owner, stock);
		if (downloadCancelled) {
			return true;
		}
		downloadCancelled = checkValidityAndRedownload(owner, stock);
		if (downloadCancelled) {
			return true;
		}
		return false;
	}

	private boolean checkLiquidutyAndRedownload(Stage owner, Stock stock) {
		if (!isLiquid(stock)) {
			return askUserForRedownloadAndRedownload(owner, stock, stockFilter.isLiquidTestWithError(stock), " not liquid");
		}
		return false;
	}

	private boolean checkValidityAndRedownload(Stage owner, Stock stock) {
		if (!isValid(stock)) {
			return askUserForRedownloadAndRedownload(owner, stock, stockFilter.isValidWithError(stock), " not valid");
		}
		return false;
	}

	private boolean askUserForRedownloadAndRedownload(Stage owner, Stock stock, String error, String value) {
		if (isUserAgreeForAction(owner, stock, "Want you redownload data?", error, value)) {
			return redownloadStock(owner, stock.getInstrumentName());
		} else {
			return true;
		}
	}

	private boolean isUserAgreeForAction(Stage owner, Stock stock, String title, String errorString, String mastheadPostfix) {
		final Alert alert = new Alert(AlertType.CONFIRMATION, errorString, ButtonType.YES, ButtonType.NO);
		alert.setTitle(title);
		alert.setHeaderText("Stock " + stock.getInstrumentName() + mastheadPostfix);
		final Optional<ButtonType> result = alert.showAndWait();
		return (result.isPresent() && result.get().equals(ButtonType.YES));
	}

	/**
	 * @param owner
	 * @return true if user refuse re-download or exception appear
	 */
	private boolean redownloadStock(Stage owner, String stockName) {
		try {
			final Optional<UnitedFormatStock> sPtr = YAHOO_DOWNLOAD_HELPER.download(stockName);
			final Optional<Stock> stockPtr = dataStockList.getStockStorage().getStock(stockName);
			if (!sPtr.isPresent() || !stockPtr.isPresent()) {
				return false;
			}
			final UnitedFormatStock s = sPtr.get();
			final boolean isSave = showStockRepresentation(owner, s, stockPtr.get(), true);
			if (isSave) {
				s.storeUniteFormatToFolder(yahooDatafeedSettings.getDataFolder());
				dataStockList.updateStock(s);
				if (isLiquid(s) && isValid(s) || filteredStockDataList.getStockStorage().getStock(s.getInstrumentName()) != null) {
					s.storeUniteFormatToFolder(yahooDatafeedSettings.getFilteredDataFolder());
					filteredStockDataList.updateStock(s);
				} else {
					YAHOO_DOWNLOAD_HELPER.deleteFilteredFile(true, yahooDatafeedSettings.getFilteredDataFolder(), UnitedFormatHelper.toFilesystem(stockName));
				}
				updateDialogModel(s);
				return false;
			} else {
				return true;
			}
		} catch (InterruptedException | IOException e) {
			new TextAreaDialog("Exception", e);
		}
		return true;
	}

	private void updateDialogModel(UnitedFormatStock s) {
		if (dialogModel != null) {
			StockDatafeedListPane.updateModel(s, dialogModel);
		}
	}

	public static Set<String> findDifferenceByDaysSizeAndStockFilter(final StockStorage dataStockStorage, final StockStorage filteredDataStockStorage,
			final Set<String> allList, final Set<String> filteredList) {
		final Set<String> notEqualStockList = new HashSet<>();
		for (String stockName : allList) {
			if (filteredList.contains(stockName)) {
				final Optional<Stock> dataStockPtr = dataStockStorage.getStock(stockName);
				final Optional<Stock> filteredDataStockPtr = filteredDataStockStorage.getStock(stockName);
				if (!dataStockPtr.isPresent() || !filteredDataStockPtr.isPresent()) {
					return notEqualStockList;
				}
				if (dataStockPtr.get().getDays().size() != filteredDataStockPtr.get().getDays().size()) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isLiquid(dataStockPtr.get()) != ZozkaDatafeedCheckerHelper.isLiquid(filteredDataStockPtr.get())) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isValid(dataStockPtr.get()) != ZozkaDatafeedCheckerHelper.isValid(filteredDataStockPtr.get())) {
					notEqualStockList.add(stockName);
				}
			}
		}
		return notEqualStockList;
	}

}
