package sample;

import com.sun.javafx.scene.control.CustomColorDialog;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import javax.swing.*;


public class GaugeBuilder {
    static Scene getGaugeBuilderScene()
    {
        BorderPane borderPane = new BorderPane();
        BorderPane editSection = new BorderPane();
        ComboBox<String> selectEditType = new ComboBox<>();
        selectEditType.getItems().addAll("Colours","Sections");

        VBox colourBox = new VBox(20);
        VBox sectionBox = new VBox(20);
        VBox gaugeBox = new VBox(15);
        sectionBox.getChildren().add(new Label("sEccTiOnZ"));
        editSection.setTop(selectEditType);
        borderPane.setLeft(editSection);
        borderPane.setCenter(gaugeBox);
        ColorPicker gaugeColour = new ColorPicker();
        gaugeColour.setTooltip(new Tooltip("test"));
        Label header = new Label("build gage");
        header.setAlignment(Pos.CENTER);
        colourBox.getChildren().addAll(gaugeColour);
        eu.hansolo.medusa.GaugeBuilder builder = eu.hansolo.medusa.GaugeBuilder.create().skinType(Gauge.SkinType.MODERN);
        Gauge newGauge = builder.decimals(0).maxValue(50).minValue(0).unit("unit").build();
        gaugeBox.getChildren().add(newGauge);
        gaugeColour.setOnAction(e->newGauge.setNeedleColor(gaugeColour.getValue()));
        borderPane.setTop(header);
        selectEditType.setOnAction(e->{switch (selectEditType.getValue()){
            case "Colours": editSection.setCenter(colourBox); break;
            case "Sections": editSection.setCenter(sectionBox); break;
        }
        });
        Scene scene = new Scene(borderPane, 960, 720);
        return scene;
    }


}
