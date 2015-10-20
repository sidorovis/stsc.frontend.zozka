package stsc.frontend.zozka.components.simulation.dialogs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;
import stsc.frontend.zozka.common.models.ExecutionDescription;
import stsc.frontend.zozka.common.models.SimulationType;
import stsc.frontend.zozka.components.DatafeedLoader;
import stsc.frontend.zozka.components.models.SimulationsDescription;
import stsc.frontend.zozka.components.simulation.helpers.ZozkaJavaFxHelper;

public class CreateMultiSimulationSettingsDialog implements Initializable {

	private Stage stage;
	private boolean valid;
	private final Parent gui;

	private SimulationsDescription model = new SimulationsDescription();

	private SimulationType simulationType;

	@FXML
	private Label datafeedLabel;
	@FXML
	private Button chooseDatafeedButton;

	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;

	@FXML
	private Button addExecutionButton;

	@FXML
	private TableView<ExecutionDescription> executionsTable;
	@FXML
	private TableColumn<ExecutionDescription, String> executionNameColumn;
	@FXML
	private TableColumn<ExecutionDescription, String> algorithmNameColumn;

	@FXML
	private Button createGridSettingsButton;
	@FXML
	private Button createGeneticSettingsButton;

	public CreateMultiSimulationSettingsDialog(final Stage owner) throws IOException {
		stage = new Stage();
		valid = false;
		final URL location = getClass().getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		stage.setScene(scene);
		stage.setMinHeight(480);
		stage.setMinWidth(640);
		stage.setTitle("Create Simulator Settings");
		stage.centerOnScreen();
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		validateGui();
		connectTableForExecutions();

		setDefaultValues();
		setOnChooseDatafeedButton();
		setOnAddExecutionButton();
		setOnCreateGeneticSettingsButton();
		setOnCreateGridSettingsButton();
	}

	private void validateGui() {
		assert chooseDatafeedButton != null : "fx:id=\"chooseDatafeedButton\" was not injected: check your FXML file.";
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";

		assert fromDate != null : "fx:id=\"fromDate\" was not injected: check your FXML file.";
		assert toDate != null : "fx:id=\"toDate\" was not injected: check your FXML file.";

		assert addExecutionButton != null : "fx:id=\"addExecutionButton\" was not injected: check your FXML file.";

		assert executionsTable != null : "fx:id=\"executionsTable\" was not injected: check your FXML file.";
		assert executionNameColumn != null : "fx:id=\"executionNameColumn\" was not injected: check your FXML file.";
		assert algorithmNameColumn != null : "fx:id=\"algorithmNameColumn\" was not injected: check your FXML file.";

		assert createGridSettingsButton != null : "fx:id=\"createGridSettingsButton\" was not injected: check your FXML file.";
		assert createGeneticSettingsButton != null : "fx:id=\"createGeneticSettingsButton\" was not injected: check your FXML file.";
	}

	public Node getGui() {
		return gui;
	}

	private void connectTableForExecutions() {
		executionsTable.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				final int index = executionsTable.getSelectionModel().getSelectedIndex();
				final ExecutionDescription ed = executionsTable.getSelectionModel().getSelectedItem();
				try {
					final CreateExecutionDescriptionDialog controller = new CreateExecutionDescriptionDialog(stage, ed);
					final Optional<ExecutionDescription> newEd = controller.getExecutionDescription();
					if (newEd.isPresent()) {
						model.getExecutionDescriptions().set(index, newEd.get());
					}
				} catch (IOException e) {
					new TextAreaDialog("exception", e);
				}
			}
		});
		ZozkaJavaFxHelper.connectDeleteAction(stage, executionsTable, model.getExecutionDescriptions());

		executionNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecutionName()));
		algorithmNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));
	}

	private void setDefaultValues() {
		setDatafeed("./test_data");

		fromDate.setValue(LocalDate.of(1990, 1, 1));
		toDate.setValue(LocalDate.of(2010, 1, 1));
	}

	private void setOnChooseDatafeedButton() {
		chooseDatafeedButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Datafeed folder");
				final File f = dc.showDialog(stage);
				if (f != null && f.isDirectory()) {
					setDatafeed(f.getAbsolutePath());
				}
			}
		});
	}

	private void setOnAddExecutionButton() {
		addExecutionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Optional<ExecutionDescription> ed = Optional.empty();
				try {
					final CreateExecutionDescriptionDialog controller = new CreateExecutionDescriptionDialog(stage);
					ed = controller.getExecutionDescription();
				} catch (IOException e) {
					new TextAreaDialog("exception", e);
				}
				if (ed.isPresent()) {
					model.getExecutionDescriptions().add(ed.get());
				}
			}
		});

	}

	private void setOnCreateGeneticSettingsButton() {
		createGeneticSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulationType = SimulationType.GENETIC;
				handleClose();
			}
		});
	}

	private void setOnCreateGridSettingsButton() {
		createGridSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulationType = SimulationType.GRID;
				handleClose();
			}
		});
	}

	protected void handleClose() {
		final LocalDate fromDateData = fromDate.getValue();
		final LocalDate toDateData = toDate.getValue();
		if (fromDateData.isAfter(toDateData)) {
			new TextAreaDialog("Validation Error", fromDateData.toString() + " is after " + toDateData.toString()).showAndWait();
		} else {
			startCheckAndLoadDatafeed();
		}
	}

	protected boolean startCheckAndLoadDatafeed() {
		try {
			loadDatafeed(model.getDatafeedPath());
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			new TextAreaDialog("Exception", e).showAndWait();
			return false;
		}
		return true;
	}

	private void loadDatafeed(Path datafeedPath) throws ClassNotFoundException, IOException, InterruptedException {
		DatafeedLoader datafeedLoader = new DatafeedLoader(datafeedPath);
		datafeedLoader.startLoad(successHandler -> {
			setValid();
			model.setStockStorage(datafeedLoader.getStockStorage());
		} , exitHandler -> {
			setInvalid();
		});
	}

	private Date createDate(LocalDate date) {
		return new Date(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	protected void setValid() {
		valid = true;
		model.setPeriod(createDate(fromDate.getValue()), createDate(toDate.getValue()));
		stage.close();
	}

	protected void setInvalid() {
		valid = false;
		new TextAreaDialog("Validation Error", "Datafeed folder: " + model.getDatafeedPath() + " is invalid.").showAndWait();
	}

	private void setDatafeed(String datafeed) {
		model.setDatafeedPath(Paths.get(datafeed));
		datafeedLabel.setText("Datafeed: " + datafeed);
	}

	public boolean isValid() {
		stage.showAndWait();
		return valid;
	}

	public SimulationType getSimulationType() {
		return simulationType;
	}

	public SimulationsDescription getModel() {
		return model;
	}

}
