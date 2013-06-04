package com.rumbleware.tesla.api;

/**
 * Thrown when an operation fails.
 * @author gscott
 */
public class TeslaPortalOperationException extends TeslaPortalException {

    public TeslaPortalOperationException() {
    }

    public TeslaPortalOperationException(String message) {
        super(message);
    }

    public TeslaPortalOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeslaPortalOperationException(Throwable cause) {
        super(cause);
    }
}
