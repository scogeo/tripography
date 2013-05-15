package com.rumbleware.tesla;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.UsernamePasswordCredentials;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author gscott
 */
public class TeslaVehicleTest {

    private static final Logger logger = LoggerFactory.getLogger(TeslaVehicleTest.class);

    Portal portal = new Portal();


    @Test
    public void testOdometer() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        List<TeslaVehicle> vehicles = VehicleFactory.getVehicles(portal, credentials);

        assertEquals(1, vehicles.size());

        TeslaVehicle vehicle = vehicles.get(0);

        ListenableFuture<Double> odometerFuture = vehicle.getOdometer();

        Double odometer = odometerFuture.get();

        logger.info("odometer is " + odometer);

        assertNotNull(odometer);
        assertTrue("greater than zero", odometer > 0.0);

    }

    @Ignore
    @Test
    public void testHonkHorn() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        //credentials.sign(null);

        List<TeslaVehicle> vehicles = VehicleFactory.getVehicles(portal, credentials);

        assertEquals(1, vehicles.size());

        TeslaVehicle vehicle = vehicles.get(0);

        //vehicle.honkHorn();


    }

    @Test
    @Ignore
    public void testDriveState() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        //credentials.sign(null);

        List<TeslaVehicle> vehicles = VehicleFactory.getVehicles(portal, credentials);

        assertEquals(1, vehicles.size());

        TeslaVehicle vehicle = vehicles.get(0);

        vehicle.driveState();
    }
}
