package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.TickLabelLocation;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

//Handles interactions regarding custom gauges in Welcome scene
public class GaugeManager {
    private ArrayList<String> loadedGaugeNames;
    private ArrayList<SGauge> loadedGaugeParameters;

    public GaugeManager(ArrayList<String> loadedGaugeNames, ArrayList<SGauge> loadedGaugeParameters){
        this.loadedGaugeNames = loadedGaugeNames;
        this.loadedGaugeParameters = loadedGaugeParameters;
    }

    public ArrayList<String> getGaugeNames(){
        return loadedGaugeNames;
    }

    public void setGaugeNames(ArrayList<String> gaugeNames){
        this.loadedGaugeNames = gaugeNames;
    }

    public ArrayList<SGauge> getGaugeParameters(){
        return loadedGaugeParameters;
    }

    public void setGaugeParameters(ArrayList<SGauge> gaugeParameters){
        this.loadedGaugeParameters = gaugeParameters;
    }

    //Builds existing gauge given parameters and returns it
    Gauge buildGauge(Gauge.SkinType type, InputTable data, CSVData csvData){
        Gauge newGauge = null;
        double maxValue, minValue;
        int decimals = PureFunctions.getDecimals(data.headerName); //gets number of DP for header
        int tickLabelDecimals;
        tickLabelDecimals = decimals - 1;
        if (decimals > 1) tickLabelDecimals = decimals;
        try {
            maxValue = Double.parseDouble(data.getMax().getText());
        }catch(Exception e){
            maxValue = PureFunctions.getMaxValue(data.headerName);
        }
        try {
            minValue = Double.parseDouble(data.getMin().getText());
        }catch(Exception e){
            minValue = PureFunctions.getMinValue(data.headerName);
        }
        eu.hansolo.medusa.GaugeBuilder builder = GaugeBuilder.create();
        if (type == Gauge.SkinType.LEVEL){//cylinder
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).decimals(PureFunctions.getDecimals(data.headerName)).skinType(Gauge.SkinType.LEVEL).build();
            return newGauge;
        }
        if (type == Gauge.SkinType.SIMPLE_SECTION){
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).decimals(PureFunctions.getDecimals(data.headerName)).skinType(Gauge.SkinType.SIMPLE_SECTION).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setValueColor(Color.WHITE);
            newGauge.setTitleColor(Color.WHITE);
            newGauge.setUnitColor(Color.WHITE);
            newGauge.setBarBackgroundColor(Color.GRAY);
            newGauge.setAnimated(true);
            return newGauge;
        } else if (type == Gauge.SkinType.TILE_SPARK_LINE) {
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).minValue(minValue).maxValue(maxValue).unit(PureFunctions.getUnit(data.headerName)).autoScale(true).decimals(PureFunctions.getDecimals(data.headerName)).skinType(Gauge.SkinType.TILE_SPARK_LINE).build();
            newGauge.setBarColor(Color.rgb(77,208,225));
            newGauge.setBarBackgroundColor(Color.WHITE);
            newGauge.setAnimated(true);
            setupLineGraph(newGauge, data.headerName, csvData);//line graph animation setup
            return newGauge;
        }
        newGauge = builder.decimals(decimals).tickLabelDecimals(tickLabelDecimals).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).skinType(Gauge.SkinType.GAUGE).build();
        setDefaultGaugeCustomisation(newGauge);
        return newGauge;
    }

    //Build custom gauge given parameters
    Gauge buildCustomGauge(InputTable data, CSVData csvData){
        Gauge newGauge;
        double maxValue, minValue; //Extract min and max values from csvData
        try {
            maxValue = Integer.parseInt(data.getMax().getText());
        }catch(Exception e){
            maxValue = PureFunctions.getMaxValue(data.headerName);
        }
        try {
            minValue = Integer.parseInt(data.getMin().getText());
        }catch(Exception e){
            minValue = PureFunctions.getMinValue(data.headerName);
        }
        try { //Apply customisations to gauge
            Gauge customisations = loadedGaugeParameters.get(loadedGaugeNames.indexOf(data.getOptions().getValue())).getGauge();
            GaugeBuilder builder = GaugeBuilder.create().skinType(customisations.getSkinType());
            newGauge = builder.decimals(PureFunctions.getDecimals(data.headerName)).maxValue(maxValue).minValue(minValue).unit(PureFunctions.getUnit(data.headerName)).build();
            updateCurrentGaugeSkin(newGauge,customisations);
            newGauge.setLedOn(true);
            newGauge.calcAutoScale();
            if (newGauge.getSkinType() == Gauge.SkinType.TILE_SPARK_LINE){ //Checks if needs building as line graph
                setupLineGraph(newGauge,data.getHeaderName(), csvData);
            }
            return newGauge;
        } catch (Exception ef) { //If gauge type cannot be built, build as MODERN skin
            System.out.println(ef);
            return buildGauge(Gauge.SkinType.MODERN, data, csvData);
        }
    }

    //Changes gauge skin, copying previous skin properties over to new skin
    private void updateCurrentGaugeSkin(Gauge currentGauge, Gauge oldGauge ){
        Color needleColour = oldGauge.getNeedleColor();
        Paint backgroundPaint = oldGauge.getBackgroundPaint();
        Paint borderColour = oldGauge.getBorderPaint();
        double borderWidth = oldGauge.getBorderWidth();
        Color titleColour = oldGauge.getTitleColor();
        Color unitColour = oldGauge.getUnitColor();
        Color majorTickColour = oldGauge.getMajorTickMarkColor();
        Color minorTickColour = oldGauge.getMinorTickMarkColor();
        Color mediumTickColour = oldGauge.getMediumTickMarkColor();
        Color valueColour = oldGauge.getValueColor();
        Color knobColour = oldGauge.getKnobColor();
        Color tickLabelColour = oldGauge.getTickLabelColor();
        Gauge.NeedleType needleType = oldGauge.getNeedleType();
        Gauge.NeedleShape needleShape = oldGauge.getNeedleShape();
        Gauge.KnobType knobType = oldGauge.getKnobType();
        boolean ledVisible = oldGauge.isLedVisible();
        boolean ledBlinking = oldGauge.isLedBlinking();
        boolean minorTicksVisible = oldGauge.getMinorTickMarksVisible();
        boolean mediumTicksVisible = oldGauge.getMediumTickMarksVisible();
        boolean majorTicksVisible = oldGauge.getMajorTickMarksVisible();
        Color modernTickColour = oldGauge.getTickMarkColor();
        Gauge.LedType ledType = oldGauge.getLedType();
        Color ledColour = oldGauge.getLedColor();
        double minorTickWidth = oldGauge.getMinorTickMarkWidthFactor();
        double minorTickLength = oldGauge.getMinorTickMarkLengthFactor();
        double mediumTickWidth = oldGauge.getMediumTickMarkWidthFactor();
        double mediumTickLength = oldGauge.getMediumTickMarkLengthFactor();
        double majorTickWidth = oldGauge.getMajorTickMarkWidthFactor();
        double majorTickLength = oldGauge.getMajorTickMarkLengthFactor();
        boolean tickLabelsVisible = oldGauge.getTickLabelsVisible();
        TickLabelLocation tickLabelLocation = oldGauge.getTickLabelLocation();
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
        currentGauge.setLedColor(ledColour);
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
    }

    //Sets gauge properties to default styling
    private void setDefaultGaugeCustomisation(Gauge gauge){
        gauge.setBackgroundPaint(Color.WHITE);
        gauge.setMajorTickMarkLengthFactor(0.6);
        gauge.setMediumTickMarkLengthFactor(0.6);
        gauge.setMinorTickMarkLengthFactor(0.5);
        gauge.setBorderWidth(0.4);
        gauge.setBorderPaint(Color.BLACK);
        gauge.setLedVisible(true);
        gauge.setLedBlinking(true);
        gauge.setLedOn(true);
        gauge.setLedColor(Color.GREEN);
        gauge.setNeedleType(Gauge.NeedleType.AVIONIC);
        gauge.setKnobType(Gauge.KnobType.PLAIN);
        //gauge.setMinorTickMarksVisible(false);
    }

    //Performs line graph setup and sets properties according to parameters
    private void setupLineGraph(Gauge newGauge,String headerName, CSVData csvData){
        int numPoints = 150;
        double firstPoint = csvData.data[0][getNameIndex(headerName,csvData.headers)];
        newGauge.setValue(firstPoint);
        newGauge.setAveragingEnabled(true);
        newGauge.setAveragingPeriod(numPoints);
        newGauge.setTitle(headerName);
    }

    //Load gauge from .gauge file
    void loadInGauge(File file) {
        try {
            FileInputStream fis = new FileInputStream(file.getPath());
            ObjectInputStream dis = new ObjectInputStream(fis);
            SGauge gauge = (SGauge) dis.readObject();
            loadedGaugeNames.add(file.getName().split("\\.")[0]);
            loadedGaugeParameters.add(gauge);
        } catch (Exception e) {

        }
    }

    //Finds index of string in
    private int getNameIndex(String name, String[] headers){
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].compareTo(name) == 0){
                return i;
            }
        }
        return -1;
    }
}
