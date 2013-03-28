package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimateState {

    @JsonProperty("inside_temp") private Double insideTemp;
    @JsonProperty("outside_temp") private Double outsideTemp;
    @JsonProperty("driver_temp_setting") private Double driverTempSetting;
    @JsonProperty("passenger_temp_setting") private Double passengerTempSetting;
    @JsonProperty("is_auto_conditioning_on") private Boolean isAutoConditioningOn;
    @JsonProperty("is_front_defroster_on") private Boolean isFrontDefrosterOn;
    @JsonProperty("is_rear_defroster_on") private Boolean isRearDefrosterOn;
    @JsonProperty("fan_status") private Double fanStatus;

    public Double getInsideTemp() {
        return insideTemp;
    }

    public Double getOutsideTemp() {
        return outsideTemp;
    }

    public Double getDriverTempSetting() {
        return driverTempSetting;
    }

    public Double getPassengerTempSetting() {
        return passengerTempSetting;
    }

    public Boolean getAutoConditioningOn() {
        return isAutoConditioningOn;
    }

    public Boolean getFrontDefrosterOn() {
        return isFrontDefrosterOn;
    }

    public Boolean getRearDefrosterOn() {
        return isRearDefrosterOn;
    }

    public Double getFanStatus() {
        return fanStatus;
    }

    @Override
    public String toString() {
        return "ClimateState{" +
                "insideTemp=" + insideTemp +
                ", outsideTemp=" + outsideTemp +
                ", driverTempSetting=" + driverTempSetting +
                ", passengerTempSetting=" + passengerTempSetting +
                ", isAutoConditioningOn=" + isAutoConditioningOn +
                ", isFrontDefrosterOn=" + isFrontDefrosterOn +
                ", isRearDefrosterOn=" + isRearDefrosterOn +
                ", fanStatus=" + fanStatus +
                '}';
    }
}
