package sample;

import java.io.Serializable;

//Helper for loading simulation files, contains relevant simulation data
public class CSVData implements Serializable {
    public String[] headers;
    public float[][] data;
    public CSVData(String[] head,float[][] dat){
        headers = head;
        data = dat;
    }

}
