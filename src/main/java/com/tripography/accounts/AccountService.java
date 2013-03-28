package com.tripography.accounts;

import com.tripography.web.security.PersistentLoginToken;

import java.util.List;

/**
 * @author gscott
 */
public interface AccountService {

    /**
     * Creates a new non-persistent account object.
     */
    Account newAccount();

    /**
     * Creates a new account specified by account.  The newly created accont will be persistend into
     * the account repository before this method returns.  Additional processing required for account creation
     * such as email notifications will happen asyncrhonously.
     *
     * @param account The account to be created.  It should specify the minimum required fields
     * @throws
     */
    void create(Account account);

    void update(Account account);

    void delete(String id);

    // Methods for stats
    long numberOfAccounts();

    /**
     * Checks to see if the specified email is available for user in a new account.
     * @param email
     * @return
     */
    boolean isEmailAvailable(String email);

    /**
     * Checks to see if the specified username is available for use in a new account.
     * @param username The username to check.
     * @return true if the specified username is available for use.
     */
    boolean isUsernameAvailable(String username);

    /**
     *
     * @param account - The prototype account to suggest a username for.
     * @return
     */
    List<String> suggestAvailableUsernames(Account account);

    // Methods to find objects

    Account findById(String id);

    Account findByUsername(String username);

    Account findByEmail(String email);

    Account findByPersistentTokenSeries(String series);

    //void setProfilePhoto(String id, byte[] image, String mimeType);

    /**
     * Adds a login token for the specified user name
     *
     * Note, should change to id for consistency, but spring auth currently uses username in principal
     * Maybe should change to username?
     *
     * @param id
     * @param token
     */
    void addPersistentLoginTokenForUser(String id, PersistentLoginToken token);

    /**
     * Updates the login token for the specified user.  The login token's series
     * is used as the key to update the token.
     * @param id
     * @param token
     */
    void updatePersistentLoginTokenForUser(String id, PersistentLoginToken token);

    /**
     * Deletes all persisten tokens associated with a particular user.
     * @param id The id of the user.
     */
    void deleteAllPersistentTokensForUser(String id);

    void updatePassword(String id, String clearTextPassword);
}
