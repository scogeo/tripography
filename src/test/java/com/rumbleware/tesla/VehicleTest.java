package com.rumbleware.tesla;

import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.UsernamePasswordCredentials;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
public class VehicleTest {


    Portal portal = new Portal();


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
