package com.tripography.vehicles;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * @author gscott
 */
public interface VehicleRepository<VehicleDocument> extends MongoRepository<VehicleDocument, ObjectId> {

    @Query(value = "{ 'owner' : ?0 }")
    List<VehicleDocument> findByAccountId(String accountId);

}
