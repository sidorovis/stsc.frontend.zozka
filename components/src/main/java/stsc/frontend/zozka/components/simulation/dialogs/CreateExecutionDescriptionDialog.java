package stsc.frontend.zozka.components.simulation.dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import stsc.common.algorithms.AlgorithmType;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.common.models.NumberAlgorithmParameter;
import stsc.frontend.zozka.common.models.TextAlgorithmParameter;
import stsc.frontend.zozka.components.simulation.helpers.ZozkaJavaFxHelper;
import stsc.storage.AlgorithmsStorage;

public final class CreateExecutionDescriptionDialog implements Initializable {

	private static final CreateExecutionDescriptionDialogHelper helper = new CreateExecutionDescriptionDialogHelper();

	private final Stage stage;
	private boolean valid;
	private ExecutionDescription model;

	@FXML
	private ComboBox<AlgorithmType> algorithmType;
	@FXML
	private ComboBox<String> algorithmClass;
	@FXML
	private Button questionButton;
	@FXML
	private TextField executionName;

	@FXML
	private TableView<NumberAlgorithmParameter> numberTable;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParName;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParType;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParFrom;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParStep;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParTo;

	@FXML
	private TableView<TextAlgorithmParameter> textTable;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParName;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParType;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParDomen;

	@FXML
	private Button addParameter;
	@FXML
	private Button saveExecution;

	public CreateExecutionDescriptionDialog(final Stage owner) throws IOException {
		stage = new Stage();
		valid = false;
		initialize(owner);
	}

	public CreateExecutionDescriptionDialog(final Stage owner, final ExecutionDescription executionDescription) throws IOException {
		stage = new Stage();
		valid = false;
		setExecutionDescription(executionDescription);
		initialize(owner);
	}

