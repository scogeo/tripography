package com.tripography.accounts;

import com.rumbleware.accounts.UserAccountDocument;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author gscott
 */
@Document(collection = "accounts")
public class AccountDocument extends UserAccountDocument implements Account {


    @Field("providers")
    private List<TeslaVehicleProvider> vehicleProviders;

    public AccountDocument() {
        super();
    }

    @PersistenceConstructor
    public AccountDocument(ObjectId id) {
        super(id);
    }

    public List<TeslaVehicleProvider> getVehicleProviders() {
        return vehicleProviders;
        //return null;
    }


}
