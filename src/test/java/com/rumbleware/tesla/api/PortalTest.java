package com.rumbleware.tesla.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */

public class PortalTest {

    private Portal portal;
    private PortalCredentials credentials;
    private VehicleDescriptor vehicle;


    @Before
    public void login() {
        credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        portal = new Portal();

        VehicleDescriptor[] vehicles = portal.vehicles(credentials);

        assertEquals(1, vehicles.length);

        vehicle = vehicles[0];
    }

    @Test
    public void testDriveState() throws Exception {
        DriveState driveState = portal.driveState(credentials, vehicle.getId());

        System.out.println("Drive state is " + driveState);
    }

    @Test
    public void testChargeState() throws Exception {
        ChargeState chargeState = portal.chargeState(credentials, vehicle.getId());
        System.out.println("Charge state is " + chargeState);
    }

    @Test
    public void testClimateState() throws Exception {
        ClimateState climateState = portal.climateState(credentials, vehicle.getId());
        System.out.println("Climate state is " + climateState);
    }

    @Test
    public void testMobileEnabled() throws Exception {
        CommandResponse response = portal.mobileEnabled(credentials, vehicle.getId());
        System.out.println("Response is " + response);
    }

    @Test
    public void testGetVehicle() throws Exception {
        VehicleDescriptor vd = portal.vehicle(credentials, vehicle.getId());

        System.out.println("vehicle state is " + vd);
    }

    @Test
    public void testVehicleState() throws Exception {
        VehicleState vehicleState = portal.vehicleState(credentials, vehicle.getId());

        System.out.println("State is " + vehicleState);
    }


}
