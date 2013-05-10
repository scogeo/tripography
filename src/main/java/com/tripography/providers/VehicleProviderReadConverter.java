package com.tripography.providers;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

/**
 * @author gscott
 */
public class VehicleProviderReadConverter implements Converter<DBObject, VehicleProvider> {

    @Override
    public VehicleProvider convert(DBObject source) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
