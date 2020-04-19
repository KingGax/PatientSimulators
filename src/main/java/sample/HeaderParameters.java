package sample;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.Serializable;

public class HeaderParameters implements Serializable {
    private String headerName;
    private String gaugeType;
    private String min;
    private String max;
    private String amber;
    private String green;
    public HeaderParameters(InputTable x){
        headerName = x.headerName;
        gaugeType = x.getOptions().getValue();
        min = x.getMin().getText();
        max = x.getMax().getText();
        amber = x.getAmber().getText();
        green = x.getGreen().getText();
    }

    public String getAmber() {
        return amber;
    }

    public String getGaugeType() {
        return gaugeType;
    }

    public String getGreen() {
        return green;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public String getHeaderName() {
        return headerName;
    }
}
