package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeState {

    @JsonProperty("charging_state") private String chargingState;
    @JsonProperty("charge_to_max_range") private Boolean chargeToMaxRange;
    @JsonProperty("max_range_charge_counter") private Integer maxRangeChargeCount;
    @JsonProperty("fast_charger_present") private Boolean fastChargerPresent;
    @JsonProperty("battery_range") private Double batteryRange;
    @JsonProperty("est_battery_range") private Double estimatedBatteryRange;
    @JsonProperty("ideal_battery_range") private Double idealBatteryRange;
    @JsonProperty("battery_level") private Double batteryLevel;
    @JsonProperty("battery_current") private Double batteryCurrent;
    @JsonProperty("charge_starting_range") private Double chargeStartingRange;
    @JsonProperty("charge_starting_soc") private Double chargeStartingSOC;
    @JsonProperty("charger_voltage") private Double chargerVoltage;
    @JsonProperty("charger_pilot_current") private Double chargerPilotCurrent;
    @JsonProperty("charger_actual_current") private Double chargerActualCurrent;
    @JsonProperty("charger_power") private Double chargerPower;
    @JsonProperty("time_to_full_charge") private Double timeToFullCharge;
    @JsonProperty("charge_rate") private Double chargeRate;
    @JsonProperty("charge_port_door_open") private Boolean chargePortDoorOpen;

    public String getChargingState() {
        return chargingState;
    }

    public Boolean getChargeToMaxRange() {
        return chargeToMaxRange;
    }

    public Integer getMaxRangeChargeCount() {
        return maxRangeChargeCount;
    }

    public Boolean getFastChargerPresent() {
        return fastChargerPresent;
    }

    public Double getBatteryRange() {
        return batteryRange;
    }

    public Double getEstimatedBatteryRange() {
        return estimatedBatteryRange;
    }

    public Double getIdealBatteryRange() {
        return idealBatteryRange;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public Double getBatteryCurrent() {
        return batteryCurrent;
    }

    public Double getChargeStartingRange() {
        return chargeStartingRange;
    }

    public Double getChargeStartingSOC() {
        return chargeStartingSOC;
    }

    public Double getChargerVoltage() {
        return chargerVoltage;
    }

    public Double getChargerPilotCurrent() {
        return chargerPilotCurrent;
    }

    public Double getChargerActualCurrent() {
        return chargerActualCurrent;
    }

    public Double getChargerPower() {
        return chargerPower;
    }

    public Double getTimeToFullCharge() {
        return timeToFullCharge;
    }

    public Double getChargeRate() {
        return chargeRate;
    }

    public Boolean getChargePortDoorOpen() {
        return chargePortDoorOpen;
    }

    @Override
    public String toString() {
        return "ChargeState{" +
                "chargingState='" + chargingState + '\'' +
                ", chargeToMaxRange=" + chargeToMaxRange +
                ", maxRangeChargeCount=" + maxRangeChargeCount +
                ", fastChargerPresent=" + fastChargerPresent +
                ", batteryRange=" + batteryRange +
                ", estimatedBatteryRange=" + estimatedBatteryRange +
                ", idealBatteryRange=" + idealBatteryRange +
                ", batteryLevel=" + batteryLevel +
                ", batteryCurrent=" + batteryCurrent +
                ", chargeStartingRange=" + chargeStartingRange +
                ", chargeStartingSOC=" + chargeStartingSOC +
                ", chargerVoltage=" + chargerVoltage +
                ", chargerPilotCurrent=" + chargerPilotCurrent +
                ", chargerActualCurrent=" + chargerActualCurrent +
                ", chargerPower=" + chargerPower +
                ", timeToFullCharge=" + timeToFullCharge +
                ", chargeRate=" + chargeRate +
                ", chargePortDoorOpen=" + chargePortDoorOpen +
                '}';
    }
}
