package com.tripography.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.api.CommandResponse;
import com.rumbleware.tesla.api.TeslaPortal;
import com.rumbleware.tesla.api.VehicleDescriptor;
import com.tripography.providers.VehicleProviderService;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import com.tripography.telemetry.analytics.DailyDistanceUpdater;
import com.tripography.telemetry.analytics.DailyHistogramUpdater;
import com.tripography.telemetry.events.DailyUpdateEvent;
import com.tripography.telemetry.events.DailyUpdateEventListener;
import com.tripography.vehicles.OdometerReading;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * Processes actions based on the state of the database, after action complete, updates the state and waits
 * for next command.
 *
 * Collection: dailyVehicleReading
 * {
 *     _id: // vehicle id?
 *     l : { // last reading
 *         o: 323.4 // odoomter in values
 *         d: Date(...) // date
 *     }
 *     n : Date(..) // next reading.
 *     s : // flag hmm, what here?
 *     s : // currently Processing
 *
 * }
 * @author gscott
 */
@Service("vehicleTelemetryService")
public class AsyncVehicleTelemetryService implements VehicleTelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncVehicleTelemetryService.class);

    private final VehicleService vehicleService;
    private final VehicleProviderService vehicleProviderService;
    private final DailyVehicleReadingRepository dailyVehicleReadingRepository;
    private final MongoOperations mongo;

    private final TeslaPortal teslaPortal;

    private final List<DailyUpdateEventListener> dailyEventListeners = new ArrayList<>();

    private final Random random = new Random();

    private boolean isRunning = false;

    private ScheduledFuture<?> dailyPollerJob;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "WebScheduledExecutorService");
            t.setDaemon(true);
            return t;
        }
    });

    private ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "WebExecutorService");
            t.setDaemon(true);
            return t;
        }
    });

    @Autowired
    public AsyncVehicleTelemetryService(VehicleService vehicleService, VehicleProviderService vehicleProviderService,
                                        TeslaPortal teslaPortal, DailyVehicleReadingRepository readingRepository, MongoOperations mongo) {
        this.vehicleService = vehicleService;
        this.vehicleProviderService = vehicleProviderService;
        this.teslaPortal = teslaPortal;
        this.dailyVehicleReadingRepository = readingRepository;
        this.mongo = mongo;

        dailyEventListeners.add(new DailyDistanceUpdater(mongo));
        dailyEventListeners.add(new DailyHistogramUpdater(mongo));

        startService();

    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void startService() {
        if (isRunning) {
            // do nothing
            return;
        }
        dailyPollerJob = scheduledExecutorService.scheduleAtFixedRate(new DailyTrackingPoller(), 0, 1, TimeUnit.MINUTES);
        isRunning = true;
    }

    @Override
    public synchronized void stopService() {
        if (!isRunning) {
            // do nothing
            return;
        }
        dailyPollerJob.cancel(false);
        isRunning = false;
    }

    @PreDestroy
    public void shutdown() {
        scheduledExecutorService.shutdown();
        executorService.shutdown();
    }

    @Override
    public void startTrackingVehicle(Vehicle vehicle) {
        DailyVehicleReading reading = dailyVehicleReadingRepository.findOne(vehicle.getObjectId());

        // no reading found
        if (reading != null) {
            reading.setEnabled(true);
            reading.setStatus(DailyVehicleReading.Status.SYNCING);
            updateReadingTimes(reading);
            // TODO probably need to update state
        }
        else {
            reading = new DailyVehicleReading(vehicle.getObjectId());
            reading.setEnabled(true);
            reading.setStatus(DailyVehicleReading.Status.SYNCING);
            reading.setLastReading(vehicle.getOdometer());
            reading.setTimeZone(vehicle.getTimeZone());
            updateReadingTimes(reading);
        }

        dailyVehicleReadingRepository.save(reading);
    }


    @Override
    public void stopTrackingVehicle(Vehicle vehicle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void updateVehicleStatistics(DailyVehicleReading vehicleReading, Vehicle vehicle, OdometerReading odometer) {
        double dailyMileage = odometer.getOdometer() - vehicleReading.getLastReading().getOdometer();

        // round it
        dailyMileage = (double)Math.round(dailyMileage * 10) / 10;

        //logger.info("The current odomter is " + odometer + " and the previous odometer was " + vehicle.getLastReading().getOdometer());
        logger.debug("Daily mileage is " + dailyMileage);

        if (dailyMileage < 0.0) {
            logger.error("woops read negative mileage");
            return;
        }

        Calendar currentDay = Calendar.getInstance(vehicleReading.getTimeZone());
        currentDay.setTime(vehicleReading.getForDate());

        DailyUpdateEvent dailyUpdateEvent = new DailyUpdateEvent(vehicle, currentDay, dailyMileage);

        for (DailyUpdateEventListener listener : dailyEventListeners) {
            try {
                listener.processEvent(dailyUpdateEvent);
            }
            catch (Exception e) {
                logger.error("Listener " + listener + " exception on update for event " + dailyUpdateEvent, e);
            }
        }

    }

    /**
     * Updates the vehicle document with the latest stats.
     */
    private void updateVehicleDocument(DailyVehicleReading reading, Vehicle vehicle, OdometerReading odometerReading) {
        try {
            vehicleService.updateOdometerReading(vehicle, odometerReading);
        }
        catch (Exception e) {
            logger.error("Unable to update vehicle with daily stats " + vehicle.getId() + " reading " + reading);
        }
    }


    private void updateWithInternalError(DailyVehicleReading reading, String message) {
        reading.setEnabled(false);
        reading.setStatus(DailyVehicleReading.Status.INTERNAL_ERROR);
        updateReadingTimes(reading);
        reading.setError(message);
        logger.error("Internal error processing daily stats for vehicle " + reading.getId() + ": " + message);
        dailyVehicleReadingRepository.save(reading);
    }

    private void updateWithReadingError(DailyVehicleReading reading, String message) {
        reading.setStatus(DailyVehicleReading.Status.READ_ERROR);
        updateReadingTimes(reading);
        reading.setError(message);
        logger.error("error processing daily stats for vehicle " + reading.getId() + ": " + message);
        dailyVehicleReadingRepository.save(reading);
    }

    private void updateReadingTimes(DailyVehicleReading reading) {
        TimeZone tz = reading.getTimeZone();

        Calendar calendar = Calendar.getInstance(tz);

        reading.setForDate(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // This shouldn't be needed, but leave for now.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, random.nextInt(60)); // Randomize the reading time within the hour.
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        reading.updateTargetAndNextReadingDate(calendar.getTime());

    }

    private boolean updateReadingSuccess(DailyVehicleReading reading, OdometerReading odometer) {


        // Create a new reading object to use for the update.  Only need fields necessary to perform update
        DailyVehicleReading newReading = new DailyVehicleReading();
        newReading.setTimeZone(reading.getTimeZone());
        newReading.setStatus(DailyVehicleReading.Status.SYNCING);

        // Update the new reading times, needs timezone
        updateReadingTimes(newReading);

        switch (reading.getStatus()) {
            case OK:
            case SYNCING:
                newReading.setStatus(DailyVehicleReading.Status.OK);
        }

        DailyVehicleReading old = mongo.findAndModify(query(
                where("_id").is(reading.getObjectId())
                        .and(DailyVehicleReading.STATUS).is(reading.getStatus().getValue())
                        .and(DailyVehicleReading.NEXT_READING_DATE).is(reading.getNextReadingDate())),
                new Update()
                        .set(DailyVehicleReading.STATUS, newReading.getStatus().getValue())
                        .set(DailyVehicleReading.FOR_DATE, newReading.getForDate())
                        .set(DailyVehicleReading.ERROR_COUNT, 0)
                        .set(DailyVehicleReading.NEXT_READING_DATE, newReading.getNextReadingDate())
                        .set(DailyVehicleReading.TARGET_READING_DATE, newReading.getTargetReadingDate())
                        .set(DailyVehicleReading.ODOMETER_READING, odometer)
                        .unset(DailyVehicleReading.MESSAGE)
                , DailyVehicleReading.class);

        return old != null;
    }


    private void updateVehicle(final DailyVehicleReading reading) {
        if (!reading.isEnabled()) {
            return;
        }

        switch (reading.getStatus()) {
            case AUTH_ERROR:
            case INTERNAL_ERROR:
                // Ignore this vehicle, requires user intervention.
                return;
        }

        try {
            // Lookup vehicle and provider
            final Vehicle vehicle = vehicleService.findById(reading.getId());
            if (vehicle == null) {
                updateWithInternalError(reading, "Vehicle id not found.");
                return;
            }

            final TeslaVehicleProvider vehicleProvider = vehicleProviderService.findById(vehicle.getProviderId());
            if (vehicleProvider == null) {
                updateWithInternalError(reading, "Provider not found.");
                return;
            }

            CommandResponse mobileEnabledResponse = teslaPortal.mobileEnabled(vehicleProvider.getCredentials(), vehicle.getDetails().getPortalId());

            if (!mobileEnabledResponse.getResult()) {
                updateWithReadingError(reading, "mobile disabled: " + mobileEnabledResponse.getReason());
                return;
            }

            // TODO, should be able to skip this step, no need to get descriptor, vehicle id should be sufficient.
            VehicleDescriptor descriptor = teslaPortal.vehicle(vehicleProvider.getCredentials(), vehicle.getDetails().getPortalId());
            TeslaVehicle tv = new TeslaVehicle(descriptor, vehicleProvider.getCredentials(), teslaPortal);

            Futures.addCallback(tv.getOdometer(), new FutureCallback<OdometerReading>() {

                @Override
                public void onSuccess(OdometerReading result) {
                    if (result == null) {
                        // TODO can this happen?
                        updateWithReadingError(reading, "Unable to read odometer.");
                        logger.error("error while reading odometer, null result" + reading);
                    }

                    logger.debug("Read an odometer for vehicle " + result);

                    try {
                        if (!updateReadingSuccess(reading, result)) {
                            logger.debug("Update already appears to have occurred for " + reading);
                            return;
                        }
                    }
                    catch (Exception e) {
                        updateWithInternalError(reading, "Unable to update status.");
                        logger.error("failed to update reading for " + reading, e);
                    }

                    try {
                        logger.debug("Updating vehicle stats for reading " + reading + " odometer " + result);
                        // Update the vehicle document
                        updateVehicleDocument(reading, vehicle, result);

                        // Only update stats if previous reading was OK
                        if (reading.getStatus() == DailyVehicleReading.Status.OK) {
                            updateVehicleStatistics(reading, vehicle, result);
                        }
                    }
                    catch (Exception e) {
                        logger.error("failed to update vehilce stats " + reading, e);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    updateWithReadingError(reading, "Unable to read odometer.");
                    logger.error("error while reading odometer " + reading, t);
                }
            });

        }
        catch (Throwable t) {
            updateWithReadingError(reading, "Unexpected error occurred.");
            logger.error("Unexpected error while performing reading " + reading, t);
        }

    }

    class DailyTrackingPoller implements Runnable {

        // How old a reading should be before we mark it as an error, and reschedule.
        private final long MAX_READING_AGE = 4 * 60 * 60 * 1000;

        @Override
        public void run() {
            try {
                Date now = new Date();

                logger.debug("Polling for vehicles to update");

                List<DailyVehicleReading> readings = dailyVehicleReadingRepository.findByNextReadingDateLessThan(now);

                if (readings.size() > 0) {
                    logger.debug("Found " + readings.size() + " during daily update poll");
                }

                for (DailyVehicleReading reading : readings) {
                    Date targetReadingDate = reading.getTargetReadingDate();

                    if (targetReadingDate != null && now.getTime() - targetReadingDate.getTime() > MAX_READING_AGE) {
                        logger.warn("Reading time has expired for reading, will mark as error " + reading);
                        updateWithReadingError(reading, "Nightly Reading Window Expired");
                    }
                    else {
                        updateVehicle(reading);
                    }
                }
            }
            catch (Throwable t) {
                logger.error("Unexpected exception", t);
            }
        }
    }


    /**
     * Tracking levels.
     *
     * For odometer readings:
     *
     * // raw data
     * {
     *     v : "1" // vehicleId
     *     o : "123.4",
     *     d : "2013-05-01" - timestamp
     *
     * }
     *
     * // daily mileage
     *
     * // multilpe documents per day
     * {
     *     v : "1" // vehicleId
     *     m : "12.4"
     *     d : "2013-03-04"
     * }
     *
     * // one document per month
     * {
     *      v : "1" // vehicleId
     *      m : "2" // month2
     *      miles : [
     *          12, 34, 24, 5, 6
     *      ]
     *
     * }
     *
     * // single document all aggregates
     * {
     *     v : "1" // vehilceId
     *     lastReading : {
     *         o : 123
     *         d : timestamp
     *     },
     *     "sum" : 3233,
     *     "2012" : { // Year
     *         "sum" : 2323"
     *         01 : [ 1, 12, 53, 523 ],  // month array of odometers
     *         02 : [ 5, 6, 23, 23 ],
     *         03 :
     *     },
     *     2013 :
     * }
     *
     * // yearly aggregates
     * {
     *     v : ObjectId(...)
     *     y : "2013"
     *     l : { o: 323.4 t: DateTime(...) }
     *     a : { sum : 3},
     *     "01" : {
     *         aggs : { sum : 3},
     *         "01" : 3
     *         "02" : 4,
     *         ...
     *     },
     *     "02" :
     *
     *
     * }
     *
     * {
     *     v : ObjectId(...)
     *     t : "
     *     type : {
     *         type : "vehicle",
     *         id : ObjectId(...)
     *         year : 2013
     *     }
     *     y : "2013"
     *     l : { o: 323.4 t: DateTime(...) }
     *     a : { s : 3, v: 1 }, // s: sum
     *     "01" : {
     *         aggs : { s : 3.2, v: 2 } // sum for month, v vehicle driven days for month
     *         "01" : { d: 3.4, v: 2 } // d is distance, v is vehicle count (# of vehicles in distance)
     *         "02" : 4,
     *         ...
     *     },
     *     "02" :
     *
     *
     * }
     *
     *
     * // So for each vehicle odometer update
     * // 1. Update vehicle's stats and aggregates
     *    2. Update global aggregates
     *    3. Update group aggregates
     *
     *
     *
     *
     * // Alternate more ORM friendly
     *
     * {
     *     v : "1" // vehicleId
     *     lastReading ...
     *
     *     data : [
     *       { year : 2012
     *         sum : 450
     *         months: [
     *            {
     *                month : 1
     *                sum: 32
     *                mileage : [ 1, 2, 3, 4, 5]
     *            }
     *         ]
     *       }
     *     ]
     *
     * }
     *
     * // For daily driving histogram there are two separate items kept.  One for miles and one for km.
     * // For miles the bin sizes are in 1 mile increments, with distances of > 200 miles lumped together
     * // For km the bins size are in 1 km increments with distances of > 320km they are lumped together.
     *
     * // Histogram
     * {
     *      v : "1" // vehicleId
     *      s : 3 // # of days
     *      b : { // bins
     *          "0" : 3 // 0 < x < 1
     *      }
     *
     * }
     *
     * // Aggregates
     * {
     *     agg : "total"
     *     type : [ "group", "global", "type" ]
     *     total : 3
     *     [ ... ]
     *
     * }
     *
     * // For some aggregates, need to average over number of vehicles per/day.  How to calculate?
     * // Keep set of vehicles and post aggregate at end of day?  Kind of yuck.
     * //
     *
     * Example aggregates
     *   - Total
     *   - Battery Type
     *   - Group
     *
     * // Trips
     * {
     *     depart: Date(...)
     *     arrive: Date(...)
     *     start: GPS(...)
     *     end: GPS(...)
     *     miles: 34
     *     path: { ... }
     *
     * }
     *
     * {
     *     start: Date(...)
     *     stop: Date(...)
     *     energy: 22.3
     *     rate: 10kwh
     *     supercharger: yes
     *     location: GPS(...)
     *     charger: ObjectId(...)
     * }
     *
     * Charger(....)
     *
     *
     */

}
