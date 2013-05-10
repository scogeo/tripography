package com.tripography.vehicles;

import com.mongodb.DBObject;
import com.tripography.providers.tesla.TeslaVehicleDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * @author gscott
 */
public class VehicleReadConverter implements Converter<DBObject, Vehicle> {

    private static Logger logger = LoggerFactory.getLogger(VehicleReadConverter.class);

    private MappingMongoConverter converter;

    public VehicleReadConverter(MappingMongoConverter converter) {
        this.converter = converter;
    }

    @Override
    public Vehicle convert(DBObject source) {
        return converter.read(TeslaVehicleDocument.class, source);

        //return new VehicleDocument();
    }
}
