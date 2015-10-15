package stsc.frontend.zozka.dialogs;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TextAreaDialog extends Dialog<Void> {

	private final TextArea textArea;
	private final BorderPane borderPane = new BorderPane();

	public TextAreaDialog(Stage owner, String title, String value) {
		super(); // (owner, title);
		setTitle(title);
		this.textArea = new TextArea(value);
		setWidth(600);
		setHeight(600);
		getDialogPane().setContent(borderPane);
		borderPane.setCenter(textArea);
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		setResultConverter(new Callback<ButtonType, Void>() {
			@Override
			public Void call(ButtonType param) {
				return null;
			}
		});
	}

}
