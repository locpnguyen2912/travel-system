package com.exercise.travel.dto;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

import static com.exercise.travel.config.Constants.DATE_FORMAT;

public class TripSummary {
    private Date started;
    private Date finished;
    private Long durationInSecond;
    private TripEnums.StopID startStopID;
    private TripEnums.StopID finishStopID;
    private Double chargeAmount;
    private String companyID;
    private String busID;
    private String hashedPan;
    private TripEnums.TripStatus status;
    private String reason = "";

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Long getDurationInSecond() {
        return durationInSecond;
    }

    public void setDurationInSecond(Long durationInSecond) {
        this.durationInSecond = durationInSecond;
    }

    public TripEnums.StopID getStartStopID() {
        return startStopID;
    }

    public void setStartStopID(TripEnums.StopID startStopID) {
        this.startStopID = startStopID;
    }

    public TripEnums.StopID getFinishStopID() {
        return finishStopID;
    }

    public void setFinishStopID(TripEnums.StopID finishStopID) {
        this.finishStopID = finishStopID;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
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

    public String getHashedPan() {
        return hashedPan;
    }

    public void setHashedPan(String hashedPan) {
        this.hashedPan = hashedPan;
    }

    public TripEnums.TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripEnums.TripStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String toCSVString() {
        if (finished == null || durationInSecond == null) {
            return DATE_FORMAT.format(started) + ",,"
                    + "," + startStopID + "," + finishStopID + ","
                    + chargeAmount + "," + companyID + "," + busID + "," + hashedPan + "," + status;
        }
        return DATE_FORMAT.format(started) + "," + DATE_FORMAT.format(finished) + ","
                + durationInSecond + "," + startStopID + "," + finishStopID + ","
                + chargeAmount + "," + companyID + "," + busID + "," + hashedPan + "," + status;
    }

    public String toUnprocessedCSVString() {
        if (finished == null || durationInSecond == null) {
            return DATE_FORMAT.format(started) + "," + null + ","
                    + null + "," + startStopID + "," + finishStopID + ","
                    + chargeAmount + "," + companyID + "," + busID + "," + hashedPan + "," + reason;
        }
        return DATE_FORMAT.format(started) + "," + DATE_FORMAT.format(finished) + ","
                + durationInSecond + "," + startStopID + "," + finishStopID + ","
                + chargeAmount + "," + companyID + "," + busID + "," + hashedPan + "," + reason;
    }

}
