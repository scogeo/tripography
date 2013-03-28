package com.tripography.web.security;

import com.tripography.accounts.Account;
import com.tripography.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author gscott
 */
@Service("userDetailsService")
public class AccountUserDetailsService implements UserDetailsService {

    private static Logger logger = Logger.getLogger(AccountUserDetailsService.class.getName());

    @Autowired
    private AccountService accountService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = null;

        logger.info("lookgin up user " + username);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Looking up user " + username);
        }
        try {
            account = accountService.findByUsername(username);
        }
        catch(Throwable t) {
            logger.info("woops");
            logger.log(Level.INFO, "shit", t);

        }
        if (account == null) {
            logger.fine("User not found " + username);
            throw new UsernameNotFoundException("user not found");
        }
        logger.info("Got an cccount " + account);

        return createFromAccount(account);

    }

    public UserDetails createFromAccount(Account account) {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // Always add the user role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Add additional roles, following spring naming convetion.
        for (String role : account.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }

        return new SaltedUser(account, authorities);
    }
}