package com.rumbleware.tesla;

import com.rumbleware.tesla.api.Portal;
import com.rumbleware.tesla.api.PortalCredentials;
import com.rumbleware.tesla.api.VehicleDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gscott
 */
public abstract class VehicleFactory {

    public static List<Vehicle> getVehicles(PortalCredentials credentials) {

        Portal portal = new Portal(credentials);

        VehicleDescriptor[] descriptors = portal.vehicles();

        List<Vehicle> vehicles = new ArrayList<Vehicle>(descriptors.length);

        for (VehicleDescriptor descriptor : descriptors) {
            vehicles.add(new Vehicle(descriptor, portal));
        }

        return vehicles;

    }

}
