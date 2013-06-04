package com.tripography.telemetry.events;

import com.tripography.telemetry.UnitConversion;
import com.tripography.vehicles.Vehicle;

import java.util.Calendar;

/**
 * @author gscott
 */
public class DailyUpdateEvent extends TelemetryEvent {

    public enum Units {
        MILES,
        KILOMETERS,
    }

    private final double dailyMiles;
    private final double dailyKilometers;
    private final Vehicle vehicle;
    private final Calendar currentDay;

    public DailyUpdateEvent(Vehicle vehicle, Calendar currentDay, double dailyMiles) {
        this.vehicle = vehicle;
        this.currentDay = currentDay;
        this.dailyMiles = UnitConversion.roundToTenths(dailyMiles);
        dailyKilometers = UnitConversion.roundToTenths(dailyMiles * UnitConversion.MILES_TO_KILOMETERS);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Calendar getCurrentDay() {
        return currentDay;
    }

    public double dailyDistance(Units units) {
        if (units == Units.MILES) {
            return dailyMiles;
        }
        else {
            return dailyKilometers;
        }
    }

    public boolean wasVehicleDriven() {
        return dailyMiles > 0.0;
    }


}
