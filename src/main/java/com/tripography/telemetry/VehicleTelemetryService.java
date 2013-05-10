package com.tripography.telemetry;

/**
 * @author gscott
 */
public interface VehicleTelemetryService {

    void startTrackingVehicle(String vehicleId);

    void stopTrackingVehicle(String vehicleId);

}
