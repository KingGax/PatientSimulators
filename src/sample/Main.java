package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.awt.*;
import javafx.event.ActionEvent;

import javax.swing.*;
import java.io.*;
import java.nio.Buffer;
import java.util.logging.Logger;

public class Main extends Application {
    private Desktop desktop = Desktop.getDesktop();
    BufferedReader reader;
    ComboBox<String> headerPicker;
    @Override
    public void start(Stage stage) {
        reader = null;
        Label title = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        Button simulationButton = new Button("Run Simulation");
        simulationButton.setOnAction(e -> SetDashboadScene());
        FileChooser fileChooser = new FileChooser();
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage));
        ListView<String>selectedHeaders = new ListView<String>();
        headerPicker =  new ComboBox<String>();
        headerPicker.setPromptText("Choose a file to select headers");
        Button addHeader = new Button("Add Header");
        addHeader.setOnAction(e -> selectedHeaders.getItems().addAll(headerPicker.getValue()));
        HBox chooseHeadersBox = new HBox(20);
        chooseHeadersBox.setAlignment(Pos.CENTER);
        VBox centreBox = new VBox(30);
        chooseHeadersBox.getChildren().addAll(headerPicker,addHeader,selectedHeaders);
        centreBox.getChildren().addAll(title,fileSelectorButton,chooseHeadersBox,simulationButton);
        centreBox.setAlignment(Pos.CENTER);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centreBox);
        Scene welcome = new Scene(borderPane, 640, 480);
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        stage.show();

    }
    void SetDashboadScene()
    {

    }


    public void openFile(FileChooser fileChooser,Stage stage){
        File file = fileChooser.showOpenDialog(stage);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException ex){
            reader = new BufferedReader(null);
            System.out.println("IOException in opening file");
        }
            try {
                String commaSep = reader.readLine();
                fillComboBoxFromReader(commaSep);
            } catch (IOException ex) {
                System.out.println("IOException in printing file");
            }

    }
    public void fillComboBoxFromReader(String commaSepItems){
        headerPicker.getItems().addAll(commaSepItems.split(","));
    }

    public static void main(String[] args) {
        launch();
    }

}
