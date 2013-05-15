package com.rumbleware.tesla;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.rumbleware.tesla.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * @author gscott
 */
public class TeslaVehicle {

    private static final Logger logger = LoggerFactory.getLogger(TeslaVehicle.class);

    private final Portal portal;
    private VehicleDescriptor descriptor;
    private final PortalCredentials credentials;

    public TeslaVehicle(VehicleDescriptor descriptor, PortalCredentials portalCredentials, Portal portal) {
        this.descriptor = descriptor;
        this.portal = portal;
        this.credentials = portalCredentials;
    }

    public void honkHorn() {
        CommandResponse response = portal.honkHorn(credentials, descriptor.getId());
        System.out.println("response is " + response);
    }

    public DriveState driveState() {
        return portal.driveState(credentials, descriptor.getId());
    }

    public ListenableFuture<Double> getOdometer() {
        OdometerReader reader = new OdometerReader();
        openStream(reader);
        return reader.getOdometer();
    }

    public void openStream(final StreamDataHandler handler) {
        portal.wakeUp(credentials, descriptor.getId());
        refreshDescriptor();
        portal.stream(credentials, descriptor, handler);
    }

    private void refreshDescriptor() {
        descriptor = portal.vehicle(credentials, descriptor.getId());
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "descriptor=" + descriptor +
                '}';
    }

    public class OdometerReader implements StreamDataHandler {

        private SettableFuture<Double> future = SettableFuture.create();

        public ListenableFuture<Double> getOdometer() {
            return future;
        }

        @Override
        public boolean handleData(StreamData data) {
            future.set(data.getOdometer());
            return false;
        }

        @Override
        public void exceptionOccured(Throwable t) {
            future.setException(t);
        }

        @Override
        public void streamClosed() {
            if (!future.isDone()) {
                future.set(null);
            }
        }
    }
}
