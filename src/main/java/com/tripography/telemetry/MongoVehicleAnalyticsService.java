package com.tripography.telemetry;

import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author gscott
 */
@Service("vehicleAnalyticsService")
public class MongoVehicleAnalyticsService implements VehicleAnalyticsService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoVehicleAnalyticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DBObject dailyDistanceForVehicle(String vehicleId, String year) {
        String id = year + "/vehicle/" + vehicleId;
        return mongoTemplate.findOne(query(where("_id").is(id)), DBObject.class, "dailyDistance");
    }

    @Override
    public DBObject dailyDistanceForAccount(String accountId, String year) {
        String id = year + "/account/" + accountId;
        return mongoTemplate.findOne(query(where("_id").is(id)), DBObject.class, "dailyDistance");
    }

    @Override
    public DBObject dailyHistogramForVehicle(String vehicleId) {
        String id = "vehicle/" + vehicleId;
        return mongoTemplate.findOne(query(where("_id").is(id)), DBObject.class, "dailyHistogram");
    }

    @Override
    public DBObject dailyHistogramForAccount(String accountId) {
        String id = "account/" + accountId;
        return mongoTemplate.findOne(query(where("_id").is(id)), DBObject.class, "dailyHistogram");
    }
}
