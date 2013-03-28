package com.tripography.web.config;

import com.amazonaws.auth.AWSCredentials;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * @author gscott
 */
public abstract class EnvironmentConfig implements ApplicationContextAware, ServletContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);

    @Autowired
    protected Environment environment;

    private ServletContext servletContext;

    private ApplicationContext applicationContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*
    @Bean
    public AssetConfig assetConfiguration() {
        AssetConfig assetConfig = new AssetConfig();
        assetConfig.setSourceResourceResolver(new ServletResourceResolver(servletContext, "/WEB-INF/assets"));
        assetConfig.setGenerateDigests(true);
        return assetConfig;
    }

    @Bean
    public AssetManager assetManager() {
        return new AssetManager(assetConfiguration());
    }
    */

    /**
     * The default AWS Credentials
     * @return The default aws credentials
     */
    @Bean
    public AWSCredentials defaultAWSCredentials() {
        logger.info("creating aws credentials");
        AWSCredentials credentials = buildDefaultCredentials();

        if (StringUtils.isEmpty(credentials.getAWSAccessKeyId()) ||
                StringUtils.isEmpty(credentials.getAWSSecretKey())) {
            logger.warn("Default AWS Credentials are not set.");
        }

        return credentials;
    }

    protected abstract AWSCredentials buildDefaultCredentials();

}
