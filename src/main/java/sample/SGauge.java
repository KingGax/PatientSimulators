package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelLocation;
import javafx.scene.paint.Color;


import java.io.Serializable;
import java.util.Objects;

public class SGauge implements Serializable {
    private String skinType;
    private String needleType;
    private Gauge.NeedleShape needleShape;
    private String needleColour;
    private String backgroundPaint;
    private String borderColour;
    private String titleColour;
    private String unitColour;
    private String majorTickColour;
    private String minorTickColour;
    private String mediumTickColour;
    private String valueColour;
    private String knobColour;
    private String tickLabelColour;
    private double borderWidth;
    private Gauge.KnobType knobType;
    private boolean ledVisible ;
    private boolean ledBlinking ;
    private boolean minorTicksVisible ;
    private boolean mediumTicksVisible ;
    private boolean majorTicksVisible ;
    private String modernTickColour ;
    private Gauge.LedType ledType ;
    private String ledColour ;
    private double minorTickWidth;
    private double minorTickLength ;
    private double mediumTickWidth ;
    private double mediumTickLength ;
    private double majorTickWidth ;
    private double majorTickLength;
    private boolean tickLabelsVisible;
    private TickLabelLocation tickLabelLocation;




    public void setGauge(Gauge gauge) {
        skinType = PureFunctions.skinTypeToString(gauge.getSkinType());
        needleType = PureFunctions.needleTypeToString(gauge.getNeedleType());
        needleShape = gauge.getNeedleShape();
        needleColour = gauge.getNeedleColor().toString();
        backgroundPaint = gauge.getBackgroundPaint().toString();
        borderColour = gauge.getBorderPaint().toString();
        titleColour = gauge.getTitleColor().toString();
        unitColour = gauge.getUnitColor().toString();
        majorTickColour = gauge.getMajorTickMarkColor().toString();
        minorTickColour= gauge.getMinorTickMarkColor().toString();
        mediumTickColour = gauge.getMediumTickMarkColor().toString();
        valueColour = gauge.getValueColor().toString();
        knobColour = gauge.getKnobColor().toString();
        tickLabelColour = gauge.getTickLabelColor().toString();
        borderWidth = gauge.getBorderWidth();
        knobType = gauge.getKnobType();
        ledVisible = gauge.isLedVisible();
        ledBlinking = gauge.isLedBlinking();
        minorTicksVisible = gauge.getMinorTickMarksVisible();
        mediumTicksVisible = gauge.getMediumTickMarksVisible();
        majorTicksVisible = gauge.getMajorTickMarksVisible();
        modernTickColour = gauge.getTickMarkColor().toString();
        ledType = gauge.getLedType();
        ledColour = gauge.getLedColor().toString();
        minorTickWidth = gauge.getMinorTickMarkWidthFactor();
        minorTickLength = gauge.getMinorTickMarkLengthFactor();
        mediumTickWidth = gauge.getMediumTickMarkWidthFactor();
        mediumTickLength = gauge.getMediumTickMarkLengthFactor();
        majorTickWidth = gauge.getMajorTickMarkWidthFactor();
        majorTickLength = gauge.getMajorTickMarkLengthFactor();
        tickLabelsVisible = gauge.getTickLabelsVisible();
        tickLabelLocation = gauge.getTickLabelLocation();
    }
    public Gauge getGauge() {
        Gauge gauge = new Gauge();
        gauge.setSkinType(Objects.requireNonNull(PureFunctions.translateStringToGaugeType(skinType)));
        gauge.setNeedleType(Objects.requireNonNull(PureFunctions.translateStringToNeedleType(needleType)));
        gauge.setNeedleShape(needleShape);
        gauge.setNeedleColor(Color.valueOf(needleColour));
        gauge.setBackgroundPaint(Color.valueOf(backgroundPaint));
        gauge.setMajorTickMarkColor(Color.valueOf(majorTickColour));
        gauge.setMinorTickMarkColor(Color.valueOf(minorTickColour));
        gauge.setMediumTickMarkColor(Color.valueOf(mediumTickColour));
        gauge.setValueColor(Color.valueOf(valueColour));
        gauge.setKnobColor(Color.valueOf(knobColour));
        gauge.setTickLabelColor(Color.valueOf(tickLabelColour));
        gauge.setUnitColor(Color.valueOf(unitColour));
        gauge.setBorderPaint(Color.valueOf(borderColour));
        gauge.setTitleColor(Color.valueOf(titleColour));
        gauge.setBorderWidth(borderWidth);
        gauge.setKnobType(knobType);
        gauge.setLedVisible(ledVisible);
        gauge.setLedBlinking(ledBlinking);
        gauge.setTickMarkColor(Color.valueOf(modernTickColour));
        gauge.setLedType(ledType);
        gauge.setLedColor(Color.valueOf(ledColour));
        gauge.setMinorTickMarkWidthFactor(minorTickWidth);
        gauge.setMinorTickMarkLengthFactor(minorTickLength);
        gauge.setMediumTickMarkWidthFactor(mediumTickWidth);
        gauge.setMediumTickMarkLengthFactor(mediumTickLength);
        gauge.setMajorTickMarkWidthFactor(majorTickWidth );
        gauge.setMajorTickMarkLengthFactor(majorTickLength);
        gauge.setTickLabelsVisible(tickLabelsVisible);
        gauge.setTickLabelLocation(tickLabelLocation);
        gauge.setMinorTickMarksVisible(minorTicksVisible);
        gauge.setMediumTickMarksVisible(mediumTicksVisible);
        gauge.setMajorTickMarksVisible(majorTicksVisible);
        return gauge;
    }



}
