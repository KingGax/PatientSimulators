package sample;

import eu.hansolo.medusa.Gauge;

import java.io.Serializable;

public class TransientExample implements Serializable {
    private static final long serialVersionUID = 6128016096756071380L;
    private transient Gauge gauge = null;

    public TransientExample(Gauge g) {
        this.gauge = g;
    }
    public Gauge getGauge(){
        return  gauge;
    }



}