package com.tripography.telemetry;

import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.VehicleDescriptor;
import com.rumbleware.web.executors.SharedExecutors;
import com.tripography.providers.VehicleProviderService;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author gscott
 */
@Service("vehicleTelemetryService")
public class AsyncVehicleTelemetryService implements VehicleTelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncVehicleTelemetryService.class);

    private VehicleService vehicleService;
    private VehicleProviderService vehicleProviderService;

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
    public AsyncVehicleTelemetryService(VehicleService vehicleService, VehicleProviderService vehicleProviderService) {
        this.vehicleService = vehicleService;
        this.vehicleProviderService = vehicleProviderService;
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
     *     year : "2013"
     *     "aggs" : { sum : 3},
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

        public DailyOdometerJob(TrackedVehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Override
        public void run() {
            try {
                logger.info("Yo stargin the job");
                logger.info("Cron job reading odometer " +  vehicle.readOdometer());
                logger.info("Yo ending the job!");
            }

            catch (Exception e) {
                logger.info("woops caught an exception ", e);
            }
        }
    }


    class TrackedVehicle {

        private final Vehicle vehicle;

        private TeslaVehicleProvider vehicleProvider;

        private ScheduledFuture<?> dailyOdometerFuture;

        public TrackedVehicle(Vehicle vehicle, TeslaVehicleProvider provider) {
            this.vehicle = vehicle;
            this.vehicleProvider = provider;
        }

        public void track() {
            // Schedule a job
            trackDailyOdometer();
        }

        public void stopTracking() {
            if (dailyOdometerFuture != null) {
                dailyOdometerFuture.cancel(false);
            }
        }

        public double readOdometer() {
            logger.info("Getting the vehicle's current descriptor ");
            VehicleDescriptor descriptor = portal.vehicle(vehicleProvider.getCredentials(), vehicle.getDetails().getPortalId());

            TeslaVehicle tv = new TeslaVehicle(descriptor, vehicleProvider.getCredentials(), portal);

            logger.info("About to read odometer");
            return tv.getOdometer();

        }

        private void trackDailyOdometer() {
            TimeZone tz = vehicle.getTimeZone();
            if (tz == null) {
                logger.warn("Vehicle has no timezone " + vehicle);
                return;
            }
            Calendar calendar = Calendar.getInstance(tz);
            // Set calendar to midnight of the next day.

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long howMany = calendar.getTimeInMillis() - System.currentTimeMillis();


            logger.info("Current odometer is " + readOdometer());

            logger.info("Scheduling a vehilce to check odomoter in " + howMany);

            dailyOdometerFuture = scheduledExecutorService.schedule(new DailyOdometerJob(this), howMany, TimeUnit.MILLISECONDS);

        }
    }

}
