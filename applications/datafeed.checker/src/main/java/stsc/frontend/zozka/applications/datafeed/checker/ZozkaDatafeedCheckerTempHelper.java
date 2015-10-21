package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatHelper;
import stsc.common.stocks.UnitedFormatStock;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.StockDescription;
import stsc.frontend.zozka.common.panes.StockDatafeedListPane;
import stsc.yahoo.YahooDatafeedSettings;
import stsc.yahoo.downloader.YahooDownloadHelper;
import stsc.yahoo.liquiditator.StockFilter;

final class ZozkaDatafeedCheckerTempHelper {

	private static final YahooDownloadHelper YAHOO_DOWNLOAD_HELPER = new YahooDownloadHelper();
	private static final StockFilter stockFilter = new StockFilter();
	private static final ZozkaDatafeedCheckerStockCorrectnessHelper stockCorrectnessHelper = new ZozkaDatafeedCheckerStockCorrectnessHelper(stockFilter);

	private final YahooDatafeedSettings yahooDatafeedSettings;
	private final StockDatafeedListPane dataStockList;
	private final StockDatafeedListPane filteredStockDataList;

	private List<ObservableList<StockDescription>> modelsToUpdate;

	public ZozkaDatafeedCheckerTempHelper(YahooDatafeedSettings yahooDatafeedSettings, StockDatafeedListPane dataStockList,
			StockDatafeedListPane filteredStockDataList, final List<ObservableList<StockDescription>> dialogModels) {
		this.yahooDatafeedSettings = yahooDatafeedSettings;
		this.dataStockList = dataStockList;
		this.filteredStockDataList = filteredStockDataList;
		this.modelsToUpdate = new ArrayList<>(dialogModels);
	}

	public void checkStockAndAskForUser(Stock toTest, Stock data, Stock filtered, Stage owner) {
		if (stockFilter.isLiquid(toTest) && stockFilter.isValid(toTest)) {
			stockCorrectnessHelper.makeUserSelectEitherHeLikeCurrentStockState(owner, data, filtered, false);
		} else {
			if (checkLiquidityAndValidityAndRedownload(owner, toTest)) {
				stockCorrectnessHelper.makeUserSelectEitherHeLikeCurrentStockState(owner, data, filtered, false);
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
		if (!stockFilter.isLiquid(stock)) {
			return askUserForRedownloadAndRedownload(owner, stock, stockFilter.isLiquidTestWithError(stock), " not liquid");
		}
		return false;
	}

	private boolean checkValidityAndRedownload(Stage owner, Stock stock) {
		if (!stockFilter.isValid(stock)) {
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
			final boolean isSave = stockCorrectnessHelper.makeUserSelectEitherHeLikeCurrentStockState(owner, sPtr, stockPtr);
			if (isSave) {
				final UnitedFormatStock s = sPtr.get();
				s.storeUniteFormatToFolder(yahooDatafeedSettings.getDataFolder());
				dataStockList.updateStock(s);
				if (stockFilter.isLiquid(s) && stockFilter.isValid(s) || filteredStockDataList.getStockStorage().getStock(s.getInstrumentName()) != null) {
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
		if (modelsToUpdate != null) {
			for (ObservableList<StockDescription> modelToUpdate : modelsToUpdate) {
				StockDatafeedListPane.updateModel(s, modelToUpdate);
			}
		}
	}

}
