package com.tripography.telemetry.analytics.cleaners;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.tripography.telemetry.VehicleAnalyticsService;
import com.tripography.telemetry.analytics.DailyDistance;
import com.tripography.vehicles.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * Cleans up and migrates DailyDistance data.
 *
 * Basic cleaning is accomplished as follows:
 *
 * 1. All daily objects are migrated to the latest version.
 * 2. Documents are checked for internal consistency of aggregates (and optionally repaired)
 * 3. Group aggregates are rebuilt from the vehicle data.
 *
 * @author gscott
 */
public class DailyDistanceCleaner implements DailyDistance{

    private static final Logger logger = LoggerFactory.getLogger(DailyDistanceCleaner.class);


    private VehicleService vehicleService;

    private VehicleAnalyticsService analyticsService;

    private MongoOperations mongo;

    public DailyDistanceCleaner(VehicleService vehicleService, VehicleAnalyticsService analyticsService, MongoOperations mongo) {
        this.vehicleService = vehicleService;
        this.analyticsService = analyticsService;
        this.mongo = mongo;
    }

    public void clean() {
        migrate();

        repair();


    }

    private void repair() {
        List<DBObject> results = mongo.findAll(DBObject.class, DailyDistance.COLLECTION_NAME);

        int count = 0;

        DBCollection collection = mongo.getCollection(COLLECTION_NAME);

        double distanceTotal = 0.0;
        int yearFlag = 0;
        int drivenTotal = 0;

        for (DBObject object : results) {
            boolean modified = false;
            // migrate version
            for (int month = 1; month <= 12; month++) {

                double monthlyDistance = 0.0;
                int monthlyFlag = 0;
                int monthlyDriven = 0;

                DBObject monthlyValues = (DBObject) object.get(Integer.toString(month));
                if (monthlyValues == null) {
                    continue;
                }
                for (int day = 1; day <= 31; day++) {
                    DBObject dailyValue = (DBObject) monthlyValues.get(Integer.toString(day));

                    if (dailyValue != null) {
                        monthlyDistance += (Double)dailyValue.get("s");
                        monthlyDriven += (Integer)dailyValue.get("d");
                        // TODO set flag based on any day being set.
                        monthlyFlag = 1;
                    }
                }

                DBObject monthlyAggregates = new BasicDBObject();

                monthlyAggregates.put("s", monthlyDistance);
                monthlyAggregates.put("d", monthlyDriven);
                monthlyAggregates.put("f", monthlyFlag);

                monthlyValues.put("a", monthlyAggregates);

                distanceTotal += monthlyDistance;
                drivenTotal += monthlyDriven;
                if (monthlyFlag == 1) {
                    yearFlag = 1;
                }
            }

            DBObject yearlyAggregates = new BasicDBObject();

            yearlyAggregates.put("s", distanceTotal);
            yearlyAggregates.put("d", drivenTotal);
            yearlyAggregates.put("f", yearFlag);

            object.put("a", yearlyAggregates);

            modified = true; // For now always update

            if (modified) {
                count++;
                logger.info("Updated daily distance document (" + object.get("_id") + ")");
                collection.save(object);
            }
        }

        logger.info("Updated " + count + " daily distance documents.");
    }


    private void migrate() {
        List<DBObject> results = mongo.findAll(DBObject.class, DailyDistance.COLLECTION_NAME);

        int count = 0;

        DBCollection collection = mongo.getCollection(COLLECTION_NAME);

        for (DBObject object : results) {
            boolean modified = false;
            // migrate version
            Integer version = (Integer) object.get(SCHEMA_KEY);
            if (version == null || version != SCHEMA_VERSION) {
                object.put(SCHEMA_KEY, SCHEMA_VERSION);
                modified = true;
            }

            for (int month = 1; month <= 12; month++) {
                DBObject monthlyValues = (DBObject) object.get(Integer.toString(month));
                //logger.info("month is " + month + " value is " + monthlyValues);
                if (monthlyValues == null) {
                    continue;
                }
                for (int day = 1; day <= 31; day++) {
                    Object dailyValue = monthlyValues.get(Integer.toString(day));
                    if (dailyValue instanceof Double) {
                        //logger.info("Migrating " + month + " and day " + day);
                        // migrate
                        DBObject dailyValues = new BasicDBObject();
                        dailyValues.put("d", 1);
                        dailyValues.put("s", dailyValue);
                        dailyValues.put("f", 1);

                        monthlyValues.put(Integer.toString(day), dailyValues);
                        modified = true;
                    }
                }
            }

            if (modified) {
                count++;
                logger.info("Migrated daily distance document (" + object.get("_id") + ")");
                collection.save(object);
            }
        }

        logger.info("Migrated " + count + " daily distance documents.");

    }






}
