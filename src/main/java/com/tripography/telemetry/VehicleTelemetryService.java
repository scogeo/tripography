package com.tripography.telemetry;

import com.tripography.vehicles.Vehicle;

/**
 * @author gscott
 */
public interface VehicleTelemetryService {

    void startTrackingVehicle(Vehicle vehicle);

    void stopTrackingVehicle(Vehicle Vehicle);

}
