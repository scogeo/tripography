package com.tripography.telemetry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.mongodb.util.MyAsserts.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author gscott
 */
public class AsyncVehicleTelemetryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncVehicleTelemetryServiceTest.class);

    @Test
    public void roundingTest() throws Exception {
        double x = 3.4;
        double y = 1.3;

        double expected = 2.1;

        double delta = x - y;

        assertFalse(expected == delta);
        double fixed = (double)Math.round(delta * 10) / 10;

        assertTrue(expected == fixed);
        assertEquals(2.1, fixed, 0.0);
    }

    @Test
    public void tzTest() throws Exception {
        for (String id : TimeZone.getAvailableIDs()) {
            //logger.info("tz is " + id);

        }

        TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
        //TimeZone tz = TimeZone.getTimeZone("America/New_York");
        if (tz == null) {
            logger.warn("Vehicle has no timezone ");
            return;
        }
        Calendar calendar = Calendar.getInstance(tz);
        //Calendar calendar = Calendar.getInstance();
        Date now = new Date();

        //calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        logger.info("Calendar is "+ calendar);
        logger.info("Now is " + now);

        logger.info("Future is " + calendar.getTime());

        long howMany = calendar.getTimeInMillis() - System.currentTimeMillis();

        logger.info("time " + howMany);

        logger.info("hours " + (howMany / 1000 / 3600));
    }
}
