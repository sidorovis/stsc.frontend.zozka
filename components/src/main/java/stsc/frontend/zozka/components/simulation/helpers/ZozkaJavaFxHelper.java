package stsc.frontend.zozka.components.simulation.helpers;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * This helper class contain additional methods common for several GUI JavaFX
 * dialogs / models.
 */
public final class ZozkaJavaFxHelper {

	public static <T> void connectDeleteAction(Stage stage, TableView<T> table, ObservableList<T> model) {
		table.setItems(model);
		table.setOnKeyReleased(e -> {
			if (e.getCode().equals(KeyCode.DELETE)) {
				final Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure to delete Algorithm Parameter?", ButtonType.YES, ButtonType.NO);
				final Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get().equals(ButtonType.YES)) {
					final T elementToDelete = table.getSelectionModel().getSelectedItem();
					if (elementToDelete != null) {
						model.remove(elementToDelete);
					}
				}
			}
		});
	}
	//
	// public static boolean chooseFolder(Window owner, Label label) {
	// final String path = label.getText();
	// final File f = new File(path);
	//
	// final Action response = Dialogs.create().owner(owner).title("Datafeed
	// Path").masthead("Do you want to change datafeed path?")
	// .message("Current path is: " + path).showConfirm();
	// if (response != Dialog.Actions.YES) {
	// return false;
	// }
	// final DirectoryChooser dc = new DirectoryChooser();
	// if (f.exists()) {
	// dc.setInitialDirectory(f);
	// }
	// final File result = dc.showDialog(owner);
	// if (result != null && result.isDirectory()) {
	// label.setText(result.getAbsolutePath());
	// return true;
	// }
	// return false;
	// }

}
