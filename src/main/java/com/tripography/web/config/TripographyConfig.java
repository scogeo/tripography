package com.tripography.web.config;

import com.rumbleware.tesla.api.TeslaPortal;
import com.rumbleware.web.config.BaseApplicationConfigurationSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
@ComponentScan({"com.tripography.vehicles",
        "com.tripography.providers",
        "com.rumbleware.invites",
        "com.tripography.accounts",
        "com.tripography.telemetry"})
public class TripographyConfig extends BaseApplicationConfigurationSupport {

    @Bean
    public TeslaPortal getTeslaPortal() {
        return new TeslaPortal();

    }
}
