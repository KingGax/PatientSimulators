package sample;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SectionTable {
    private ColorPicker color;
    private TextField min;
    private TextField max;

    public SectionTable(ColorPicker cp, TextField min, TextField max) {
        this.color = cp;
        this.min = min;
        this.max = max;
    }

    public void setColor(ColorPicker cb) {
        this.color = cb;
    }
    public ColorPicker getOptions(){
        return this.color;
    }
    public void setMin(TextField min){
        this.min = min;
    }
    public TextField getMin(){
        return this.min;
    }
    public void setMax(TextField max) {
        this.max = max;
    }
    public TextField getMax(){
        return this.max;
    }
    //public String selectedValue() {return  options.getValue();}
}
