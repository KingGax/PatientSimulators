package sample;
import javafx.scene.control.ComboBox;
public class InputTable {
    String headerName;
    private ComboBox<String> options;

    public InputTable(String name, ComboBox<String> cb) {
        this.headerName = name;
        this.options = cb;
    }

    public String getHeaderName(){
        return headerName;
    }
    public void setHeaderName(String name){
        this.headerName = name;
    }
    public void setOptions(ComboBox<String> cb) {
        this.options = cb;
    }
    public ComboBox<String> getOptions(){
        return this.options;
    }
}
