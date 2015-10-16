package stsc.frontend.zozka.dialogs;

import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestTextAreaDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final TextAreaDialog ad = new TextAreaDialog("Strategy: 14",
				"hello world\nresult\n " + System.getProperty("java.version") + "\n"
						+ "This is a long text. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et "
						+ "dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, "
						+ "no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy "
						+ "eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et "
						+ "ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		ad.showAndWait();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestTextAreaDialog.class, (java.lang.String[]) null);
	}
}
