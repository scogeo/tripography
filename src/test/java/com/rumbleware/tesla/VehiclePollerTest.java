package com.rumbleware.tesla;

import com.rumbleware.tesla.api.TeslaPortal;
import com.rumbleware.tesla.api.UsernamePasswordCredentials;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
public class VehiclePollerTest {

    private TeslaPortal portal = new TeslaPortal();

    @Test
    @Ignore
    public void testBasic() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        //credentials.sign(null);

        List<TeslaVehicle> vehicles = VehicleFactory.getVehicles(portal, credentials);

        assertEquals(1, vehicles.size());

        TeslaVehicle vehicle = vehicles.get(0);
        VehiclePoller poller = new VehiclePoller(vehicle);

        synchronized(this) {
            this.wait(120 * 1000);
        }
        poller.shutdown();

    }
}
