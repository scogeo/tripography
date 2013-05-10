package com.tripography.vehicles;

import com.rumbleware.mongodb.DatedDocument;
import com.tripography.providers.tesla.TeslaVehicleDocument;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.TimeZone;

/**
 * @author gscott
 */
@Document(collection = "vehicles")
public class Vehicle<Details> extends DatedDocument {

    @Field("a")
    @Indexed
    @NotNull
    protected ObjectId ownerId;

    @Field("n")
    protected String name;

    @Field("v")
    @Indexed
    @NotEmpty
    protected String vin;

    @Field("o")
    protected OdometerReading odometer;

    @Field("p")
    @NotNull
    protected ObjectId providerId;

    @Field("d")
    protected TeslaVehicleDocument.Details details;

    @Transient
    private TimeZone timeZone;

    @Field("t")
    private String timeZoneId;

    @Field("l")
    private Boolean locationEnabled;

    public TeslaVehicleDocument.Details getDetails() {
        return details;
    }

    public String getAccountId() {
        return ownerId.toString();
    }

    public void setAccountId(String id) {
        this.ownerId = new ObjectId(id);
    }

    public String getProviderId() {
        return providerId.toString();
    }

    public void setProviderId(String providerId) {
        this.providerId = new ObjectId(providerId);
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

    public Boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(Boolean value) {
        locationEnabled = value;
    }

    public TimeZone getTimeZone() {
        if (timeZone != null) {
            return timeZone;
        }
        if (timeZoneId != null) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
            return timeZone;
        }
        return null;
    }

    public void setTimeZone(TimeZone tz) {
        this.timeZone = tz;
        this.timeZoneId = tz.getID();
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", vin='" + vin + '\'' +
                ", odometer=" + odometer +
                ", providerId=" + providerId +
                ", details=" + details +
                ", timeZone=" + timeZone +
                ", timeZoneId='" + timeZoneId + '\'' +
                ", locationEnabled=" + locationEnabled +
                '}';
    }
}
