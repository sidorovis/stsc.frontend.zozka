package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import stsc.common.stocks.Stock;
import stsc.frontend.zozka.charts.panes.StockComparePane;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
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
	public boolean makeUserSelectEitherHeLikeCurrentStockState(final Stage owner, final Optional<? extends Stock> data,
			final Optional<? extends Stock> filtered) {
		try {
			final Alert alert = generateStockComparePane(data, filtered, true);
			alert.setTitle("Do you want to download stock from scratch?");
			final Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get().equals(ButtonType.YES)) {
				return makeUserSelectEitherHeWantsToSaveNewStockData(data.get());
			}
		} catch (IOException e) {
			new TextAreaDialog("Exception", e).showAndWait();
		}
		return false;
	}

	/**
	 * Create Chart
	 * 
	 * @param owner
	 * @param data
	 * @param filtered
	 */
	public void chartCurrentStockState(final Stage owner, final Optional<Stock> data, final Optional<Stock> filtered) {
		try {
			final Alert alert = generateStockComparePane(data, filtered, false);
			alert.setTitle("Stock Chart Representation");
			alert.showAndWait();
		} catch (IOException e) {
			new TextAreaDialog("Exception", e).showAndWait();
		}
	}

	private Alert generateStockComparePane(final Optional<? extends Stock> data, final Optional<? extends Stock> filtered, final boolean askForSave)
			throws IOException {
		Alert alert;
		if (askForSave) {
			alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO);
		} else {
			alert = new Alert(AlertType.INFORMATION, null, ButtonType.CLOSE);
		}
		alert.setHeaderText(null);
		alert.setResizable(true);
		final StockComparePane stockComparePane = new StockComparePane();
		if (data.isPresent()) {
			stockComparePane.addStockToCompare("Data", data.get());
		}
		if (filtered.isPresent()) {
			stockComparePane.addStockToCompare("Filtered", filtered.get());
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
		if (result.isPresent() && result.get().equals(ButtonType.YES)) {
			return true;
		}
		return false;
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
