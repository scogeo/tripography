package com.tripography.accounts;

import com.tripography.web.security.PersistentLoginToken;

import java.util.List;

/**
 * @author gscott
 */
public interface Account {

    String getUsername();

    String getId();

    String getFullname();

    void setFullname(String fullname);

    String getEmail();

    void setEmail(String email);

    void setUsername(String username);

    String getHashedPassword();

    void setClearTextPassword(String password);

    enum State {
        ACTIVE,
        SUSPENDED,
        DELETED
    }

    State getState();


    /**
     *
     * @return An immutable list of user roles. Note, these are additional roles beyond the standard "user" role
     */
    List<String> getRoles();

    /**
     *
     * @param roles The roles for the account.
     */
    void setRoles(List<String> roles);

    /**
     *
     */

    /**
     * Returns a list of all issued login tokens for the current account.  Note, that login tokens
     * must be updated using using a separate mechanism.
     *
     * @return A list of the user's login tokens
     */
    List<PersistentLoginToken> getLoginTokens();
}
