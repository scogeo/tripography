package com.tripography.web.security;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author gscott
 */
public class PersistentLoginToken {

    @Field("s")
    private final String series;

    @Field("t")
    private final String token;

    @Field("d")
    private final Date date;

    public PersistentLoginToken(String series, String token, Date date) {
        this.series = series;
        this.token = token;
        this.date = new Date(date.getTime());
    }

    public String getSeries() {
        return series;
    }

    public String getToken() {
        return token;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }
}
