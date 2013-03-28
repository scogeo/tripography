package com.tripography.web.security;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * @author gscott
 */
public class PasswordUtil {

    // Global salt value for extra protection
    // Could be rotated occasionally based on date, etc.
    private static final String GLOBAL_SALT = "21a270d5f59c9b05813a72bb41707266";

    private static final ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);

    public static String getSalt(String id) {
        return GLOBAL_SALT + id;
    }

    public static String hashPassword(String id, String clearTextPassword) {
        return encoder.encodePassword(clearTextPassword, getSalt(id));
    }

}
