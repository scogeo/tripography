package com.tripography.providers;

import com.rumbleware.dao.BasicService;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * @author gscott
 */
public interface VehicleProviderService extends BasicService<TeslaVehicleProvider> {

    List<TeslaVehicleProvider> findAll();

    TeslaVehicleProvider findByAccountId(ObjectId accountId);
}
