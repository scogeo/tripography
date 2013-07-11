package com.tripography.telemetry;

import com.mongodb.DBObject;

import java.util.Map;

/**
 * @author gscott
 */
public interface VehicleAnalyticsService {


    DBObject dailyDistanceForAll(String year);

    DBObject dailyDistanceForVehicle(String vehicleId, String year);

    DBObject dailyDistanceForAccount(String accountId, String year);

    DBObject dailyHistogramForAll();

    DBObject dailyHistogramForVehicle(String vehicleId);

    DBObject dailyHistogramForAccount(String accountId);

}
