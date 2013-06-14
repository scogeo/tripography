package com.tripography.telemetry;

import com.tripography.vehicles.Vehicle;

/**
 * @author gscott
 */
public interface VehicleTelemetryService {

    void startTrackingVehicle(Vehicle vehicle);

    void stopTrackingVehicle(Vehicle Vehicle);

    /**
     *
     * @return true if the service is running.
     */
    boolean isRunning();

    /**
     * Starts the service if it has been previously stopped.
     */
    void startService();

    /**
     * Stops the service.
     */
    void stopService();

}
