package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickMarkType;
import eu.hansolo.medusa.skins.GaugeSkin;

class PureFunctions {
    static String skinTypeToString(Gauge.SkinType type) {
        switch (type) {
            case SLIM:
                return "Slim";
            case SIMPLE_SECTION:
                return "Simple Section";
            case TILE_SPARK_LINE:
                return "Line Graph";
            case MODERN:
                return "Modern";
            case AMP:
                return "Scientific Box";
            case QUARTER:
                return "Quarter Gauge";
            case HORIZONTAL:
                return "Horizontal Half";
            case VERTICAL:
                return "Vertical Half";
            case GAUGE:
                return "Default Gauge";
            default:
                return null;
        }
    }

    //Returns number of decimal places needed to display a given heading
    static int getDecimals(String val) {
        switch (val) {
            case "TBlood":
            case "TBody":
            case "CO":
            case "ICP":
                return 1;
            case "pH":
                return 2;
            default:
                return 0;
        }
    }

    static String getGreenRange(String val){
        switch (val) {
            case "HR":
                return "60,120";
            case "SBP":
                return "70,140";
            case "DBP":
                return "60,80";
            case "MAP":
                return "70,100";
            case "RR":
                return "7,30";
            case "VT":
                return "400,1000";
            case "SpO2":
                return "95,100";
            case "TBody":
            case "TBlood":
                return "35,38";
            case "CVP":
                return "-2,7";
            case "CO":
                return "5,7";
            case "ICP":
                return "0,10";
            case "PaO2":
                return "55,350";
            case "PaCO2":
                return "30,45";
            case "pH":
                return "7.40,7.47";
            case "Hb":
                return "12,16";
            case "Hct":
                return "30-50";
            case "PvO2":
            case "PvCO2":
            case "PACO2":
                return "35,45";
            case "PAO2":
                return "100,600";
            default:
                return "";
        }
    }

    static String getAmberRange(String val){
        switch (val) {
            case "HR":
                return "40,160";
            case "SBP":
                return "60,160";
            case "DBP":
                return "50,110";
            case "MAP":
                return "60,120";
            case "RR":
                return "6,35";
            case "VT":
                return "200,1200";
            case "SpO2":
                return "85,95";
            case "TBody":
            case "TBlood":
                return "33-39";
            case "CVP":
                return "-5,15";
            case "CO":
                return "4,9";
            case "ICP":
                return "0,20";
            case "PaO2":
                return "50,360";
            case "PaCO2":
                return "25,50";
            case "pH":
                return "7.3,7.5";
            case "Hb":
                return "10.5,18";
            case "Hct":
                return "25,55";
            case "PvO2":
            case "PvCO2":
                return "30,55";
            case "PACO2":
                return "30,50";
            case "PAO2":
                return "90,760";
            default:
                return "";
        }
    }

    static String getRedRange(String val){
        switch (val) {
            case "HR":
                return "0,220";
            case "SBP":
                return "0,250";
            case "DBP":
                return "0,200";
            case "MAP":
                return "0,150";
            case "RR":
                return "0,40";
            case "VT":
                return "0,2000";
            case "SpO2":
                return "0,100";
            case "TBody":
            case "TBlood":
                return "20,42";
            case "CVP":
                return "-7,35";
            case "CO":
                return "0,15";
            case "ICP":
                return "0,30";
            case "PaO2":
                return "0,400";
            case "PaCO2":
                return "0,60";
            case "pH":
                return "7.1-7.65";
            case "Hb":
                return "0,25";
            case "Hct":
            case "PACO2":
            case "PvO2":
            case "PvCO2":
                return "0,70";
            case "PAO2":
                return "0,800";
            default:
                return "";
        }
    }


