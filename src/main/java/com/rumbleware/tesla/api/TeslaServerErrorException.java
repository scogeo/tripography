package com.rumbleware.tesla.api;

/**
 * Thrown when the API returns a 500 status code.  At this time this status code is treated as a permanent error
 * as it is present on auth failures and
 * @author gscott
 */
public class TeslaServerErrorException extends TeslaPortalException {

    public TeslaServerErrorException() {
    }

    public TeslaServerErrorException(String message) {
        super(message);
    }

    public TeslaServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeslaServerErrorException(Throwable cause) {
        super(cause);
    }
}
