package com.tripography.vehicles;

import com.rumbleware.dao.BasicRepositoryService;
import com.rumbleware.maps.TimeZoneGeoCoder;
import com.rumbleware.tesla.api.TeslaPortal;
import com.tripography.providers.VehicleProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author gscott
 */
@Service("vehicleService")
public class VehicleRepositoryService extends BasicRepositoryService<Vehicle, VehicleRepository> implements VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRepositoryService.class);

    private TeslaPortal teslaPortal;

    @Autowired
    public VehicleRepositoryService(VehicleRepository vehicleRepository, TeslaPortal teslaPortal) {
        super(Vehicle.class, vehicleRepository);
        this.teslaPortal = teslaPortal;
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
    @Cacheable("vehicleCount")
    public long getVehicleCount() {
        return repository.count();
    }

    @Override
    @CacheEvict("vehicleCount")
    public List<Vehicle> addVehiclesFromProvider(String ownerId, VehicleProvider provider) {
        List<Vehicle> vehicles = provider.getVehicles(teslaPortal);
        for (Vehicle vehicle : vehicles) {
            vehicle.setAccountId(ownerId);
            vehicle.setProviderId(provider.getId());
            create(vehicle);
        }
        return vehicles;
    }

    @Override
    public void updateOdometerReading(Vehicle vehicle, OdometerReading reading) {
        mongo.updateFirst(query(where("_id").is(vehicle.getObjectId())),
                new Update().set("o", reading), Vehicle.class);
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
