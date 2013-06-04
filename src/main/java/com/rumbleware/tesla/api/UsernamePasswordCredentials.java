package com.rumbleware.tesla.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.net.HttpCookie;
import java.util.Date;
import java.util.List;

/**
 * @author gscott
 */
public class UsernamePasswordCredentials implements PortalCredentials {

    private static final Logger logger = LoggerFactory.getLogger(UsernamePasswordCredentials.class);

    private final String username;
    private final String password;

    private HttpCookie httpCookie;
    private Cookie cookie;

    private String host;

    public UsernamePasswordCredentials(String username, String password) {
        this(username, password, TeslaPortal.DEFAULT_PORTAL_HOST);
    }

    public UsernamePasswordCredentials(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.host = hostname;
    }

    private void login() {
        Client c = Client.create();
        //c.addFilter(new LoggingFilter(System.out));
        c.setFollowRedirects(false);

        Form loginForm = new Form();
        loginForm.add("user_session[email]", username);
        loginForm.add("user_session[password]", password);

        WebResource resource = c.resource(host + "/login");

        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, loginForm);

        MultivaluedMap<String, String> headers = response.getHeaders();

        HttpCookie credentials = null;

        for (String value : headers.get("Set-Cookie")) {
            List<HttpCookie> cookies = HttpCookie.parse(value);

            for (HttpCookie cookie : cookies) {
                if (cookie.getName().equals(TeslaPortal.USER_CREDENTIAL_COOKIE)) {
                    credentials = cookie;
                    this.httpCookie = cookie;
                    this.cookie = new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        }

        if (credentials == null) {
            throw new IllegalStateException("Not logged in!");
        }
    }


    public HttpCookie checkLogin() {
        if (httpCookie == null) {
            login();
        }
        return httpCookie;
    }

    @Override
    public void sign(RequestBuilder builder) {
        if (cookie == null) {
            login();
        }
        builder.cookie(cookie);
    }

    @Override
    public String getUsername() {
        return username;
    }
}
