package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDescriptor {

    @JsonProperty("id") private String id;
    @JsonProperty("user_id") private String userId;
    @JsonProperty("vehicle_id") private String vehicleId;
    @JsonProperty("option_codes") private String optionCodes;
    @JsonProperty("vin") private String vin;
    @JsonProperty("tokens") private List<String> tokens;
    @JsonProperty("state") private String state;

    public VehicleDescriptor() {

    }

    public VehicleDescriptor(String id, String userId, String vehicleId, String optionCodes, String vin, List<String> tokens, String state) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.optionCodes = optionCodes;
        this.vin = vin;
        this.tokens = tokens;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getOptionCodes() {
        return optionCodes;
    }

    public String getVin() {
        return vin;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "VehicleDescriptor{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", optionCodes='" + optionCodes + '\'' +
                ", vin='" + vin + '\'' +
                ", tokens=" + tokens +
                ", state='" + state + '\'' +
                '}';
    }
}
