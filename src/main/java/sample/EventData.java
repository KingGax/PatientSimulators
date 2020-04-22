package sample;

import javafx.scene.text.Text;

import java.io.Serializable;

public class EventData implements Serializable {
    private int time;
    private String event;
    transient private Text box;

    public EventData(int time, String event) {
        this.time = time;
        this.event = event;
        this.box = new Text(event);
        box.setWrappingWidth(300);
    }

    public Text getBox() {
        return box;
    }

    public void setBox(Text box) {
        this.box = box;
    }

    public int getTime(){
        return time;
    }

    public String getEvent(){
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
