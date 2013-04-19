package com.tripography.vehicles;

import com.rumbleware.mongodb.DatedDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author gscott
 */
@Document(collection = "vehicles")
public class VehicleDocument extends DatedDocument implements Vehicle {

    @Field("owner")
    private String owner;

    @Field("name")
    private String name;

    @Field("vin")
    @Indexed
    private String vin;

    @Field("odometer")
    private OdometerReading odometer;


    @Override
    public String getAccountId() {
        return owner;
    }

    @Override
    public void setAccountId(String id) {
        this.owner = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVIN() {
        return vin;
    }

    public void setVIN(String vin) {
        this.vin = vin;
    }

    public OdometerReading getOdometer() {
        return odometer;
    }

    public void setOdometer(OdometerReading odometer) {
        this.odometer = odometer;
    }


}
