package com.tripography.telemetry.analytics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.tripography.providers.tesla.TeslaVehicleDocument;
import com.tripography.telemetry.events.DailyUpdateEvent;
import com.tripography.telemetry.events.DailyUpdateEventListener;
import com.tripography.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Updat
 *
 * For daily driving histogram there are two separate items kept.  One for miles and one for km.
 * For miles the bin sizes are in 1 mile increments, with distances of > 200 miles lumped together
 * For km the bins size are in 1 km increments with distances of > 320km they are lumped together.
 *
 * We do this because the binning process loses information and users generally, want to be presented
 * data in whole numbers they are familiar with.  For presentation bins can be re-computed to present
 * the best granularity of the data.
 *
 * Histogram
 * {
 *      _id : "analytics id"
 *      v : 1 // The schema version identifier
 *      c : Date(...) // the earliest date of data in this histogram
 *      l : Date(...) // the last date this data was updated.
 *      s : 3 // # of vehicle days in data set
 *
 *      b : { // bins for miles
 *          "0" : 3 // 0 < x < 1
 *      },
 *      k : { // bins for kilometers
 *          "0" : 3 // 0 < x < 1
 *      }
 *
 * }
 * @author gscott
 */
public class DailyHistogramUpdater implements DailyHistogram, DailyUpdateEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DailyHistogramUpdater.class);

    private final MongoOperations mongo;

    public DailyHistogramUpdater(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public void processEvent(DailyUpdateEvent event) {
        // Skip this event if we did not drive today.
        if (!event.wasVehicleDriven()) {
            return;
        }

        Vehicle vehicle = event.getVehicle();
        String milesBucket = binNum(event.dailyDistance(DailyUpdateEvent.Units.MILES), 200);
        String kmBucket = binNum(event.dailyDistance(DailyUpdateEvent.Units.KILOMETERS), 320);

        Date now = new Date();

        // For histograms, initialize with values of 0, when rendering graph, treat zero as null or no data.
        for (String aggGroupId : vehicle.getAggregateGroupIds()) {

            DBObject result = mongo.findOne(query(where("_id").is(aggGroupId)), DBObject.class, COLLECTION_NAME);
            if (result != null) {
                //migrateData(result);
            }

            mongo.upsert(query(where("_id").is(aggGroupId)),
                    new Update()
                            .inc("s", 1)
                            .inc("b." + milesBucket, 1)
                            .inc("k." + kmBucket, 1)
                            .set("l", now)
                    , COLLECTION_NAME);
        }

    }

    /**
     * Returns the string bin  number for the specified value.  Values than are greater than max are
     * placed in the bin of that value.
     *
     * @param value
     * @param max
     * @return
     */
    String binNum(double value, double max) {
        if (value <= 0) {
            throw new IllegalArgumentException("Illegal value " + value);
        }
        if (value > max) {
            value = max;
        }
        Integer binNum = (int)Math.floor(value);
        return binNum.toString();
    }

    /**
     * Update the schema to the latest version.
     */
    private void migrateData(DBObject result) {
        try {
            Integer currentVersion = (Integer)result.get("v");
            if (currentVersion == null || currentVersion != SCHEMA_VERSION) {
                // do migraiton
                result.put("v", SCHEMA_VERSION);
                result.put("c", new Date());
                DBObject bins = (DBObject) result.get("b");
                if (bins != null) {
                    DBObject kmBins = new BasicDBObject();
                    for (String bin : bins.keySet()) {
                        Integer count = (Integer)bins.get(bin);


                    }
                }
            }
        }
        catch (Exception e) {
            logger.warn("Unable to migrate object " + result.get("_id"), e);
        }

    }
}