    //Returns the maximum value a heading should ever have
    static double getMaxValue(String val) {
        switch (val) {
            case "HR":
                return 220;
            case "SBP":
                return 250;
            case "DBP":
                return 200;
            case "MAP":
                return 150;
            case "RR":
                return 40;
            case "VT":
                return 600;
            case "SpO2":
                return 100;
            case "TBody":
            case "TBlood":
                return 42;
            case "CVP":
                return 35;
            case "CO":
                return 15;
            case "ICP":
                return 30;
            case "PaO2":
                return 400;
            case "PaCO2":
                return 60;
            case "pH":
                return 7.65;
            case "Hb":
                return 25;
            case "Hct":
            case "PvO2":
            case "PvCO2":
            case "PACO2":
                return 70;
            case "PAO2":
                return 800;
            default:
                return 0;
        }
    }
    static double getMinValue(String val){
        switch (val){
            case "TBody":
            case "TBlood":
                return 20;
            case "CVP":
                return -7;
            case "PH":
                return 7.1;
            default:
                return 0;
        }
    }

    //Returns the unit for a given heading
    static String getUnit(String val) {
        switch (val) {
            case "HR":
            case "RR":
                return "BPM";
            case "SBP":
            case "DBP":
            case "MAP":
            case "ICP":
            case "PaO2":
            case "PaCO2":
            case "PAO2":
                return "mmHg";
            case "VT":
                return "ml";
            case "SpO2":
            case "Hct":
                return "%";
            case "TBody":
            case "TBlood":
                return "oC";
            case "CVP":
                return "-2,7";
            case "CO":
                return "l/min";
            case "Hb":
                return "g/dl";
            case "PvO2":
            case "PvCO2":
            case "PACO2":
                return "mmH";
            default:
                return "";
        }
    }

    static Gauge.SkinType translateStringToGaugeType(String title) {
        switch (title) {
            case "Slim":
                return Gauge.SkinType.SLIM;
            case "Simple Section":
                return Gauge.SkinType.SIMPLE_SECTION;
            case "Line Graph":
                return Gauge.SkinType.TILE_SPARK_LINE;
            case "Modern":
                return Gauge.SkinType.MODERN;
            case "Scientific Box":
                return Gauge.SkinType.AMP;
            case "Quarter Gauge":
                return Gauge.SkinType.QUARTER;
            case "Horizontal Half":
                return Gauge.SkinType.HORIZONTAL;
            case "Vertical Half":
                return Gauge.SkinType.VERTICAL;
            case "Default Gauge":
                return Gauge.SkinType.GAUGE;
            default:
                return null;
        }
    }
    static Gauge.NeedleType translateStringToNeedleType(String str){
        switch (str){
            case "Big":
                return Gauge.NeedleType.BIG;
            case "Fat":
                return Gauge.NeedleType.FAT;
            case "Standard":
                return  Gauge.NeedleType.STANDARD;
            case "Scientific":
                return Gauge.NeedleType.SCIENTIFIC;
            case "Avionic":
                return Gauge.NeedleType.AVIONIC;
            case "Variometer":
                return Gauge.NeedleType.VARIOMETER;
            default:
                return null;
        }
    }
    static Gauge.NeedleShape translateStringToNeedleShape(String str){
        switch (str){
            case "Flat":
                return Gauge.NeedleShape.FLAT;
            case "Angled":
                return Gauge.NeedleShape.ANGLED;
            case "Round":
                return  Gauge.NeedleShape.ROUND;
            default:
                return null;
        }
    }

    static TickMarkType translateStringToTickMarkType(String str){
        switch (str){
            case "Line":
                return TickMarkType.LINE;
            case "Dot":
                return TickMarkType.DOT;
            case "Trapezoid":
                return  TickMarkType.TRAPEZOID;
            case "Triangle":
                return TickMarkType.TRIANGLE;
            case "Box":
                return TickMarkType.BOX;
            case "Tick Label":
                return  TickMarkType.TICK_LABEL;
            case "Pill":
                return  TickMarkType.PILL;
            default:
                return null;
        }
    }

    public static Gauge.KnobType translateStringToknobType(String str) {
        switch (str){
            case "Flat":
                return Gauge.KnobType.FLAT;
            case "Metal":
                return Gauge.KnobType.METAL;
            case "Plain":
                return  Gauge.KnobType.PLAIN;
            case "Standard":
                return  Gauge.KnobType.STANDARD;
            default:
                return null;
        }
    }
}