package com.tripography.telemetry.analytics.cleaners;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.tripography.config.TripDbTestConfig;
import com.tripography.telemetry.analytics.DailyDistance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.junit.Assert.*;

/**
 *
 *
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TripDbTestConfig.class)
public class DailyDistanceCleanerTest implements DailyDistance {

    private static final Logger logger = LoggerFactory.getLogger(DailyDistanceCleanerTest.class);

    @Autowired
    private MongoTemplate mongo;

    @Test
    public void testClean() {
        DailyDistanceCleaner cleaner = new DailyDistanceCleaner(null, null, mongo);

        String document = "{ '_id' : '2013/all', '3' : { '3' : 4.2, '4' : 2.4 }, '4' : { '1' : 23.0 }}";

        DBObject testData = (DBObject) JSON.parse(document);

        DBCollection collection = mongo.getCollection(COLLECTION_NAME);

        collection.save(testData);

        cleaner.clean();

        DBObject result = mongo.findOne(query(where("_id").is("2013/all")), DBObject.class, COLLECTION_NAME);

        assertNotNull(result);

        assertEquals("2013/all", result.get("_id"));

        logger.info("migrated is " + result);

     }


}
