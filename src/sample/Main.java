package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
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
import javafx.scene.text.Font;

import java.io.File;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


//TODO:
//-Add max values for every conceivable header
//-Fix occasional "IndexOutOfRangeException index -1 out of range 2 error popping up approx. 1/20 tests.
//-Fix bugs listed on Jira

public class Main extends Application {
    private BufferedReader dataReader;
    private BufferedReader eventReader;
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
    private ArrayList<eventData> eventLog = new ArrayList<>();
    private boolean paused = false;
    @FXML
    private TableView<InputTable> selectedHeaderTitles;
    private Popup popup;
    private ComboBox<String> typeChooserTemplate;
    private TableView eventBox;
    private Label timeLabel;
    private int eventIndex = 0;

    @Override

    //Initial scene setup
    public void start(Stage stage) {
        dataReader = null;
        eventReader = null;
        popup = new Popup();
        popup.setAutoHide(true);
        //ColorPicker test = new ColorPicker();
        //eventLog = new ArrayList<>();
        typeChooserTemplate = new ComboBox<>();
        typeChooserTemplate.getItems().addAll("Default Gauge","Simple Section","Line Graph");
        FileChooser fileChooser = new FileChooser();
        Label title = new Label("Welcome to Patient Simulators");
        title.getStyleClass().add("title");
        title.setPadding(new Insets(10, 20, 0, 20));
        title.setFont(Font.font("fantasy"));
        Label selectFileLabel = new Label("Please select either a Data File or an Event Log:");
        selectFileLabel.getStyleClass().add("select-file-label");
        selectFileLabel.setFont(Font.font("fantasy"));
        Button fileSelectorButton = new Button("Upload Data File");
        fileSelectorButton.getStyleClass().add("button-blue");
        Button eventLogSelecter = new Button("Upload Event Log");
        eventLogSelecter.getStyleClass().add("button-yellow");
        Button simulationButton = new Button("Run Simulation");
        simulationButton.getStyleClass().add("button-green");
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage,false));
        eventLogSelecter.setOnAction(e -> openFile(fileChooser,stage,true));
        GridPane selectedHeaders = new GridPane();
        selectedHeaderTitles = new TableView<>();
        selectedHeaderTitles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        selectedHeaderTitles.setOnMouseClicked(e -> handleMouse(e,selectedHeaderTitles));
        Button gaugeButton = new Button("Build a Custom Gauge");
        gaugeButton.getStyleClass().add("button-yellow");
        sample.GaugeBuilder gb = new sample.GaugeBuilder();
        selectedHeaders.addColumn(0,selectedHeaderTitles);
        headerPicker =  new ComboBox<>();
        headerPicker.setPromptText("Select headers");
        headerPicker.getStyleClass().add("button-blue");
        headerPicker.setMinWidth(200);
        Button addHeader = new Button("Add Header");
        addHeader.getStyleClass().add("button-blue");
        addHeader.setMinWidth(200);
        addHeader.setOnAction(e -> tryAddItem(headerPicker.getValue(),selectedHeaderTitles));
        HBox fileSelectionBox = new HBox(15);
        fileSelectionBox.setAlignment(Pos.CENTER);
        fileSelectionBox.getChildren().addAll(fileSelectorButton,eventLogSelecter);
        HBox chooseHeadersBox = new HBox(20);
        chooseHeadersBox.setAlignment(Pos.CENTER);
        VBox centreBox = new VBox(30);
        chooseHeadersBox.getChildren().addAll(headerPicker,addHeader, selectedHeaders);
        selectedHeaderTitles.prefWidthProperty().bind(chooseHeadersBox.widthProperty());
        centreBox.getChildren().addAll(title, selectFileLabel, fileSelectionBox,chooseHeadersBox,gaugeButton,simulationButton);
        centreBox.setAlignment(Pos.CENTER);
        HBox header = new HBox(20);
        header.getChildren().addAll(title);
        header.setAlignment(Pos.CENTER);
        header.setMinHeight(30);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centreBox);
        BorderPane.setMargin(centreBox,new Insets(0,10,10,10));
        //TilePane testpane = new TilePane();//testpane.getChildren().add(borderPane);
        //borderPane.setTop(test);
        Scene welcome = new Scene(borderPane, 960, 720);
        welcome.getStylesheets().add("sample/stylesheet/styling.css");
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        simulationButton.setOnAction(e->tryRunSimulation(stage));
        borderPane.setTop(header);
        borderPane.setId("background");

        TableColumn<InputTable, String> headerName = new TableColumn<>();
        headerName.setMinWidth(40);
        headerName.setText("Heading");
        headerName.getStyleClass().add("table-heads");
        headerName.setCellValueFactory(new PropertyValueFactory<>("headerName"));
        TableColumn<InputTable, ComboBox<String>> dataType = new TableColumn<>();
        dataType.setMinWidth(150);
        dataType.setCellValueFactory(new PropertyValueFactory<>("options"));
        TableColumn<InputTable, TextField> minVal = new TableColumn<>();
        minVal.setMinWidth(80);
        minVal.setCellValueFactory(new PropertyValueFactory<>("min"));
        minVal.setText("Min");
        minVal.getStyleClass().add("table-heads");
        TableColumn<InputTable, TextField> maxVal = new TableColumn<>();
        maxVal.setMinWidth(80);
        maxVal.setCellValueFactory(new PropertyValueFactory<>("max"));
        maxVal.setText("Max");
        maxVal.getStyleClass().add("table-heads");
        dataType.setText("Gauge Type");
        dataType.getStyleClass().add("table-heads");
        TableColumn<InputTable, TextField> redSection = new TableColumn<>();
        redSection.setMinWidth(80);
        redSection.setCellValueFactory(new PropertyValueFactory<>("red"));
        redSection.setText("Red Section");
        redSection.getStyleClass().add("table-heads");
        TableColumn<InputTable, TextField> amberSection = new TableColumn<>();
        amberSection.setMinWidth(80);
        amberSection.setCellValueFactory(new PropertyValueFactory<>("amber"));
        amberSection.setText("Amber Section");
        amberSection.getStyleClass().add("table-heads");
        TableColumn<InputTable, TextField> greenSection = new TableColumn<>();
        greenSection.setMinWidth(80);
        greenSection.setCellValueFactory(new PropertyValueFactory<>("green"));
        greenSection.setText("Green Section");
        greenSection.getStyleClass().add("table-heads");
        gaugeButton.setOnMouseClicked(e-> stage.setScene(gb.getGaugeBuilderScene(welcome)));
        selectedHeaderTitles.getColumns().addAll(headerName, dataType,minVal,maxVal,redSection,amberSection,greenSection);
        //borderPane.setCenter(new ColorPicker());
        stage.show();
    }


    private void openEventLog()
    {
        String line;
        try{
            eventLog.clear();
            line = eventReader.readLine();
            while (line != null){if (line.compareTo("SCE Time, Message,") == 0) break; line = eventReader.readLine();} //reading to start of data
            while ((line = eventReader.readLine()) != null) {
                String[] item = line.split(",");                 //item[] is each row of csv file
                String[] b = item[0].split(":");                 //split the time into hours minutes and seconds
                int i =Integer.parseInt(b[0])*60*60 + Integer.parseInt(b[1])*60 + Integer.parseInt(b[2]);   //converting
                eventData p = new eventData(i,item[1]);
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
        popupLabel.getStyleClass().add("pop-up-message");
        popupLabel.setMinWidth(960); // set size of label
        popupLabel.setMinHeight(50);
        popupLabel.setAlignment(Pos.CENTER);
        popup.getContent().clear();
        popup.getContent().add(popupLabel);// add the label
        popup.show(stage, stage.getScene().getWindow().getX(), stage.getScene().getWindow().getY() + 30);

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
        pane.setPrefSize(570, 400);
        pane.setPadding(new Insets(20));
        pane.setHgap(15);
        pane.setVgap(15);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        eventTimer = new Timer();
        TimerTask task = new EventTimerTask(this);
        eventTimer.scheduleAtFixedRate(task, 0,updateFrequency);
        timerStarted = true;
    }
    private Gauge buildGauge(Gauge.SkinType type, InputTable data){
        Gauge newGauge = null;
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
        int[] sections = parseSectionData(data);
        Section redSection1 = new Section(sections[0],sections[1],Color.RED);
        Section redSection2 = new Section(sections[4],sections[5],Color.RED);
        Section amberSection1 = new Section(sections[1],sections[2],Color.valueOf("#FFBF00"));
        Section amberSection2 = new Section(sections[3],sections[4],Color.valueOf("#FFBF00"));
        Section greenSection = new Section(sections[2],sections[3],Color.GREEN);
        GaugeBuilder builder = GaugeBuilder.create();
        if (type == Gauge.SkinType.SIMPLE_SECTION){

            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).skinType(Gauge.SkinType.SIMPLE_SECTION).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setBarBackgroundColor(Color.rgb(39,44,50));
            newGauge.setAnimated(true);
        }
        if (type == Gauge.SkinType.TILE_SPARK_LINE) {
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).skinType(Gauge.SkinType.TILE_SPARK_LINE).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setBarBackgroundColor(Color.rgb(39,44,50));
            newGauge.setAnimated(true);
        }
        if (newGauge != null){
            newGauge.getSections().addAll(redSection1,redSection2,amberSection1,amberSection2,greenSection);
            return newGauge;
        }
        newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).skinType(Gauge.SkinType.GAUGE).build();
        newGauge.getSections().addAll(redSection1,redSection2,amberSection1,amberSection2,greenSection);
        return newGauge;
    }
    private int[] parseSectionData(InputTable data){
        int[] results = new int[6];
        parseOneSection(results,0,data.getRed().getText());
        parseOneSection(results,1,data.getAmber().getText());
        parseOneSection(results,2,data.getGreen().getText());
        return results;
    }
    private void parseOneSection(int[]results, int index,String text ){
        String[] textArray = text.split(",");
        if (textArray.length == 2){
            try {
                results[index] = Integer.parseInt(textArray[0]);
                results[results.length-1-index] = Integer.parseInt(textArray[1]);
            } catch(Exception e) {
                results[index] = 0;
                results[results.length-1-index] = 0;
            }
        } else {
            results[index] = 0;
            results[results.length-1-index] = 0;
        }
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
        updateTimeLabel();
        updateEventBox();
    }

    //Updates time label in FX thread
    private void updateTimeLabel(){
        Platform.runLater(() -> timeLabel.setText("Time Elapsed: " + (currentStep + mu) * 5 +"s"));
    }

    private void updateEventBox(){
        float currentTime = (currentStep+mu)*5;
        while (eventLog.get(eventIndex).getTime() <= currentTime){
            eventBox.getItems().add(new eventData(eventLog.get(eventIndex).getTime(),eventLog.get(eventIndex).getEvent()));
            eventIndex++;
        }
    }

    private void rebuildEventLog(){
        eventBox.getItems().clear();
        eventIndex = 0;
        updateEventBox();
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
            TextField red = new TextField();
            TextField amber = new TextField();
            TextField green = new TextField();
            tv.getItems().add(new InputTable(item,typePicker,min,max,red,amber,green));
        }
    }

    public class eventData {
        private int time;
        private String event;

        public eventData(){
        }
        public eventData(int time, String event) {
            this.time = time;
            this.event = event;
        }

        public int getTime(){
            return time;
        }

        public String getEvent(){
            return event;
        }

    }

    //Setup for dashboard JavaFX scene
    private Scene getDashboardScene()
    {
        dataArray = fillDataArray(selectedHeaderTitles, dataReader);
        BorderPane bp = new BorderPane();
        GridPane gp = new GridPane();
        HBox midHBox = new HBox();
        HBox topHBox = new HBox();
        eventBox = new TableView<>();
        midHBox.getStylesheets().add("sample/stylesheet/styling.css");
        TableColumn<String, eventData> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.getStyleClass().add("table-heads");
        TableColumn<String, eventData> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        eventCol.getStyleClass().add("table-heads");
        eventBox.getColumns().add(timeCol);
        eventBox.getColumns().add(eventCol);
        timeCol.prefWidthProperty().bind(eventBox.widthProperty().multiply(0.2));
        eventCol.prefWidthProperty().bind(eventBox.widthProperty().multiply(0.8));
        timeCol.setResizable(false);
        eventCol.setResizable(false);
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
        midHBox.getChildren().addAll(gp, eventBox);
        bp.setCenter(midHBox);
        VBox topVBox = new VBox();
        timeLabel = new Label();
        timeSlider = new Slider(0, dataArray[dataArray.length-1][0], 0);
        timeSlider.setMajorTickUnit(updateFrequency/1000f);
        timeSlider.setMinorTickCount((int)(dataArray[rowCount-2][0] * (1000f/updateFrequency)) + 1);
        timeSlider.setSnapToTicks(true);
        timeSlider.prefWidthProperty().bind(topHBox.widthProperty());
        timeSlider.setOnMousePressed((MouseEvent event) -> PauseTimer(playbackButton));
        timeSlider.setOnMouseReleased(event -> {
            int prevStep = currentStep;
            currentStep = (int) Math.floor(timeSlider.getValue())/5;
            mu = ((float)Math.floor(timeSlider.getValue()) - currentStep*5)/((float) (5000/updateFrequency));
            if (currentStep < prevStep){
                rebuildEventLog();
            }
            updateEventBox();
            updateTimeLabel();
        });
        timeSlider.setPadding(new Insets(15, 0, 0, 0));
        timeLabel.setStyle("-fx-text-fill: white");
        topVBox.getChildren().addAll(timeSlider, timeLabel);
        topHBox.getChildren().addAll(playbackButton, topVBox);
        topHBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        topHBox.setStyle("-fx-border-color: white; -fx-border-radius: 5;");
        bp.setTop(topHBox);
        initialiseGauges(selectedHeaderTitles, gp);
        return new Scene(bp, 960, 800);
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
                if (eventsLog){
                    eventReader = new BufferedReader(new FileReader(file));
                    openEventLog();
                }
                else {
                    dataReader = new BufferedReader(new FileReader(file));
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
            dataReader.mark(1);//reads the line and then goes back so that it can work out later which columns to read
            String commaSep = dataReader.readLine();
            dataReader.reset();
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
