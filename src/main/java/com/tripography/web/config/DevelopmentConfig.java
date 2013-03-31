package com.tripography.web.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author gscott
 */
//@Configuration
@Profile(Profiles.DEV)
public class DevelopmentConfig extends EnvironmentConfig {

    public static final String DEFAULT_AWS_ACCESS_KEY_PROPERTY = "aws.default.access_key";
    public static final String DEFAULT_AWS_SECRET_KEY_PROPERTY = "aws.default.secret_key";

    @Override
    protected AWSCredentials buildDefaultCredentials() {
        return new BasicAWSCredentials(environment.getProperty(DEFAULT_AWS_ACCESS_KEY_PROPERTY),
                environment.getProperty(DEFAULT_AWS_SECRET_KEY_PROPERTY));
    }
}
