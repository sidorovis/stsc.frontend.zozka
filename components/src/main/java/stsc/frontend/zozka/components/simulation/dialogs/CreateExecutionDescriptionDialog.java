package stsc.frontend.zozka.components.simulation.dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import stsc.common.algorithms.AlgorithmType;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.common.models.NumberAlgorithmParameter;
import stsc.frontend.zozka.common.models.ParameterType;
import stsc.frontend.zozka.common.models.TextAlgorithmParameter;
import stsc.frontend.zozka.components.simulation.helpers.ZozkaJavaFxHelper;
import stsc.storage.AlgorithmsStorage;

public final class CreateExecutionDescriptionDialog implements Initializable {

	private static final CreateExecutionDescriptionDialogHelper helper = new CreateExecutionDescriptionDialogHelper();

	private final Stage stage;
	private boolean valid;
	private ExecutionDescription model;

	public static final Pattern parameterNamePattern = Pattern.compile("^([\\w_])([\\w_\\d])*$");

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
					new TextAreaDialog("Exception", e);
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
			new TextAreaDialog("Information", "To understand what is happening\nhere than please ask developer and then\nchange this text. Thanks!").showAndWait();
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
			final Optional<String> parameterName = getParameterName();
			if (!parameterName.isPresent()) {
				return;
			}
			final Optional<ParameterType> parameterType = getParameterType();
			if (!parameterType.isPresent()) {
				return;
			}
			switch (parameterType.get()) {
			case DOUBLE:
				addDoubleParameter(parameterName.get(), "0.0", "1.0", "22.0");
				break;
			case INTEGER:
				addIntegerParameter(parameterName.get(), "0", "1", "22");
				break;
			case STRING:
				addStringParameter(parameterName.get());
				break;
			case SUB_EXECUTION:
				addSubExecutionParameter(parameterName.get());
				break;
			default:
				break;
			}
		});
	}

	private Optional<String> getParameterName() {
		final TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Parameter Name");
		dialog.setHeaderText("Parameter name:");
		dialog.setContentText("Enter: ");
		final Optional<String> parameterName = dialog.showAndWait();

		if (parameterName.isPresent()) {
			if (!parameterNamePattern.matcher(parameterName.get()).matches()) {
				new TextAreaDialog("Parameter name does not match pattern", "Parameter name should contain only letters, numbers and '_' symbol.").showAndWait();
				return Optional.empty();
			}
			if (model.parameterNameExists(parameterName.get())) {
				new TextAreaDialog("Parameter name does not match pattern", "You could add only one parameter (one for both for number or test tables).").showAndWait();
				return Optional.empty();
			}
		}
		return parameterName;
	}

	private Optional<ParameterType> getParameterType() {
		final Alert alert = new Alert(AlertType.NONE, "Choose type for parameter");
		for (ParameterType pt : ParameterType.values()) {
			final ButtonType buttonType = new ButtonType(pt.getName());
			alert.getButtonTypes().add(buttonType);
		}
		final Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			return Optional.ofNullable(ParameterType.findByName(result.get().getText()));
		}
		return Optional.empty();
	}

	private void addIntegerParameter(String parameterName, String defaultFrom, String defaultStep, String defaultTo) {
		final String errorMessage = "Integer is a number (-)?([0-9])+";
		final Optional<String> from = readIntegerParameter(defaultFrom, "Enter From", "From: ", errorMessage);
		if (!from.isPresent()) {
			return;
		}
		final Optional<String> step = readIntegerParameter(defaultStep, "Enter Step", "Step: ", errorMessage);
		if (!step.isPresent()) {
			return;
		}
		final Optional<String> to = readIntegerParameter(defaultTo, "Enter To", "To: ", errorMessage);
		if (!to.isPresent()) {
			return;
		}
		model.addNumberAlgorithm(new NumberAlgorithmParameter(parameterName, ParameterType.INTEGER, from.get(), step.get(), to.get()));
	}

	private void addDoubleParameter(String parameterName, String defaultFrom, String defaultStep, String defaultTo) {
		CreateExecutionDescriptionDialogHelper.NumberParameters numberParameters = helper.readAllDoubleParameters();
		if (numberParameters.isValid()) {
			model.addNumberAlgorithm(
					new NumberAlgorithmParameter(parameterName, ParameterType.DOUBLE, numberParameters.from.get(), numberParameters.step.get(), numberParameters.to.get()));
		}
	}

	private Optional<String> readIntegerParameter(final String defaultValue, String masthead, String message, String errorMessage) {
		final TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle("Integer Parameter");
		dialog.setHeaderText(masthead);
		dialog.setContentText(message);
		final Optional<String> integerParameter = dialog.showAndWait();
		if (integerParameter.isPresent() && !NumberAlgorithmParameter.integerParPattern.matcher(integerParameter.get()).matches()) {
			new TextAreaDialog("Integer value is incorrect", errorMessage).showAndWait();
			return Optional.empty();
		}
		return Optional.of(integerParameter.get());
	}

	private void addStringParameter(String parameterName) {
		final List<String> values = getStringDomen("String Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		model.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.STRING, domen));
	}

	private void addSubExecutionParameter(String parameterName) {
		final List<String> values = getStringDomen("SubExecution Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		model.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.SUB_EXECUTION, domen));
	}

	private List<String> getStringDomen(String title) {
		final ArrayList<String> values = new ArrayList<>();
		while (true) {
			final TextInputDialog dialog = new TextInputDialog("Enter domen value: ");
			dialog.setContentText("Hack: add several divided by ','.\nPress 'Cancel' to finish enter.");
			final Optional<String> stringValue = dialog.showAndWait();
			if (stringValue.isPresent()) {
				values.add(stringValue.get());
			} else {
				break;
			}
		}
		return values;
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
