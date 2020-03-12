package sample;

public class Pair {
    private String event;
    private int time;

    Pair(int i, String s) {
        time = i;
        event = s;
    }

    public String getEvent() {
        return event;
    }

    public int getTime() {
        return time;
    }

    public void setEvent(String string) {
        this.event = string;
    }

    public void setTime(int integer) {
        this.time = integer;
    }
}