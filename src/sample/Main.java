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

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label label1 = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        File outFile = null;
        FileChooser fileChooser = new FileChooser();
        BufferedReader reader;
        System.out.println("jewotihso");
        fileSelectorButton.setOnAction(new EventHandler<ActionEvent>(outFile) {
            public void handle(final ActionEvent e, File f)  {
                File file = fileChooser.showOpenDialog(stage);
                BufferedReader reader;
                System.out.println("okokok");
                try {
                    reader = new BufferedReader(new FileReader(file));
                } catch (FileNotFoundException ex){
                    reader = new BufferedReader(null);
                    System.out.println("LULW");
                }
                return reader;
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
            System.out.println("Yes yes yes yes");
            desktop.open(f);
        } catch (IOException e){
            System.out.println("Oh no");
        }
    }


    public static void main(String[] args) {
        launch();
    }

}
