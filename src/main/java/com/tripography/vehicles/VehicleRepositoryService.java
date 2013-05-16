package com.tripography.vehicles;

import com.rumbleware.dao.BasicRepositoryService;
import com.rumbleware.maps.TimeZoneGeoCoder;
import com.tripography.providers.VehicleProvider;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * @author gscott
 */
@Service("vehicleService")
public class VehicleRepositoryService extends BasicRepositoryService<Vehicle, VehicleRepository> implements VehicleService {

    @Autowired
    public VehicleRepositoryService(VehicleRepository vehicleRepository) {
        super(Vehicle.class, vehicleRepository);
    }

    @Override
    public List<Vehicle> getVehiclesByAccount(String accountId) {
        return Collections.unmodifiableList(repository.findByAccountId(new ObjectId(accountId)));
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return repository.findAll();
    }

    @Override
    public List<Vehicle> addVehiclesFromProvider(String ownerId, VehicleProvider provider) {
        List<Vehicle> vehicles = provider.getVehicles();
        for (Vehicle vehicle : vehicles) {
            vehicle.setAccountId(ownerId);
            vehicle.setProviderId(provider.getId());
            create(vehicle);
        }
        return vehicles;
    }

    @Override
    public void updateTimeZoneUsingCurrentLocation(Vehicle vehicle) {
        TimeZoneGeoCoder tzCoder = new TimeZoneGeoCoder();


        //tzCoder.getTimeZone();

    }

    @Override
    public void updateTimeZoneUsingLocation(Vehicle vehicle, double latitude, double longitude) {
        TimeZoneGeoCoder tzCoder = new TimeZoneGeoCoder();
        TimeZone tz = tzCoder.getTimeZone(latitude, longitude, true);
        vehicle.setTimeZone(tz);
        update(vehicle);
    }
}
