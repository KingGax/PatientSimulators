package sample;

import eu.hansolo.medusa.Gauge;

import java.io.Serializable;

public class TransientGauge implements Serializable {
    private static final long serialVersionUID = 6128016096756071380L;
    private Gauge gauge = null;

    public TransientGauge(Gauge g) {
        this.gauge = g;
    }
    public Gauge getGauge(){
        return  gauge;
    }



}