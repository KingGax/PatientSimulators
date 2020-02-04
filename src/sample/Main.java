package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
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
        FileChooser fileChooser = new FileChooser();
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage));
        ListView<String>selectedHeaders = new ListView<String>();
        selectedHeaders.setOnMouseClicked(new EventHandler<MouseEvent>() {

                                       @Override
                                       public void handle(MouseEvent click) {

                                           if (click.getClickCount() == 2) {
                                               //Use ListView's getSelected Item
                                               selectedHeaders.getItems().remove(selectedHeaders.getSelectionModel().getSelectedItem());
                                           }
                                       }
                                   });
        headerPicker =  new ComboBox<String>();
        headerPicker.setPromptText("Choose a file to select headers");
        Button addHeader = new Button("Add Header");
        addHeader.setOnAction(e -> tryAddItem(headerPicker.getValue(),selectedHeaders));
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
        simulationButton.setOnAction(e->stage.setScene(getDashboardScene(selectedHeaders)));
        stage.show();

    }
    void tryAddItem(String item, ListView<String> lv)
    {
        if (!lv.getItems().contains(item)&&(item!=null)){
            lv.getItems().add(item);
        }
    }
    Scene getDashboardScene(ListView<String>selectedItems)
    {

        BorderPane bp = new BorderPane();
        Scene simulation = new Scene(bp, 640, 480);
        return simulation;
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
                reader.mark(1);//reads the line and then goes back so that it can work out later which columns to read
                String commaSep = reader.readLine();
                reader.reset();
                fillComboBoxFromReader(commaSep);
            } catch (IOException ex) {
                System.out.println("IOException in printing file");
            }

    }
    public void fillComboBoxFromReader(String commaSepItems){
        headerPicker.getItems().clear();
        headerPicker.getItems().addAll(commaSepItems.split(","));
    }

    public static void main(String[] args) {
        launch();
    }

}
