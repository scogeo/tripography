package com.tripography.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.VehicleDescriptor;
import com.rumbleware.web.executors.SharedExecutors;
import com.tripography.providers.VehicleProviderService;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author gscott
 */
@Service("vehicleTelemetryService")
public class AsyncVehicleTelemetryService implements VehicleTelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncVehicleTelemetryService.class);

    private VehicleService vehicleService;
    private VehicleProviderService vehicleProviderService;
    private MongoTemplate mongoTemplate;

    private Portal portal = new Portal();

    private ConcurrentHashMap<String, TrackedVehicle> trackedVehicles = new ConcurrentHashMap<String, TrackedVehicle>();

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

    private ConcurrentHashMap<String, TeslaVehicleProvider> providersMap = new ConcurrentHashMap<String, TeslaVehicleProvider>();

    @Autowired
    public AsyncVehicleTelemetryService(VehicleService vehicleService, VehicleProviderService vehicleProviderService,
                                        MongoTemplate mongoTemplate) {
        this.vehicleService = vehicleService;
        this.vehicleProviderService = vehicleProviderService;
        this.mongoTemplate = mongoTemplate;
        loadProviders();
        loadVehicles();
    }

    private void loadProviders() {
        List<TeslaVehicleProvider> providers = vehicleProviderService.findAll();

        for (TeslaVehicleProvider provider : providers) {
            providersMap.put(provider.getId(), provider);
        }

    }

    private void loadVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        for (Vehicle vehicle : vehicles) {
            TeslaVehicleProvider provider = providersMap.get(vehicle.getProviderId());
            TrackedVehicle trackedVehicle = new TrackedVehicle(vehicle, provider);
            trackedVehicle.track();
        }

    }

    @Override
    public void startTrackingVehicle(String vehicleId) {
        TrackedVehicle trackedVehicle = trackedVehicles.get(vehicleId);
        if (trackedVehicle == null) {
            Vehicle vehicle = vehicleService.findById(vehicleId);

        }

    }

    @Override
    public void stopTrackingVehicle(String vehicleId) {
        //To change body of implemented methods use File | Settings | File Templates.
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
     * // Histogram
     * {
     *      v : "1" // vehicleId
     *      total : 3
     *      [
     *         {
     *             daily : 1,
     *             count : x
     *         }
     *      ]
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
    enum TrackingLevel {
        DAILY,
        TRIPS,
    }

    class DailyOdometerJob implements Runnable {

        private TrackedVehicle vehicle;
        private Calendar currentDay;

        public DailyOdometerJob(Calendar currentDay, TrackedVehicle vehicle) {
            this.currentDay = currentDay;
            this.vehicle = vehicle;
        }

        @Override
        public void run() {
            try {
                ListenableFuture<Double> odometer = vehicle.readOdometer();

                Futures.addCallback(odometer, new FutureCallback<Double>() {

                    @Override
                    public void onSuccess(Double result) {
                        if (result != null) {
                            logger.info("Read an odometer for vehicle " + result);
                            try {
                                updateVehicleStatistics(result);
                            }
                            catch (Exception e) {
                                logger.warn("woops", e);
                            }

                        }
                        else {
                            // retry
                            // TODO implement a retryable job.
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.warn("Woops got an error while reading odometer " + t);
                    }
                });

                vehicle.scheduleDailyOdometer();

            }

            catch (Exception e) {
                logger.info("woops caught an exception ", e);
            }
        }

        private void updateVehicleStatistics(Double odometer) {
            double dailyMileage = odometer - vehicle.getLastOdometer();

            if (dailyMileage < 0.0) {
                logger.error("woops read negative mileage");
            }

            logger.info("tracked vehicle " + vehicle.vehicle.getObjectId());

            int month = currentDay.get(Calendar.MONTH) + 1;

            String monthAndDay = month + "." + currentDay.get(Calendar.DAY_OF_MONTH);

            logger.info("month and day " + monthAndDay);

            int year = currentDay.get(Calendar.YEAR);

            String id = year + "/vehicle/" + vehicle.vehicle.getId();

            mongoTemplate.upsert(query(where("_id").is(id)),
                    new Update()
                            .set(monthAndDay, dailyMileage)
                            .inc("a.s", dailyMileage)
                            .inc(month + ".a.s", dailyMileage),
                    "yearlyOdometer");

            // Redo query for groups



        }

    }


    class TrackedVehicle {

        private final Vehicle vehicle;

        private Double lastOdometer;

        private TeslaVehicleProvider vehicleProvider;

        private ScheduledFuture<?> dailyOdometerFuture;

        public TrackedVehicle(Vehicle vehicle, TeslaVehicleProvider provider) {
            this.vehicle = vehicle;
            this.vehicleProvider = provider;
        }

        public void track() {

            Future<Double> odometer = readOdometer();

            try {
                lastOdometer = odometer.get();
                logger.info("Getting latest odometer");

                scheduleDailyOdometer();
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }

            // Schedule a job

        }

        Double getLastOdometer() {
            return lastOdometer;
        }

        public void stopTracking() {
            if (dailyOdometerFuture != null) {
                dailyOdometerFuture.cancel(false);
            }
        }

        public ListenableFuture<Double> readOdometer() {
            logger.info("Getting the vehicle's current descriptor ");
            VehicleDescriptor descriptor = portal.vehicle(vehicleProvider.getCredentials(), vehicle.getDetails().getPortalId());

            TeslaVehicle tv = new TeslaVehicle(descriptor, vehicleProvider.getCredentials(), portal);

            return tv.getOdometer();

        }

        void scheduleDailyOdometer() {
            TimeZone tz = vehicle.getTimeZone();
            if (tz == null) {
                logger.warn("Vehicle has no timezone " + vehicle);
                return;
            }
            Calendar calendar = Calendar.getInstance(tz);
            // Set calendar to midnight of the next day.

            DailyOdometerJob job = new DailyOdometerJob(calendar, this);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long howMany = calendar.getTimeInMillis() - System.currentTimeMillis();

            //logger.info("Current odometer is " + readOdometer());

            logger.info("Scheduling a vehicle to check odometer in " + howMany);

            dailyOdometerFuture = scheduledExecutorService.schedule(job, howMany, TimeUnit.MILLISECONDS);
        }


    }

}
