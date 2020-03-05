package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.skins.GaugeSkin;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Window;

import javax.swing.*;


public class GaugeBuilder {
    private Gauge currentGauge;
    private Color foregroundColour;
    public Scene getGaugeBuilderScene()
    {
        foregroundColour = Color.WHITE;
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
        gaugeBox.getChildren().add(currentGauge);
        currentGauge.setPrefSize(800,800);
        borderPane.setTop(header);
        selectEditType.setOnAction(e->{switch (selectEditType.getValue()){
            case "Colours": editSection.setCenter(colourBox); break;
            case "Sections": editSection.setCenter(sectionBox); break;
        } });
        Event.fireEvent(selectEditType,new ActionEvent());//triggers it to show colours by default
        Scene scene = new Scene(borderPane, 960, 720);
        return scene;
    }
    private VBox getColourBox(){
        VBox colourBox = new VBox(10);

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

        /*VBox backgroundPaintVBox = new VBox(0);
        ColorPicker backgroundPaintColorPicker = new ColorPicker();
        backgroundPaintColorPicker.setValue(Color.BLACK);
        Label backgroundPaintLabel = new Label("Backdrop Colour");
        backgroundPaintVBox.getChildren().addAll(backgroundPaintLabel,backgroundPaintColorPicker);
        backgroundPaintColorPicker.setOnAction(e->currentGauge.setBackgroundPaint(backgroundPaintColorPicker.getValue()));*/

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
        borderWidthLabel.setPadding(new Insets(5,0,0,0));//TODO add border width
        borderColorVBox.getChildren().addAll(borderColorLabel,borderColorPicker);
        borderColorPicker.setOnAction(e->currentGauge.setBorderPaint(borderColorPicker.getValue()));

        colourBox.getChildren().addAll(needleColourVBox,backgroundPaintVBox,foregroundColorVBox,borderColorVBox);
        return colourBox;
    }
    private void updateCurrentGaugeSkin(Gauge.SkinType skin){//TODO preserve customisation between swaps
        Color needleColour = currentGauge.getNeedleColor();
        Paint backgroundPaint = currentGauge.getBackgroundPaint();
        currentGauge.setSkinType(skin);
    }

}