	private void initialize(Stage owner) throws IOException {
		final URL location = getClass().getResource("create_execution_description_dialog.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		scene.getStylesheets().add(getClass().getResource("create_execution_description_dialog.css").toExternalForm());
		stage.setScene(scene);
		stage.setMinHeight(480);
		stage.setMinWidth(640);
		stage.setTitle("Create Algorithm Settings");
		stage.centerOnScreen();
	}

	public Optional<ExecutionDescription> getExecutionDescription() {
		this.stage.showAndWait();
		if (isValid()) {
			return Optional.of(model);
		}
		return Optional.empty();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		validateGui();
		connectActionsForAlgorithmType();
		connectActionsForAlgorithmClass();
		connectQuestionButton();
		if (model == null) {
			model = new ExecutionDescription(this.algorithmType.getValue(), this.executionName.getText(), this.algorithmClass.getValue());
		} else {
			algorithmType.getSelectionModel().select(model.getAlgorithmType());
			executionName.setText(model.getExecutionName());
			algorithmClass.getSelectionModel().select(model.getAlgorithmName());
		}
		connectTableForNumber();
		connectTableForText();
		connectAddParameter();
		connectSaveExecution();
	}

	private void validateGui() {
		assert algorithmType != null : "fx:id=\"algorithmType\" was not injected: check your FXML file.";
		assert algorithmClass != null : "fx:id=\"algorithmClass\" was not injected: check your FXML file.";
		assert questionButton != null : "fx:id=\"questionButton\" was not injected: check your FXML file.";
		assert executionName != null : "fx:id=\"executionName\" was not injected: check your FXML file.";

		assert numberTable != null : "fx:id=\"numberParameters\" was not injected: check your FXML file.";
		assert numberParName != null : "fx:id=\"numberParName\" was not injected: check your FXML file.";
		assert numberParType != null : "fx:id=\"numberParType\" was not injected: check your FXML file.";
		assert numberParFrom != null : "fx:id=\"numberParFrom\" was not injected: check your FXML file.";
		assert numberParStep != null : "fx:id=\"numberParStep\" was not injected: check your FXML file.";
		assert numberParTo != null : "fx:id=\"numberParTo\" was not injected: check your FXML file.";

		assert textTable != null : "fx:id=\"textParameters\" was not injected: check your FXML file.";
		assert textParName != null : "fx:id=\"textParName\" was not injected: check your FXML file.";
		assert textParType != null : "fx:id=\"textParType\" was not injected: check your FXML file.";
		assert textParDomen != null : "fx:id=\"textParDomen\" was not injected: check your FXML file.";

		assert addParameter != null : "fx:id=\"addParameter\" was not injected: check your FXML file.";
		assert saveExecution != null : "fx:id=\"saveExecution\" was not injected: check your FXML file.";
		valid = false;
	}

	private void connectActionsForAlgorithmType() {
		algorithmType.setItems(FXCollections.observableArrayList(AlgorithmType.values()));
		algorithmType.getSelectionModel().select(0);
		algorithmType.valueProperty().addListener(new ChangeListener<AlgorithmType>() {
			@Override
			public void changed(ObservableValue<? extends AlgorithmType> observable, AlgorithmType oldValue, AlgorithmType newValue) {
				try {
					populateAlgorithmClassWith(newValue);
				} catch (BadAlgorithmException e) {
					new TextAreaDialog(e);
					stage.close();
				}
			}
		});
	}

	private void connectActionsForAlgorithmClass() {
		try {
			populateAlgorithmClassWith(algorithmType.getSelectionModel().getSelectedItem());
		} catch (BadAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		algorithmClass.getSelectionModel().select(0);
	}

	private void connectQuestionButton() {
		questionButton.setOnAction(e -> {
			new TextAreaDialog("Information", "To understand what is happening\nhere than please ask developer and then\nchange this text. Thanks!");
		});
	}

	protected void populateAlgorithmClassWith(AlgorithmType newValue) throws BadAlgorithmException {
		final ObservableList<String> model = algorithmClass.getItems();
		model.clear();
		if (newValue.isStock()) {
			final Set<String> stockLabels = AlgorithmsStorage.getInstance().getStockLabels();
			for (String label : stockLabels) {
				model.add(label);
			}
		} else {
			final Set<String> eodLabels = AlgorithmsStorage.getInstance().getEodLabels();
			for (String label : eodLabels) {
				model.add(label);
			}
		}
		algorithmClass.getSelectionModel().select(0);
	}

	private void connectTableForNumber() {
		ZozkaJavaFxHelper.connectDeleteAction(stage, numberTable, model.getNumberAlgorithms());

		numberParName.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("parameterName"));
		numberParName.setCellFactory(TextFieldTableCell.forTableColumn());
		numberParType.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("type"));

		connectColumn(numberParFrom, "from");
		numberParFrom.setOnEditCommit(e -> {
			e.getRowValue().setFrom(e.getNewValue());
		});
		connectColumn(numberParStep, "step");
		numberParStep.setOnEditCommit(e -> {
			e.getRowValue().setStep(e.getNewValue());
		});
		connectColumn(numberParTo, "to");
		numberParTo.setOnEditCommit(e -> {
			e.getRowValue().setTo(e.getNewValue());
		});
	}

	private void connectTableForText() {
		ZozkaJavaFxHelper.connectDeleteAction(stage, textTable, model.getTextAlgorithms());

		textParName.setCellValueFactory(new PropertyValueFactory<TextAlgorithmParameter, String>("parameterName"));
		textParName.setCellFactory(TextFieldTableCell.forTableColumn());
		textParType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().getName()));

		connectColumn(textParDomen, "domen");
	}

	private <T> void connectColumn(TableColumn<T, String> column, String name) {
		column.setCellValueFactory(new PropertyValueFactory<T, String>(name));
		column.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	private void connectAddParameter() {
		addParameter.setOnAction(e -> {
			helper.processAddParameterAction(model);
		});
	}

	private void connectSaveExecution() {
		saveExecution.setOnAction(e -> {
			valid = true;
			stage.close();
		});
	}

	private boolean isValid() {
		model.setAlgorithmName(this.algorithmClass.getValue());
		model.setExecutionName(this.executionName.getText());
		model.setAlgorithmType(this.algorithmType.getValue());
		return valid;
	}

	private void setExecutionDescription(ExecutionDescription ed) {
		model = ed;
	}
}
