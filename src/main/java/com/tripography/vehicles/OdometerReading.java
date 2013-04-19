package com.tripography.vehicles;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author gscott
 */
public class OdometerReading {

    @Field("o")
    private double odometer;

    @Field("t")
    private long timestamp;

    public OdometerReading(double odometer, long timestamp) {
        this.odometer = odometer;
        this.timestamp = timestamp;
    }

    public OdometerReading(double odometer) {
        this(odometer, System.currentTimeMillis());
    }

    public double getOdometer() {
        return odometer;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
