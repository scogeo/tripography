package com.tripography.providers.tesla;

import com.rumbleware.tesla.api.TeslaPortal;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import java.security.CryptoPrimitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gscott
 */
public class TeslaVehicleProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(TeslaVehicleProvider.class);
    TeslaPortal teslaPortal = new TeslaPortal();


    @Test
    public void testEncryptCookie() throws Exception {

        TeslaVehicleProvider provider = new TeslaVehicleProvider();

        String cookie = "goodbye world";

        byte[] encrypted = provider.encryptCookie(cookie);
        assertEquals(cookie, provider.decryptCookie(encrypted));


    }

    @Test
    public void testLogin() throws Exception {

        String username = "george.scott@gmail.com";

        TeslaVehicleProvider provider = new TeslaVehicleProvider("george.scott@gmail.com", "kyrdn3yt3sla");

        provider.validate(teslaPortal);

        assertEquals(username, provider.getUsername());

        logger.info("expired " + provider.getExpires());
        assertNotNull(provider.getExpires());

    }

}
