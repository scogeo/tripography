package com.tripography.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mongodb.DBObject;
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

        private final TrackedVehicle vehicle;
        private final Calendar currentDay;

        public DailyOdometerJob(Calendar currentDay, TrackedVehicle vehicle) {
            this.currentDay = (Calendar) currentDay.clone();
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
                                vehicle.setLastOdometer(result);
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

            // round it
            dailyMileage = (double)Math.round(dailyMileage * 10) / 10;

            if (dailyMileage < 0.0) {
                logger.error("woops read negative mileage");
            }

            logger.info("tracked vehicle " + vehicle.vehicle.getObjectId());

            int month = currentDay.get(Calendar.MONTH) + 1;

            String monthAndDay = month + "." + currentDay.get(Calendar.DAY_OF_MONTH);

            logger.info("month and day " + monthAndDay);

            int year = currentDay.get(Calendar.YEAR);

            String id = year + "/vehicle/" + vehicle.vehicle.getId();

            // This should peform a find and modify to make sure that the odometer value is not updated
            // by another process.  If so, then other stats should not be run.  Process should then track to see
            // if other vehicles are tracking it.

            // Increment the vehicle driven count by this amount.  Used to keep track of the number of days
            // the vehicle was driven.
            int vehicleDriven = dailyMileage > 0.0 ? 1 : 0;


            mongoTemplate.upsert(query(where("_id").is(id)),
                    new Update()
                            .set("o.v", odometer)
                            .set("o.t", new Date())
                            .set(monthAndDay, dailyMileage) // Note, setting this will override default of -1, never increment.
                            .inc("a.s", dailyMileage)
                            .inc("a.d", vehicleDriven)
                            .inc(month + ".a.s", dailyMileage)
                            .inc(month + ".a.d", vehicleDriven)
                    , "dailyDistance");


            // Redo query for groups

            List<String> groupIds = getGroupIds();

            // TODO fix this shit.  mongotemplate doesn't support lists in in() for some reason.  For now, submit
            // multiple updates.  Ok for low # of users.

            for (String groupId : groupIds) {
                mongoTemplate.upsert(query(where("_id").is(groupId)),
                        new Update()
                                .inc(monthAndDay, dailyMileage)
                                .inc("a.s", dailyMileage)
                                .inc(month + ".a.s", dailyMileage),
                        "dailyDistance");
            }


            // If we drove at all, then update the mileage histograms.
            if (dailyMileage > 0.0) {
                Integer bucket = (int)Math.floor(dailyMileage);

                // For histograms, intialize with values of 0, when rendering graph, treat zero as null or no data.

                mongoTemplate.upsert(query(where("_id").is(id)),
                        new Update()
                                .inc(bucket.toString(), 1)
                        , "dailyHistogram");


                // TODO fix this shit.  mongotemplate doesn't support lists in in() for some reason.  For now, submit
                // multiple updates.  Ok for low # of users.

                for (String groupId : groupIds) {
                    mongoTemplate.upsert(query(where("_id").is(groupId)),
                            new Update()
                                    .inc(bucket.toString(), 1)
                            , "dailyHistogram");
                }
            }


        }

        private List<String> getGroupIds() {
            List<String> groupIds = new ArrayList<>();
            groupIds.add("2013/all");

            // Calculate regions"

            groupIds.add("2013/region/us"); // united states
            groupIds.add("2013/region/us/ca"); // california
            groupIds.add("2013/region/us/ca/city/sunnyvale"); // city
            groupIds.add("2013/region/us/ca/county/santa_clara"); // county
            groupIds.add("2013/region/us/94085"); // postal code
            groupIds.add("2013/region/us/norcal"); // US "mega-region" based on county


            // TODO we need to add the notion of regions tied to either postal code or
            // county.  In the US, county seems to work well.  Regions may or may not be overlapping.
            /**
             * {
             *    country: "us"  // ISO code
             *    slug: "norcal",
             *    name: "Northern California",
             *    type: "postal" || "county" || "state", etc..
             *    members: [ "Alameda", "Contra Costa", "Marin", "Napa", ...]
             *    wikipeida: "http://en.wikipedia.org/..."
             *
             * }
             */




            // These should come from the provider.
            groupIds.add("2013/make/tesla");
            groupIds.add("2013/make/tesla/s");
            groupIds.add("2013/make/tesla/s/battery/85");

            // TODO Now query DB to see what groups we belong to. :)
            return groupIds;

        }

        /**
         * Updates the vehicle object's stats.  This includes the vehicle's odometer and refreshes
         * any other vehicle stats such as firmware version, international settings, etc.
         */
        private void updateVehicleInfo() {

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

            try {

                String id = "2013" + "/vehicle/" + vehicle.getId();

                DBObject result = mongoTemplate.findOne(query(where("_id").is(id)), DBObject.class, "dailyDistance");

                if (result != null) {
                    logger.info("got a result " + result);
                    DBObject previousOdometer = (DBObject) result.get("o");
                    logger.info("got an o " + previousOdometer);
                    if (previousOdometer != null) {
                        Double value = (Double)previousOdometer.get("v");
                        lastOdometer = value;
                        logger.info("Woo hoo got a last odometer value " + lastOdometer);
                    }
                }

                if (lastOdometer == null) {
                    logger.info("Getting latest odometer");

                    Future<Double> odometer = readOdometer();

                    lastOdometer = odometer.get();
                }

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

        void setLastOdometer(Double lastOdometer) {
            this.lastOdometer = lastOdometer;
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

            //calendar.add(Calendar.MINUTE, 1);

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
