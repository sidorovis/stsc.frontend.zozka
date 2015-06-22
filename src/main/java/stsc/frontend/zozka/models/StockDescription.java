package stsc.frontend.zozka.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import stsc.common.stocks.Stock;
import stsc.frontend.zozka.settings.ZozkaDatafeedCheckerHelper;


public final class StockDescription {
	private final IntegerProperty id;
	private final StringProperty name;
	private final BooleanProperty liquid;
	private final BooleanProperty valid;

	private Stock stock;

	public StockDescription(int id, Stock stock) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(stock.getInstrumentName());
		this.liquid = new SimpleBooleanProperty(ZozkaDatafeedCheckerHelper.isLiquid(stock));
		this.valid = new SimpleBooleanProperty(ZozkaDatafeedCheckerHelper.isValid(stock));
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
		return "StockDescription('" + stock.getInstrumentName() + "' with days size:" + stock.getDays().size() + ")\n[\n\tliquid: "
				+ liquid.getValue().booleanValue() + "; \n\tvalid: " + valid.getValue().booleanValue() + ";\n]";
	}

	public void setStock(Stock newStockData) {
		this.liquid.set(ZozkaDatafeedCheckerHelper.isLiquid(newStockData));
		this.valid.set(ZozkaDatafeedCheckerHelper.isValid(newStockData));
		stock = newStockData;
	}
}