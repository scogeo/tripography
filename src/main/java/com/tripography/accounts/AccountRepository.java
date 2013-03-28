package com.tripography.accounts;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author gscott
 */
public interface AccountRepository extends MongoRepository<AccountDocument, ObjectId> {

    public static final String EXCLUDE_NESTED = "{ 't': 0 }";
    // Queries by lowercase username field,  The passed in parameter should be lowercased.
    // By default we do not query nested items
    @Query(value = "{ 'U' : ?0 }", fields = EXCLUDE_NESTED)
    AccountDocument findByUsername(String username);

    // Queries the lowercase email field.
    @Query(value = "{ 'E' : ?0 }", fields = EXCLUDE_NESTED)
    AccountDocument findByEmail(String email);

    @Query("{ 't' : {'$elemMatch' : { 's': ?0 } } }")
    AccountDocument findByPersistentTokenSeries(String series);

}
