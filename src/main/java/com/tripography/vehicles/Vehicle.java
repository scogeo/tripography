package com.tripography.vehicles;

import com.rumbleware.dao.DatedObject;

/**
 * @author gscott
 */
public interface Vehicle extends DatedObject {

    public String getAccountId();
    public void setAccountId(String id);

    public String getName();
    public void setName(String name);

    public String getVIN();
    public void setVIN(String vin);

    public OdometerReading getOdometer();
    public void setOdometer(OdometerReading odometer);


}
