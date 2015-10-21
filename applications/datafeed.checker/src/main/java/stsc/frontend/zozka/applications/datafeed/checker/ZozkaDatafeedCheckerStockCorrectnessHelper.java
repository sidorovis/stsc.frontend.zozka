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
	public boolean makeUserSelectEitherHeLikeCurrentStockState(final Stage owner, Stock data, Stock filtered, boolean askForSave) {
		try {
			final Alert alert = generateAlert(data, filtered, askForSave);
			alert.setTitle(generateRepresentationTitle(askForSave));
			final Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get().equals(ButtonType.YES)) {
				return makeUserSelectEitherHeWantsToSaveNewStockData(data);
			}
		} catch (IOException e) {
			new TextAreaDialog("Exception", e).showAndWait();
		}
		return false;
	}

	private Alert generateAlert(Stock data, Stock filtered, boolean askForSave) throws IOException {
		Alert alert;
		if (askForSave) {
			alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO);
		} else {
			alert = new Alert(AlertType.INFORMATION, null, ButtonType.CLOSE);
		}
		alert.setTitle(generateRepresentationTitle(askForSave));
		alert.setHeaderText(null);
		alert.setResizable(true);
		final StockComparePane stockComparePane = new StockComparePane();
		stockComparePane.addStockToCompare(data);
		stockComparePane.addStockToCompare(filtered);
		alert.getDialogPane().setContent(stockComparePane);
		alert.getDialogPane().setPrefHeight(640);
		alert.getDialogPane().setPrefWidth(800);
		return alert;
	}

	private String generateRepresentationTitle(boolean askForSave) {
		if (askForSave) {
			return "ForAdjectiveClose - do you want to save it?";
		} else {
			return "ForAdjectiveClose";
		}
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
