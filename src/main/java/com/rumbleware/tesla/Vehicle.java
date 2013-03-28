package com.rumbleware.tesla;

import com.rumbleware.tesla.api.CommandResponse;
import com.rumbleware.tesla.api.DriveState;
import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.VehicleDescriptor;

/**
 * @author gscott
 */
public class Vehicle {

    private final Portal portal;
    private final VehicleDescriptor descriptor;

    public Vehicle(VehicleDescriptor descriptor, Portal portal) {
        this.descriptor = descriptor;
        this.portal = portal;
    }

    public void honkHorn() {
        CommandResponse response = portal.honkHorn(descriptor.getId());
        System.out.println("response is " + response);
    }

    public DriveState driveState() {
        return portal.driveState(descriptor.getId());
    }
}
