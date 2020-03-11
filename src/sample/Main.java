package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.skins.GaugeSkin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//TODO:
//-Add max values for every conceivable header
//-Fix occasional "IndexOutOfRangeException index -1 out of range 2 error popping up approx. 1/20 tests.
//-Fix bugs listed on Jira

public class Main extends Application {
    private BufferedReader reader;
    private int rowCount;
    private List<Gauge> gauges;
    private float[][] dataArray; //Ignore this warning, dataArray to be used for gauges
    private ComboBox<String> headerPicker;
    private int currentStep = 0;
    private final int updateFrequency = 500; //Frequency of gauge updates in ms
    private float mu = 0.0f;
    private Timer eventTimer;
    private boolean timerStarted = false;
    private Slider timeSlider;
    private ArrayList<Pair> eventLog = new ArrayList<>();
    private boolean paused = false;
    @FXML
    private TableView<InputTable> selectedHeaderTitles;
    private Popup popup;
    private ComboBox<String> typeChooserTemplate;
    private Label timeLabel;

    @Override

    //Initial scene setup
    public void start(Stage stage) {
        reader = null;
        popup = new Popup();
        popup.setAutoHide(true);
        //ColorPicker test = new ColorPicker();
        //eventLog = new ArrayList<>();
        typeChooserTemplate = new ComboBox<>();
        typeChooserTemplate.getItems().addAll("Default Gauge","Simple Section","Line Graph");
        FileChooser fileChooser = new FileChooser();
        Label title = new Label("Welcome to Patient Simulators");
        Button fileSelectorButton = new Button("Select File");
        Button eventLogSelecter = new Button("Select Event Log");
        Button simulationButton = new Button("Run Simulation");
        Button loadCustomGaugeButton = new Button("Load Custom Gauge");
        loadCustomGaugeButton.setOnAction(e -> loadCustomGauge());
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage,false));
        eventLogSelecter.setOnAction(e -> openFile(fileChooser,stage,true));
        GridPane selectedHeaders = new GridPane();
        selectedHeaderTitles = new TableView<>();
        selectedHeaderTitles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        selectedHeaderTitles.setOnMouseClicked(e -> handleMouse(e,selectedHeaderTitles));
        Button gaugeButton = new Button("Custom Gauges");
        sample.GaugeBuilder gb = new sample.GaugeBuilder();
        gaugeButton.setOnMouseClicked(e-> stage.setScene(gb.getGaugeBuilderScene()));
        selectedHeaders.addColumn(0,selectedHeaderTitles);
        headerPicker =  new ComboBox<>();
        headerPicker.setPromptText("Choose a file to select headers");
        headerPicker.setMinWidth(200);
        Button addHeader = new Button("Add Header");
        addHeader.setMinWidth(200);
        addHeader.setOnAction(e -> tryAddItem(headerPicker.getValue(),selectedHeaderTitles));
        HBox fileSelectionBox = new HBox(15);
        fileSelectionBox.setAlignment(Pos.CENTER);
        fileSelectionBox.getChildren().addAll(fileSelectorButton,eventLogSelecter,loadCustomGaugeButton);
        HBox chooseHeadersBox = new HBox(20);
        chooseHeadersBox.setAlignment(Pos.CENTER);
        VBox centreBox = new VBox(30);
        chooseHeadersBox.getChildren().addAll(headerPicker,addHeader, selectedHeaders);
        selectedHeaderTitles.prefWidthProperty().bind(chooseHeadersBox.widthProperty());
        centreBox.getChildren().addAll(title,fileSelectionBox,chooseHeadersBox,simulationButton,gaugeButton);
        centreBox.setAlignment(Pos.CENTER);
        HBox header = new HBox(20);
        header.getChildren().addAll(title);
        header.setAlignment(Pos.CENTER);
        header.setMinHeight(30);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centreBox);
        BorderPane.setMargin(centreBox,new Insets(0,10,0,10));
        //TilePane testpane = new TilePane();//testpane.getChildren().add(borderPane);
        //borderPane.setTop(test);
        Scene welcome = new Scene(borderPane, 960, 720);
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        simulationButton.setOnAction(e->tryRunSimulation(stage));
        borderPane.setTop(header);
        TableColumn<InputTable, String> headerName = new TableColumn<>();
        headerName.setMinWidth(40);
        headerName.setText("Heading");
        headerName.setCellValueFactory(new PropertyValueFactory<>("headerName"));
        TableColumn<InputTable, ComboBox<String>> dataType = new TableColumn<>();
        dataType.setMinWidth(150);
        dataType.setCellValueFactory(new PropertyValueFactory<>("options"));
        TableColumn<InputTable, TextField> minVal = new TableColumn<>();
        minVal.setMinWidth(80);
        minVal.setCellValueFactory(new PropertyValueFactory<>("min"));
        minVal.setText("Min");
        TableColumn<InputTable, TextField> maxVal = new TableColumn<>();
        maxVal.setMinWidth(80);
        maxVal.setCellValueFactory(new PropertyValueFactory<>("max"));
        maxVal.setText("Max");
        dataType.setText("Gauge Type");
        selectedHeaderTitles.getColumns().addAll(headerName, dataType,minVal,maxVal);
        //borderPane.setCenter(new ColorPicker());
        stage.show();
    }
    private void loadCustomGauge(){
        //todo do
    }
    /*public void start(Stage s)
    {
        // set title for the stage
        s.setTitle("creating color picker");

        // create a tile pane
        TilePane r = new TilePane();
        BorderPane b = new BorderPane();
        b.setCenter(r);
        // create a label
        Label l = new Label("This is a color picker example ");
        Label l1 = new Label("no selected color ");

        // create a color picker
        ColorPicker cp = new ColorPicker();
        ColorPicker cp2 = new ColorPicker();

        // create a event handler
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // color
                Color c = cp.getValue();

                // set text of the label to RGB value of color
                l1.setText("Red = " + c.getRed() + ", Green = " + c.getGreen()
                        + ", Blue = " + c.getBlue());
            }
        };

        // set listener
        cp.setOnAction(event);

        // add label
        r.getChildren().add(l);
        r.getChildren().add(cp);
        r.getChildren().add(cp2);
        r.getChildren().add(l1);
        b.setCenter(cp);
        // create a scene
        Scene sc = new Scene(b, 500, 200);

        // set the scene
        s.setScene(sc);

        s.show();
    }*/
    private void openEventLog()
    {
        String line;
        try{
            eventLog.clear();
            line = reader.readLine();
            while (line != null){if (line.compareTo("SCE Time, Message,") == 0) break; line = reader.readLine();} //reading to start of data
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(",");                 //item[] is each row of csv file
                String[] b = item[0].split(":");                 //split the time into hours minutes and seconds
                int i =Integer.parseInt(b[0])*60*60 + Integer.parseInt(b[1])*60 + Integer.parseInt(b[2]);   //converting
                Pair p = new Pair(i,item[1]);
                eventLog.add(p);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        /*for (int i = 0; i < eventLog.size(); i++) {
            System.out.println(eventLog.get(i).getTime() + " " + eventLog.get(i).getEvent());
        }*/
    }
    //Checks at least one header has been selected
    private void tryRunSimulation(Stage stage)
    {
        if (!selectedHeaderTitles.getItems().isEmpty())
        {
            stage.setScene(getDashboardScene());
        }
        else
        {
            showPopup("Choose at least one header to display",stage);
        }
    }

    //Displays popup with given message
    private void showPopup(String message, Stage stage) {
        Label popupLabel = new Label(message);
        popupLabel.setStyle(" -fx-background-color: orangered;");// set background
        popupLabel.setMinWidth(80); // set size of label
        popupLabel.setMinHeight(50);
        popup.getContent().clear();
        popup.getContent().add(popupLabel);// add the label
        popup.show(stage, stage.getScene().getWindow().getX() + 5, stage.getScene().getWindow().getY() + 20);

    }

    //Programmatically creates gauges and stores them in global list, starts timer
    private void initialiseGauges(TableView<InputTable>selectedItems, GridPane pane){
        gauges = new ArrayList<>();
        for (int i = 0; i < selectedItems.getItems().size(); i++){
            Gauge.SkinType type = PureFunctions.translateStringToGaugeType(selectedItems.getItems().get(i).selectedValue());
            String header = selectedItems.getItems().get(i).headerName;
            Gauge gauge = buildGauge(type,selectedItems.getItems().get(i));
            VBox gaugeBox = getTopicBox(selectedItems.getItems().get(i).headerName, Color.rgb(77,208,225), gauge);
            pane.add(gaugeBox, i%2, i /2);
            gauges.add(gauge);
        }
        pane.setPadding(new Insets(20));
        pane.setHgap(15);
        pane.setVgap(15);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(39,44,50), CornerRadii.EMPTY, Insets.EMPTY)));
        eventTimer = new Timer();
        TimerTask task = new EventTimerTask(this);
        eventTimer.scheduleAtFixedRate(task, 0,updateFrequency);
        timerStarted = true;
    }
    private Gauge buildGauge(Gauge.SkinType type, InputTable data){
        Gauge newGauge;
        int maxValue, minValue;
        try {
            maxValue = Integer.parseInt(data.getMax().getText());
        }catch(Exception e){
          maxValue = PureFunctions.getMaxValue(data.headerName);
        }
        try {
            minValue = Integer.parseInt(data.getMin().getText());
        }catch(Exception e){
            minValue = 0;
        }
        if (type == Gauge.SkinType.SIMPLE_SECTION){
            GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.SIMPLE_SECTION);
            Section section = new Section((PureFunctions.getMaxValue(data.headerName) / 2 - 15),(PureFunctions.getMaxValue(data.headerName) / 2 + 20),Color.GREEN);
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setBarBackgroundColor(Color.rgb(39,44,50));
            newGauge.setAnimated(true);
            newGauge.getSections().add(section);
            return newGauge;
        }
        if (type == Gauge.SkinType.TILE_SPARK_LINE) {
            GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.TILE_SPARK_LINE);
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setBarBackgroundColor(Color.rgb(39,44,50));
            newGauge.setAnimated(true);
            return newGauge;
        }
        GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.MODERN);
        newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).build();
        Section section = new Section((PureFunctions.getMaxValue(data.headerName) / 2 - 15),(PureFunctions.getMaxValue(data.headerName) / 2 + 20),Color.GREEN);
        newGauge.getSections().add(section);
        return newGauge;
    }
    //Updates gauges at a regular interval, called by EventTimerTask.run()
    void updateGauges(){
        for (int i = 0; i < gauges.size(); i++) {
            float currentVal, nextVal, gaugeVal;
            currentVal = dataArray[currentStep][i + 1];
            if (currentStep < dataArray.length - 1) {
                nextVal = dataArray[currentStep + 1][i + 1];
                gaugeVal = cosineInterpolate(currentVal, nextVal, mu);
            } else {
                gaugeVal = currentVal;
                eventTimer.cancel();
                timerStarted = false;
            }
            try {
                final int x = i;
                Platform.runLater(() ->gauges.get(x).setValue(gaugeVal));
            } catch (NullPointerException e){
                System.out.println("Data value at indices " + currentStep + ", " + i+1 + "appears to be null.");
            }
        }
        final float muStep = (float) updateFrequency/5000;
        mu = roundToDP((mu+muStep)%1, (int) Math.ceil(Math.log(1/(double) muStep)));
        if (mu == 0){
            currentStep++;
        }
        Platform.runLater(() -> timeSlider.setValue((currentStep+mu)*5));
        UpdateTimeLabel();
    }

    //Updates time label in FX thread
    private void UpdateTimeLabel(){
        Platform.runLater(() -> timeLabel.setText((currentStep + mu) * 5 +"s"));
    }

    //Rounds float to a positive number of decimal places
    private float roundToDP(float x, int y){
        if (y >=0) {
            return (float) (Math.round(x * Math.pow(10, y)) / Math.pow(10, y));
        } else{
            return x;
        }
    }

    //Setup for TopicBox
    private VBox getTopicBox(final String TEXT, final Color COLOR, final Gauge GAUGE) {
        Rectangle bar = new Rectangle(200, 3);
        bar.setArcWidth(6);
        bar.setArcHeight(6);
        bar.setFill(COLOR);

        Label label = new Label(TEXT);
        label.setTextFill(COLOR);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 0, 10, 0));

        VBox vBox = new VBox(bar, label, GAUGE);
        vBox.setSpacing(3);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    //Returns interpolated value between y0 and y1 depending on mu
    private float cosineInterpolate(float y0, float y1, float mu){
        float mu2;
        mu2=(float)(1-Math.cos(mu*Math.PI))/2;
        return (y0*(1-mu2)+y1*mu2);
    }

    //Handles mouse interaction with ListView
    private void handleMouse(MouseEvent click, TableView<InputTable> selectedHeaders){
        if (click.getClickCount() == 2) {
            //Use ListView's getSelected Item
            selectedHeaders.getItems().remove(selectedHeaders.getSelectionModel().getSelectedItem());
        }
    }
    //Checks if the header name is already present
    private boolean checkItemPresent(String item, TableView<InputTable> tv){
        for (InputTable i: tv.getItems()) {
            if (i.headerName.compareTo(item) == 0)
            {
                return true;
            }
        }
        return false;
    }
    //Attempts to add item to ListView
    private void tryAddItem(String item, TableView<InputTable> tv)
    {
        if (!checkItemPresent(item,tv)&&(item!=null)){

            ComboBox<String> typePicker = new ComboBox<>();
            typePicker.getItems().addAll(typeChooserTemplate.getItems());
            typePicker.getSelectionModel().selectFirst();
            TextField min = new TextField();
            min.textProperty().setValue("0");
            TextField max = new TextField();
            max.textProperty().setValue(Integer.toString(PureFunctions.getMaxValue(item)));
            tv.getItems().add(new InputTable(item,typePicker,min,max));
        }
    }

    //Setup for dashboard JavaFX scene
    private Scene getDashboardScene()
    {
        dataArray = fillDataArray(selectedHeaderTitles, reader);
        BorderPane bp = new BorderPane();
        GridPane gp = new GridPane();
        HBox topHBox = new HBox();
        Button playbackButton = new Button();
        playbackButton.setMinWidth(48f);
        playbackButton.setMaxWidth(48f);
        playbackButton.setMinHeight(48f);
        playbackButton.setMaxHeight(48f);
        Image image = new Image(getClass().getResourceAsStream("res/PauseIcon.png"));
        ImageView imgView = new ImageView(image);
        imgView.fitWidthProperty().bind(playbackButton.widthProperty());
        imgView.fitHeightProperty().bind(playbackButton.heightProperty());
        playbackButton.setGraphic(imgView);
        playbackButton.setOnAction(e -> playBackHandler(playbackButton));
        bp.setCenter(gp);
        VBox topVBox = new VBox();
        timeLabel = new Label();
        timeSlider = new Slider(0, dataArray[dataArray.length-1][0], 0);
        timeSlider.setMajorTickUnit(updateFrequency/1000f);
        timeSlider.setMinorTickCount((int)(dataArray[rowCount-2][0] * (1000f/updateFrequency)) + 1);
        timeSlider.setSnapToTicks(true);
        timeSlider.prefWidthProperty().bind(topHBox.widthProperty());
        timeSlider.setOnMousePressed((MouseEvent event) -> PauseTimer(playbackButton));
        timeSlider.setOnMouseReleased(event -> {
            currentStep = (int) Math.floor(timeSlider.getValue())/5;
            mu = ((float)Math.floor(timeSlider.getValue()) - currentStep*5)/((float) (5000/updateFrequency));
            UpdateTimeLabel();
        });
        topVBox.getChildren().addAll(timeSlider, timeLabel);
        topHBox.getChildren().addAll(playbackButton, topVBox);
        bp.setTop(topHBox);
        initialiseGauges(selectedHeaderTitles, gp);
        return new Scene(bp, 640, 480);
    }

    //Handles stopping and starting of playback
    private void playBackHandler(Button b){
        if (!paused) {
            PauseTimer(b);
        }
        else {
            ResumeTimer(b);
        }
    }

    //Pauses timer
    private void PauseTimer(Button b){
        if (!paused){
            eventTimer.cancel();
            Image image = new Image(getClass().getResourceAsStream("res/PlayIcon.png"));
            ImageView imgView = new ImageView(image);
            imgView.fitWidthProperty().bind(b.widthProperty());
            imgView.fitHeightProperty().bind(b.heightProperty());
            b.setGraphic(imgView);
            paused = true;
        }
    }

    //Resumes timer
    private void ResumeTimer(Button b){
        if (paused) {
            eventTimer = new Timer();
            TimerTask task = new EventTimerTask(this);
            eventTimer.schedule(task, 0, updateFrequency);
            Image image = new Image(getClass().getResourceAsStream("res/PauseIcon.png"));
            ImageView imgView = new ImageView(image);
            imgView.fitWidthProperty().bind(b.widthProperty());
            imgView.fitHeightProperty().bind(b.heightProperty());
            b.setGraphic(imgView);
            paused = false;
        }
    }

    //Fills data array with values from global file corresponding to headers passed through selectedItems
    private float[][] fillDataArray(TableView<InputTable>selectedItems, BufferedReader reader){
        String[] selectedItemsArr = new String[selectedItems.getItems().size()+1];
        for (int i = 1; i < selectedItemsArr.length; i ++){
            selectedItemsArr[i] = selectedItems.getItems().get(i-1).headerName;
        }
        selectedItemsArr[0] = "PatientTime";
        float[][] datArray = new float[rowCount-1][selectedItemsArr.length];
        int[][] offSetArray = new int [selectedItemsArr.length][2];
        for (int i=0; i < datArray.length+1; i++){
            String[] tempData = new String[1];
            try{
                tempData = reader.readLine().split(", ");
            }
            catch (IOException ex){
                System.out.println("IOException in reading from file.");
            }
            if (i == 0){
                for (int j = 0; j < datArray[0].length; j++){
                    offSetArray[j][0] = j;
                    offSetArray[j][1] = Arrays.asList(tempData).indexOf(selectedItemsArr[j]);
                }
            } else {
                for (int j = 0; j < datArray[i-1].length; j++) {
                    if (offSetArray[j][1] == 0 || offSetArray[j][1] == 1) { //Time
                        datArray[i-1][j] = (float) Math.round(timeToFloat(tempData[offSetArray[j][1]]) * 10000f) / 10000f;
                    } else if (offSetArray[j][1] == 2) { //Date
                        datArray[i-1][j] = dateTimeToFloat(tempData[offSetArray[j][1]]);
                    } else { //Regular float
                        datArray[i-1][j] = (float) Math.round(Float.parseFloat(tempData[offSetArray[j][1]])*10000f)/10000f;
                    }
                }
            }
        }
        return datArray;
    }

    //Potentially no longer needed
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

    //Converts time to float
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
    private String getFileExtension(String path)
    {
        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (i > p) {
            return path.substring(i+1);
        }
        return "";
    }
    //Prompts user to select a file and stores contents in BufferedReader reader
    private void openFile(FileChooser fileChooser,Stage stage,boolean eventsLog){
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && (getFileExtension(file.getPath()).compareTo("csv") == 0)) {
            try {
                reader = new BufferedReader(new FileReader(file));
                if (eventsLog){
                    openEventLog();
                }
                else {
                    openData(file);
                }
            } catch (IOException ex){
                System.out.println("IOException in opening file");
            }
        }
        else if (file != null){
            if (getFileExtension(file.getPath()).compareTo("csv")!=0)
            {
                showPopup("Please choose a CSV file",stage);
            }
        }
    }
    private void openData(File file){
        try {
            reader.mark(1);//reads the line and then goes back so that it can work out later which columns to read
            String commaSep = reader.readLine();
            reader.reset();
            fillComboBoxFromReader(commaSep);
            selectedHeaderTitles.getItems().clear();
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
    //Fills ComboBox items with appropriate contents from csv file
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
        if (timerStarted) {
            eventTimer.cancel();
            eventTimer.purge();
        }
    }

}
