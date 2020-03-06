package sample;

import eu.hansolo.medusa.Gauge;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.atomic.AtomicReference;

import static javafx.scene.paint.Color.GREEN;


public class GaugeBuilder {
    private Gauge currentGauge;
    private Color foregroundColour;
    public Scene getGaugeBuilderScene()
    {
        TilePane testPane = new TilePane();
        foregroundColour = Color.BLACK;
        BorderPane borderPane = new BorderPane();
        BorderPane editSection = new BorderPane();
        ComboBox<String> selectEditType = new ComboBox<>();
        ComboBox<String> selectGaugeType = new ComboBox<>();
        selectEditType.getItems().addAll("Colours","Sections");
        selectEditType.setValue("Colours");
        selectGaugeType.getItems().addAll("Default Gauge","Slim","Simple Section","Line Graph","Modern","Scientific Box","Quarter Gauge","Horizontal Half","Vertical Half");
        selectGaugeType.setValue("Default Gauge");
        selectGaugeType.setOnAction(e->updateCurrentGaugeSkin(PureFunctions.translateStringToGaugeType(selectGaugeType.getValue())));
        VBox colourBox = getColourBox();
        VBox sectionBox = new VBox(20);
        VBox gaugeBox = new VBox(15);
        VBox selectBox = new VBox(0);
        selectBox.setPadding(new Insets(0,0,10,0));
        Label selectEditTypeLabel = new Label("Edit type:");
        selectEditTypeLabel.setPadding(new Insets(10,0,0,0));
        selectBox.getChildren().addAll(selectGaugeType,selectEditTypeLabel,selectEditType);
        sectionBox.getChildren().add(new Label("sEccTiOnZ"));
        editSection.setTop(selectBox);
        borderPane.setLeft(editSection);
        borderPane.setCenter(gaugeBox);

        Label header = new Label("build gage");
        header.setAlignment(Pos.CENTER);

        eu.hansolo.medusa.GaugeBuilder builder = eu.hansolo.medusa.GaugeBuilder.create().skinType(Gauge.SkinType.GAUGE);
        currentGauge = builder.decimals(0).maxValue(50).minValue(0).unit("unit").title("Title").build();
        currentGauge.setForegroundBaseColor(Color.BLACK);
        gaugeBox.getChildren().add(currentGauge);
        currentGauge.setPrefSize(800,800);
        borderPane.setTop(header);
        selectEditType.setOnAction(e->{switch (selectEditType.getValue()){
            case "Colours": editSection.setCenter(colourBox); break;
            case "Sections": editSection.setCenter(sectionBox); break;
        } });
        Event.fireEvent(selectEditType,new ActionEvent());//triggers it to show colours by default
        //testPane.getChildren().addAll(borderPane);
        Scene scene = new Scene(borderPane, 960, 720);
        return scene;
    }
    private VBox getColourBox(){
        VBox colourBox = new VBox(10);
        AtomicReference<String> oldTextValue = new AtomicReference<>("");
        VBox needleColourVBox = new VBox(0);
        ColorPicker needleColourPicker = new ColorPicker();
        needleColourPicker.setValue(Color.BLACK);
        Label needleColourLabel = new Label("Needle Colour");
        needleColourVBox.getChildren().addAll(needleColourLabel,needleColourPicker);
        needleColourPicker.setOnAction(e->currentGauge.setNeedleColor(needleColourPicker.getValue()));

        VBox backgroundPaintVBox = new VBox(0);
        ColorPicker backgroundPaintColorPicker = new ColorPicker();
        backgroundPaintColorPicker.setValue(Color.BLACK);
        Label backgroundPaintLabel = new Label("Backdrop Colour");
        backgroundPaintVBox.getChildren().addAll(backgroundPaintLabel,backgroundPaintColorPicker);
        backgroundPaintColorPicker.setOnAction(e->currentGauge.setBackgroundPaint(backgroundPaintColorPicker.getValue()));

        VBox titleColourBox = new VBox(0);
        ColorPicker titlePaintColorPicker = new ColorPicker();
        titlePaintColorPicker.setValue(Color.BLACK);
        Label titleLabel = new Label("Title Colour");
        titleColourBox.getChildren().addAll(titleLabel,titlePaintColorPicker);
        titlePaintColorPicker.setOnAction(e->currentGauge.setTitleColor(titlePaintColorPicker.getValue()));

        VBox unitColourBox = new VBox(0);
        ColorPicker unitColorPicker = new ColorPicker();
        unitColorPicker.setValue(Color.BLACK);
        Label unitLabel = new Label("Unit Colour");
        unitColourBox.getChildren().addAll(unitLabel,unitColorPicker);
        unitColorPicker.setOnAction(e->currentGauge.setUnitColor(unitColorPicker.getValue()));

        VBox knobColorVBox = new VBox(0);
        ColorPicker knobColorPicker = new ColorPicker();
        knobColorPicker.setValue(Color.BLACK);
        Label knobColorLabel = new Label("Knob Colour");
        knobColorVBox.getChildren().addAll(knobColorLabel,knobColorPicker);
        knobColorPicker.setOnAction(e->currentGauge.setKnobColor(knobColorPicker.getValue()));

        VBox tickColorVBox = new VBox(0);
        ColorPicker tickColorPicker = new ColorPicker();
        tickColorPicker.setValue(Color.BLACK);
        Label tickColorLabel = new Label("Medium Tick Colour");
        tickColorVBox.getChildren().addAll(tickColorLabel,tickColorPicker);
        tickColorPicker.setOnAction(e->currentGauge.setMediumTickMarkColor(tickColorPicker.getValue()));

        VBox majorTickColorVBox = new VBox(0);
        ColorPicker majorTickColorPicker = new ColorPicker();
        majorTickColorPicker.setValue(Color.BLACK);
        Label majorTickColorLabel = new Label("Major Tick Colour");
        majorTickColorVBox.getChildren().addAll(majorTickColorLabel,majorTickColorPicker);
        majorTickColorPicker.setOnAction(e->currentGauge.setMajorTickMarkColor(majorTickColorPicker.getValue()));

        VBox valueColorVBox = new VBox(0);
        ColorPicker valueColorPicker = new ColorPicker();
        valueColorPicker.setValue(Color.BLACK);
        Label valueColorLabel = new Label("Value Colour");
        valueColorVBox.getChildren().addAll(valueColorLabel,valueColorPicker);
        valueColorPicker.setOnAction(e->currentGauge.setValueColor(valueColorPicker.getValue()));

        VBox minorTickColorVBox = new VBox(0);
        ColorPicker minorTickColorPicker = new ColorPicker();
        minorTickColorPicker.setValue(Color.BLACK);
        Label minorTickColorLabel = new Label("Minor Tick Colour");
        minorTickColorVBox.getChildren().addAll(minorTickColorLabel,minorTickColorPicker);
        minorTickColorPicker.setOnAction(e->currentGauge.setMinorTickMarkColor(minorTickColorPicker.getValue()));

        VBox tickLabelColorVBox = new VBox(0);
        ColorPicker tickLabelColorPicker = new ColorPicker();
        tickLabelColorPicker.setValue(Color.BLACK);
        Label tickLabelColorLabel = new Label("Tick Label Colour");
        tickLabelColorVBox.getChildren().addAll(tickLabelColorLabel,tickLabelColorPicker);
        tickLabelColorPicker.setOnAction(e->currentGauge.setTickLabelColor(tickLabelColorPicker.getValue()));

        VBox foregroundColorVBox = new VBox(0);
        ColorPicker foregroundColorPicker = new ColorPicker();
        foregroundColorPicker.setValue(Color.BLACK);
        Label foregroundColorLabel = new Label("Foreground Colour");
        foregroundColorVBox.getChildren().addAll(foregroundColorLabel,foregroundColorPicker);
        foregroundColorPicker.setOnAction(e->{currentGauge.setForegroundBaseColor(foregroundColorPicker.getValue()); foregroundColour = foregroundColorPicker.getValue();});

        VBox borderColorVBox = new VBox(0);
        ColorPicker borderColorPicker = new ColorPicker();
        borderColorPicker.setValue(Color.BLACK);
        Label borderColorLabel = new Label("Border Colour");
        Label borderWidthLabel = new Label("Border Width");
        borderWidthLabel.setPadding(new Insets(5,0,0,0));
        TextField borderWidthText = new TextField();
        borderWidthText.setOnMouseClicked(e -> oldTextValue.set(borderWidthText.getText()));
        borderWidthText.focusedProperty().addListener((obs, oldVal, newVal) -> validateAndUpdateWidth(borderWidthText,oldVal, oldTextValue.get()));
        borderWidthText.setOnAction(e->{validateAndUpdateWidth(borderWidthText,true,oldTextValue.get());oldTextValue.set(borderWidthText.getText());});
        borderColorVBox.getChildren().addAll(borderColorLabel,borderColorPicker,borderWidthLabel,borderWidthText);
        borderColorPicker.setOnAction(e->currentGauge.setBorderPaint(borderColorPicker.getValue()));
        colourBox.getChildren().addAll(backgroundPaintVBox,borderColorVBox,titleColourBox,valueColorVBox,unitColourBox,needleColourVBox,knobColorVBox,foregroundColorVBox,tickLabelColorVBox,majorTickColorVBox,minorTickColorVBox,tickColorVBox);

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
        currentGauge.setSkinType(skin);
        currentGauge.setForegroundBaseColor(foregroundColour);
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

    }

}
