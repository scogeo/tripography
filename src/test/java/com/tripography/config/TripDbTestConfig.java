package com.tripography.config;

import com.tripography.web.config.TripDbConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
public class TripDbTestConfig extends TripDbConfig {

    @Override
    protected String getDatabaseName() {
        return "unittest";
    }
}
