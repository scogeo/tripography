package com.tripography.providers.tesla;

import com.rumbleware.maps.TimeZoneGeoCoder;
import com.rumbleware.tesla.api.*;
import com.tripography.providers.VehicleProvider;
import com.tripography.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author gscott
 */
public class TeslaVehicleProvider extends VehicleProvider {

    private static final Logger logger = LoggerFactory.getLogger(TeslaVehicleProvider.class);

    public static final String TESLA_VENDOR = "tesla";

    @Field("username")
    private String username;

    @Transient
    private String password;

    @Field("cookie")
    private String cookie;

    @PersistenceConstructor
    public TeslaVehicleProvider() {
    }

    public TeslaVehicleProvider(String username, String password) {
        super(TESLA_VENDOR);
        this.username = username;
        this.password = password;
    }

    public void validate() {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

        HttpCookie cookie = credentials.checkLogin();

        logger.info("got cookie " + cookie);

        this.cookie = cookie.getValue();

        //cookie.getMaxAge();

        //CookieCredentials cookieCredentials = new CookieCredentials(username, cookie.getValue());

        Portal portal = new Portal();

        VehicleDescriptor[] vehicles = portal.vehicles(credentials);

        logger.info("Descriptor is " + vehicles[0]);



    }

    @Override
    public List<Vehicle> getVehicles() {
        Portal portal = new Portal();
        VehicleDescriptor[] descriptors = portal.vehicles(getCredentials());
        List<Vehicle> vehicles = new ArrayList<Vehicle>(descriptors.length);

        TimeZoneGeoCoder tzCoder = new TimeZoneGeoCoder();

        for (VehicleDescriptor descriptor : descriptors) {
            DriveState driveState = portal.driveState(getCredentials(), descriptor.getId());
            TimeZone tz = tzCoder.getTimeZone(driveState.getLatitude(), driveState.getLongitude(), true);

            TeslaVehicleDocument vehicle = new TeslaVehicleDocument(descriptor);
            vehicle.setTimeZone(tz);
            vehicles.add(vehicle);
        }

        return vehicles;
    }

    public PortalCredentials getCredentials() {
        return new CookieCredentials(username, cookie);
    }


}
