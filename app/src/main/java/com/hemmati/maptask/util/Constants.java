package com.hemmati.maptask.util;

public class Constants {
    private static final String PACKAGE_NAME = "com.hemmati.maptask";

    public static final int LOCATION_SERVICE_ID = 12;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final String ACTION_START_LOCATION_SERVICE = "start service";
    public static final String ACTION_STOP_LOCATION_SERVICE = "stop service";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_EXIT = "exit";
    public static final String LAT_KEY = "lat";
    public static final String LNG_KEY = "lng";
    public static final String SEARCH_RESULT_KEY = "search result";
}
