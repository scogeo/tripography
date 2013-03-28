package com.rumbleware.tesla.api;

import com.sun.jersey.api.client.RequestBuilder;

/**
 * @author gscott
 */
public interface PortalCredentials {


    void sign(RequestBuilder builder);

    String getUsername();

}
