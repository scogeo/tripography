package com.tripography.telemetry;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author gscott
 */
@Repository
public interface DailyVehicleReadingRepository extends MongoRepository<DailyVehicleReading, ObjectId> {

    @Query("{ 'n' : { '$lt' : ?0 }, 'e' : true }")
    public List<DailyVehicleReading> findByNextReadingDateLessThan(Date date);

}
