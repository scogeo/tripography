package com.tripography.vehicles;

import com.rumbleware.dao.BasicService;
import com.tripography.providers.VehicleProvider;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author gscott
 */
public interface VehicleService extends BasicService<Vehicle> {

    public List<Vehicle> getVehiclesByAccount(String accountId);

    public List<Vehicle> addVehiclesFromProvider(String ownerId, VehicleProvider provider);

    /**
     * Temporary interface for telemetry for small list of vehicles.  Need to change as we add more.
     *
     * @return
     */
    public List<Vehicle> getAllVehicles();

    /**
     *
     */
    public void updateOdometerReading(Vehicle vehicle, OdometerReading reading);

    /**
     * Updates the vehicle's time zone using the vehicle's current location.  The vehicle will be saved to the
     * database after the update.
     *
     * @param vehicle
     */
    public void updateTimeZoneUsingCurrentLocation(Vehicle vehicle);

    public void updateTimeZoneUsingLocation(Vehicle vehicle, double latitude, double longitude);
}
