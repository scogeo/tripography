package com.tripography.web.config;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.tripography.vehicles.VehicleReadConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.*;

/**
 * @author gscott
 */
@Configuration
@EnableMongoRepositories({"com.tripography", "com.rumbleware.invites"})
public class TripDbConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "tripdb";
    }

    @Bean
    @Override
    public Mongo mongo() throws Exception {
        return new Mongo("127.0.0.1");
    }

    @Override
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate template = super.mongoTemplate();
        template.setWriteConcern(WriteConcern.SAFE);
        return template;
    }

    /*
    @Override
    protected String getMappingBasePackage() {
        return "com.tripography";
    }
    */

    /*
    @Bean
    @Override
    public CustomConversions customConversions() {

            List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
            converters.add(new VehicleReadConverter(mappingMongoConverter()));
            return new CustomConversions(converters);

    }
    */

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        MappingMongoConverter converter = super.mappingMongoConverter();
        converter.setTypeMapper(new DefaultMongoTypeMapper(null, converter.getMappingContext()));
        return converter;
    }



}
