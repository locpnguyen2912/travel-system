package com.exercise.travel.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.exercise.travel.config.Constants.*;
import static com.exercise.travel.config.Constants.INVALID_PAN;

public class TouchRecord {
    private int id;
    private Date dateTime;
    private TripEnums.TouchType touchType;
    private TripEnums.StopID stopID;
    private String companyID;
    private String busID;
    private String pan;

    public TouchRecord() {
    }

    public TouchRecord(int id, Date dateTime, TripEnums.TouchType touchType, TripEnums.StopID stopID, String companyID, String busID, String pan) {
        this.id = id;
        this.dateTime = dateTime;
        this.touchType = touchType;
        this.stopID = stopID;
        this.companyID = companyID;
        this.busID = busID;
        this.pan = pan;
    }

    public String getDateString() {
        return new SimpleDateFormat("dd-MM-yyyy").format(dateTime);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public TripEnums.TouchType getTouchType() {
        return touchType;
    }

    public void setTouchType(TripEnums.TouchType touchType) {
        this.touchType = touchType;
    }

    public TripEnums.StopID getStopID() {
        return stopID;
    }

    public void setStopID(TripEnums.StopID stopID) {
        this.stopID = stopID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String validatePan() {
        if (pan.isEmpty()) {
            return MISSING_PAN;
        }

        // Remove any whitespace characters from the PAN string
        pan = pan.replaceAll("\\s", "");

        // Check if the PAN contains only numeric digits
        if (!pan.matches("\\d+")) {
            return INVALID_PAN;
        }
        return "";
    }
}
