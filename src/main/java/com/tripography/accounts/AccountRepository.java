package com.tripography.accounts;

import com.rumbleware.accounts.UserAccountRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author gscott
 */
public interface AccountRepository extends UserAccountRepository<AccountDocument> {

}
