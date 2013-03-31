package com.tripography.web.config;

import com.rumbleware.web.config.BaseApplicationConfigurationSupport;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
/*
@ComponentScan({"com.tripography.web.controller",
        "com.tripography.accounts",
        "com.tripography.web.security"})
        */
public class TripographyConfig extends BaseApplicationConfigurationSupport {

    /*
    private static final Logger logger = LoggerFactory.getLogger(TripographyConfig.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AWSCredentials defaultAWSCredentials;

*/
    /*
    @Bean
    public StorageService getProfileImageStorageService() {
        StorageService storageService = new StorageService();
        storageService.setBucketName("profiles.fitunity.com");
        return storageService;
    }
    */

    /*

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
