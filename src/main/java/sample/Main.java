package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.util.Callback;
import jdk.jfr.Event;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    private BufferedReader dataReader;
    private int rowCount = 0;
    private List<Gauge> gauges;
    private float[][] dataArray; //Ignore this warning, dataArray to be used for gauges
    private ComboBox<String> headerPicker;
    private int currentStep = 0;
    private final int updateFrequency = 500; //Frequency of gauge updates in ms
    private float speedModifier = 1.0f;
    private float mu = 0.0f;
    private Timer eventTimer;
    private boolean timerStarted = false;
    private Slider timeSlider;
    private ArrayList<EventData> eventLog = new ArrayList<>();
    private boolean paused = false;
    private boolean eventsSelected = false;
    @FXML
    private TableView<InputTable> selectedHeaderTitles;
    private Popup popup;
    private ComboBox<String> typeChooserTemplate;
    private TableView eventBox;
    private Label timeLabel;
    private ComboBox<String> speedCB;
    private int eventIndex = 0;
    private Stage mainStage;
    private CSVData csvData;
    private String[] headerNames;
    //private ArrayList<String> loadedGaugeNames = new ArrayList<String>();
    //private ArrayList<SGauge> loadedGaugeParameters = new ArrayList<SGauge>();
    private GaugeManager gaugeManager;
    @Override

    //Initial scene setup
    public void start(Stage stage) {
        /*Setting up all elements on the setup page
        Title - Top header
        typeChooserTemplate - An item that will be cloned for every header added to the table
        fileChooser - The file browser object that will be passed around
        popup - global variable that will be styled and used as a default popup
        selectedHeaderTitles - The tableview that will store simulation details

        ## For Every button we setOnAction which calls these functions:
        tryAddHeader - Will add a new header if it doesn't already exist
        trySaveSimulation - Will create a popup make sure simulation is valid then save it to that filename
        tryLoadSimulation - Will open a filepicker to let you load a .sim file
        openFile - Opens data and events log, depending on the bool you give it
        addCustomGaugeOption - Opens the file chooser and tried to load in a custom gauge
         */

        gaugeManager = new GaugeManager(new ArrayList<String>(), new ArrayList<SGauge>());
        mainStage = stage;
        dataReader = null;
        popup = new Popup();
        popup.setAutoHide(true);
        typeChooserTemplate = new ComboBox<>();
        //setting style classes and initialising style classes
        typeChooserTemplate.getItems().addAll("Default Gauge","Simple Section","Line Graph","Cylinder");
        FileChooser fileChooser = new FileChooser();
        Label title = new Label("Welcome to Patient Simulators");
        title.getStyleClass().add("title");
        title.setPadding(new Insets(10, 20, 0, 20));
        title.setFont(Font.font("fantasy"));
        Label selectFileLabel = new Label("Please select either a Data File or an Event Log:");
        selectFileLabel.getStyleClass().add("select-file-label");
        selectFileLabel.setFont(Font.font("fantasy"));
        Button fileSelectorButton = new Button("Load Data File");
        fileSelectorButton.getStyleClass().add("button-blue");
        Button eventLogSelecter = new Button("Load Event Log");
        eventLogSelecter.getStyleClass().add("button-yellow");
        Button simulationButton = new Button("Run Simulation");
        simulationButton.getStyleClass().add("button-green");
        Button saveSimulationButton = new Button("Save Simulation");
        saveSimulationButton.getStyleClass().add("button-yellow");
        Button loadSimulationButton = new Button("Load Simulation File");
        loadSimulationButton.getStyleClass().add("button-blue");
        Button customGaugeButton = new Button("Add Custom Gauge");
        customGaugeButton.setOnAction(e -> addCustomGaugeOption(fileChooser,stage));
        customGaugeButton.getStyleClass().add("button-yellow");

        saveSimulationButton.setOnAction(e -> trySaveSimulation());
        loadSimulationButton.setOnAction(e -> tryLoadSimulation(fileChooser));
        fileSelectorButton.setOnAction(e -> openFile(fileChooser,stage,false));
        eventLogSelecter.setOnAction(e -> openFile(fileChooser,stage,true));

        //Setup layout on the page with gridPanes and hboxes
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
        fileSelectionBox.getChildren().addAll(fileSelectorButton,eventLogSelecter,customGaugeButton,loadSimulationButton);
        HBox chooseHeadersBox = new HBox(10);
        VBox inputHeadersBox = new VBox(20);
        inputHeadersBox.setPadding(new Insets(120, 0, 0, 0));
        inputHeadersBox.getChildren().addAll(headerPicker, addHeader);
        chooseHeadersBox.setAlignment(Pos.CENTER);
        VBox centreBox = new VBox(30);
        chooseHeadersBox.getChildren().addAll(inputHeadersBox, selectedHeaders);
        selectedHeaderTitles.prefWidthProperty().bind(chooseHeadersBox.widthProperty());
        HBox extraOptionsBox = new HBox(20);
        extraOptionsBox.setAlignment(Pos.CENTER);
        extraOptionsBox.getChildren().addAll(gaugeButton,saveSimulationButton);
        centreBox.getChildren().addAll(title, selectFileLabel, fileSelectionBox,chooseHeadersBox,extraOptionsBox,simulationButton);
        centreBox.setAlignment(Pos.CENTER);
        HBox header = new HBox(20);
        header.getChildren().addAll(title);
        header.setAlignment(Pos.CENTER);
        header.setMinHeight(30);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centreBox);
        BorderPane.setMargin(centreBox,new Insets(0,10,10,10));
        Scene welcome = new Scene(borderPane, 960, 720);
        welcome.getStylesheets().add("css/styling.css");
        stage.setScene(welcome);
        stage.setTitle("Patient Simulators");
        fileChooser.setTitle("Open Resource File");
        borderPane.setTop(header);
        borderPane.setId("background");

        simulationButton.setOnAction(e->tryRunSimulation(stage,welcome));

        //Setting up the main tableview columns
        TableColumn<InputTable, String> headerName = new TableColumn<>();
        headerName.setMinWidth(40);
        headerName.setText("Heading");
        headerName.getStyleClass().add("table-heads");
        headerName.setCellValueFactory(new PropertyValueFactory<>("headerName"));

        TableColumn<InputTable, ComboBox<String>> dataType = new TableColumn<>();
        dataType.setMinWidth(110);
        dataType.setCellValueFactory(new PropertyValueFactory<>("options"));
        dataType.getStyleClass().add("table-heads");

        TableColumn<InputTable, TextField> minVal = new TableColumn<>();
        minVal.setMinWidth(50);
        minVal.setCellValueFactory(new PropertyValueFactory<>("min"));
        minVal.setText("Min");
        minVal.getStyleClass().add("table-heads");

        TableColumn<InputTable, TextField> maxVal = new TableColumn<>();
        maxVal.setMinWidth(50);
        maxVal.setCellValueFactory(new PropertyValueFactory<>("max"));
        maxVal.setText("Max");
        maxVal.getStyleClass().add("table-heads");
        dataType.setText("Gauge Type");

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
        selectedHeaderTitles.getColumns().addAll(headerName, dataType,minVal,maxVal,amberSection,greenSection);

        stage.getIcons().add(new Image("res/Patient_Simulator_Logo.png"));
        stage.show();
    }

    private void addCustomGaugeOption(FileChooser fileChooser,Stage stage){//Opens file browser and loads a custom gauge into memory, adding it to the gauge[] array
        fileChooser.setTitle("Select Custom Gauge");
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(workingDirectory);
        File file = fileChooser.showOpenDialog(stage);//open file chooser
        if (file != null) {
            if (getFileExtension(file.getPath()).compareTo("gauge") == 0){//check it is a gauge file
                typeChooserTemplate.getItems().add(file.getName().split("\\.")[0]);//adds the name of the gauge name to the template
                if (selectedHeaderTitles.getItems().size()>0){
                    for (InputTable item:selectedHeaderTitles.getItems() ) {//adds the item to the comboboxes already inside the table
                        item.getOptions().getItems().add(file.getName().split("\\.")[0]);
                    }
                }
                gaugeManager.loadInGauge(file);//loads in the file and adds it to gauge[] array
            } else {
                showPopup("Please select a .gauge file");
            }
        }
    }

    private void tryLoadSimulation(FileChooser fileChooser){ //tries to load in a simulation
        try {
            fileChooser.setTitle("Select Simulation");
            File workingDirectory = new File(System.getProperty("user.dir"));
            fileChooser.setInitialDirectory(workingDirectory);
            File file = fileChooser.showOpenDialog(mainStage); //open directory
            if (getFileExtension(file.getPath()).compareTo("sim") == 0){//checks correct file
                FileInputStream fis = new FileInputStream(file);//reads in file
                ObjectInputStream dis = new ObjectInputStream(fis);
                SimulationParameters sim = (SimulationParameters) dis.readObject();
                csvData = sim.getCSVData();//extract data from sim into the program
                HeaderParameters[] headerData = sim.getHeaders();
                eventLog = sim.getEventLog();
                eventsSelected = (eventLog.size() != 0);//sets whether events log is empty or not
                gaugeManager.setGaugeNames(sim.getCustomNames());//sets list of custom names
                gaugeManager.setGaugeParameters(sim.getCustomGauges());//sets list of custom presets
                rowCount = csvData.data.length;
                updateTablesWithLoadedSimulation(csvData.headers,headerData);//populates table and combo box
            } else {
                showPopup("Please select a .sim file");
            }


        } catch (Exception ef) {
            System.out.println("we;re out "+ef);

        }

    }
    private void updateTablesWithLoadedSimulation(String[] allHeaders, HeaderParameters[] headerSettings){//Fills table when simulation loaded
        headerPicker.getItems().clear();
        selectedHeaderTitles.getItems().clear();
        for (String customGaugeName:gaugeManager.getGaugeNames() ) {//adds custom gauges to template
            typeChooserTemplate.getItems().add(customGaugeName);
        }
        for (String currentItem : allHeaders) {//fils out header selection box
            if (!currentItem.equals("PatientTime") && !currentItem.equals("SCETime") && !currentItem.equals("WorldTime")) {
                headerPicker.getItems().add(currentItem);
            }
        }
        for (HeaderParameters h : headerSettings) {//fills out the table with all saved headers
                ComboBox<String> typePicker = new ComboBox<>();
                typePicker.getItems().addAll(typeChooserTemplate.getItems());
                typePicker.getSelectionModel().select(h.getGaugeType());
                TextField min = newValidatingDoubleTextField(h.getMin());
                TextField max = newValidatingDoubleTextField(h.getMax());
                TextField amber = newValidatingRangeTextField(h.getAmber());
                TextField green = newValidatingRangeTextField(h.getGreen());
                selectedHeaderTitles.getItems().add(new InputTable(h.getHeaderName(),typePicker,min,max,amber,green));
        }
        headerPicker.getSelectionModel().selectFirst();
    }
    private boolean validateSimulationInputs(){//checks simulation can be run
        if (csvData == null){//checks data log loaded
            showPopup("Please load in a valid data array");
            return false;
        }
        if (selectedHeaderTitles.getItems().size() < 1){//checks a header selected
            showPopup("Please select at least one header");
            return false;
        }
        for (InputTable row: selectedHeaderTitles.getItems()) {//runs validation on every row
            try {
                double min, max, amber1, amber2, green1, green2;
                min = Double.parseDouble(row.getMin().getText());
                max = Double.parseDouble(row.getMax().getText());
                if (row.getAmber().getText().compareTo("") != 0){//if section is added
                    String[] amberVals = row.getAmber().getText().split(",");
                    amber1 = Double.parseDouble(amberVals[0]);
                    amber2 = Double.parseDouble(amberVals[1]);
                    if (amber1 > amber2){
                        showPopup("The first number in amber on row " + row.getHeaderName() + " should be smaller than the second");
                        return false;
                    }
                    if (amber1 > max || amber1 < min || amber2 > max || amber2 < min){
                        showPopup("Amber ranges do not fall within the gauge range on row " + row.getHeaderName());
                        return false;
                    }
                }
                if (row.getGreen().getText().compareTo("") != 0){//if section is added
                    String[] greenVals = row.getGreen().getText().split(",");
                    green1 = Double.parseDouble(greenVals[0]);
                    green2 = Double.parseDouble(greenVals[1]);
                    if (green1 > green2) {
                        showPopup("The first number in green on row " + row.getHeaderName() + " should be smaller than the second");
                        return false;
                    }
                    if (green1 > max || green1 < min || green2 > max || green2 < min){
                        showPopup("Green ranges do not fall within the gauge range on row " + row.getHeaderName());
                        return false;
                    }
                }

                if (max < min) {
                    showPopup("Min is larger than max on row " + row.getHeaderName());
                    return false;
                }

            } catch (Exception e) {
                showPopup("Inputs on row " + row.headerName + " are not of the right type");
                return false;
            }
        }
        return true;
    }
    private boolean checkFilenameValid(String filename){//checks if the file can be created
        for (String i: PureFunctions.UserForbiddenCharacters) {//checks for characters that would change how the file is saved, --> / . or \
            if (filename.contains(i)){
                showPopup("Filename cannot contain " + i);
                return false;
            }
        }
        File newFile = new File(filename);
        try {
            newFile.createNewFile();//creates file with filename
            if (newFile.exists()){//if location is ok it deletes
                newFile.delete();
                return true;//therefore file is valid
            } else { //if it does not exist windows has not let them create the file for whatever reason
                showPopup(filename + " is a forbidden filename");
                return  false;
            }
        } catch (Exception e) {//if it crashes it contains an invalid character, one that windows forbids
            for (String i: PureFunctions.WindowsForbiddenCharacters) {
                if (filename.contains(i)){
                    showPopup("Filename cannot contain " + i);
                    return false;
                }
            }
            showPopup(filename + " is a forbidden filename");
            return false;
        }
    }

    private void trySaveSimulation(){//attempts to save simulation presets
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Choose filename");
        textInput.getDialogPane().setContentText("Please choose a filename:");
        textInput.setHeaderText("Save file");
        textInput.setGraphic(null);
        Optional<String> result = textInput.showAndWait();//shows popup
        TextField input = textInput.getEditor();
        if (input.getText() != null && input.getText().length() != 0){//checks something non null has been input
            if(checkFilenameValid(input.getText())){//validates filename
                if (validateSimulationInputs()){//validates input
                    int numGauges = selectedHeaderTitles.getItems().size();
                    HeaderParameters[] headers = new HeaderParameters[numGauges];//sets up array to store gauge related values
                    int count = 0;
                    for (InputTable row: selectedHeaderTitles.getItems() ) {//turns rows into saveable objects
                        headers[count] = new HeaderParameters(row);
                        count++;
                    }
                    try {//package all information into saveable SimulationParameters class and saves
                        FileOutputStream fos = new FileOutputStream(result.get() + ".sim");
                        ObjectOutputStream dos = new ObjectOutputStream(fos);
                        dos.writeObject(new SimulationParameters(headers,csvData,eventLog, gaugeManager.getGaugeParameters(),gaugeManager.getGaugeNames()));
                        System.out.println("file created: " + result.get());
                        dos.flush();
                        fos.close();

                    } catch (Exception ef) {
                        ef.printStackTrace();
                    }

                }
            }
        } else {
            showPopup("Please enter a filename");
        }

    }




    //Checks simulation can be run and calls dashboard scene
    private void tryRunSimulation(Stage stage,Scene welcome)
    {
        if (validateSimulationInputs())
        {
            stage.setScene(getDashboardScene(welcome));//updates scene
            for (Gauge gauge : gauges){
                if (gauge.getSkinType() == Gauge.SkinType.TILE_SPARK_LINE){//this must be done while the lines are on screen in v11.5 due to an strange library bug
                    double firstValue = gauge.getCurrentValue();//this is filling the line graphs data array
                        for (int j = 0; j < gauge.getAveragingPeriod(); j++) {
                            gauge.setValue(firstValue);
                            gauge.setValue(firstValue * 0.99999);
                        }
                }
            }
            stage.setMaximized(true);
            paused = false;
        }
    }

    //Displays popup with given message
    private void showPopup(String message) {
        Label popupLabel = new Label(message);
        popupLabel.getStyleClass().add("pop-up-message");
        popupLabel.setMinWidth(mainStage.getScene().getWidth()); // set size of label
        popupLabel.setMinHeight(50);
        popupLabel.setAlignment(Pos.CENTER);
        popup.getContent().clear();
        popup.getContent().add(popupLabel);// add the label
        popup.show(mainStage, mainStage.getX() + 8, mainStage.getY() + 30);
    }

    //Programmatically creates gauges and stores them in global list, starts timer
    private void initialiseGauges(TableView<InputTable>selectedItems, GridPane pane){
        gauges = new ArrayList<>();
        int numGauges = selectedItems.getItems().size();
        int numColumns = 3; //arbitrary guesses atm this decides the layout of gauges on screen
        if (numGauges > 6) numColumns = 4;
        if (numGauges >=10) numColumns = 5;
        if (numGauges >=12) numColumns = 6;
        if (numGauges >= 21) numColumns = 7;
        if (numGauges >= 28) numColumns = 8;
        for (int i = 0; i < selectedItems.getItems().size(); i++){
            Gauge.SkinType type = PureFunctions.translateStringToGaugeType(selectedItems.getItems().get(i).selectedValue());
            Gauge gauge;
            if (type != null){//this checks whether the name exists already or not, if it exists in the system then it will create it. otherwise its custom
                gauge = gaugeManager.buildGauge(type,selectedItems.getItems().get(i), csvData);
            } else {
                gauge = gaugeManager.buildCustomGauge(selectedItems.getItems().get(i), csvData);
            }
            double[] sections = parseSectionData(selectedItems.getItems().get(i));//takes section data out of the values and adds red to it
            gauge.calcAutoScale();//calculates the number of ticks on the gauge
            addSections(sections,gauge);//adds sections
            gauge.setPrefSize(800,800);
            VBox gaugeBox = getTopicBox(selectedItems.getItems().get(i).headerName, Color.rgb(77,208,225), gauge);//puts the gauges into boxes that will be displayed on screen
            pane.add(gaugeBox,i %numColumns, i/numColumns);
            gauges.add(gauge);
        }
        pane.setPadding(new Insets(20));
        pane.setHgap(15);
        pane.setVgap(15);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        eventTimer = new Timer();
        TimerTask task = new EventTimerTask(this);//begin event timer for updating gauges
        eventTimer.scheduleAtFixedRate(task, 0,(int)(updateFrequency/speedModifier));
        timerStarted = true;
    }












    private void addSections(double[] sections,Gauge newGauge){ //sections laid out as [amber1,green1,green2,amber2]
        if (!(sections[0] == 0 && sections[1] == 0 && sections[2] == 0 && sections[3] == 0)) {//checks there is at least one section
            if (sections[0] == 0 && sections[3] == 0){//no amber section so add just green
                newGauge.addSection(new Section(sections[1],sections[2],Color.GREEN));
                Section redSection1 = new Section(newGauge.getMinValue(),sections[1],Color.RED);
                Section redSection2 = new Section(sections[2],newGauge.getMaxValue(),Color.RED);
                newGauge.getSections().addAll(redSection1,redSection2);
            } else if (sections[1] == 0 && sections[2] == 0) { //if no green section just add amber
                newGauge.addSection(new Section(sections[0],sections[3],Color.valueOf("#FFBF00")));
                Section redSection1 = new Section(newGauge.getMinValue(),sections[0],Color.RED);
                Section redSection2 = new Section(sections[3],newGauge.getMaxValue(),Color.RED);
                newGauge.getSections().addAll(redSection1,redSection2);
            } else { //this means there is green and amber sections so add green immediately
                newGauge.addSection(new Section(sections[1],sections[2],Color.GREEN));
                if (sections[0] < sections[1]){ // if there is amber below the green add lower amber section
                    if (sections[3] < sections[1]){//if amber is disjoint
                        newGauge.addSection(new Section(sections[0],sections[3],Color.valueOf("#FFBF00")));
                        Section redSection1 = new Section(newGauge.getMinValue(),sections[0],Color.RED);
                        Section redSection2 = new Section(sections[3],sections[1],Color.RED);
                        Section redSection3 = new Section(sections[2],newGauge.getMaxValue(),Color.RED);
                        newGauge.getSections().addAll(redSection1,redSection2,redSection3);
                    } else {//amber is not disjoint so add lower amber up to green
                        newGauge.addSection(new Section(sections[0], sections[1], Color.valueOf("#FFBF00")));
                        if (sections[2] < sections[3]) { //if amber also goes over the top add higher as expected and add red
                            newGauge.addSection(new Section(sections[2], sections[3], Color.valueOf("#FFBF00")));
                            Section redSection1 = new Section(newGauge.getMinValue(), sections[0], Color.RED);
                            Section redSection2 = new Section(sections[3], newGauge.getMaxValue(), Color.RED);
                            newGauge.getSections().addAll(redSection1, redSection2);
                        } else { //other end of amber is contained within the green so add red from green boundary and no other amber
                            Section redSection1 = new Section(newGauge.getMinValue(), sections[0], Color.RED);
                            Section redSection2 = new Section(sections[2], newGauge.getMaxValue(), Color.RED);
                            newGauge.getSections().addAll(redSection1, redSection2);
                        }
                    }
                } else { //there is no starting amber below green so first red goes up to green1
                    if (sections[0] > sections[2]){//if amber is disjoint
                        newGauge.addSection(new Section(sections[0],sections[3],Color.valueOf("#FFBF00")));
                        Section redSection1 = new Section(newGauge.getMinValue(),sections[1],Color.RED);
                        Section redSection2 = new Section(sections[2],sections[0],Color.RED);
                        Section redSection3 = new Section(sections[3],newGauge.getMaxValue(),Color.RED);
                        newGauge.getSections().addAll(redSection1,redSection2,redSection3);
                    } else if (sections[2] < sections[3]){ //this means a second amber is necessary so add that and red goes min-green1 and amber2-max
                        newGauge.addSection(new Section(sections[2],sections[3],Color.valueOf("#FFBF00")));
                        Section redSection1 = new Section(newGauge.getMinValue(),sections[1],Color.RED);
                        Section redSection2 = new Section(sections[3],newGauge.getMaxValue(),Color.RED);
                        newGauge.getSections().addAll(redSection1,redSection2);
                    } else { //other end of amber is contained within the green so add red from green boundary hence min-green1 and green2-max
                        Section redSection1 = new Section(newGauge.getMinValue(),sections[1],Color.RED);
                        Section redSection2 = new Section(sections[3],newGauge.getMaxValue(),Color.RED);
                        newGauge.getSections().addAll(redSection1,redSection2);
                    }
                }
            }
            newGauge.setSectionsVisible(true);
        }//if no sections, do not add red or any sections
    }

    private double[] parseSectionData(InputTable data){//extracts the numbers into the correct layout from the "x,x" strings
        double[] results = new double[4];
        parseOneSection(results,0,data.getAmber().getText());
        parseOneSection(results,1,data.getGreen().getText());
        return results;
    }

    private void parseOneSection(double[]results, int index,String text ){//turns a singular "x,x" into an array, if it is invalid sets the value as 0
        String[] textArray = text.split(",");
        if (textArray.length == 2){//this function weaves them into the results array as [amber,green,green2,amber2]
            try {
                results[index] = Double.parseDouble(textArray[0]);
                results[results.length-1-index] = Double.parseDouble(textArray[1]);
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
                if (gauges.get(x).getSkinType() == Gauge.SkinType.TILE_SPARK_LINE){
                    if (gaugeVal == gauges.get(x).getCurrentValue()){
                        Platform.runLater(() ->gauges.get(x).setValue(gaugeVal*1.00000000001));
                    } else {
                        Platform.runLater(() ->gauges.get(x).setValue(gaugeVal));
                    }
                } else {
                    Platform.runLater(() ->gauges.get(x).setValue(gaugeVal));
                }

                Platform.runLater(() -> updateBlinkingLight(gauges.get(x)));
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
        if (eventsSelected) updateEventBox();

    }

    private void updateBlinkingLight(Gauge gauge){//checks if the section colour has changed and if it has it changes the gauge colour
        for (Section section :gauge.getSections()) {
            if (gauge.getCurrentValue() >= section.getStart() && gauge.getCurrentValue() <= section.getStop()){
                if (gauge.getLedColor() != section.getColor()){
                    gauge.setLedColor(section.getColor());//for led gauges
                    gauge.setBarColor(section.getColor());//for line and cylinder
                }
                break;
            }
        }
    }

    //Updates time label in FX thread
    private void updateTimeLabel(){
        Platform.runLater(() -> timeLabel.setText("Time Elapsed: " + (currentStep + mu) * 5 +"s"));
    }

    //Updates event box
    private void updateEventBox(){
        float currentTime = (currentStep+mu)*5;
        while (eventLog.get(eventIndex).getTime() <= currentTime){
            eventBox.getItems().add(new EventData(eventLog.get(eventIndex).getTime(),eventLog.get(eventIndex).getEvent()));
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
        Rectangle bar = new Rectangle(200, 3);//blue bar
        bar.setArcWidth(6);
        bar.setArcHeight(6);
        bar.setFill(COLOR);

        Label label = new Label(TEXT);//header
        label.setTextFill(COLOR);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD,34));
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 0, 10, 0));

        VBox vBox = new VBox(bar, label, GAUGE);//final box
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
        if (!checkItemPresent(item,tv)&&(item!=null)){//if not already added
            ComboBox<String> typePicker = new ComboBox<>();
            typePicker.getItems().addAll(typeChooserTemplate.getItems());//duplicates the template
            typePicker.getSelectionModel().selectFirst();//selects default gauge
            //These next text boxes are each set up so that they prevent the user inputting invalid data
            TextField min = newValidatingDoubleTextField(Double.toString(PureFunctions.getMinValue(item)));
            TextField max = newValidatingDoubleTextField(Double.toString(PureFunctions.getMaxValue(item)));
            TextField amber = newValidatingRangeTextField(PureFunctions.getAmberRange(item));
            TextField green = newValidatingRangeTextField(PureFunctions.getGreenRange(item));
            tv.getItems().add(new InputTable(item,typePicker,min,max,amber,green));
        }
    }

    //creates an textbox that will auto validate itself when the user either presses enter or exits the focus, ensuring it always has valid inputs
    private TextField newValidatingRangeTextField(String initialValue){
        AtomicReference<String> oldTxt = new AtomicReference<>(initialValue);
        TextField tf = new TextField();
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateRange(tf,oldVal)){oldTxt.set(tf.getText());} else{tf.setText(oldTxt.get());}});
        tf.textProperty().setValue(initialValue);
        return tf;
    }

    //this is how it checks if a range is valid, and "" is valid to allow no section to be entered
    private boolean validateRange(TextField textBox,Boolean oldVal){
        if (oldVal){//this is whether the value has changed
            if (textBox.getText().compareTo("") == 0){
                return true;
            }
            String[] textArray = textBox.getText().split(",");
            if (textArray.length == 2){
                try {
                    double first = Double.parseDouble(textArray[0]);
                    double second = Double.parseDouble(textArray[1]);
                    if(first<=second){
                        return true;
                    } else {
                        showPopup("First value of a range must be smaller than the second");
                        return false;
                    }
                } catch(Exception e) {
                    showPopup("Range should be \"Decimal,Decimal\"");
                    return false;
                }
            }
            showPopup("Range should be \"Decimal,Decimal\"");
        }
        return false;
    }

    //this is the same as the range validating textbox just it calls a different validation function
    private TextField newValidatingDoubleTextField(String initialValue){
        AtomicReference<String> oldTxt = new AtomicReference<>(initialValue);
        TextField tf = new TextField();
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateDouble(tf.getText(),oldVal)){oldTxt.set(tf.getText());} else{tf.setText(oldTxt.get());}});
        tf.textProperty().setValue(initialValue);
        return tf;
    }

    //checks if a double is valid and outputs error if it is not
    private boolean validateDouble(String text, boolean oldVal){
        if (oldVal){
            try {
                double newV = Double.parseDouble(text);
                return true;
            } catch (NumberFormatException e) {
                showPopup("Error inputting decimal number");
            }
        }
        return false;
    }

    //Setup for dashboard JavaFX scene
    private Scene getDashboardScene(Scene welcome)
    {
        String[] selectedItemsArr = headersToStrings(selectedHeaderTitles);
        dataArray = extractDataFromCSVData(selectedItemsArr);
        BorderPane bp = new BorderPane();
        GridPane gaugeTiles = new GridPane();
        HBox midHBox = new HBox();
        HBox topHBox = new HBox();
        eventBox = new TableView<>();
        eventBox.setPlaceholder(new Label("No events log selected"));
        midHBox.getStylesheets().add("css/styling.css");
        TableColumn<EventData, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.getStyleClass().add("table-heads");
        TableColumn<EventData, String> eventCol = new TableColumn<>("Event");
        //eventCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getEvent()));
        eventCol.setCellValueFactory(new PropertyValueFactory<EventData,String>("event"));
        eventCol.getStyleClass().add("table-heads-left");
        eventBox.setMinWidth(380);
        timeCol.setMinWidth(80);
        eventCol.setMinWidth(300);
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        eventCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    text.getStyleClass().add("table-heads-left");
                    text.setWrappingWidth(eventCol.getWidth());
                    this.setWrapText(true);
                    setGraphic(text);
                }
            }
        });
        eventBox.getColumns().add(timeCol);
        eventBox.getColumns().add(eventCol);
        Button playbackButton = new Button();
        playbackButton.setMinWidth(48f);
        playbackButton.setMaxWidth(48f);
        playbackButton.setMinHeight(48f);
        playbackButton.setMaxHeight(48f);
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {eventTimer.cancel();currentStep = 0;mu = 0;eventIndex = 0;mainStage.setScene(welcome);});
        Image image = new Image(getClass().getClassLoader().getResource("res/PauseIcon.png").toExternalForm());
        ImageView imgView = new ImageView(image);
        imgView.fitWidthProperty().bind(playbackButton.widthProperty());
        imgView.fitHeightProperty().bind(playbackButton.heightProperty());
        playbackButton.setGraphic(imgView);
        playbackButton.setOnAction(e -> playBackHandler(playbackButton));
        midHBox.getChildren().addAll(gaugeTiles, eventBox);
        midHBox.setHgrow(gaugeTiles,Priority.ALWAYS);
        bp.setCenter(midHBox);
        VBox topVBox = new VBox();
        HBox timeHBox = new HBox();
        timeLabel = new Label();
        String[] speeds = {"0.25x", "0.5x","1x","1.5x","2x","3x","4x","5x","6x","8x"};
        speedCB = new ComboBox<>(FXCollections
                .observableArrayList(speeds));
        speedCB.setOnAction(event -> changePlaybackSpeed());
        speedCB.setValue("1x");
        speedModifier = 1;
        timeSlider = new Slider(0, dataArray[dataArray.length-1][0], 0);
        timeSlider.setMajorTickUnit(updateFrequency/1000f);
        timeSlider.setMinorTickCount((int)(dataArray[rowCount-2][0] * (1000f/updateFrequency)) + 1);
        timeSlider.setSnapToTicks(true);
        timeSlider.prefWidthProperty().bind(topHBox.widthProperty());
        timeSlider.setOnMousePressed((MouseEvent event) -> pauseTimer(playbackButton));
        timeSlider.setOnMouseReleased(event -> {
            int prevStep = currentStep;
            currentStep = (int) Math.floor(timeSlider.getValue())/5;
            mu = ((float)Math.floor(timeSlider.getValue()) - currentStep*5)/((float) (5000/updateFrequency));
            if (eventsSelected && currentStep < prevStep){
                if (currentStep < prevStep) rebuildEventLog();
                updateEventBox();
            }
            updateTimeLabel();
        });
        timeSlider.setPadding(new Insets(15, 0, 0, 0));
        timeLabel.setStyle("-fx-text-fill: white");
        timeHBox.setSpacing(15);
        timeHBox.getChildren().addAll(timeLabel, speedCB);
        topVBox.getChildren().addAll(timeSlider, timeHBox);
        topHBox.getChildren().addAll(backButton,playbackButton, topVBox);
        topHBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        topHBox.setStyle("-fx-border-color: white; -fx-border-radius: 5;");
        bp.setTop(topHBox);
        initialiseGauges(selectedHeaderTitles, gaugeTiles);
        return new Scene(bp, 1920, 1600);
    }

    //Handles stopping and starting of playback
    private void playBackHandler(Button b){
        if (!paused) {
            pauseTimer(b);
        }
        else {
            resumeTimer(b);
        }
    }

    //Pauses timer
    private void pauseTimer(Button b){
        if (!paused){
            eventTimer.cancel();
            Image image = new Image(getClass().getClassLoader().getResource("res/PlayIcon.png").toExternalForm());
            ImageView imgView = new ImageView(image);
            imgView.fitWidthProperty().bind(b.widthProperty());
            imgView.fitHeightProperty().bind(b.heightProperty());
            b.setGraphic(imgView);
            paused = true;
        }
    }

    //Resumes timer
    private void resumeTimer(Button b){
        if (paused) {
            eventTimer = new Timer();
            TimerTask task = new EventTimerTask(this);
            eventTimer.schedule(task, 0, (int)(updateFrequency/speedModifier));
            Image image = new Image(getClass().getClassLoader().getResource("res/PauseIcon.png").toExternalForm());
            ImageView imgView = new ImageView(image);
            imgView.fitWidthProperty().bind(b.widthProperty());
            imgView.fitHeightProperty().bind(b.heightProperty());
            b.setGraphic(imgView);
            paused = false;
        }
    }

    private void changePlaybackSpeed(){
        speedModifier = Float.parseFloat(speedCB.getValue().substring(0, speedCB.getValue().length()-1));
        if (!paused){
            eventTimer.cancel();
            eventTimer = new Timer();
            TimerTask task = new EventTimerTask(this);
            eventTimer.schedule(task, 0, (int)(updateFrequency/speedModifier));
        }
    }


    private String[] headersToStrings(TableView<InputTable> selectedItems){
        String[] selectedItemsArr = new String[selectedItems.getItems().size()+1];
        for (int i = 1; i < selectedItemsArr.length; i ++){
            selectedItemsArr[i] = selectedItems.getItems().get(i-1).headerName;
        }
        selectedItemsArr[0] = "PatientTime";
        return selectedItemsArr;
    }

    //Fills data array with values from global file corresponding to headers passed through selectedItems
    private float[][] fillDataArray(String[] selectedItemsArr, BufferedReader reader){//removes all spaces
        String[] selectedItemsCopy = new String[selectedItemsArr.length];
        for (int i = 0; i < selectedItemsArr.length; i++) {
            selectedItemsCopy[i] = selectedItemsArr[i].replace(" ","");
        }
        float[][] datArray = new float[rowCount-1][selectedItemsCopy.length];
        int[][] offSetArray = new int [selectedItemsCopy.length][2];
        int i = 0; int j = 0;//for error handling
        try {
            for (i=0; i < datArray.length+1; i++){
                String[] tempData = new String[1];
                try{
                    tempData = reader.readLine().replace(" ","").split(",");
                }
                catch (IOException ex){
                    showPopup("Error reading csv file");
                }
                if (i == 0){
                    for (j = 0; j < datArray[0].length; j++){
                        offSetArray[j][0] = j;
                        offSetArray[j][1] = Arrays.asList(tempData).indexOf(selectedItemsCopy[j]);
                    }
                } else {
                    for (j = 0; j < datArray[i-1].length; j++) {
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
        } catch (Exception e){
            showPopup("Error parsing csv, invalid input on row " + (i+1) + " of " + selectedItemsArr[j]);
            return null;
        }

        return datArray;
    }

    //Fills data array with values from global file corresponding to headers passed through selectedItems
    private float[][] extractDataFromCSVData(String[] selectedItemsArr){
        /*for (int j = 0; j < csvData.data.length; j++){
            System.out.println(csvData.headers[j] + ":  " + csvData.data[j][0] + " " + csvData.data[j][1] + " " + csvData.data[j][2]);
        }*/
        float[][] datArray = new float[rowCount-1][selectedItemsArr.length];
        int[][] offSetArray = new int [selectedItemsArr.length][2];
        for (int j = 0; j < datArray[0].length; j++){
            offSetArray[j][0] = j;
            offSetArray[j][1] = Arrays.asList(csvData.headers).indexOf(selectedItemsArr[j]);
        }
        for (int i=0; i < datArray.length; i++){
                for (int j = 0; j < datArray[i].length; j++) {
                    datArray[i][j] = csvData.data[i][offSetArray[j][1]];
                }
        }
        return datArray;
    }

    //Potentially no longer needed
    private float dateTimeToFloat(String date){
        float outVal;
        String pattern = "\\d\\d\\d\\d-\\d\\d-\\d\\d\\d\\d:\\d\\d:\\d\\d";
        if (!date.matches(pattern)){
            return -1f;
        }
        else{
            int years = Integer.parseInt(date.substring(0, 4));
            int months = Integer.parseInt(date.substring(5, 7));
            int days = Integer.parseInt(date.substring(8, 10));
            float time = timeToFloat(date.substring(10,18));
            if (time != -1f) {
                outVal = ((24 * (days + 28 * months + 365 * years)) * 3600f) + time;
            }
            else{
                outVal = -1f;
            }
            return outVal;
        }
    }

    //Converts time to float
    private float timeToFloat(String time){
        float outVal;
        String pattern = "\\d+\\d:\\d\\d:\\d\\d";
        if (!time.matches(pattern)){// || time.indexOf("-") != -1){ //r̝̮̥͔͎̱̜eg̭̺̰̪e̮̝͕̗̙̗ͅx̨
            return -1;
        } else {
            int hourLength = time.indexOf(':');
            int minuteStart = hourLength + 1;
            int secondStart = minuteStart + 3;
            int hours = Integer.parseInt(time.substring(0, hourLength));
            int minutes = Integer.parseInt(time.substring(minuteStart, minuteStart+2));
            int seconds = Integer.parseInt(time.substring(secondStart, secondStart+2));
            outVal = seconds + 60*(minutes+60*hours);
            return outVal;
        }
    }

    //gets the string after the last dot in a filename
    private String getFileExtension(String path)
    {
        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        if (i > p) {//makes sure its not a directory within the path that has a dot
            return path.substring(i+1);
        }
        return "";
    }

    //Prompts user to select a file and stores contents in BufferedReader reader
    private void openFile(FileChooser fileChooser, Stage stage, boolean eventsLog){
        File workingDirectory = new File(System.getProperty("user.dir"));
        if (eventsLog){//to help user know what to do
            fileChooser.setTitle("Select Events Log");
        } else {
            fileChooser.setTitle("Select Data Log");
        }
        fileChooser.setInitialDirectory(workingDirectory);
        File file = fileChooser.showOpenDialog(stage);//open file dialog
        if (file != null && (getFileExtension(file.getPath()).compareTo("csv") == 0)) {//check theyve chosen a csv file
            try {
                if (eventsLog){//for events logs
                    BufferedReader eventReader = new BufferedReader(new FileReader(file));
                    eventLog = openEventLog(eventReader);//call event log reader function
                }
                else {//for data
                    dataReader = new BufferedReader(new FileReader(file));//create a new reader
                    extractHeaders(file, dataReader);//put headers into the header picker box
                    fillCSVData(file, dataReader);//fill csvdata for saving
                    dataReader.close(); //must be closed and reopened to use later
                    dataReader = new BufferedReader(new FileReader(file));
                }
            } catch (IOException ex){
                System.out.println("IOException in opening file");
            }
        }
        else if (file != null){//displays error if file is not csv
            if (getFileExtension(file.getPath()).compareTo("csv")!=0)//
            {
                showPopup("Please choose a CSV file");
            }
        }
    }
    //creates a csvData object using fillDataArray with all headers selected
    private void fillCSVData(File file, BufferedReader dataReader){
        csvData = new CSVData(headerNames,fillDataArray(headerNames,dataReader));
    }
    //counts number of rows in the simulation
    private void countRows(File file){
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

    //takes headers of a file and fills combo box
    private void extractHeaders(File file, BufferedReader reader){
        try {
            reader.mark(1);//reads the line and then goes back so that it can work out later which columns to read
            String commaSep = reader.readLine();
            reader.reset();
            fillComboBoxFromReader(commaSep);//fills options combo box
        } catch (IOException ex) {
            System.out.println("IOException in printing file");
        }
        countRows(file);
    }

    //opens event log into an arrayList of event data objects
    private ArrayList<EventData> openEventLog(BufferedReader eventReader)
    {
        ArrayList<EventData> eLog = new ArrayList<>();
        String line;
        try{
            line = eventReader.readLine();
            while (line != null){
                if (line.compareTo("SCE Time, Message,") == 0) break; line = eventReader.readLine();
            } //reading to start of data
            while ((line = eventReader.readLine()) != null) {
                String[] item = line.split(",");                 //item[] is each row of csv file
                String[] b = item[0].split(":");                 //split the time into hours minutes and seconds
                int i = Integer.parseInt(b[0])*60*60 + Integer.parseInt(b[1])*60 + Integer.parseInt(b[2]);   //converting
                EventData p = new EventData(i,item[1]);
                eLog.add(p);
            }
            eventsSelected = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return eLog;
    }


    //Fills ComboBox items with appropriate contents from csv file
    private void fillComboBoxFromReader(String commaSepItems){
        headerPicker.getItems().clear();
        String[] commaSepItemsArr = commaSepItems.split(", ");
        headerNames = commaSepItemsArr;
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