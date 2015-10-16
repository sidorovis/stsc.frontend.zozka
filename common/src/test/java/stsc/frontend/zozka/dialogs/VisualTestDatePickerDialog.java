package stsc.frontend.zozka.dialogs;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.stage.Stage;

public final class VisualTestDatePickerDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final DatePickerDialog dialog = new DatePickerDialog("Date Choose Title", parent, LocalDate.of(1990, 1, 1));
		dialog.centerOnScreen();
		dialog.showAndWait();
		new TextAreaDialog("Result", String.valueOf(dialog.isOk()) + " for " + dialog.getDate().toString()).showAndWait();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestDatePickerDialog.class, (java.lang.String[]) null);
	}

}
