package sample;

import java.io.Serializable;

public class CSVData implements Serializable {
    public String[] headers;
    public float[][] data;
    public CSVData(String[] head,float[][] dat){
        headers = head;
        data = dat;
    }

}
