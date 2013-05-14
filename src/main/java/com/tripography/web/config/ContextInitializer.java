package com.tripography.web.config;

import com.rumbleware.web.config.Profiles;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author gscott
 */
public class ContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    private static Logger logger = LoggerFactory.getLogger(ContextInitializer.class);

    public void initialize(ConfigurableWebApplicationContext configurableWebApplicationContext) {
        logger.info("Initializing application environment.");

        ConfigurableEnvironment environment = configurableWebApplicationContext.getEnvironment();


        // TODO remove this, after fixing mongo properties.
        //environment.getPropertySources().addFirst(getDevProps());

        String os = System.getProperty("os.name");

        if ("Mac OS X".equals(os)) {
            // Assume dev if on Mac OS X
            // TODO special case for build server?  Need to check env in that case.
            environment.addActiveProfile(Profiles.DEV);
            setDevelopemntProps(environment);
        }
        else if ("Linux".equals(os)) {
            // If we are on Linux, then check to see if on AWS Beanstalk, by looking at environment variables
            Properties props = System.getProperties();

            // We use PARAM1 env var to detect AWS and set the profile.
            if (props.containsKey("PARAM1")) {

                // Assume we are on AWS
                environment.addActiveProfile(Profiles.AWS);

                String awsEnvName = props.getProperty("PARAM1");
                if (StringUtils.isNotEmpty(awsEnvName)) {
                    environment.addActiveProfile(awsEnvName);
                }
            }
        }

        logger.info("Active profiles are " + ArrayUtils.toString(environment.getActiveProfiles()));
    }

    private void setDevelopemntProps(ConfigurableEnvironment environment) {
        // TODO Allow for an override property to specify a different location
        String filename = System.getProperty("user.home") + File.separator + ".tripography";

        File file = new File(filename);
        if (file.exists() && file.canRead()) {
            logger.info("loading properties from " + file);
            try {
                PropertySource props = new ResourcePropertySource("developerProperties", file.toURI().toString());
                environment.getPropertySources().addFirst(props);
            }
            catch (IOException e) {
                logger.error("Could not load property file", e);
            }
        }
        else {
            logger.warn("No developer properties found at " + filename);
        }
    }

    private MapPropertySource getDevProps() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        // Should we load from a file?
        //props.put("mongodb.host", "127.0.0.1"); // This needs to be 127.0.0.1 and not localhost
        //props.put("mongodb.db", "tripdb");
        return new MapPropertySource("runtimeEnvironemnt", props);
    }
}
