package com.tripography.telemetry;

/**
 * @author gscott
 */
public class UnitConversion {

    public static final double MILES_TO_KILOMETERS = 1.6093;
    public static final double KILOMETERS_TO_MILES = 0.6214;

    public static double milesFromKilometers(double km) {
        return km * KILOMETERS_TO_MILES;
    }

    public static double kilometersFromMiles(double mi) {
        return mi * MILES_TO_KILOMETERS;
    }

    public static double roundToTenths(double value) {
        return (double)Math.round(value * 10) / 10;
    }
}
