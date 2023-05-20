package com.exercise.travel.config;

import org.apache.commons.csv.CSVFormat;

import java.text.SimpleDateFormat;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static final Double FARE_A_B = 4.50;
    public static final Double FARE_B_C = 6.25;
    public static final Double FARE_A_C = 8.45;
    public static final String TEMP_PATH = "tmp/";

    public static final String MISSING_PAN = "Touch was missing PAN";
    public static final String INVALID_PAN = "Invalid PAN";

    private Constants() {}
}
