package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Main extends Application {


    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Welcome to Patient Simulators");
        Scene welcome = new Scene(new StackPane(l), 640, 480);
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
