package com.rumbleware.tesla;

import com.rumbleware.tesla.api.DriveState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author gscott
 */
public class VehiclePoller {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePoller.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final TeslaVehicle vehicle;

    /**
     * Polls the vehicle stats.
     *
     * Scheduled stats:
     *   Poll odometer readings once per day.
     *
     *
     * State based stats:
     *   If car's current state is driving, then record telemetry?
     *
     *
     *
     * Others polling:
     * charge_state every 60 secs?
     * drive_state every 30 seconds?
     *
     * Other streaming
     */

    public VehiclePoller(TeslaVehicle vehicle) {
        this.vehicle = vehicle;
        long startDelay = 0; // Should randomize

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(new UpdateDriveState(), startDelay, 30, TimeUnit.SECONDS);


    }

    public void shutdown() {
        scheduler.shutdown();
    }

    class UpdateDriveState implements Runnable {

        @Override
        public void run() {

            DriveState state = vehicle.driveState();

            logger.info("state is " + state);
            logger.info("Callback initiated");
        }
    }
}
