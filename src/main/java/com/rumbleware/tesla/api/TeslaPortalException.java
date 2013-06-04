package com.rumbleware.tesla.api;

/**
 * @author gscott
 */
public class TeslaPortalException extends RuntimeException {

    public TeslaPortalException() {
    }

    public TeslaPortalException(String message) {
        super(message);
    }

    public TeslaPortalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeslaPortalException(Throwable cause) {
        super(cause);
    }

    public TeslaPortalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
