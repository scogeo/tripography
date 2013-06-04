package com.tripography.providers;

import com.mongodb.DBCollection;
import com.tripography.config.TripDbTestConfig;
import com.tripography.providers.tesla.TeslaVehicleDocument;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleRepository;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TripDbTestConfig.class)
public class VehicleProviderRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(VehicleProviderRepositoryTest.class);

    @Autowired
    private MongoTemplate template;

    @Autowired
    private VehicleProviderRepository repository;

    private DBCollection providers;

    @Before
    public void getCollection() {
        providers = template.getCollection("providers");
        deleteCollections();
        assertEquals(0, repository.count());
    }

    @After
    public void deleteCollections() {
        repository.deleteAll();
    }


    @Test
    public void testFindById() throws Exception {
        TeslaVehicleProvider provider = new TeslaVehicleProvider("user@example.com", "password");

        repository.save(provider);

        TeslaVehicleProvider result = repository.findOne(provider.getObjectId());

        assertEquals(provider.getObjectId(), result.getObjectId());
        assertEquals(provider.getUsername(), result.getUsername());

    }
    @Test
    public void testFindByAccountId() throws Exception {
        TeslaVehicleProvider provider = new TeslaVehicleProvider("user@example.com", "password");
        ObjectId accountId = new ObjectId();
        provider.setAccountId(accountId);

        repository.save(provider);

        TeslaVehicleProvider result = repository.findByAccountId(accountId);

        assertEquals(provider.getObjectId(), result.getObjectId());
        assertEquals(provider.getUsername(), result.getUsername());
        assertEquals(provider.getAccountId(), result.getAccountId());
    }
}
