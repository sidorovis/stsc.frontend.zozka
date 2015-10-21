package stsc.frontend.zozka.applications.datafeed.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.liquiditator.StockFilter;

/**
 * This helper contain all methods related to datafeed load process. <br/>
 * Datafeed load process is a list of methods that provide possibility to read
 * {@link YahooFileStockStorage} datafeed (separately for data folder and
 * filtered folder. <br/>
 * This is required to inspect current datafeed state.
 */
public final class ZozkaDatafeedCheckerDatafeedLoaderHelper {

	private final StockFilter stockFilter;

	public ZozkaDatafeedCheckerDatafeedLoaderHelper(final StockFilter stockFilter) {
		this.stockFilter = stockFilter;
	}

	/**
	 * {@link #makeUserChooseFolder(Window, Label)} method is a GUI component
	 * dialog that could be used together with {@link Label} that should contain
	 * directory to datafeed. Algorithm: <br/>
	 * 1. ask user if he actually want's to choose datafeed folder; <br/>
	 * 1.2 if no return false; <br/>
	 * 2. show to user {@link DirectoryChooser} with current directory (
	 * {@link Label#getText()}); <br/>
	 * 2.2. if user close (cancel) dialog return false; <br/>
	 * 2.3. if user choose not a folder ot not existed path return false; <br/>
	 * 3. {@link Label#setText(String)} with choose result and return true;
	 * 
	 * @return boolean in case when directory was changed
	 */
	public boolean makeUserChooseFolder(final Window owner, final Label label) {
		final Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to set-up datafeed path?", ButtonType.YES, ButtonType.NO);
		alert.setTitle("Datafeed path setup");
		alert.setHeaderText(null);
		final Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent() || !result.get().equals(ButtonType.YES)) {
			return false;
		}

		final String path = label.getText();
		final File initialDirectory = new File(path);
		final DirectoryChooser dc = new DirectoryChooser();
		if (initialDirectory.exists()) {
			dc.setInitialDirectory(initialDirectory);
		}
		final File newSelectedDirectory = dc.showDialog(owner);
		if (newSelectedDirectory != null && newSelectedDirectory.exists() && newSelectedDirectory.isDirectory()) {
			label.setText(newSelectedDirectory.getAbsolutePath());
			return true;
		}
		return false;
	}

	/**
	 * Generates possible stock name prefixes and make user choose such prefix.
	 * (Could have '' value to choose no prefix).
	 * 
	 * @return selected value or {@link Optional#empty()} if user cancelled
	 *         action.
	 */
	public Optional<String> makeUserChooseStockNamePrefix() {
		final List<String> prefixVariants = generatePrefixForNames();
		final ChoiceDialog<String> dialog = new ChoiceDialog<String>("a", prefixVariants);
		dialog.setTitle("Select Prefix for StockStorage");
		return dialog.showAndWait();
	}

	/**
	 * Generates next list of Strings: '', 'a', 'b', 'c', ... 'z'. (27
	 * elements).
	 */
	List<String> generatePrefixForNames() {
		final List<String> prefixVariants = new ArrayList<>();
		prefixVariants.add("");
		for (char z = 'a'; z != 'z' + 1; z++) {
			prefixVariants.add(String.valueOf(z));
		}
		return prefixVariants;
	}

	/**
	 * This is kinda comparator for two different {@link StockStorage} 's. <br/>
	 * 
	 * @return set of stock names that are: <br/>
	 *         1. in filteredDataStockStorage parameter but not dataStockStorage
	 *         parameters; </br>
	 *         2. exists at both dataStockStorage, filteredDataStockStorage
	 *         parameters but have different amount of days or
	 *         {@link StockFilter#isLiquid(Stock)},
	 *         {@link StockFilter#isValid(Stock)} methods return.
	 */
	public Set<String> findDifferenceByDaysSizeAndStockFilter(final StockStorage dataStockStorage, final StockStorage filteredDataStockStorage) {
		final Set<String> notEqualStockList = new HashSet<>();
		final Set<String> dataStockNames = dataStockStorage.getStockNames();
		final Set<String> filteredStockNames = filteredDataStockStorage.getStockNames();
		for (String stockName : dataStockNames) {
			if (filteredStockNames.contains(stockName)) {
				final Stock dataStockPtr = dataStockStorage.getStock(stockName).get();
				final Stock filteredDataStockPtr = filteredDataStockStorage.getStock(stockName).get();

				if (dataStockPtr.getDays().size() != filteredDataStockPtr.getDays().size()) {
					notEqualStockList.add(stockName);
				} else if (stockFilter.isLiquid(dataStockPtr) != stockFilter.isLiquid(filteredDataStockPtr)) {
					notEqualStockList.add(stockName);
				} else if (stockFilter.isValid(dataStockPtr) != stockFilter.isValid(filteredDataStockPtr)) {
					notEqualStockList.add(stockName);
				}
			}
		}
		for (String stockName : filteredStockNames) {
			if (!dataStockNames.contains(stockName)) {
				notEqualStockList.add(stockName);
			}
		}
		return notEqualStockList;
	}

}
