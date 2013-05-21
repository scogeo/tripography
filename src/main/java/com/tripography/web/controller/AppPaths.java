package com.tripography.web.controller;

/**
 * @author gscott
 */
public interface AppPaths {

    public static final String HOME = "/";

    public static final String SIGNUP = "/signup";

    public static final String INVITE = "/invite";

    public static final String LOGIN = "/login";

    public static final String SETTINGS = "/settings";

    public static final String PROFILE = "/profile";
    public static final String PROFILE_PATTERN = "/profile/{username}";
    public static final String PROFILE_VEHICLE_PATTERN = "/profile/{username}/vehicle/{vehicle}";

    public static final String CHARTS = "/data/charts";


}