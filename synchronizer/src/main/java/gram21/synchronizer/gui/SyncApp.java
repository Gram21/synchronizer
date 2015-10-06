package gram21.synchronizer.gui;

import java.io.File;

import gram21.synchronizer.Source;
import gram21.synchronizer.Sync;
import gram21.synchronizer.SyncAPI;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class SyncApp extends Application {

	private static final int textFieldMinWidth = 250;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(createContent(stage));

		stage.setTitle(SyncAPI.getName());
		stage.setScene(scene);
		stage.show();

		// TODO: mvn and git
		// TODO: parallelize?
		// TODO: more beautiful with css etc
		// TODO: tidy up? better MVC (with xml)?
	}

	private Parent createContent(Stage stage) {
		// carrying borderPane
		BorderPane border = new BorderPane();
		border.setPrefSize(SyncAPI.getWindowWidth(), SyncAPI.getWindowHeight());

		// init GridPane, that holds most of the program
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(25, 25, 25, 25));

		// add Text
		Text sceneTitle = new Text("Sync"); // TODO: Something better than this!
		sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		gridPane.add(sceneTitle, 0, 0, 2, 1);

		// add Source A row
		Label srcALabel = new Label("Source A: ");
		gridPane.add(srcALabel, 0, 1);
		TextField txtFieldA = SyncAPI.getSrcATextField();
		txtFieldA.setMinWidth(textFieldMinWidth);
		gridPane.add(txtFieldA, 1, 1);
		Button chooserA = createDirectoryChooserButton(stage, SyncAPI.getSrcATextField(), "A");
		gridPane.add(chooserA, 2, 1);
		// source A Info:
		gridPane.add(SyncAPI.getSrcAInfo(), 1, 2, 2, 1);

		// add Source B row
		Label srcBLabel = new Label("Source B: ");
		gridPane.add(srcBLabel, 0, 3);
		TextField txtFieldB = SyncAPI.getSrcBTextField();
		txtFieldB.setMinWidth(textFieldMinWidth);
		gridPane.add(txtFieldB, 1, 3);
		Button chooserB = createDirectoryChooserButton(stage, SyncAPI.getSrcBTextField(), "B");
		gridPane.add(chooserB, 2, 3);
		// source B Info:
		gridPane.add(SyncAPI.getSrcBInfo(), 1, 4, 2, 1);

		// add Button
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.getChildren().add(createStartButton());
		gridPane.add(hbox, 2, 5);

		// add progress bar
		gridPane.add(SyncAPI.getProgressBar(), 1, 5);
		SyncAPI.getProgressBar().setVisible(false);
		SyncAPI.getProgressBar().setMinWidth(textFieldMinWidth);
		SyncAPI.getProgressBar().setMaxWidth(Double.MAX_VALUE);

		// add the whole gridPane to the borderPane
		border.setCenter(gridPane);

		// add Status Text Field
		border.setBottom(SyncAPI.getStatusText());

		return border;
	}

	private Button createDirectoryChooserButton(Stage stage, TextField srcTxtField, String sourceVal) {
		Button btn = new Button();
		btn.setText("...");
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Select Directory To Synchronize");
		dc.setInitialDirectory(new File(System.getProperty("user.home")));

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (sourceVal.equals("A")) {
					dc.setInitialDirectory(new File(SyncAPI.getSrcATextField().getText()));
				} else if (sourceVal.equals("B")) {
					dc.setInitialDirectory(new File(SyncAPI.getSrcBTextField().getText()));
				}

				File file = dc.showDialog(stage);
				if (file != null) {
					srcTxtField.setText(file.getAbsolutePath());
					if (sourceVal.equals("A")) {
						SyncAPI.setSourceA(new Source(file));
						SyncAPI.updateSourceAInfo();
					} else if (sourceVal.equals("B")) {
						SyncAPI.setSourceB(new Source(file));
						SyncAPI.updateSourceBInfo();
					}
				}
			}
		});

		return btn;
	}

	private Button createStartButton() {
		Button btn = new Button();
		btn.setText("Start");

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Set progressBar visible
				SyncAPI.getProgressBar().setVisible(true);
				// start Sync
				Sync s = new Sync(SyncAPI.getSrcATextField().getText(), SyncAPI.getSrcBTextField().getText());
				s.start();
			}
		});

		return btn;
	}
}
