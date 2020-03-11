package sample;

import javafx.scene.Scene;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.skins.GaugeSkin;
import javafx.application.Application;
import javafx.application.Platform;
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


public class GaugeDesigner {
    public Scene getGaugeScene(){
        HBox gaugeConfig = new HBox(20);
        VBox inputComboBoxes = new VBox(10);
        ComboBox<String> gaugeSkin = new ComboBox<>();
        gaugeSkin.getItems().addAll("Slim", "Simple Section", "Line Graph", "Default Gauge", "Scientific Box","Quarter Gauge","Horizontal Half","Vertical Half");
        return null;
    }

}
