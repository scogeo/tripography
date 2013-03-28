package com.tripography.web.config;

/**
 * @author gscott
 */
public interface Profiles {

    // Running in a local development environment.
    String DEV = "development";

    // Running in a production environment
    String PRODUCTION = "production";

    // Test environment
    String TEST = "test";

    // Running under a continuous integration build server, mostly for integration testing.
    String BUILD = "build";

    // Running in the the AWS Cloud under Beanstalk.
    String AWS = "aws";

}
