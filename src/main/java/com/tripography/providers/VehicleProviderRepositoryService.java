package com.tripography.providers;

import com.rumbleware.dao.BasicRepositoryService;
import com.rumbleware.tesla.TeslaVehicle;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gscott
 */
@Service("providerService")
public class VehicleProviderRepositoryService extends BasicRepositoryService<TeslaVehicleProvider, VehicleProviderRepository> implements VehicleProviderService {

    @Autowired
    public VehicleProviderRepositoryService(VehicleProviderRepository vehicleProviderRepository) {
        super(TeslaVehicleProvider.class, vehicleProviderRepository);
    }

    @Override
    public List<TeslaVehicleProvider> findAll() {
        return repository.findAll();
    }
}