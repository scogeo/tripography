package com.tripography.vehicles;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.rumbleware.tesla.api.VehicleDescriptor;
import com.tripography.config.TripDbTestConfig;
import com.tripography.providers.tesla.TeslaVehicleDocument;
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

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TripDbTestConfig.class)
public class VehicleRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRepositoryTest.class);

    @Autowired
    private MongoTemplate template;

    @Autowired
    private VehicleRepository repository;

    private DBCollection vehicles;

    @Before
    public void getCollection() {
        vehicles = template.getCollection("vehicles");
        deleteCollections();
        assertEquals(0, repository.count());
    }

    @After
    public void deleteCollections() {
        repository.deleteAll();
    }

    private TeslaVehicleDocument createVehicle() {
        VehicleDescriptor descriptor = new VehicleDescriptor("123", "456", "789", "MS01,PS03", "5YJS00XXX", null, "online");

        TeslaVehicleDocument vehicle = new TeslaVehicleDocument(descriptor);

        return vehicle;
    }

    @Test
    public void testBasicInsert() throws Exception {
        // For now test Tesla docs
        TeslaVehicleDocument vehicle = createVehicle();

        repository.save(vehicle);

        assertEquals(1, repository.count());
        assertEquals(1, vehicles.count());

        DBObject object = vehicles.findOne(vehicle.getObjectId());
        assertNotNull(object);

        Set<String> keys = object.keySet();

        //logger.info("object is " + object);

        assertEquals(7, keys.size());
        assertEquals(vehicle.getObjectId(), object.get("_id"));
        assertEquals("5YJS00XXX", object.get("v"));


    }

    @Test
    public void testFindById() throws Exception {
        TeslaVehicleDocument vehicle = createVehicle();

        repository.save(vehicle);

        Vehicle result = repository.findOne(vehicle.getObjectId());

        assertEquals(vehicle.getObjectId(), result.getObjectId());
        assertEquals(vehicle.getVIN(), result.getVIN());

    }
}
