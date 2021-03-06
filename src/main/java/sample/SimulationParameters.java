package sample;

import eu.hansolo.medusa.Gauge;

import java.io.Serializable;
import java.util.ArrayList;

//Class for saving simulations
public class SimulationParameters implements Serializable {
    private static final long serialVersionUID = 6128016096756071380L;
    private HeaderParameters[] headers;
    private  CSVData csvData;
    private ArrayList<EventData> eventLog;
    private ArrayList<SGauge> customGauges;
    private ArrayList<String> customNames;
    public SimulationParameters(HeaderParameters[] headerParameters, CSVData data, ArrayList<EventData> events, ArrayList<SGauge> _customGauges,ArrayList<String> _customNames) {
        headers = headerParameters;
        csvData = data;
        eventLog = events;
        customGauges = _customGauges;
        customNames = _customNames;
    }
    public CSVData getCSVData() {
        return csvData;
    }
    public HeaderParameters[] getHeaders() {
        return headers;
    }
    public ArrayList<EventData> getEventLog() {
        return eventLog;
    }
    public ArrayList<String> getCustomNames() { return customNames; }
    public ArrayList<SGauge> getCustomGauges() { return customGauges; }

}

