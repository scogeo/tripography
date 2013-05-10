package com.tripography.vehicles;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gscott
 */
@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, ObjectId> {

    @Query(value = "{ 'a' : ?0 }")
    List<Vehicle> findByAccountId(ObjectId accountId);

}
