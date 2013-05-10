package com.rumbleware.tesla;

import com.rumbleware.tesla.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Double getOdometer() {
        Double value = null;

        OdometerReader reader = new OdometerReader();
        openStream(reader);

        value = reader.getOdometer();

        return value;
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

        private Double odometer = null;

        public Double getOdometer() {
            synchronized (this) {
                while (odometer == null) {
                    try {
                        this.wait(5000);
                    }
                    catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }
            return odometer;
        }

        @Override
        public boolean handleData(StreamData data) {
            synchronized (this) {
                odometer = data.getOdometer();
                this.notifyAll();
            }
            logger.info("Yo, got an odometer " + odometer);
            return false;

        }

        @Override
        public void exceptionOccured(Throwable t) {

        }

        @Override
        public void streamClosed() {

        }
    }
}
