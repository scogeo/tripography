package com.rumbleware.tesla.api;

/**
 * @author gscott
 */
public class StreamData {

    private final long timestamp;
    private final Double speed;
    private final Double odometer;
    private final Double soc;
    private final Double heading;
    private final Double altitude;
    private final Double latitude;
    private final Double longitude;
    private final Double power;
    private final String shiftState;



    public StreamData(String[] values) {
        if (values.length != 10) {
            throw new IllegalArgumentException("Expected 10 values");
        }

        int index = 0;

        timestamp = Long.parseLong(values[index++]);
        speed = parseDouble(values[index++], 0.0);
        odometer = parseDouble(values[index++], null);
        soc = parseDouble(values[index++], null);
        altitude = parseDouble(values[index++], null);
        heading = parseDouble(values[index++], null);
        latitude = parseDouble(values[index++], null);
        longitude = parseDouble(values[index++], null);
        power = parseDouble(values[index++], 0.0);
        shiftState = values[index++];


    }

    private Double parseDouble(String value, Double defaultValue) {
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Double getOdometer() {
        return odometer;
    }

    @Override
    public String toString() {
        return "StreamData{" +
                "timestamp=" + timestamp +
                ", speed=" + speed +
                ", odometer=" + odometer +
                ", soc=" + soc +
                ", altitude=" + altitude +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", shiftState='" + shiftState + '\'' +
                '}';
    }
}
