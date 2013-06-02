package com.tripography.accounts;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.tripography.config.TripDbTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TripDbTestConfig.class)
public class AccountRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryTest.class);

    @Autowired
    private AccountRepository repository;

    @Autowired
    private MongoTemplate template;

    private DBCollection accounts;

    @Before
    public void getCollection() {
        accounts = template.getCollection("accounts");
        deleteCollections();
        assertEquals(0, repository.count());
    }

    @After
    public void deleteCollections() {
        repository.deleteAll();
    }

    @Test
    public void testIndexes() {
        List<DBObject> indexes = accounts.getIndexInfo();

        // There should be 4 indexes
        assertEquals(5, indexes.size());

        // Build hash of indexes
        Map<String, DBObject> indexesByName = new HashMap<String, DBObject>();

        for (DBObject index : indexes){
            indexesByName.put((String)index.get("name"), index);
        }

        // process each index
        DBObject index = null;
        DBObject key = null;

        // TODO cleanup and refactor, need to verify actual index keys, and other constraints
        index = indexesByName.get("_id_");
        assertNotNull(index);

        index = indexesByName.get("U");
        assertNotNull(index);
        assertEquals(true, index.get("unique"));

        index = indexesByName.get("E");
        assertNotNull(index);
        assertEquals(true, index.get("unique"));

        index = indexesByName.get("t.s");
        assertNotNull(index);

        index = indexesByName.get("c");
        assertNotNull(index);


    }

    @Test
    public void testBasicInsert() {
        // given an empty repo

        String email = "abc@foo.com";
        String username = "foo";

        // when a document is inserted
        AccountDocument account = new AccountDocument();
        account.setUsername(username);
        account.setEmail(email);
        repository.save(account);

        // then the object is present and count has increased
        assertEquals(1, repository.count());

        DBObject object = accounts.findOne(account.getObjectId());
        assertNotNull(object);

        Set<String> keys = object.keySet();

        // Key size should be 5, but Spring Data puts _class field currently.  Need to remove eventually.
        assertEquals(6, keys.size());
        assertEquals(username, object.get("u"));
        assertEquals(username, object.get("U"));
        assertEquals(email, object.get("e"));
        assertEquals(email, object.get("E"));
        assertEquals(account.getObjectId(), object.get("_id"));
        assertEquals(new Date(account.getCreatedDate()), object.get("c"));
        assertFalse(keys.contains("_class")); // Verify the the _class key is absent.
    }

    @Test(expected = DuplicateKeyException.class)
    public void testDupUsername() {
        // when a document is inserted

        AccountDocument account = new AccountDocument();
        account.setUsername("foo");
        account.setEmail("abc@foo.com");
        repository.save(account);

        // then the object is present and count has increased
        assertEquals(1, repository.count());

        AccountDocument dup = new AccountDocument();
        dup.setUsername(account.getUsername());
        dup.setEmail("abc@bar.com");

        repository.save(dup);

        logger.info("After save, no problems!");


    }


}
