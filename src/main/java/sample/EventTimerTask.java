package sample;

import java.util.TimerTask;

//Custom timer class
public class EventTimerTask extends TimerTask {
    private boolean started = false;
    final Main outer;
    EventTimerTask (Main outer){
        this.outer = outer;
    }
    @Override
    public void run(){
        this.started = true;
        outer.updateGauges();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean hasStarted) {
        this.started = hasStarted;
    }

}
