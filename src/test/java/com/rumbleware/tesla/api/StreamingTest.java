package com.rumbleware.tesla.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
public class StreamingTest {


    @Test
    public void testStream() throws Exception {

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        Portal portal = new Portal(credentials);

        VehicleDescriptor[] vehicles = portal.vehicles();

        assertEquals(1, vehicles.length);

        VehicleDescriptor vehicle = vehicles[0];

        Streaming stream = new Streaming(credentials.getUsername(), vehicle.getTokens().get(0), vehicle.getVehicleId());

        stream.stream(null);

        synchronized (this) {
            this.wait();
        }
    }
}
