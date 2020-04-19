package sample;

import eu.hansolo.medusa.Gauge;

import java.io.Serializable;
import java.util.ArrayList;

public class SimulationParameters implements Serializable {
    private static final long serialVersionUID = 6128016096756071380L;
    private HeaderParameters[] headers;
    private  CSVData csvData;
    private ArrayList<EventData> eventLog;
    public SimulationParameters(HeaderParameters[] h, CSVData data,ArrayList<EventData> events) {
        headers = h;
        csvData = data;
        eventLog = events;
    }
    public CSVData getCsvData() {
        return csvData;
    }
    public HeaderParameters[] getHeaders() {
        return headers;
    }
    public ArrayList<EventData> getEventLog() {
        return eventLog;
    }
}

