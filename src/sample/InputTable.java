package sample;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class InputTable {
    String headerName;
    private ComboBox<String> options;
    private TextField min;
    private TextField max;

    public InputTable(String name, ComboBox<String> cb, TextField min, TextField max) {
        this.headerName = name;
        this.options = cb;
        this.min = min;
        this.max = max;
    }

    public void setHeaderName(String name){
        this.headerName = name;
    }
    public String getHeaderName(){
        return headerName;
    }
    public void setOptions(ComboBox<String> cb) {
        this.options = cb;
    }
    public ComboBox<String> getOptions(){
        return this.options;
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
    public String selectedValue() {return  options.getValue();}
}
