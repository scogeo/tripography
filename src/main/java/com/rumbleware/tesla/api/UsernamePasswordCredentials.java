package com.rumbleware.tesla.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.util.List;

/**
 * @author gscott
 */
public class UsernamePasswordCredentials implements PortalCredentials {

    private final String username;
    private final String password;
    private Cookie cookie;
    private String host;
    private String protocol;

    public UsernamePasswordCredentials(String username, String password) {
        this(username, password, Portal.DEFAULT_HOST, Portal.DEFAULT_SCHEME);
    }

    public UsernamePasswordCredentials(String username, String password, String hostname, String protocol) {
        this.username = username;
        this.password = password;
        this.host = hostname;
        this.protocol = protocol;
    }

    private void login() {
        Client c = Client.create();
        c.addFilter(new LoggingFilter(System.out));
        c.setFollowRedirects(false);

        Form loginForm = new Form();
        loginForm.add("user_session[email]", username);
        loginForm.add("user_session[password]", password);

        WebResource resource = c.resource(protocol + "://" + host + "/login");

        System.out.println("Resource is " + resource);
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, loginForm);

        for (Cookie cookie : response.getCookies()) {
            System.out.println("Got cookie " + cookie);

        }

        MultivaluedMap<String, String> headers = response.getHeaders();

        System.out.println("headers are " + headers);

        List<NewCookie> loginCookies = response.getCookies();

        NewCookie credentials = null;
        for (NewCookie cookie : response.getCookies()) {
            if (cookie.getName().equals(Portal.USER_CREDENTIAL_COOKIE)) {
                System.out.println("cookie expiration si " + cookie.getMaxAge());
                credentials = cookie;
                this.cookie = cookie;
            }
        }


        System.out.println("response " + response.toString());

        System.out.println("got cookies " + loginCookies);

        System.out.println("cookie is " + cookie);

        if (credentials == null) {
            throw new IllegalStateException("Not logged in!");
        }
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
