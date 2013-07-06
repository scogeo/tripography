package com.rumbleware.email;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class EmailServiceTest {

    //private EmailService emailService = new EmailService("Tripography", "no-reply@tripography.com");
    @Configuration
    static class EmailServiceConfiguration {

        @Bean
        public EmailService getEmailService() {
            return null;
            //return new EmailService("Tripography", "no-reply@tripography.com");
        }
    }

    @Autowired
    EmailService emailSerivce;

    @Test
    public void testSendMessageToEmail() throws Exception {

    }
}