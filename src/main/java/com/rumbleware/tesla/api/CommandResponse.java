package com.rumbleware.tesla.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author gscott
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandResponse {

    @JsonProperty("reason") private String reason;
    @JsonProperty("result") private Boolean result;

    public String getReason() {
        return reason;
    }

    public Boolean getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "CommandResponse{" +
                "reason='" + reason + '\'' +
                ", result=" + result +
                '}';
    }
}
