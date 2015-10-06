package gram21.synchronizer;

import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Central point to save some variables and provide needed utility methods etc
 *
 */
public final class SyncAPI {

	private static final String name = "Sync";
	private static final int windowWidth = 450;
	private static final int windowHeight = 150;
	private static final Text statusText = new Text();
	private static final TextField srcATextField = new TextField(System.getProperty("user.home"));
	private static final TextField srcBTextField = new TextField(System.getProperty("user.home"));
	private static final Label srcAInfo = new Label(" ");
	private static final Label srcBInfo = new Label(" ");
	private static final ProgressBar progressBar = new ProgressBar(0.0);
	private static Source sourceA = null;
	private static Source sourceB = null;


	public static void error(String errorText) {
		statusText.setText("   " + errorText);
		statusText.setFill(Color.RED);
	}

	public static void updateStatus(String text) {
		statusText.setText("   " + text);
		statusText.setFill(Color.BLACK);
	}

	public static void updateProgress(double percent) {
		progressBar.setProgress(percent);
	}

	public static void updateSourceAInfo() {
		if (sourceA == null)
			return;
		String text = " " + sourceA.getAmountOfFiles() + " Files with a total of " + sourceA.getTotalSizeInMBString();
		srcAInfo.setText(text);
	}

	public static void updateSourceBInfo() {
		if (sourceB == null)
			return;
		String text = " " + sourceB.getAmountOfFiles() + " Files with a total of " + sourceB.getTotalSizeInMBString();
		srcBInfo.setText(text);
	}

	public static Source getSourceA() {
		return sourceA;
	}

	public static void setSourceA(Source src) {
		sourceA = src;
	}

	public static Source getSourceB() {
		return sourceB;
	}

	public static void setSourceB(Source src) {
		sourceB = src;
	}

	public static ProgressBar getProgressBar() {
		return progressBar;
	}

	public static final TextField getSrcATextField() {
		return srcATextField;
	}

	public static final TextField getSrcBTextField() {
		return srcBTextField;
	}

	public static final Text getStatusText() {
		return statusText;
	}

	public static final String getName() {
		return name;
	}

	public static final String getSeparator() {
		return File.separator;
	}

	public static final int getWindowWidth() {
		return windowWidth;
	}

	public static final int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @return the srcainfo
	 */
	public static Label getSrcAInfo() {
		return srcAInfo;
	}

	/**
	 * @return the srcbinfo
	 */
	public static Label getSrcBInfo() {
		return srcBInfo;
	}
}
