package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveState {

    @JsonProperty("shift_state") private String shiftState;
    @JsonProperty("speed") private Double speed;
    @JsonProperty("latitude") private Double latitude;
    @JsonProperty("longitude") private Double longitude;
    @JsonProperty("heading") private Double heading;
    @JsonProperty("gps_as_of") private Integer gpsTimestamp;

    public String getShiftState() {
        return shiftState;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getHeading() {
        return heading;
    }

    public Integer getGpsTimestamp() {
        return gpsTimestamp;
    }

    @Override
    public String toString() {
        return "DriveState{" +
                "shiftState='" + shiftState + '\'' +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", heading=" + heading +
                ", gpsTimestamp=" + gpsTimestamp +
                '}';
    }
}
