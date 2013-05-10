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

    public static List<TeslaVehicle> getVehicles(Portal portal, PortalCredentials credentials) {

        VehicleDescriptor[] descriptors = portal.vehicles(credentials);

        List<TeslaVehicle> vehicles = new ArrayList<TeslaVehicle>(descriptors.length);

        for (VehicleDescriptor descriptor : descriptors) {
            vehicles.add(new TeslaVehicle(descriptor, credentials, portal));
        }

        return vehicles;

    }

}
