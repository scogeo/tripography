package com.tripography.web.config;

import com.rumbleware.accounts.AccountConfigurationSupport;
import com.rumbleware.accounts.UserAccountService;
import com.tripography.accounts.Account;
import com.tripography.accounts.AccountRepository;
import com.tripography.accounts.AccountRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
public class AccountConfig extends AccountConfigurationSupport<Account> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Bean(name="accountService")
    public UserAccountService<Account> getAccountService() {
        return new AccountRepositoryService(accountRepository);
    }
}
