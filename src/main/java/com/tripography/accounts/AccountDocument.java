package com.tripography.accounts;

import com.rumbleware.accounts.UserAccountDocument;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gscott
 */
@Document(collection = "accounts")
public class AccountDocument extends UserAccountDocument implements Account {

    public AccountDocument() {
        super();
    }

    @PersistenceConstructor
    public AccountDocument(ObjectId id) {
        super(id);
    }

}
