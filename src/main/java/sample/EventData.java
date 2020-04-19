package sample;

import java.io.Serializable;

public class EventData implements Serializable {
    private int time;
    private String event;

    public EventData(){
    }
    public EventData(int time, String event) {
        this.time = time;
        this.event = event;
    }

    public int getTime(){
        return time;
    }

    public String getEvent(){
        return event;
    }

}
