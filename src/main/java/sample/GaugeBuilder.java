package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.atomic.AtomicReference;


public class GaugeBuilder {
    private Gauge currentGauge;
    public Scene getGaugeBuilderScene(Scene defaultScene)
    {
        TilePane testPane = new TilePane();
        BorderPane borderPane = new BorderPane();
        BorderPane editSection = new BorderPane();
        editSection.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        editSection.setPadding(new Insets(5,15, 0 ,15));
        editSection.setStyle("-fx-border-color: white; -fx-border-radius: 10;");
        ComboBox<String> selectEditType = new ComboBox<>();
        ComboBox<String> selectGaugeType = new ComboBox<>();
        selectEditType.getItems().addAll("Colours","Other","Tick Marks");
        selectEditType.setValue("Colours");
        selectGaugeType.getItems().addAll("Default Gauge","Slim","Simple Section","Line Graph","Modern","Scientific Box","Quarter Gauge","Horizontal Half","Vertical Half");
        selectGaugeType.setValue("Default Gauge");
        selectGaugeType.setOnAction(e->updateCurrentGaugeSkin(PureFunctions.translateStringToGaugeType(selectGaugeType.getValue())));
        VBox colourBox = getColourBox();
        VBox tickMarkBox = getTickMarkBox();
        VBox otherBox = getOtherBox();
        VBox gaugeBox = new VBox(15);
        VBox selectBox = new VBox(0);new VBox(20);
        Label selectEditTypeLabel = new Label("Edit type:");
        selectEditTypeLabel.getStyleClass().add("headings-gb-label");
        Label gaugePickerTitle = new Label("Options");
        gaugePickerTitle.getStyleClass().add("headings-options");
        selectBox.getChildren().addAll(gaugePickerTitle, selectGaugeType,selectEditTypeLabel,selectEditType);
        editSection.setTop(selectBox);
        borderPane.setLeft(editSection);
        borderPane.setPadding(new Insets(0, 20, 0 ,20));
        borderPane.setCenter(gaugeBox);
        HBox footerBox = new HBox(5);
        Label filenameLabel = new Label("Filename:");
        filenameLabel.getStyleClass().add("headings-gb");
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(0, 0, 20, 0));
        TextField gaugeNameTextbox = new TextField();
        Button saveGaugeButton = new Button("Save Gauge");
        saveGaugeButton.getStyleClass().add("button-green-small");
        Button backButton = new Button("Back");
        backButton.getStyleClass().add("button-blue-small");
        backButton.setOnAction(e->((javafx.stage.Stage)backButton.getScene().getWindow()).setScene(defaultScene));
        footerBox.getChildren().addAll(filenameLabel,gaugeNameTextbox,saveGaugeButton, backButton);
        saveGaugeButton.setOnAction(e->saveCurrentGauge(gaugeNameTextbox.getText()));
        Label header = new Label("Build Gauges");
        header.getStyleClass().add("title-gb");
        HBox headerBox = new HBox();
        headerBox.minHeight(30);
        headerBox.setPadding(new Insets(20, 0, 0, 0));
        headerBox.getChildren().addAll(header);
        headerBox.setAlignment(Pos.CENTER);
        eu.hansolo.medusa.GaugeBuilder builder = eu.hansolo.medusa.GaugeBuilder.create().skinType(Gauge.SkinType.GAUGE);
        currentGauge = builder.decimals(0).maxValue(50).minValue(0).unit("unit").title("Title").build();
        currentGauge.setForegroundBaseColor(Color.WHITE);
        gaugeBox.getChildren().add(currentGauge);
        gaugeBox.setAlignment(Pos.CENTER);
        currentGauge.setPrefSize(800,800);
        borderPane.setTop(headerBox);
        borderPane.setId("background");
        borderPane.setBottom(footerBox);
        selectEditType.setOnAction(e->{switch (selectEditType.getValue()){
            case "Colours": editSection.setCenter(colourBox); break;
            case "Tick Marks": editSection.setCenter(tickMarkBox); break;
            case "Other": editSection.setCenter(otherBox); break;
        } });
        Event.fireEvent(selectEditType,new ActionEvent());//triggers it to show colours by default
        Scene scene = new Scene(borderPane, 960, 800);
        scene.getStylesheets().add("css/gaugeBuilder.css");
        return scene;
    }
    private void saveCurrentGauge(String filename){
        //TODO do
        throw new IllegalArgumentException("implement me");
    }
    private VBox getTickMarkBox(){
        VBox tickBox = new VBox(10);

        HBox tickLabelsVisibleHBox = new HBox(5);
        CheckBox tickLabelsVisibleCheckBox = new CheckBox();
        tickLabelsVisibleCheckBox.setSelected(true);
        Label tickLabelsVisibleLabel = new Label("Tick Labels Visible");
        tickLabelsVisibleLabel.getStyleClass().add("headings-gb-label");
        tickLabelsVisibleHBox.getChildren().addAll(tickLabelsVisibleLabel,tickLabelsVisibleCheckBox);
        tickLabelsVisibleCheckBox.setOnAction(e->currentGauge.setTickLabelsVisible(tickLabelsVisibleCheckBox.isSelected()));

        HBox tickLabelsInsideHBox = new HBox(5);
        CheckBox tickLabelsInsideCheckBox = new CheckBox();
        tickLabelsInsideCheckBox.setSelected(true);
        Label tickLabelsInsideLabel = new Label("Tick Labels Inside");
        tickLabelsInsideLabel.getStyleClass().add("headings-gb-label");
        tickLabelsInsideHBox.getChildren().addAll(tickLabelsInsideLabel,tickLabelsInsideCheckBox);
        tickLabelsInsideCheckBox.setOnAction(e->currentGauge.setTickLabelLocation(tickLabelsInsideCheckBox.isSelected() ? TickLabelLocation.INSIDE : TickLabelLocation.OUTSIDE));

        HBox majorTicksVisibleHBox = new HBox(5);
        CheckBox majorTicksVisibleCheckBox = new CheckBox();
        majorTicksVisibleCheckBox.setSelected(true);
        Label majorTicksVisibleLabel = new Label("Major Ticks Visible");
        majorTicksVisibleLabel.getStyleClass().add("headings-gb-label");
        majorTicksVisibleHBox.getChildren().addAll(majorTicksVisibleLabel,majorTicksVisibleCheckBox);
        majorTicksVisibleCheckBox.setOnAction(e->currentGauge.setMajorTickMarksVisible(majorTicksVisibleCheckBox.isSelected()));

        HBox mediumTicksVisibleHBox = new HBox(5);
        CheckBox mediumTicksVisibleCheckBox = new CheckBox();
        mediumTicksVisibleCheckBox.setSelected(true);
        Label mediumTicksVisibleLabel = new Label("Medium Ticks Visible");
        mediumTicksVisibleLabel.getStyleClass().add("headings-gb-label");
        mediumTicksVisibleHBox.getChildren().addAll(mediumTicksVisibleLabel,mediumTicksVisibleCheckBox);
        mediumTicksVisibleCheckBox.setOnAction(e->currentGauge.setMediumTickMarksVisible(mediumTicksVisibleCheckBox.isSelected()));

        HBox minorTicksVisibleHBox = new HBox(5);
        CheckBox minorTicksVisibleCheckBox = new CheckBox();
        minorTicksVisibleCheckBox.setSelected(true);
        Label minorTicksVisibleLabel = new Label("Minor Ticks Visible");
        minorTicksVisibleLabel.getStyleClass().add("headings-gb-label");
        minorTicksVisibleHBox.getChildren().addAll(minorTicksVisibleLabel,minorTicksVisibleCheckBox);
        minorTicksVisibleCheckBox.setOnAction(e->currentGauge.setMinorTickMarksVisible(minorTicksVisibleCheckBox.isSelected()));

        VBox tickColorVBox = new VBox(0);
        ColorPicker tickColorPicker = new ColorPicker();
        tickColorPicker.setValue(Color.BLACK);
        Label tickColorLabel = new Label("Medium Tick Colour");
        tickColorLabel.getStyleClass().add("headings-gb-label");
        tickColorVBox.getChildren().addAll(tickColorLabel,tickColorPicker);
        tickColorPicker.setOnAction(e->currentGauge.setMediumTickMarkColor(tickColorPicker.getValue()));

        VBox majorTickColorVBox = new VBox(0);
        ColorPicker majorTickColorPicker = new ColorPicker();
        majorTickColorPicker.setValue(Color.BLACK);
        Label majorTickColorLabel = new Label("Major Tick Colour");
        majorTickColorLabel.getStyleClass().add("headings-gb-label");
        majorTickColorVBox.getChildren().addAll(majorTickColorLabel,majorTickColorPicker);
        majorTickColorPicker.setOnAction(e->currentGauge.setMajorTickMarkColor(majorTickColorPicker.getValue()));

        VBox minorTickColorVBox = new VBox(0);
        ColorPicker minorTickColorPicker = new ColorPicker();
        minorTickColorPicker.setValue(Color.BLACK);
        Label minorTickColorLabel = new Label("Minor Tick Colour");
        minorTickColorLabel.getStyleClass().add("headings-gb-label");
        minorTickColorVBox.getChildren().addAll(minorTickColorLabel,minorTickColorPicker);
        minorTickColorPicker.setOnAction(e->currentGauge.setMinorTickMarkColor(minorTickColorPicker.getValue()));

        VBox majorTickShapeVBox = new VBox(0);
        ComboBox<String> majorTickShapeComboBox = new ComboBox<>();
        majorTickShapeComboBox.getItems().addAll("Line","Dot","Trapezoid","Triangle","Box","Tick Label","Pill");
        Label majorTickShapeLabel = new Label("Major Tick Shape");
        majorTickShapeLabel.getStyleClass().add("headings-gb-label");
        majorTickShapeVBox.getChildren().addAll(majorTickShapeLabel,majorTickShapeComboBox);
        majorTickShapeComboBox.setOnAction(e->currentGauge.setMajorTickMarkType(PureFunctions.translateStringToTickMarkType(majorTickShapeComboBox.getValue())));

        VBox mediumTickShapeVBox = new VBox(0);
        ComboBox<String> mediumTickShapeComboBox = new ComboBox<>();
        mediumTickShapeComboBox.getItems().addAll("Line","Dot","Trapezoid","Triangle","Box","Tick Label","Pill");
        Label mediumTickShapeLabel = new Label("Medium Tick Shape");
        mediumTickShapeLabel.getStyleClass().add("headings-gb-label");
        mediumTickShapeVBox.getChildren().addAll(mediumTickShapeLabel,mediumTickShapeComboBox);
        mediumTickShapeComboBox.setOnAction(e->currentGauge.setMediumTickMarkType(PureFunctions.translateStringToTickMarkType(mediumTickShapeComboBox.getValue())));

        VBox minorTickShapeVBox = new VBox(0);
        ComboBox<String> minorTickShapeComboBox = new ComboBox<>();
        minorTickShapeComboBox.getItems().addAll("Line","Dot","Trapezoid","Triangle","Box","Tick Label","Pill");
        Label minorTickShapeLabel = new Label("Minor Tick Shape");
        minorTickShapeLabel.getStyleClass().add("headings-gb-label");
        minorTickShapeVBox.getChildren().addAll(minorTickShapeLabel,minorTickShapeComboBox);
        minorTickShapeComboBox.setOnAction(e->currentGauge.setMinorTickMarkType(PureFunctions.translateStringToTickMarkType(minorTickShapeComboBox.getValue())));

        AtomicReference<String> oldMajorWidth = new AtomicReference<>("");
        VBox majorTickWidthVBox = new VBox(0);
        Label majorTickWidthLabel = new Label("Major Width Factor (0-1)");
        majorTickWidthLabel.getStyleClass().add("headings-gb-label");
        TextField majorTickWidthText = new TextField();
        majorTickWidthText.setOnMouseClicked(e -> oldMajorWidth.set(majorTickWidthText.getText()));
        majorTickWidthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(majorTickWidthText,oldVal, oldMajorWidth.get())){currentGauge.setMajorTickMarkWidthFactor(Double.parseDouble(majorTickWidthText.getText()));}});
        majorTickWidthText.setOnAction(e->{if(validateTickFactor(majorTickWidthText,true,oldMajorWidth.get())){currentGauge.setMajorTickMarkWidthFactor(Double.parseDouble(majorTickWidthText.getText()));}oldMajorWidth.set(majorTickWidthText.getText());});
        majorTickWidthVBox.getChildren().addAll(majorTickWidthLabel,majorTickWidthText);

        AtomicReference<String> oldMinorWidth = new AtomicReference<>("");
        VBox minorTickWidthVBox = new VBox(0);
        Label minorTickWidthLabel = new Label("Minor Width Factor (0-1)");
        minorTickWidthLabel.getStyleClass().add("headings-gb-label");
        TextField minorTickWidthText = new TextField();
        minorTickWidthText.setOnMouseClicked(e -> oldMinorWidth.set(minorTickWidthText.getText()));
        minorTickWidthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(minorTickWidthText,oldVal, oldMinorWidth.get())){currentGauge.setMinorTickMarkWidthFactor(Double.parseDouble(minorTickWidthText.getText()));}});
        minorTickWidthText.setOnAction(e->{if(validateTickFactor(minorTickWidthText,true,oldMinorWidth.get())){currentGauge.setMinorTickMarkWidthFactor(Double.parseDouble(minorTickWidthText.getText()));}oldMinorWidth.set(minorTickWidthText.getText());});
        minorTickWidthVBox.getChildren().addAll(minorTickWidthLabel,minorTickWidthText);

        AtomicReference<String> oldMajorLength = new AtomicReference<>("");
        VBox majorTickLengthVBox = new VBox(0);
        Label majorTickLengthLabel = new Label("Major Length Factor (0-1)");
        majorTickLengthLabel.getStyleClass().add("headings-gb-label");
        TextField majorTickLengthText = new TextField();
        majorTickLengthText.setOnMouseClicked(e -> oldMajorLength.set(majorTickLengthText.getText()));
        majorTickLengthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(majorTickLengthText,oldVal, oldMajorLength.get())){currentGauge.setMajorTickMarkLengthFactor(Double.parseDouble(majorTickLengthText.getText()));}});
        majorTickLengthText.setOnAction(e->{if(validateTickFactor(majorTickLengthText,true,oldMajorLength.get())){currentGauge.setMajorTickMarkLengthFactor(Double.parseDouble(majorTickLengthText.getText()));}oldMajorLength.set(majorTickLengthText.getText());});
        majorTickLengthVBox.getChildren().addAll(majorTickLengthLabel,majorTickLengthText);

        AtomicReference<String> oldMediumLength = new AtomicReference<>("");
        VBox mediumTickLengthVBox = new VBox(0);
        Label mediumTickLengthLabel = new Label("Medium Length Factor (0-1)");
        mediumTickLengthLabel.getStyleClass().add("headings-gb-label");
        TextField mediumTickLengthText = new TextField();
        mediumTickLengthText.setOnMouseClicked(e -> oldMediumLength.set(mediumTickLengthText.getText()));
        mediumTickLengthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(mediumTickLengthText,oldVal, oldMediumLength.get())){currentGauge.setMediumTickMarkLengthFactor(Double.parseDouble(mediumTickLengthText.getText()));}});
        mediumTickLengthText.setOnAction(e->{if(validateTickFactor(mediumTickLengthText,true,oldMediumLength.get())){currentGauge.setMediumTickMarkLengthFactor(Double.parseDouble(mediumTickLengthText.getText()));}oldMediumLength.set(mediumTickLengthText.getText());});
        mediumTickLengthVBox.getChildren().addAll(mediumTickLengthLabel,mediumTickLengthText);

        AtomicReference<String> oldMinorLength = new AtomicReference<>("");
        VBox minorTickLengthVBox = new VBox(0);
        Label minorTickLengthLabel = new Label("Minor Length Factor (0-1)");
        minorTickLengthLabel.getStyleClass().add("headings-gb-label");
        TextField minorTickLengthText = new TextField();
        minorTickLengthText.setOnMouseClicked(e -> oldMinorLength.set(minorTickLengthText.getText()));
        minorTickLengthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(minorTickLengthText,oldVal, oldMinorLength.get())){currentGauge.setMinorTickMarkLengthFactor(Double.parseDouble(minorTickLengthText.getText()));}});
        minorTickLengthText.setOnAction(e->{if(validateTickFactor(minorTickLengthText,true,oldMinorLength.get())){currentGauge.setMinorTickMarkLengthFactor(Double.parseDouble(minorTickLengthText.getText()));}oldMinorLength.set(minorTickLengthText.getText());});
        minorTickLengthVBox.getChildren().addAll(minorTickLengthLabel,minorTickLengthText);

        AtomicReference<String> oldMediumWidth = new AtomicReference<>("");
        VBox mediumTickWidthVBox = new VBox(0);
        Label mediumTickWidthLabel = new Label("Medium Width Factor (0-1)");
        mediumTickWidthLabel.getStyleClass().add("headings-gb-label");
        TextField mediumTickWidthText = new TextField();
        mediumTickWidthText.setOnMouseClicked(e -> oldMediumWidth.set(mediumTickWidthText.getText()));
        mediumTickWidthText.focusedProperty().addListener((obs, oldVal, newVal) -> {if(validateTickFactor(mediumTickWidthText,oldVal, oldMediumWidth.get())){currentGauge.setMediumTickMarkWidthFactor(Double.parseDouble(mediumTickWidthText.getText()));}});
        mediumTickWidthText.setOnAction(e->{if(validateTickFactor(mediumTickWidthText,true,oldMediumWidth.get())){currentGauge.setMediumTickMarkWidthFactor(Double.parseDouble(mediumTickWidthText.getText()));}oldMediumWidth.set(mediumTickWidthText.getText());});
        mediumTickWidthVBox.getChildren().addAll(mediumTickWidthLabel,mediumTickWidthText);

        tickBox.getChildren().addAll(tickLabelsVisibleHBox,tickLabelsInsideHBox,majorTicksVisibleHBox,majorTickShapeVBox,majorTickColorVBox,majorTickWidthVBox,majorTickLengthVBox,mediumTicksVisibleHBox,mediumTickShapeVBox,tickColorVBox,mediumTickWidthVBox,mediumTickLengthVBox,minorTicksVisibleHBox,minorTickShapeVBox,minorTickColorVBox,minorTickWidthVBox,minorTickLengthVBox);
        return tickBox;
    }

    private VBox getOtherBox(){
        VBox otherBox = new VBox(10);

        VBox needleTypeVBox = new VBox(0);
        ComboBox<String> needleTypeComboBox = new ComboBox<>();
        needleTypeComboBox.getItems().addAll("Standard","Big","Fat","Scientific","Avionic","Variometer");
        Label needleTypeLabel = new Label("Needle Type");
        needleTypeLabel.getStyleClass().add("headings-gb-label");
        needleTypeVBox.getChildren().addAll(needleTypeLabel,needleTypeComboBox);
        needleTypeComboBox.setOnAction(e->currentGauge.setNeedleType(PureFunctions.translateStringToNeedleType(needleTypeComboBox.getValue())));

        VBox needleShapeVBox = new VBox(0);
        ComboBox<String> needleShapeComboBox = new ComboBox<>();
        needleShapeComboBox.getItems().addAll("Flat","Angled","Round");
        Label needleShapeLabel = new Label("Needle Shape");
        needleShapeLabel.getStyleClass().add("headings-gb-label");
        needleShapeVBox.getChildren().addAll(needleShapeLabel,needleShapeComboBox);
        needleShapeComboBox.setOnAction(e->currentGauge.setNeedleShape(PureFunctions.translateStringToNeedleShape(needleShapeComboBox.getValue())));

        VBox knobTypeVBox = new VBox(0);
        ComboBox<String> knobTypeComboBox = new ComboBox<>();
        knobTypeComboBox.getItems().addAll("Standard","Flat","Metal","Plain");
        Label knobTypeLabel = new Label("Knob Type");
        knobTypeLabel.getStyleClass().add("headings-gb-label");
        knobTypeVBox.getChildren().addAll(knobTypeLabel,knobTypeComboBox);
        knobTypeComboBox.setOnAction(e->currentGauge.setKnobType(PureFunctions.translateStringToknobType(knobTypeComboBox.getValue())));

        HBox ledVisibleHBox = new HBox(5);
        CheckBox ledVisibleCheckBox = new CheckBox();
        Label ledVisibleLabel = new Label("LED Visible");
        ledVisibleLabel.getStyleClass().add("headings-gb-label");
        ledVisibleHBox.getChildren().addAll(ledVisibleLabel,ledVisibleCheckBox);
        ledVisibleCheckBox.setOnAction(e->currentGauge.setLedVisible(ledVisibleCheckBox.isSelected()));

        HBox ledBlinkingHBox = new HBox(5);
        CheckBox ledBlinkingCheckBox = new CheckBox();
        Label ledBlinkingLabel = new Label("LED Blinking");
        ledBlinkingLabel.getStyleClass().add("headings-gb-label");
        ledBlinkingHBox.getChildren().addAll(ledBlinkingLabel,ledBlinkingCheckBox);
        ledBlinkingCheckBox.setOnAction(e->currentGauge.setLedBlinking(ledBlinkingCheckBox.isSelected()));

        HBox ledFlatHBox = new HBox(5);
        CheckBox ledFlatCheckBox = new CheckBox();
        Label ledFlatLabel = new Label("LED Flat");
        ledFlatLabel.getStyleClass().add("headings-gb-label");
        ledFlatHBox.getChildren().addAll(ledFlatLabel,ledFlatCheckBox);
        ledFlatCheckBox.setOnAction(e->currentGauge.setLedType(ledFlatCheckBox.isSelected() ? Gauge.LedType.FLAT : Gauge.LedType.STANDARD));

        VBox ledColorVBox = new VBox(0);
        ColorPicker ledColorPicker = new ColorPicker();
        ledColorPicker.setValue(Color.RED);
        Label ledColorLabel = new Label("LED Colour");
        ledColorLabel.getStyleClass().add("headings-gb-label");
        ledColorVBox.getChildren().addAll(ledColorLabel,ledColorPicker);
        ledColorPicker.setOnAction(e->currentGauge.setLedColor(ledColorPicker.getValue()));

        otherBox.getChildren().addAll(needleTypeVBox,needleShapeVBox,knobTypeVBox,ledVisibleHBox,ledBlinkingHBox,ledFlatHBox,ledColorVBox);
        return otherBox;

    }
    private boolean validateTickFactor(TextField tickBox,boolean oldVal,String oldValue){
        if (oldVal){
            try {
                double newV = Double.parseDouble(tickBox.getText());
                if (newV <= 1 && newV >= 0){
                    return true;
                } else {
                    tickBox.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                tickBox.setText(oldValue);
            }
        }
        return false;
    }
    private VBox getColourBox(){
        VBox colourBox = new VBox(10);
        AtomicReference<String> oldTextValue = new AtomicReference<>("");
        VBox needleColourVBox = new VBox(0);
        ColorPicker needleColourPicker = new ColorPicker();
        needleColourPicker.setValue(Color.BLACK);
        Label needleColourLabel = new Label("Needle Colour");
        needleColourLabel.getStyleClass().add("headings-gb-label");
        needleColourVBox.getChildren().addAll(needleColourLabel,needleColourPicker);
        needleColourPicker.setOnAction(e->currentGauge.setNeedleColor(needleColourPicker.getValue()));

        VBox backgroundPaintVBox = new VBox(0);
        ColorPicker backgroundPaintColorPicker = new ColorPicker();
        backgroundPaintColorPicker.setValue(Color.BLACK);
        Label backgroundPaintLabel = new Label("Backdrop Colour");
        backgroundPaintLabel.getStyleClass().add("headings-gb-label");
        backgroundPaintVBox.getChildren().addAll(backgroundPaintLabel,backgroundPaintColorPicker);
        backgroundPaintColorPicker.setOnAction(e->currentGauge.setBackgroundPaint(backgroundPaintColorPicker.getValue()));

        VBox titleColourBox = new VBox(0);
        ColorPicker titlePaintColorPicker = new ColorPicker();
        titlePaintColorPicker.setValue(Color.BLACK);
        Label titleLabel = new Label("Title Colour");
        titleLabel.getStyleClass().add("headings-gb-label");
        titleColourBox.getChildren().addAll(titleLabel,titlePaintColorPicker);
        titlePaintColorPicker.setOnAction(e->currentGauge.setTitleColor(titlePaintColorPicker.getValue()));

        VBox unitColourBox = new VBox(0);
        ColorPicker unitColorPicker = new ColorPicker();
        unitColorPicker.setValue(Color.BLACK);
        Label unitLabel = new Label("Unit Colour");
        unitLabel.getStyleClass().add("headings-gb-label");
        unitColourBox.getChildren().addAll(unitLabel,unitColorPicker);
        unitColorPicker.setOnAction(e->currentGauge.setUnitColor(unitColorPicker.getValue()));

        VBox knobColorVBox = new VBox(0);
        ColorPicker knobColorPicker = new ColorPicker();
        knobColorPicker.setValue(Color.BLACK);
        Label knobColorLabel = new Label("Knob Colour");
        knobColorLabel.getStyleClass().add("headings-gb-label");
        knobColorVBox.getChildren().addAll(knobColorLabel,knobColorPicker);
        knobColorPicker.setOnAction(e->currentGauge.setKnobColor(knobColorPicker.getValue()));

        VBox valueColorVBox = new VBox(0);
        ColorPicker valueColorPicker = new ColorPicker();
        valueColorPicker.setValue(Color.BLACK);
        Label valueColorLabel = new Label("Value Colour");
        valueColorLabel.getStyleClass().add("headings-gb-label");
        valueColorVBox.getChildren().addAll(valueColorLabel,valueColorPicker);
        valueColorPicker.setOnAction(e->currentGauge.setValueColor(valueColorPicker.getValue()));

        VBox tickLabelColorVBox = new VBox(0);
        ColorPicker tickLabelColorPicker = new ColorPicker();
        tickLabelColorPicker.setValue(Color.BLACK);
        Label tickLabelColorLabel = new Label("Tick Label Colour");
        tickLabelColorLabel.getStyleClass().add("headings-gb-label");
        tickLabelColorVBox.getChildren().addAll(tickLabelColorLabel,tickLabelColorPicker);
        tickLabelColorPicker.setOnAction(e->currentGauge.setTickLabelColor(tickLabelColorPicker.getValue()));

        VBox modernTickColourVBox = new VBox(0);
        ColorPicker modernTickColorPicker = new ColorPicker();
        modernTickColorPicker.setValue(Color.BLACK);
        Label modernTickColorLabel = new Label("Modern Skin Tick Colour");
        modernTickColorLabel.getStyleClass().add("headings-gb-label");
        modernTickColourVBox.getChildren().addAll(modernTickColorLabel,modernTickColorPicker);
        modernTickColorPicker.setOnAction(e->currentGauge.setTickMarkColor(modernTickColorPicker.getValue()));


        VBox borderColorVBox = new VBox(0);
        ColorPicker borderColorPicker = new ColorPicker();
        borderColorPicker.setValue(Color.BLACK);
        Label borderColorLabel = new Label("Border Colour");
        borderColorLabel.getStyleClass().add("headings-gb-label");
        Label borderWidthLabel = new Label("Border Width");
        borderWidthLabel.getStyleClass().add("headings-gb-label");
        borderWidthLabel.setPadding(new Insets(5,0,0,0));
        TextField borderWidthText = new TextField();
        borderWidthText.setOnMouseClicked(e -> oldTextValue.set(borderWidthText.getText()));
        borderWidthText.focusedProperty().addListener((obs, oldVal, newVal) -> validateAndUpdateWidth(borderWidthText,oldVal, oldTextValue.get()));
        borderWidthText.setOnAction(e->{validateAndUpdateWidth(borderWidthText,true,oldTextValue.get());oldTextValue.set(borderWidthText.getText());});
        borderColorVBox.getChildren().addAll(borderColorLabel,borderColorPicker,borderWidthLabel,borderWidthText);
        borderColorPicker.setOnAction(e->currentGauge.setBorderPaint(borderColorPicker.getValue()));
        colourBox.getChildren().addAll(backgroundPaintVBox,borderColorVBox,titleColourBox,valueColorVBox,unitColourBox,needleColourVBox,knobColorVBox,tickLabelColorVBox,modernTickColourVBox);

        return colourBox;

    }
    private void validateAndUpdateWidth(TextField widthBox,boolean oldVal,String oldValue){
        if (oldVal){
            try {
                double newV = Double.parseDouble(widthBox.getText());
                if (newV <= 50 && newV >= 0){
                    currentGauge.setBorderWidth(newV);
                } else {
                    widthBox.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                widthBox.setText(oldValue);
            }
        }
    }
    private void updateCurrentGaugeSkin(Gauge.SkinType skin){
        Color needleColour = currentGauge.getNeedleColor();
        Paint backgroundPaint = currentGauge.getBackgroundPaint();
        Paint borderColour = currentGauge.getBorderPaint();
        double borderWidth = currentGauge.getBorderWidth();
        Color titleColour = currentGauge.getTitleColor();
        Color unitColour = currentGauge.getUnitColor();
        Color majorTickColour = currentGauge.getMajorTickMarkColor();
        Color minorTickColour = currentGauge.getMinorTickMarkColor();
        Color mediumTickColour = currentGauge.getMediumTickMarkColor();
        Color valueColour = currentGauge.getValueColor();
        Color knobColour = currentGauge.getKnobColor();
        Color tickLabelColour = currentGauge.getTickLabelColor();
        Gauge.NeedleType needleType = currentGauge.getNeedleType();
        Gauge.NeedleShape needleShape = currentGauge.getNeedleShape();
        Gauge.KnobType knobType = currentGauge.getKnobType();
        boolean ledVisible = currentGauge.isLedVisible();
        boolean ledBlinking = currentGauge.isLedBlinking();
        boolean minorTicksVisible = currentGauge.getMinorTickMarksVisible();
        boolean mediumTicksVisible = currentGauge.getMediumTickMarksVisible();
        boolean majorTicksVisible = currentGauge.getMajorTickMarksVisible();
        Color modernTickColour = currentGauge.getTickMarkColor();
        Gauge.LedType ledType = currentGauge.getLedType();
        Color ledColour = currentGauge.getLedColor();
        double minorTickWidth = currentGauge.getMinorTickMarkWidthFactor();
        double minorTickLength = currentGauge.getMinorTickMarkLengthFactor();
        double mediumTickWidth = currentGauge.getMediumTickMarkWidthFactor();
        double mediumTickLength = currentGauge.getMediumTickMarkLengthFactor();
        double majorTickWidth = currentGauge.getMajorTickMarkWidthFactor();
        double majorTickLength = currentGauge.getMajorTickMarkLengthFactor();
        boolean tickLabelsVisible = currentGauge.getTickLabelsVisible();
        TickLabelLocation tickLabelLocation = currentGauge.getTickLabelLocation();
        currentGauge.setSkinType(skin);
        currentGauge.setNeedleColor(needleColour);
        currentGauge.setBackgroundPaint(backgroundPaint);
        currentGauge.setBorderPaint(borderColour);
        currentGauge.setBorderWidth(borderWidth);
        currentGauge.setTitleColor(titleColour);
        currentGauge.setUnitColor(unitColour);
        currentGauge.setMajorTickMarkColor(majorTickColour);
        currentGauge.setMinorTickMarkColor(minorTickColour);
        currentGauge.setMediumTickMarkColor(mediumTickColour);
        currentGauge.setKnobColor(knobColour);
        currentGauge.setValueColor(valueColour);
        currentGauge.setTickLabelColor(tickLabelColour);
        currentGauge.setNeedleType(needleType);
        currentGauge.setNeedleShape(needleShape);
        currentGauge.setKnobType(knobType);
        currentGauge.setLedVisible(ledVisible);
        currentGauge.setLedType(ledType);
        currentGauge.setLedBlinking(ledBlinking);
        currentGauge.setMajorTickMarksVisible(majorTicksVisible);
        currentGauge.setMinorTickMarksVisible(minorTicksVisible);
        currentGauge.setMediumTickMarksVisible(mediumTicksVisible);
        currentGauge.setTickMarkColor(modernTickColour);
        currentGauge.setMediumTickMarkLengthFactor(mediumTickLength);
        currentGauge.setMediumTickMarkWidthFactor(mediumTickWidth);
        currentGauge.setMajorTickMarkLengthFactor(majorTickLength);
        currentGauge.setMajorTickMarkWidthFactor(majorTickWidth);
        currentGauge.setMinorTickMarkWidthFactor(minorTickWidth);
        currentGauge.setMinorTickMarkLengthFactor(minorTickLength);
        currentGauge.setTickLabelsVisible(tickLabelsVisible);
        currentGauge.setTickLabelLocation(tickLabelLocation);
        currentGauge.addSection(new Section(0,20,Color.GREEN));
        currentGauge.addArea(new Section(20,40,Color.RED));
    }

}