package com.tripography.accounts;

import com.rumbleware.accounts.UserAccountRepositoryService;
import com.tripography.providers.VehicleProvider;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.logging.Logger;

/**
 * Implementation of AccountService that stores accounts in a MongoDB Repository.  This is a fairly
 * light wrapper to abstract MongoDB from the rest of the application.
 *
 * Its main purpose is to provide validation enforcement prior to insertion into the Database.
 *
 */
public class AccountRepositoryService extends UserAccountRepositoryService<Account, AccountDocument> implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountRepositoryService.class.getName());

    public AccountRepositoryService(AccountRepository accountRepository) {
        super(AccountDocument.class, accountRepository);
    }

    /*
    @Override
    public void addVehicleProvider(String accountId, VehicleProvider provider) {

        logger.info("Updating provider " + accountId);

        mongo.updateFirst(new Query(Criteria.where("_id").is(accountId)), new Update().push("providers", provider), AccountDocument.class);

        logger.info("Updated provider");


    }
    */
}
