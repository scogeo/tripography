package com.tripography.telemetry.events;

/**
 * @author gscott
 */
public interface TelemetryEventListener<T extends TelemetryEvent> {

    public void processEvent(T event);

}
