package com.tripography.providers.tesla;

import com.rumbleware.tesla.api.VehicleDescriptor;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleProviderInfo;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gscott
 */
public class TeslaVehicleDocument extends Vehicle<TeslaVehicleDocument.Details> {

    @PersistenceConstructor
    public TeslaVehicleDocument() {

    }

    public TeslaVehicleDocument(VehicleDescriptor descriptor) {
        setVIN(descriptor.getVin());
        setName("Test");
        this.details = new Details(descriptor);
    }

    public static class Details extends VehicleProviderInfo {

        @Field("pid")
        private String portalId;

        @Field("vid")
        private String vehicleId;

        @Field("options")
        private Set<String> options = new HashSet<String>();

        @PersistenceConstructor
        public Details() {
        }

        public Details(VehicleDescriptor descriptor) {
            this.portalId = descriptor.getId();
            this.vehicleId = descriptor.getVehicleId();
            for (String option : descriptor.getOptionCodes().split(",")) {
                options.add(option);
            }
        }

        public String getPortalId() {
            return portalId;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        public Set<String> getOptions() {
            return options;
        }
    }

}
