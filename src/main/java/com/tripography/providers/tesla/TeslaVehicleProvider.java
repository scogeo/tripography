package com.tripography.providers.tesla;

import com.google.common.base.Charsets;
import com.rumbleware.maps.OSMAddress;
import com.rumbleware.maps.ReverseGeoCoder;
import com.rumbleware.maps.TimeZoneGeoCoder;
import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.api.*;
import com.tripography.providers.VehicleProvider;
import com.tripography.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.util.*;

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

    @Transient
    private String cookie;

    @Field("token")
    private byte[] token;

    @Field("expires")
    private Date expires;

    private static final int cryptoKeyVersion = 1;

    public static final String CRYPTO_HEX_KEY = "3d0a37627816cb661f98ab85fec4f85f";


    private static final SecretKeySpec cryptoKey = new SecretKeySpec(Hex.decode(CRYPTO_HEX_KEY), "AES");


    @PersistenceConstructor
    public TeslaVehicleProvider() {
    }

    public TeslaVehicleProvider(String username, String password) {
        super(TESLA_VENDOR);
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getExpires() {
        return expires;
    }

    public void validate(TeslaPortal portal) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

        HttpCookie cookie = credentials.checkLogin();

        this.token = encryptCookie(cookie.getValue());

        long maxAge = cookie.getMaxAge();
        if (maxAge > 0) {
            // Compute the expiration time
            long time = System.currentTimeMillis() + (1000 * maxAge);
            expires = new Date(time);
        }
        else {
            long ninetyDays = 90L * 24L * 3600L * 1000L;
            logger.warn("No cookie age set for auth cookie");
            long time = System.currentTimeMillis() + ninetyDays;
            expires = new Date(time);
        }

    }

    @Override
    public List<Vehicle> getVehicles(TeslaPortal portal) {


        VehicleDescriptor[] descriptors = portal.vehicles(getCredentials());
        List<Vehicle> vehicles = new ArrayList<Vehicle>(descriptors.length);

        TimeZoneGeoCoder tzCoder = new TimeZoneGeoCoder();

        ReverseGeoCoder geoCoder = new ReverseGeoCoder();

        for (VehicleDescriptor descriptor : descriptors) {

            TeslaVehicleDocument vehicle = new TeslaVehicleDocument(descriptor);

            vehicle.setVehicleAggregates(computeVehicleAggregates(vehicle));

            try {
                DriveState driveState = portal.driveState(getCredentials(), descriptor.getId());

                TimeZone tz = tzCoder.getTimeZone(driveState.getLatitude(), driveState.getLongitude(), true);
                vehicle.setTimeZone(tz);

                OSMAddress address = geoCoder.getAddress(driveState.getLatitude(), driveState.getLongitude());

                vehicle.setRegion(address);

                vehicle.setRegionAggregates(computeRegionAggregates(address));

                TeslaVehicle teslaVehicle = new TeslaVehicle(descriptor, getCredentials(), portal);

                vehicle.setOdometer(teslaVehicle.getOdometer().get());
            }
            catch (Exception e) {
                logger.warn("Unable to set timezone and location", e);
            }

            vehicles.add(vehicle);
        }

        return vehicles;
    }

    public boolean updateVehicleLocation(TeslaPortal portal, Vehicle vehicle) {

        TimeZoneGeoCoder tzCoder = new TimeZoneGeoCoder();

        ReverseGeoCoder geoCoder = new ReverseGeoCoder();

        try {
            DriveState driveState = portal.driveState(getCredentials(), vehicle.getDetails().getPortalId());

            TimeZone tz = tzCoder.getTimeZone(driveState.getLatitude(), driveState.getLongitude(), true);
            vehicle.setTimeZone(tz);

            OSMAddress address = geoCoder.getAddress(driveState.getLatitude(), driveState.getLongitude());

            vehicle.setRegion(address);

            vehicle.setRegionAggregates(computeRegionAggregates(address));

            VehicleDescriptor descriptor = portal.vehicle(getCredentials(), vehicle.getDetails().getPortalId());

            TeslaVehicle teslaVehicle = new TeslaVehicle(descriptor, getCredentials(), portal);

            vehicle.setOdometer(teslaVehicle.getOdometer().get());
        }
        catch (Exception e) {
            logger.warn("Unable to set timezone and location", e);
            return false;
        }
        return true;
    }

    private Set<String> computeVehicleAggregates(Vehicle vehicle) {
        Set<String> aggregates = new HashSet<>();
        aggregates.add("make/tesla");
        aggregates.add("make/tesla/s");

        if (vehicle.getDetails().getOptions().contains("BT85")) {
            aggregates.add("make/tesla/s/battery/85");
        }
        if (vehicle.getDetails().getOptions().contains("BT60")) {
            aggregates.add("make/tesla/s/battery/60");
        }
        if (vehicle.getDetails().getOptions().contains("BT40")) {
            aggregates.add("make/tesla/s/battery/40");
        }

        return aggregates;
    }


    private Set<String> computeRegionAggregates(OSMAddress address) {
        Set<String> aggregates = new HashSet<>();

        String country = address.getCountryCode();
        if (country != null) {
            String countryPath = "region/" + country;
            aggregates.add(countryPath);
            countryPath += "/";

            if (address.getState() != null) {
                String statePath = countryPath + lowerUnderScore(address.getState());
                aggregates.add(statePath);
                statePath += "/";

                if (address.getCounty() != null) {
                    aggregates.add(statePath + "county/" + lowerUnderScore(address.getCounty()));
                }
            }

        }
        return aggregates;
    }

    private String lowerUnderScore(String value) {
        String result = value.toLowerCase();
        result = result.replace(" ", "_");
        return result;
    }

    private void setAggregats() {
        // Calculate regions"

        /*
        groupIds.add("2013/region/us"); // united states
        groupIds.add("2013/region/us/ca"); // california
        groupIds.add("2013/region/us/ca/city/sunnyvale"); // city
        groupIds.add("2013/region/us/ca/county/santa_clara"); // county
        groupIds.add("2013/region/us/94085"); // postal code
        groupIds.add("2013/region/us/norcal"); // US "mega-region" based on county
*/

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



        /*
        // These should come from the provider.
        groupIds.add("2013/make/tesla");
        groupIds.add("2013/make/tesla/s");
        groupIds.add("2013/make/tesla/s/battery/85");

        */

    }

    byte[] encryptCookie(String cookie) {
        try {
            byte[] input = cookie.getBytes(Charsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, cryptoKey);
            byte[] output = cipher.doFinal(input);

            // For now allocate byte buffer based on cipher text.
            ByteBuffer byteBuffer = ByteBuffer.allocate(output.length + 4);

            byteBuffer.putInt(cryptoKeyVersion);
            byteBuffer.put(output);

            return byteBuffer.array();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    String decryptCookie(byte[] token) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(token);

            int version = buffer.getInt();

            if (version != cryptoKeyVersion) {
                throw new IllegalStateException("Unknown crypto version");
            }

            byte[] input = new byte[buffer.remaining()];
            buffer.get(input);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, cryptoKey);
            byte[] output = cipher.doFinal(input);

            return new String(output, Charsets.UTF_8);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public PortalCredentials getCredentials() {
        if (cookie == null) {
            cookie = decryptCookie(token);
        }

        return new CookieCredentials(username, cookie);
    }


}
