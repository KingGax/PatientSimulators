package sample;

import java.util.Timer;
import java.util.TimerTask;

public class EventTimerTask extends TimerTask {
    private boolean hasStarted = false;
    final Main outer;
    EventTimerTask (Main outer){
        this.outer = outer;
    }
    @Override
    public void run(){
        this.hasStarted = true;
        outer.updateGauges();
    }

}
