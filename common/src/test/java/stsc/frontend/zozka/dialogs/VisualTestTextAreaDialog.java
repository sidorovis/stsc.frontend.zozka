package stsc.frontend.zozka.dialogs;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.frontend.zozka.common.dialogs.TextAreaDialog;

public final class VisualTestTextAreaDialog extends Application {

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

		try {
			throw new Exception("Test Reason");
		} catch (Exception e) {
			final PipedInputStream pis = new PipedInputStream(4096);
			final PipedOutputStream pos = new PipedOutputStream(pis);
			final PrintWriter pw = new PrintWriter(pos);
			e.printStackTrace(pw);
			int size = pis.available();
			byte[] b = new byte[size];
			pis.read(b, 0, size);
			new TextAreaDialog("Exception arrived while loading yahoo file stock storage datafeed.", b.toString()).showAndWait();
		}
	}

	public static void main(String[] args) {
		Application.launch(VisualTestTextAreaDialog.class, (java.lang.String[]) null);
	}
}
