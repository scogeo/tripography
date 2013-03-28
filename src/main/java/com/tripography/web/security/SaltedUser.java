package com.tripography.web.security;

import com.tripography.accounts.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Principal;
import java.util.Collection;

/**
 * @author gscott
 */
public class SaltedUser extends User {

    private final String _id;

    SaltedUser(Account account, Collection<GrantedAuthority> authorities) {
        super(account.getUsername(), account.getHashedPassword(), true, true, true, true, authorities);
        _id = account.getId().toString();
    }

    public static SaltedUser fromPrincipal(Principal principal) {
        if (principal instanceof Authentication) {
            Object o = ((Authentication) principal).getPrincipal();
            if (o instanceof SaltedUser) {
                return (SaltedUser)o;
            }
        }
        return null;
    }

    public static String userIdFromPrincipal(Principal principal) {
        SaltedUser user = fromPrincipal(principal);
        return user != null ? user.getId() : null;
    }

    public String getSalt() {
        return PasswordUtil.getSalt(_id);
    }

    public String getId() {
        return _id;
    }

    // Shut up find bugs.
    @Override
    public boolean equals(Object that) {
        return super.equals(that);
    }

    // Shut up find bugs.
    @Override
    public int hashCode() {
        return super.hashCode();
    }

}