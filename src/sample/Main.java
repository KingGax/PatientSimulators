package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.awt.*;
import javafx.event.ActionEvent;

import java.io.*;
import java.nio.Buffer;
import java.util.logging.Logger;

public class Main extends Application {
    private Desktop desktop = Desktop.getDesktop();
    BufferedReader reader;
    @Override
    public void start(Stage stage) {
        reader = null;
        Label label1 = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        FileChooser fileChooser = new FileChooser();
        fileSelectorButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            openFile(file);
            try {
                System.out.println(reader.readLine());
            } catch (IOException ex) {
                System.out.println("IOException in printing file");
            }
        });
        Scene welcome = new Scene(new StackPane(label1, fileSelectorButton), 640, 480);
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(stage);
        stage.show();

    }


    public void openFile(File f){
        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (IOException ex){
            reader = new BufferedReader(null);
            System.out.println("IOException in opening file");
        }
    }


    public static void main(String[] args) {
        launch();
    }

}
