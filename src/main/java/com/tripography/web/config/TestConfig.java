package com.tripography.web.config;

import com.rumbleware.web.config.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author gscott
 */
@Configuration
@Profile(Profiles.TEST)
public class TestConfig {

    @Bean
    public String cloudFrontHostName() {
        return "//d11xx7b36sm23b.cloudfront.net";
    }
}
