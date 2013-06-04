package com.tripography.telemetry.analytics;

import com.tripography.telemetry.events.DailyUpdateEvent;
import com.tripography.telemetry.events.DailyUpdateEventListener;
import com.tripography.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * // yearly aggregates
 * {
 *     _id: "..."
 *     v : 1 // schema version
 *     v : ObjectId(...)
 *     y : "2013"
 *     l : { o: 323.4 t: DateTime(...) }
 *     a : { s: 334, d: 3 },
 *     "01" : {
 *         a : { sum : 3},
 *         "01" : {
 *              d: 3.4, // distance traveled
 *              v: 1, // number of vehicles
 *              f: 1 // flag, initial NO_READING or -1
*          }
 *         "02" : 4,
 *         ...
 *     },
 *     "02" :
 *
 *
 * }
 *
 * We need to track the difference between recording a daily mileage of zero due to an accurate reading
 * versus not reading any data, or an error.  Therefore, we introduce a flag. as follows
 *
 * 1
 *
 * @author gscott
 */
public class DailyDistanceUpdater implements DailyDistance, DailyUpdateEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DailyDistanceUpdater.class);

    private final MongoOperations mongo;

    public DailyDistanceUpdater(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public void processEvent(DailyUpdateEvent event) {

        Calendar currentDay = event.getCurrentDay();
        Vehicle vehicle = event.getVehicle();
        double dailyMileage = event.dailyDistance(DailyUpdateEvent.Units.MILES);

        logger.info("tracked vehicle " + vehicle.getObjectId());

        int month = currentDay.get(Calendar.MONTH) + 1;

        String monthAndDay = month + "." + currentDay.get(Calendar.DAY_OF_MONTH);

        logger.info("month and day " + monthAndDay);

        int year = currentDay.get(Calendar.YEAR);

        // This should peform a find and modify to make sure that the odometer value is not updated
        // by another process.  If so, then other stats should not be run.  Process should then track to see
        // if other vehicles are tracking it.

        // Increment the vehicle driven count by this amount.  Used to keep track of the number of days
        // the vehicle was driven.
        int vehicleDriven = event.wasVehicleDriven() ? 1 : 0;

        for (String aggGroupId : vehicle.getAggregateGroupIds()) {
            Update update = new Update();
            mongo.upsert(query(where("_id").is(year + "/" + aggGroupId)),
                    new Update()
                            .inc(monthAndDay + ".s", dailyMileage)
                            .inc(monthAndDay + ".d", vehicleDriven)
                            .set(monthAndDay + ".f", READING_FLAG.HAS_READING.getValue())
                            .inc(month + ".a.s", dailyMileage)
                            .inc(month + ".a.d", vehicleDriven)
                            .set(month + ".a.f", READING_FLAG.HAS_READING.getValue())
                            .inc("a.s", dailyMileage)
                            .inc("a.d", vehicleDriven)
                            .set("a.f", READING_FLAG.HAS_READING.getValue())
                    , "dailyDistance");

        }

    }
}
