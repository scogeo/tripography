package com.tripography.mongodb;

import com.tripography.dao.DatedObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author gscott
 */
public class DatedDocument extends BaseDocument implements DatedObject {

    @Field("c") @Indexed
    protected Date createdDate;

    public DatedDocument() {
        super();
        createdDate = new Date();
    }

    public DatedDocument(ObjectId id) {
        super(id);
    }

    @Override
    public long getCreatedDate() {
        return createdDate.getTime();
    }

}
