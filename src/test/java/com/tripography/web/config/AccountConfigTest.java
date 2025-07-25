package com.tripography.web.config;

import com.tripography.accounts.AccountRepository;
import com.tripography.accounts.AccountRepositoryService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertNotNull;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Ignore
public class AccountConfigTest {

    @Autowired
    AccountRepositoryService accountService;

    @Autowired
    AccountRepository accountRepository;


    @Test
    public void testConfiguration() {

        assertNotNull(accountRepository);
        assertNotNull(accountService);
    }
}
