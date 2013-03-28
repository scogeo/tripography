package com.rumbleware.tesla.api;

import com.sun.jersey.api.client.RequestBuilder;

import javax.ws.rs.core.Cookie;

/**
 * @author gscott
 */
public class CookieCredentials implements PortalCredentials {

    private final String username;
    private final Cookie cookie;

    public CookieCredentials(String username, String cookie) {
        this.username = username;
        this.cookie = new Cookie(Portal.USER_CREDENTIAL_COOKIE, cookie);
    }

    @Override
    public void sign(RequestBuilder builder) {
        builder.cookie(cookie);
    }

    @Override
    public String getUsername() {
        return username;
    }
}
