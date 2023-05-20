package com.exercise.travel.dto;

public class TripEnums {
    public enum TouchType {
        ON,
        OFF;

        public static TouchType fromString(String value) {
            return value.equalsIgnoreCase("ON") ? TouchType.ON : TouchType.OFF;
        }
    }

    public enum StopID {
        StopA,
        StopB,
        StopC;

        public static StopID fromString(String value) {
            return StopID.valueOf(value);
        }
    }

    public enum TripStatus {
        COMPLETE,
        INCOMPLETE,
        CANCELLED
    }

}
