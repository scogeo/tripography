package com.tripography.providers;

import com.rumbleware.tesla.TeslaVehicle;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gscott
 */
@Repository
public interface VehicleProviderRepository  extends MongoRepository<TeslaVehicleProvider, ObjectId> {

    @Query(value = "{ 'account' : ?0 }")
    TeslaVehicleProvider findByAccountId(ObjectId accountId);

}
