package stsc.frontend.zozka.common.dialogs;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * This is a dialog windows (GUI) for showing large texts with possibility to
 * copy them (text area). Resize-able, not editable.
 */
public final class TextAreaDialog extends Alert {

	private final TextArea textArea;

	public TextAreaDialog(String title, String value) {
		super(AlertType.NONE);
		setTitle(title);
		setHeaderText(null);
		this.textArea = new TextArea(value);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane.setVgrow(textArea, Priority.ALWAYS);

		final GridPane gridPane = new GridPane();
		gridPane.setMaxHeight(Double.MAX_VALUE);
		gridPane.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(textArea, 0, 0);
		getDialogPane().setContent(gridPane);
		getButtonTypes().add(ButtonType.CLOSE);
		setResizable(true);
		getDialogPane().setPrefSize(600, 600);
	}

	public TextAreaDialog(String title, Throwable e) {
		this(title, ExceptionUtils.getStackTrace(e));
	}

}
