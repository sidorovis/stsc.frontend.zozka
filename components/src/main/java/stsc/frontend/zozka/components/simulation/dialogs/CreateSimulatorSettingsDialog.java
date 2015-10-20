package stsc.frontend.zozka.components.simulation.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;

import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.common.models.SimulatorSettingsModel;
import stsc.frontend.zozka.components.simulation.helpers.ZozkaJavaFxHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class CreateSimulatorSettingsDialog {

	private final Stage owner;
	private final Parent gui;

	private final SimulatorSettingsModel model;

	@FXML
	private BorderPane mainPane;
	@FXML
	private TableView<ExecutionDescription> executionsTable;
	@FXML
	private TableColumn<ExecutionDescription, String> executionsNameColumn;
	@FXML
	private TableColumn<ExecutionDescription, String> algorithmsNameColumn;

	public CreateSimulatorSettingsDialog(Stage owner) throws IOException {
		this.owner = owner;
		this.model = new SimulatorSettingsModel();
		final URL location = getClass().getResource("simulation_settings_dialog.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		initialize();
	}

	private void initialize() {
		validateGui();
		executionsTable.setItems(model.getModel());
		ZozkaJavaFxHelper.connectDeleteAction(owner, executionsTable, model.getModel());
		executionsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecutionName()));
		algorithmsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));
	}

	public Parent getGui() {
		return gui;
	}

	private void validateGui() {
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file.";
		assert executionsTable != null : "fx:id=\"executionsTable\" was not injected: check your FXML file.";
		assert executionsNameColumn != null : "fx:id=\"executionsNameColumn\" was not injected: check your FXML file.";
		assert algorithmsNameColumn != null : "fx:id=\"algorithmsNameColumn\" was not injected: check your FXML file.";
	}

	@FXML
	private void loadFromFile() {
		if (!model.isEmpty()) {
			final Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to erase exist model?", ButtonType.YES, ButtonType.NO);
			final Optional<ButtonType> result = alert.showAndWait();
			if (!result.isPresent() || !result.equals(ButtonType.YES)) {
				return;
			}
		}
		final FileChooser dc = new FileChooser();
		dc.setTitle("File To Load");
		final File f = dc.showOpenDialog(owner);
		try {
			if (f != null) {
				if (!(f.exists() && f.isFile())) {
					new TextAreaDialog("Simulator Settings Load Error", "File can't be loaded (" + f.getAbsolutePath() + ")").showAndWait();
					return;
				}
				try (InputStream is = new FileInputStream(f)) {
					model.loadFromFile(is);
				}
			}
		} catch (Exception e) {
			new TextAreaDialog("Exception", e);
		}
	}

	@FXML
	private void saveToFile() {
		final FileChooser dc = new FileChooser();
		dc.setTitle("File To Save");
		final File f = dc.showSaveDialog(owner);
		try {
			if (f != null) {
				if (f.exists() && !f.canWrite()) {
					new TextAreaDialog("Simulator Settings Save Error", "File can't be writen (" + f.getAbsolutePath() + ")").showAndWait();
					return;
				}
				try (OutputStream os = new FileOutputStream(f)) {
					model.saveToFile(os);
				}
			}
		} catch (IOException e) {
			new TextAreaDialog("Exception", e);
		}
	}

	@FXML
	private void mouseClicked(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 && !executionsTable.getSelectionModel().getSelectedItems().isEmpty()) {
			editExecution();
		}
	}

	@FXML
	private void addNewExecution() {
		Optional<ExecutionDescription> executionDescription = Optional.empty();
		try {
			final CreateExecutionDescriptionDialog createExecutionDescriptionDialog = new CreateExecutionDescriptionDialog(owner);
			executionDescription = createExecutionDescriptionDialog.getExecutionDescription();
		} catch (IOException e) {
			new TextAreaDialog("Exception", e);
		}
		if (executionDescription.isPresent()) {
			model.add(executionDescription.get());
		}
	}

	private void editExecution() {
		final int index = executionsTable.getSelectionModel().getSelectedIndex();
		final ExecutionDescription executionDescription = executionsTable.getSelectionModel().getSelectedItem();
		try {
			final CreateExecutionDescriptionDialog controller = new CreateExecutionDescriptionDialog(owner, executionDescription);
			final Optional<ExecutionDescription> newExecutionDescription = controller.getExecutionDescription();
			if (newExecutionDescription.isPresent()) {
				model.set(index, newExecutionDescription.get());
			}
		} catch (IOException e) {
			new TextAreaDialog("Exception", e);
		}
	}

	public SimulatorSettingsModel getModel() {
		return model;
	}
}
