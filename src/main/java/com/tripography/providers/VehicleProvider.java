package com.tripography.providers;

import com.rumbleware.mongodb.BaseDocument;
import com.rumbleware.tesla.api.TeslaPortal;
import com.tripography.vehicles.Vehicle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;
import java.util.List;

/**
 * @author gscott
 */
@Document(collection = "providers")
public class VehicleProvider extends BaseDocument {

    @Field("vendor")
    private String vendor;

    @Field("account")
    @Indexed(unique = true)
    private ObjectId accountId;

    public VehicleProvider() {

    }

    public VehicleProvider(String vendor) {
        id = new ObjectId();
        this.vendor = vendor;
    }

    public String getAccountId() {
        return accountId.toString();
    }

    public void setAccountId(ObjectId accountId) {
        this.accountId = accountId;
    }


    public String getVendor() {
        return vendor;
    }

    public boolean updateVehicleLocation(TeslaPortal portal, Vehicle vehicle) {
        return false;
    }

    public List<Vehicle> getVehicles(TeslaPortal portal) {
        return Collections.emptyList();
    }
}
