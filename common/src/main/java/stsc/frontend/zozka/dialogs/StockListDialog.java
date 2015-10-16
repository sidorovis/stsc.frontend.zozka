package stsc.frontend.zozka.dialogs;

import java.util.Optional;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stsc.frontend.zozka.models.StockDescription;

/**
 * {@link StockListDialog} is GUI dialog with stock list placed into table order
 * with {@link StockDescription} table model. <br/>
 * Used for processing / updating / fixing yahoo datafeed state.
 */
public final class StockListDialog extends Alert {

	private final ObservableList<StockDescription> model = FXCollections.observableArrayList();
	private final BorderPane borderPane = new BorderPane();
	private final TableView<StockDescription> table = new TableView<>();

	private final TableColumn<StockDescription, Number> idColumn = new TableColumn<>();
	private final TableColumn<StockDescription, String> nameColumn = new TableColumn<>();
	private final TableColumn<StockDescription, Boolean> liquidColumn = new TableColumn<>();
	private final TableColumn<StockDescription, Boolean> validColumn = new TableColumn<>();

	public StockListDialog(Stage owner, String title) {
		this(owner, title, true, true);
	}

	public StockListDialog(Stage owner, String title, boolean showLiquidColumn, boolean showValidColumn) {
		super(AlertType.NONE);
		getDialogPane().setPrefSize(800, 600);
		getDialogPane().setContent(borderPane);
		borderPane.setCenter(table);
		borderPane.setBottom(new Label());
		setResizable(true);
		configurateTable(showLiquidColumn, showValidColumn);
		getButtonTypes().add(ButtonType.CLOSE);
	}

	private void configurateTable(boolean showLiquidColumn, boolean showValidColumn) {
		table.setItems(model);
		idColumn.setText("Id");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		table.getColumns().add(idColumn);
		nameColumn.setText("Stock Name");
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColumn.setPrefWidth(350.0);
		table.getColumns().add(nameColumn);
		if (showLiquidColumn) {
			configurateBooleanColumn(liquidColumn, "Liquid", "liquid");
			table.getColumns().add(liquidColumn);
		}
		if (showValidColumn) {
			configurateBooleanColumn(validColumn, "Valid", "valid");
			table.getColumns().add(validColumn);
		}
	}

	private void configurateBooleanColumn(TableColumn<StockDescription, Boolean> booleanColumn, String title, String propertyName) {
		booleanColumn.setText(title);
		booleanColumn.setCellValueFactory(new PropertyValueFactory<StockDescription, Boolean>(propertyName));
		booleanColumn.setPrefWidth(80);
		booleanColumn.setCellFactory(CheckBoxTableCell.forTableColumn(booleanColumn));
	}

	public ObservableList<StockDescription> getModel() {
		return model;
	}

	public void setOnMouseDoubleClicked(final Function<StockDescription, Optional<Void>> function) {
		table.setOnMouseClicked(eh -> {
			if (eh.getButton().equals(MouseButton.PRIMARY) && eh.getClickCount() == 2) {
				final StockDescription selectedItem = table.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					function.apply(selectedItem);
				}
			}
		});
	}
}
