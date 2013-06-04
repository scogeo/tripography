package com.tripography.vehicles;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author gscott
 */
public class OdometerReading {

    @Field("o")
    private double odometer;

    @Field("t")
    private Date timestamp;

    @PersistenceConstructor
    public OdometerReading() {
    }

    public OdometerReading(double odometer, long timestamp) {
        this.odometer = odometer;
        this.timestamp = new Date(timestamp);
    }

    public OdometerReading(double odometer, Date timestamp) {
        this.odometer = odometer;
        this.timestamp = timestamp;
    }

    public OdometerReading(double odometer) {
        this(odometer, new Date());
    }

    public double getOdometer() {
        return odometer;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "OdometerReading{" +
                "odometer=" + odometer +
                ", timestamp=" + timestamp +
                '}';
    }
}
