package sample;

import javafx.application.Application;
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

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    private BufferedReader reader;
    private int rowCount;
    private float[][] dataArray; //Ignore this warning, dataArray to be used for gauges
    private ComboBox<String> headerPicker;
    @Override
    public void start(Stage stage) {
        reader = null;
        Label title = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        Button simulationButton = new Button("Run Simulation");
        FileChooser fileChooser = new FileChooser();
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage));
        ListView<String>selectedHeaders = new ListView<>();
        selectedHeaders.setOnMouseClicked(e -> handleMouse(e,selectedHeaders));
        /*new EventHandler<MouseEvent>() {

                                       @Override
                                       public void handle(MouseEvent click) {

                                           if (click.getClickCount() == 2) {
                                               //Use ListView's getSelected Item
                                               selectedHeaders.getItems().remove(selectedHeaders.getSelectionModel().getSelectedItem());
                                           }
                                       }
                                   });*/
        headerPicker =  new ComboBox<>();
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

    private void handleMouse(MouseEvent click, ListView<String> selectedHeaders){
        if (click.getClickCount() == 2) {
            //Use ListView's getSelected Item
            selectedHeaders.getItems().remove(selectedHeaders.getSelectionModel().getSelectedItem());
        }
    }
    private void tryAddItem(String item, ListView<String> lv)
    {
        if (!lv.getItems().contains(item)&&(item!=null)){
            lv.getItems().add(item);
        }
    }
    private Scene getDashboardScene(ListView<String>selectedItems)
    {
        fillDataArray(selectedItems);
        BorderPane bp = new BorderPane();
        Scene simulation = new Scene(bp, 640, 480);
        return simulation;
    }

    private void fillDataArray(ListView<String>selectedItems){
        dataArray = new float[rowCount][selectedItems.getItems().size()];
        System.out.println(selectedItems.getItems().get(0));
        int[][] offSetArray = new int [selectedItems.getItems().size()][2];
        for (int i=0; i < dataArray.length; i++){
            String[] tempData = new String[1];
            try{
                tempData = reader.readLine().split(", ");
            }
            catch (IOException ex){
                System.out.println("IOException in reading from file.");
            }
            if (i == 0){
                for (int j = 0; j < dataArray[i].length; j++){
                    offSetArray[j][0] = j;
                    offSetArray[j][1] = Arrays.asList(tempData).indexOf(selectedItems.getItems().get(j));
                }
            } else {
                for (int j = 0; j < dataArray[i].length; j++) {
                    if (offSetArray[j][1] == 0 || offSetArray[j][1] == 1) { //Time
                        dataArray[i][j] = (float) Math.round(timeToFloat(tempData[offSetArray[j][1]]) * 10000f) / 10000f;
                    } else if (offSetArray[j][1] == 2) { //Date
                        dataArray[i][j] = dateTimeToFloat(tempData[offSetArray[j][1]]);
                    } else { //Regular float
                        dataArray[i][j] = (float) Math.round(Float.parseFloat(tempData[offSetArray[j][1]])*10000f)/10000f;
                    }
                }
            }
        }
    }

    private float dateTimeToFloat(String date){
        float outVal;
        String pattern = "\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(date);
        if (!m.find()){
            return 0;
        }
        else{
            int years = Integer.parseInt(date.substring(0, 4));
            int months = Integer.parseInt(date.substring(5, 7));
            int days = Integer.parseInt(date.substring(8, 10));
            outVal = timeToFloat((24*(days+28*months+365*years))+":00:00")+timeToFloat(date.substring(11,19));
            return outVal;
        }
    }

    private float timeToFloat(String time){
        float outVal;
        String pattern = "\\d+\\d:\\d\\d:\\d\\d";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(time);
        if (!m.find()){ //r̝̮̥͔͎̱̜eg̭̺̰̪e̮̝͕̗̙̗ͅx̨
            return 0;
        } else {
            int hours = Integer.parseInt(time.substring(0,2));
            int minutes = Integer.parseInt(time.substring(3,5));
            int seconds = Integer.parseInt(time.substring(6,8));
            outVal = seconds + 60*(minutes+60*hours);
            return outVal;
        }
    }

    private void openFile(FileChooser fileChooser,Stage stage){
        File file = fileChooser.showOpenDialog(stage);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException ex){
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
            try{
                BufferedReader rowReader = new BufferedReader(new FileReader(file));
                rowCount = 0;
                while (rowReader.readLine() != null) {
                    rowCount++;
                }
                rowReader.close();
            } catch (IOException ex) {
                System.out.println("IOException in counting rows");
            }

    }
    private void fillComboBoxFromReader(String commaSepItems){
        headerPicker.getItems().clear();
        headerPicker.getItems().addAll(commaSepItems.split(", "));
    }

    public static void main(String[] args) {
        launch();
    }

}
