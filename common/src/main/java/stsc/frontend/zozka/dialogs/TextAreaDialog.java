package stsc.frontend.zozka.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * This is a dialog windows (GUI) for showing large texts with possibility to
 * copy them (text area). Resize-able, not editable.
 */
public final class TextAreaDialog extends Alert {

	private final TextArea textArea;

	public TextAreaDialog(Stage owner, String title, String value) {
		super(AlertType.NONE);
		setTitle(title);
		setHeaderText(null);
		setContentText(null);
		this.textArea = new TextArea(value);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 0);
		getDialogPane().setContent(expContent);
		getButtonTypes().add(ButtonType.CLOSE);
		setResizable(true);
		getDialogPane().setPrefSize(600, 600);
	}

}
