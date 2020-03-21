package sample;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class InputTable {
    String headerName;
    private ComboBox<String> options;
    private TextField min;
    private TextField max;
    private TextField red;
    private TextField amber;
    private TextField green;

    public InputTable(String name, ComboBox<String> cb, TextField min, TextField max,TextField red,TextField amber,TextField green) {
        this.headerName = name;
        this.options = cb;
        this.min = min;
        this.max = max;
        this.red = red;
        this.amber = amber;
        this.green = green;
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
    public void setRed(TextField red){
        this.red = red;
    }
    public TextField getRed(){
        return this.red;
    }
    public void setAmber(TextField amber) {
        this.amber = amber;
    }
    public TextField getAmber(){
        return this.amber;
    }
    public void setGreen(TextField green) {
        this.green = green;
    }
    public TextField getGreen(){
        return this.green;
    }

    public String selectedValue() {return  options.getValue();}
}
