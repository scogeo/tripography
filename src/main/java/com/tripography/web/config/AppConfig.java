package com.tripography.web.config;

import com.amazonaws.auth.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Properties;

/**
 * @author gscott
 */
@Configuration
@ComponentScan({"com.tripography.web.controller",
        "com.tripography.accounts",
        "com.tripography.web.security"})
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AWSCredentials defaultAWSCredentials;

    /*
    @Bean
    public StorageService getProfileImageStorageService() {
        StorageService storageService = new StorageService();
        storageService.setBucketName("profiles.fitunity.com");
        return storageService;
    }
    */

    @Bean(name="authenticationProvider")
    public AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new ShaPasswordEncoder(256));
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("salt");
        provider.setSaltSource(saltSource);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setProtocol("aws");
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "aws");

        // Don't set these props if they are null, or spring container won't start.
        if (defaultAWSCredentials.getAWSAccessKeyId() != null && defaultAWSCredentials.getAWSSecretKey() != null) {
            props.setProperty("mail.aws.user", defaultAWSCredentials.getAWSAccessKeyId());
            props.setProperty("mail.aws.password", defaultAWSCredentials.getAWSSecretKey());
        }
        else {
            logger.warn("Mail service not configured properly, no AWS credentials.");
        }
        sender.setJavaMailProperties(props);
        sender.setDefaultEncoding("utf-8");
        return sender;
    }

    /*
    @Bean
    public AmazonS3Client getAmazonS3Client() {
        return new AmazonS3Client(defaultAWSCredentials);
    }
    */
}
