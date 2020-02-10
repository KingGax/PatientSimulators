package sample;

import eu.hansolo.medusa.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.stage.Popup;

//TODO:
//-Add max values for every conceivable header
//-Have gauges update with time
public class Main extends Application {
    private BufferedReader reader;
    private int rowCount;
    private List<Gauge> gauges;
    private float[][] dataArray; //Ignore this warning, dataArray to be used for gauges
    private ComboBox<String> headerPicker;
    private int currentStep = 0;
    private final int updateFrequency = 100; //Frequency of gauge updates in ms
    private float mu = 0.0f;
    private Timer eventTimer;
    private Popup popup;

    @Override
    public void start(Stage stage) {
        reader = null;

        popup = new Popup();
        popup.setAutoHide(true);

        Label title = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        Button simulationButton = new Button("Run Simulation");
        FileChooser fileChooser = new FileChooser();
        fileSelectorButton.setOnAction(e -> openFile(fileChooser, stage));
        ListView<String> selectedHeaders = new ListView<>();
        selectedHeaders.setOnMouseClicked(e -> handleMouse(e, selectedHeaders));
        headerPicker = new ComboBox<>();
        headerPicker.setPromptText("Choose a file to select headers");
        headerPicker.setMaxWidth(300);
        Button addHeader = new Button("Add Header");
        addHeader.setOnAction(e -> tryAddItem(headerPicker.getValue(), selectedHeaders));
        HBox header = new HBox(20);
        header.getChildren().addAll(title);
        header.setAlignment(Pos.CENTER);
        header.setMinHeight(30);
        HBox chooseHeadersBox = new HBox(20);
        chooseHeadersBox.setAlignment(Pos.CENTER);
        VBox centreBox = new VBox(30);
        chooseHeadersBox.getChildren().addAll(headerPicker, addHeader, selectedHeaders);
        centreBox.getChildren().addAll(fileSelectorButton, chooseHeadersBox, simulationButton);
        centreBox.setAlignment(Pos.CENTER);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centreBox);
        borderPane.setTop(header);
        Scene welcome = new Scene(borderPane, 640, 480);
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        simulationButton.setOnAction(e -> stage.setScene(getDashboardScene(selectedHeaders)));
        stage.show();
    }
    private void showPopup(String message, Stage stage)
    {
        Label popupLabel = new Label(message);
        popupLabel.setStyle(" -fx-background-color: orangered;");// set background
        popupLabel.setMinWidth(80); // set size of label
        popupLabel.setMinHeight(50);
        popup.getContent().clear();
        popup.getContent().add(popupLabel);// add the label
        popup.show(stage,stage.getScene().getWindow().getX() + 5,stage.getScene().getWindow().getY() + 20);

    }
    private int getMaxValue(String val) {
        switch (val) {
            case "HR":
                return 200;
            case "SBP":
                return 140;
            case "DBP":
                return 100;
            case "MAP":
                return 100;
            case "CVP":
                return 10;
            case "VT":
                return 600;
            default:
                return -1;
        }

    }

    private String getUnit(String val) {
        switch (val) {
            case "HR":
                return "BPM";
            case "SBP":
                return "SBP";
            case "DBP":
                return "DBP";
            case "MAP":
                return "MAP";
            case "CVP":
                return "CVP";
            case "VT":
                return "VT";
            default:
                return "N/A";
        }
    }

    private void initialiseGauges(ListView<String> selectedItems, GridPane pane) {
        gauges = new ArrayList<>();
        GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.SLIM);
        for (int i = 0; i < selectedItems.getItems().size(); i++) {
            Gauge gauge = builder.decimals(0).maxValue(getMaxValue(selectedItems.getItems().get(i))).unit(getUnit(selectedItems.getItems().get(i))).build();
            VBox gaugeBox = getTopicBox(selectedItems.getItems().get(i), Color.rgb(77, 208, 225), gauge);
            pane.add(gaugeBox, i % 2, i / 2);
            //gauge.setAnimationDuration(5000);
            gauges.add(gauge);
        }
        pane.setPadding(new Insets(20));
        pane.setHgap(15);
        pane.setVgap(15);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(39, 44, 50), CornerRadii.EMPTY, Insets.EMPTY)));
        eventTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("hi");
                for (int i = 0; i < gauges.size(); i++) {
                    System.out.println(i);
                    System.out.println(dataArray[currentStep][i + 1]);
                    float currentVal, nextVal, gaugeVal;
                    currentVal = dataArray[currentStep][i + 1];
                    if (currentStep < dataArray.length - 1) {
                        nextVal = dataArray[currentStep + 1][i + 1];
                        gaugeVal = cosineInterpolate(currentVal, nextVal, mu);
                    } else {
                        gaugeVal = currentVal;
                    }
                    gauges.get(i).setValue(gaugeVal);
                    //gauges.get(i).setValue(dataArray[currentStep][i+1]);
                }
                final float muStep = (float) updateFrequency / 5000;
                mu = roundToDP((mu + muStep) % 1, (int) Math.ceil(Math.log(1 / (double) muStep)));
                System.out.println("MU: " + mu);
                //console.log(mu);
                if (mu == 0) {
                    currentStep++;
                }
                //currentStep++;
            }

        };
        eventTimer.scheduleAtFixedRate(task, 0, updateFrequency);
    }

    private float roundToDP(float x, int y) {
        System.out.println("Rounding " + x + "to: " + (float) (Math.round(x * Math.pow(10, y)) / Math.pow(10, y)));
        return (float) (Math.round(x * Math.pow(10, y)) / Math.pow(10, y));
    }

    private VBox getTopicBox(final String TEXT, final Color COLOR, final Gauge GAUGE) {
        Rectangle bar = new Rectangle(200, 3);
        bar.setArcWidth(6);
        bar.setArcHeight(6);
        bar.setFill(COLOR);

        Label label = new Label(TEXT);
        label.setTextFill(COLOR);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 0, 10, 0));

        GAUGE.setBarColor(COLOR);
        GAUGE.setBarBackgroundColor(Color.rgb(39, 44, 50));
        GAUGE.setAnimated(true);

        VBox vBox = new VBox(bar, label, GAUGE);
        vBox.setSpacing(3);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private float cosineInterpolate(float y0, float y1, float mu) {
        float mu2;
        mu2 = (float) (1 - Math.cos(mu * Math.PI)) / 2;
        return (y0 * (1 - mu2) + y1 * mu2);
    }

    private void handleMouse(MouseEvent click, ListView<String> selectedHeaders) {
        if (click.getClickCount() == 2) {
            //Use ListView's getSelected Item
            selectedHeaders.getItems().remove(selectedHeaders.getSelectionModel().getSelectedItem());
        }
    }

    private void tryAddItem(String item, ListView<String> lv) {
        if (!lv.getItems().contains(item) && (item != null)) {
            lv.getItems().add(item);
        }
    }

    private Scene getDashboardScene(ListView<String> selectedItems) {
        fillDataArray(selectedItems);
        GridPane gp = new GridPane();
        initialiseGauges(selectedItems, gp);
        //Scene simulation = ;
        return new Scene(gp, 640, 480);
    }

    private void fillDataArray(ListView<String> selectedItems) {
        String[] selectedItemsArr = new String[selectedItems.getItems().size() + 1];
        for (int i = 1; i < selectedItemsArr.length; i++) {
            selectedItemsArr[i] = selectedItems.getItems().get(i - 1);
            System.out.println(selectedItemsArr[i] + " - string");
        }
        selectedItemsArr[0] = "PatientTime";
        dataArray = new float[rowCount - 1][selectedItemsArr.length];
        int[][] offSetArray = new int[selectedItemsArr.length][2];
        for (int i = 0; i < dataArray.length + 1; i++) {
            String[] tempData = new String[1];
            try {
                tempData = reader.readLine().split(", ");
                System.out.println("Tempdata: ");
                /*for (int j = 0; j < tempData.length; j++){
                    System.out.println(tempData[j]);
                }*/
            } catch (IOException ex) {
                System.out.println("IOException in reading from file.");
            }
            if (i == 0) {
                for (int j = 0; j < dataArray[0].length; j++) {
                    offSetArray[j][0] = j;
                    offSetArray[j][1] = Arrays.asList(tempData).indexOf(selectedItemsArr[j]);
                }
            } else {
                for (int j = 0; j < dataArray[i - 1].length; j++) {
                    System.out.println("Length: " + dataArray[i - 1].length);
                    if (offSetArray[j][1] == 0 || offSetArray[j][1] == 1) { //Time
                        dataArray[i - 1][j] = (float) Math.round(timeToFloat(tempData[offSetArray[j][1]]) * 10000f) / 10000f;
                    } else if (offSetArray[j][1] == 2) { //Date
                        dataArray[i - 1][j] = dateTimeToFloat(tempData[offSetArray[j][1]]);
                    } else { //Regular float
                        dataArray[i - 1][j] = (float) Math.round(Float.parseFloat(tempData[offSetArray[j][1]]) * 10000f) / 10000f;
                    }
                }
            }
        }
        /*for (int i = 0; i < dataArray.length; i++){
            System.out.println("");
            for (int j = 0; j < dataArray[i].length; j++){
                System.out.print(dataArray[i][j] + ",");
            }
            System.out.println("");
        }*/
    }

    private float dateTimeToFloat(String date) {
        float outVal;
        String pattern = "\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(date);
        if (!m.find()) {
            return 0;
        } else {
            int years = Integer.parseInt(date.substring(0, 4));
            int months = Integer.parseInt(date.substring(5, 7));
            int days = Integer.parseInt(date.substring(8, 10));
            outVal = timeToFloat((24 * (days + 28 * months + 365 * years)) + ":00:00") + timeToFloat(date.substring(11, 19));
            return outVal;
        }
    }

    private float timeToFloat(String time) {
        float outVal;
        String pattern = "\\d+\\d:\\d\\d:\\d\\d";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(time);
        if (!m.find()) { //r̝̮̥͔͎̱̜eg̭̺̰̪e̮̝͕̗̙̗ͅx̨
            return 0;
        } else {
            int hours = Integer.parseInt(time.substring(0, 2));
            int minutes = Integer.parseInt(time.substring(3, 5));
            int seconds = Integer.parseInt(time.substring(6, 8));
            outVal = seconds + 60 * (minutes + 60 * hours);
            return outVal;
        }
    }

    private String getFileExtension(String path)
    {
        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (i > p) {
            return path.substring(i+1);
        }
        return null;
    }
    private void openFile(FileChooser fileChooser,Stage stage){
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && (getFileExtension(file.getPath()).compareTo("csv") == 0)) {
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
        else if (file != null){
            if (getFileExtension(file.getPath()).compareTo("csv")!=0)
            {
                showPopup("Please choose a CSV file",stage);
            }
        }
    }


    private void fillComboBoxFromReader(String commaSepItems){
        headerPicker.getItems().clear();
        String[] commaSepItemsArr = commaSepItems.split(", ");
        for (String currentItem : commaSepItemsArr) {
            if (!currentItem.equals("PatientTime") && !currentItem.equals("SCETime") && !currentItem.equals("WorldTime")) {
                headerPicker.getItems().add(currentItem);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop(){
        eventTimer.cancel();
        eventTimer.purge();
    }

}
