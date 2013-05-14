package com.tripography.web.config;

import com.rumbleware.web.config.BaseApplicationConfigurationSupport;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
@ComponentScan({"com.tripography.vehicles",
        "com.tripography.providers",
        "com.tripography.accounts",
        "com.tripography.telemetry"})
public class TripographyConfig extends BaseApplicationConfigurationSupport {

}
