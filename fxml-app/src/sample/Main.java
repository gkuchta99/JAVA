package sample;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        String fxmlDocPath = "src/sample.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        VBox root = (VBox) loader.load(fxmlStream);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("application.css");
        stage.setScene(scene);
        stage.setTitle("A FXML Example without any Controller");
        stage.show();

    }
}