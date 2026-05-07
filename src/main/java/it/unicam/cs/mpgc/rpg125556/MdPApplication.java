package it.unicam.cs.mpgc.rpg125556;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MdPApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test");

        TextArea terminalArea = new TextArea();
        terminalArea.setEditable(false);
        terminalArea.setStyle("-fx-control-inner-background: black; " +
                "-fx-text-fill: #00FF00; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px;");

        terminalArea.setText("test");

        StackPane root = new StackPane();
        root.getChildren().add(terminalArea);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
