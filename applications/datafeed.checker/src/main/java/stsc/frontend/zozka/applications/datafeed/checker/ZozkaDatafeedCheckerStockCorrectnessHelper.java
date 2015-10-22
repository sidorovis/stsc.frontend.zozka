package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.frontend.zozka.charts.panes.StockComparePane;
import stsc.yahoo.liquiditator.StockFilter;

/**
 * This helper shows graphical {@link Stock} representation both for data and
 * filtered data folder. <br/>
 * This provide possibility to see exact data difference on one stock between
 * data / filtered_data folders and to get information about corruptness.
 */
final class ZozkaDatafeedCheckerStockCorrectnessHelper {

	private final StockFilter stockFilter;

	public ZozkaDatafeedCheckerStockCorrectnessHelper(StockFilter stockFilter) {
		this.stockFilter = stockFilter;
	}

	/**
	 * Represents Yes - No dialog window with huge stock information
	 * {@link Stock} with user interaction request.
	 * 
	 * @param stock
	 * @return true if user selected yes
	 */
	public boolean makeUserSelectEitherHeLikeToRedownloadCurrentStockState(final Stage owner, final Optional<? extends Stock> data, final Optional<? extends Stock> filtered)
			throws IOException {
		final Map<String, Optional<? extends Stock>> paneDescriptions = new LinkedHashMap<>();
		paneDescriptions.put("Data", data);
		paneDescriptions.put("Filtered", filtered);
		final Alert alert = generateStockCompareDialog(paneDescriptions, true);
		alert.setTitle("Do you want to download stock from scratch?");
		final Optional<ButtonType> result = alert.showAndWait();
		return (result.isPresent() && result.get().equals(ButtonType.YES));
	}

	/**
	 * Show to user pane with just downloaded version and asks if he like to
	 * save that to the yahoo file stock storage.
	 */
	public boolean makeUserSelectEitherHeLineNewVersion(final Stage owner, final Optional<? extends Stock> data, final Optional<? extends Stock> filtered,
			Optional<UnitedFormatStock> newVersion) throws IOException {
		final Map<String, Optional<? extends Stock>> paneDescriptions = new LinkedHashMap<>();
		paneDescriptions.put("Data", data);
		paneDescriptions.put("Filtered", filtered);
		paneDescriptions.put("Version From Internet", newVersion);
		final Alert alert = generateStockCompareDialog(paneDescriptions, true);
		alert.setTitle("Do you want to save new stock data?");
		final Optional<ButtonType> result = alert.showAndWait();
		return (result.isPresent() && result.get().equals(ButtonType.YES));
	}

	/**
	 * Create Chart for current stock state.
	 */
	public void chartCurrentStockState(final Stage owner, final Optional<Stock> data, final Optional<Stock> filtered) throws IOException {
		final Map<String, Optional<? extends Stock>> paneDescriptions = new LinkedHashMap<>();
		paneDescriptions.put("Data", data);
		paneDescriptions.put("Filtered", filtered);
		final Alert alert = generateStockCompareDialog(paneDescriptions, false);
		alert.setTitle("Stock Chart Representation");
		alert.showAndWait();
	}

	private Alert generateStockCompareDialog(Map<String, Optional<? extends Stock>> paneDescriptions, final boolean askForSave) throws IOException {
		Alert alert;
		if (askForSave) {
			alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO);
		} else {
			alert = new Alert(AlertType.INFORMATION, null, ButtonType.CLOSE);
		}
		alert.setHeaderText(null);
		alert.setResizable(true);
		final StockComparePane stockComparePane = new StockComparePane();
		for (Entry<String, Optional<? extends Stock>> e : paneDescriptions.entrySet()) {
			if (e.getValue().isPresent()) {
				stockComparePane.addStockToCompare(e.getKey(), e.getValue().get());
			}
		}
		alert.getDialogPane().setContent(stockComparePane);
		alert.getDialogPane().setPrefHeight(640);
		alert.getDialogPane().setPrefWidth(800);
		return alert;
	}

	/**
	 * Represents Yes - No dialog window with short information about
	 * {@link Stock} with user interaction request.
	 * 
	 * @param stock
	 * @return true if user selected yes
	 */
	boolean makeUserSelectEitherHeWantsToSaveNewStockData(final Stock stock) {
		final String error = createErrorMessage(stock);
		final Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, error, ButtonType.YES, ButtonType.NO);
		confirmationAlert.setHeaderText("Do you want to save new stock data");
		confirmationAlert.setTitle("Stock Data Information");
		final Optional<ButtonType> result = confirmationAlert.showAndWait();
		return (result.isPresent() && result.get().equals(ButtonType.YES));
	}

	private String createErrorMessage(final Stock s) {
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

}
