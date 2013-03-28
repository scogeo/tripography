package com.tripography.mongodb;

import com.mongodb.MongoException;
import com.tripography.dao.DatedObject;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * @author gscott
 */
public class BaseDocument implements DatedObject {

    @Id
    @NotNull
    protected ObjectId id;

    public BaseDocument() {
        id = new ObjectId();
    }

    public BaseDocument(ObjectId id) {
        this.id = id;
    }

    public ObjectId getObjectId() {
        return id;
    }

    public String getId() {
        return id.toString();
    }

    public long getCreatedDate() {
        return id != null ? id.getTime() : 0;
    }

    private RuntimeException convertException(RuntimeException t) {
        if (t instanceof DuplicateKeyException) {
            DuplicateKeyException dke = (DuplicateKeyException) t;
            Throwable cause = dke.getCause();
            if (cause instanceof MongoException) {
                MongoException me = (MongoException)cause;
                System.out.println("code is " + me.getCode());
                if (me.getCode() == 11000) {
                    System.out.println("Found a dup key");
                    System.out.println("message is " + me.getMessage());

                }
            }

        }
        return t;
    }
}