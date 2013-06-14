package com.tripography.vehicles;

import com.rumbleware.maps.OSMAddress;
import com.rumbleware.mongodb.DatedDocument;
import com.tripography.providers.tesla.TeslaVehicleDocument;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author gscott
 */
@Document(collection = "vehicles")
public class Vehicle extends DatedDocument {

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

    @Field("ra")
    private Set<String> regionAggregates = new HashSet<>();

    @Field("va")
    private Set<String> vehicleAggregates = new HashSet<>();

    @Field("r")
    private Map<String, String> osmRegion = new HashMap<>();

    @Transient
    private Set<String> aggregateGroups;

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

    public void setRegionAggregates(Set<String> regionAggregates) {
        this.regionAggregates = regionAggregates;
    }

    public String getMakeAndModel() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tesla Model S");
        if (details.getOptions().contains("TR01")) {
            builder.append(" Signature");
        }
        if (details.getOptions().contains("PF01")) {
            builder.append(" Performance");
        }
        if (details.getOptions().contains("BT85")) {
            builder.append(" (85 kWh)");
        }
        if (details.getOptions().contains("BT60")) {
            builder.append(" (60 kWh)");
        }
        if (details.getOptions().contains("BT40")) {
            builder.append(" (40 kWh)");
        }
        return builder.toString();
    }

    public void setVehicleAggregates(Set<String> vehicleAggregates) {
        this.vehicleAggregates = vehicleAggregates;
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

    public void setRegion(OSMAddress address) {
        setOSMRegionField("country_code", address.getCountryCode());
        setOSMRegionField("state", address.getState());
        setOSMRegionField("county", address.getCounty());
        setOSMRegionField("city", address.getCity());
        setOSMRegionField("town", address.getTown());
        setOSMRegionField("village", address.getVillage());
    }

    public Map<String, String> getRegion() {
        return osmRegion;
    }

    private void setOSMRegionField(String name, String value) {
        if (value != null) {
            osmRegion.put(name, value);
        }
    }

    public String getHomeLocation() {
        if (osmRegion != null) {
            StringBuilder sb = new StringBuilder();
            String county = osmRegion.get("county");
            if (county != null) {
                sb.append(county);
                sb.append((", "));
            }
            sb.append(osmRegion.get("state"));
            return sb.toString();
        }
        else {
            return null;
        }
    }

    public Set<String> getAggregateGroupIds() {
        if (aggregateGroups != null) {
            return aggregateGroups;
        }
        else {
            Set<String> ag = new HashSet<>();
            ag.addAll(regionAggregates);
            ag.addAll(vehicleAggregates);
            ag.add("vehicle/" + getId());
            if (ownerId != null) {
                ag.add("account/" + ownerId);
            }
            ag.add("all");
            aggregateGroups = ag;
        }
        return aggregateGroups;
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
