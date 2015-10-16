package stsc.frontend.zozka.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import stsc.common.stocks.Stock;
import stsc.yahoo.liquiditator.StockFilter;

/**
 * GUI Model for {@link Stock} store next fields: <br/>
 * 1. {@link #id} - personal row id (for example for table view); <br/>
 * 2. {@link #name} - stock name; <br/>
 * 3. {@link #liquid} - boolean flag that describe
 * {@link StockFilter#isLiquid(Stock)} result for such stock; <br/>
 * 4. {@link #valid} - boolean flag that describe
 * {@link StockFilter#isValid(Stock)} result for such stock;
 */
public final class StockDescription {

	private final static StockFilter stockFilter = new StockFilter();

	private final IntegerProperty id;
	private final StringProperty name;
	private final BooleanProperty liquid;
	private final BooleanProperty valid;

	private Stock stock;

	public StockDescription(int id, Stock stock) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(stock.getInstrumentName());
		this.liquid = new SimpleBooleanProperty(stockFilter.isLiquid(stock));
		this.valid = new SimpleBooleanProperty(stockFilter.isValid(stock));
		this.stock = stock;
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public BooleanProperty liquidProperty() {
		return liquid;
	}

	public BooleanProperty validProperty() {
		return valid;
	}

	public Stock getStock() {
		return stock;
	}

	@Override
	public String toString() {
		return "StockDescription('" + stock.getInstrumentName() + "' with days size:" + stock.getDays().size() + ")\n[\n\tliquid: " + liquid.getValue().booleanValue()
				+ "; \n\tvalid: " + valid.getValue().booleanValue() + ";\n]";
	}

	public void setStock(final Stock newStockData) {
		this.liquid.set(stockFilter.isLiquid(newStockData));
		this.valid.set(stockFilter.isValid(newStockData));
		stock = newStockData;
	}
}