package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleState {

    @JsonProperty("car_version") private String carVersion;
    @JsonProperty("locked") private Boolean isLocked;


    public String getCarVersion() {
        return carVersion;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    @Override
    public String toString() {
        return "VehicleState{" +
                "carVersion='" + carVersion + '\'' +
                ", isLocked=" + isLocked +
                '}';
    }
}
