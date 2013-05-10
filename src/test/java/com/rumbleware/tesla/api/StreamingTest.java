package com.rumbleware.tesla.api;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
public class StreamingTest {

    private final static Logger logger = LoggerFactory.getLogger(StreamingTest.class);


    @Test
    public void testStream() throws Exception {

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        Portal portal = new Portal();

        VehicleDescriptor[] vehicles = portal.vehicles(credentials);

        assertEquals(1, vehicles.length);

        VehicleDescriptor vehicle = vehicles[0];

        logger.info("vehicle " + vehicle);

        portal.wakeUp(credentials, vehicle.getId());

        vehicle = portal.vehicle(credentials, vehicle.getId());

        logger.info("vehicle " + vehicle);

        portal.stream(credentials, vehicle, new StreamDataHandler() {
            @Override
            public boolean handleData(StreamData data) {
                logger.info("received data " + data);
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void exceptionOccured(Throwable t) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void streamClosed() {
                logger.info("Stream closed");
            }
        });

        synchronized (this) {
            this.wait();
        }
    }
}
