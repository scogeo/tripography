package com.tripography.web.config;

import com.rumbleware.web.config.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author gscott
 */
@Configuration
@Profile(Profiles.PRODUCTION)
public class ProductionConfig {

    @Bean
    public String cloudFrontHostName() {
        return "//d2rkk7xaam7arg.cloudfront.net";
    }
}