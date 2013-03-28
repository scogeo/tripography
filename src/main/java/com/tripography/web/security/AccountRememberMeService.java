package com.tripography.web.security;

import com.tripography.accounts.Account;
import com.tripography.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author gscott
 */
@Service("rememberMeServices")
public class AccountRememberMeService extends AbstractRememberMeServices {

    private static final Logger logger = Logger.getLogger(AccountRememberMeService.class.getName());

    @Autowired
    private AccountService accountService;

    private AccountUserDetailsService userDetailsService;

    private SecureRandom random;

    public static final int DEFAULT_SERIES_LENGTH = 16;
    public static final int DEFAULT_TOKEN_LENGTH = 16;

    @Autowired
    public AccountRememberMeService(AccountUserDetailsService userDetailsService) throws Exception {
        super("token", userDetailsService);
        random = SecureRandom.getInstance("SHA1PRNG");
        // This needs to match the key set in the XML config file.  It is irrelevant what it is set to as long
        // as they match.

        setCookieName("auth_token");
        setParameter("remember_me");
        setUseSecureCookie(false);

        this.userDetailsService = userDetailsService;
    }

    /**
     * Locates the presented cookie data in the token repository, using the series id.
     * If the data compares successfully with that in the persistent store, a new token is generated and stored with
     * the same series. The corresponding cookie value is set on the response.
     *
     * @param cookieTokens the series and token values
     *
     * @throws org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException if there is no stored token corresponding to the submitted cookie, or
     * if the token in the persistent store has expired.
     * @throws org.springframework.security.web.authentication.rememberme.InvalidCookieException if the cookie doesn't have two tokens as expected.
     * @throws org.springframework.security.web.authentication.rememberme.CookieTheftException if a presented series value is found, but the stored token is different from the
     * one presented.
     */
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        Account account = accountService.findByPersistentTokenSeries(presentedSeries);
        PersistentLoginToken token = null;

        if (account != null) {
            for (PersistentLoginToken t : account.getLoginTokens()) {
                if (t.getSeries().equals(presentedSeries)) {
                    token = t;
                    break;
                }
            }
        }

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getToken())) {
            // Token doesn't match series value. Delete all logins for this user and throw an exception to warn them.
            accountService.deleteAllPersistentTokensForUser(account.getId());

            throw new CookieTheftException(messages.getMessage("PersistentTokenBasedRememberMeServices.cookieStolen",
                    "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }

        if (token.getDate().getTime() + getTokenValiditySeconds()*1000L < System.currentTimeMillis()) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Refreshing persistent login token for user '" + account.getUsername() + "', series '" +
                    token.getSeries() + "'");
        }

        PersistentLoginToken newToken = new PersistentLoginToken(token.getSeries(), generateTokenData(), new Date());

        try {
            accountService.updatePersistentLoginTokenForUser(account.getId(), newToken);
            addCookie(newToken, request, response);
        } catch (DataAccessException e) {
            logger.severe("Failed to update token: " + e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        return userDetailsService.createFromAccount(account);
    }

    /**
     * Creates a new persistent login token with a new series number, stores the data in the
     * persistent token repository and adds the corresponding cookie to the response.
     *
     */
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        PersistentLoginToken persistentToken = new PersistentLoginToken(generateSeriesData(), generateTokenData(), new Date());
        try {
            SaltedUser user = (SaltedUser)successfulAuthentication.getPrincipal();
            accountService.addPersistentLoginTokenForUser(user.getId(), persistentToken);
            addCookie(persistentToken, request, response);
        } catch (DataAccessException e) {
            logger.severe("Failed to save persistent token " + e);

        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);

        if (authentication != null && authentication.getPrincipal() instanceof SaltedUser) {
            String id = ((SaltedUser)authentication.getPrincipal()).getId();

            logger.info("Removing token for " + id);
            try {
                accountService.deleteAllPersistentTokensForUser(id);
            }
            catch (Exception e) {
                logger.severe("Could not delete persistent tokens for user " + id);
            }
        }

    }

    protected String generateSeriesData() {
        try {
            byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
            random.nextBytes(newSeries);
            return new String(Base64.encode(newSeries), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    protected String generateTokenData() {
        try {
            byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
            random.nextBytes(newToken);
            return new String(Base64.encode(newToken), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addCookie(PersistentLoginToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] {token.getSeries(), token.getToken()}, getTokenValiditySeconds(), request, response);
    }

    @Override
    public void setTokenValiditySeconds(int tokenValiditySeconds) {
        Assert.isTrue(tokenValiditySeconds > 0, "tokenValiditySeconds must be positive for this implementation");
        super.setTokenValiditySeconds(tokenValiditySeconds);
    }
}
