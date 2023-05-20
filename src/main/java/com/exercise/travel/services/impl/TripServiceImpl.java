package com.exercise.travel.services.impl;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripEnums;
import com.exercise.travel.dto.TripSummary;
import com.exercise.travel.services.TripService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exercise.travel.config.Constants.*;

@Service
public class TripServiceImpl implements TripService {
    @Override
    public Map<String, TripSummary> createTripsFromTouchRecords(List<TouchRecord> touchRecords) {
        Map<String, TripSummary> tripSummaries = new HashMap<>();

        for (TouchRecord touchRecord : touchRecords) {
            String key = touchRecord.getDateString() + "," + touchRecord.getCompanyID() + "," + touchRecord.getBusID();
            TripSummary tripSummary = tripSummaries.computeIfAbsent(key, k -> new TripSummary());

            if (touchRecord.getTouchType() == TripEnums.TouchType.ON) {
                tripSummary.setCompanyID(touchRecord.getCompanyID());
                tripSummary.setBusID(touchRecord.getBusID());
                tripSummary.setStartStopID(touchRecord.getStopID());
                tripSummary.setStarted(touchRecord.getDateTime());
                tripSummary.setStatus(TripEnums.TripStatus.INCOMPLETE);
                if (!touchRecord.validatePan().isEmpty()) {
                    tripSummary.setHashedPan(touchRecord.getPan());
                    tripSummary.setReason(touchRecord.validatePan());
                } else {
                    tripSummary.setHashedPan(hashPAN(touchRecord.getPan()));
                }
                double chargeAmount = calculateChargeAmount(tripSummary.getStartStopID(), null);
                tripSummary.setChargeAmount(chargeAmount);
            } else if (touchRecord.getTouchType() == TripEnums.TouchType.OFF) {
                // handle in case on & off in the same stop
                if (touchRecord.getStopID().equals(tripSummary.getStartStopID())) {
                    tripSummary.setFinishStopID(touchRecord.getStopID());
                    tripSummary.setStatus(TripEnums.TripStatus.CANCELLED);
                    double chargeAmount
                            = calculateChargeAmount(tripSummary.getStartStopID(), tripSummary.getFinishStopID());
                    tripSummary.setChargeAmount(chargeAmount);
                    // move to next record in case touch on already has invalid data
                    if (!tripSummary.getReason().isEmpty()) {
                        continue;
                    }
                    if (!touchRecord.validatePan().isEmpty()) {
                        tripSummary.setHashedPan(touchRecord.getPan());
                        tripSummary.setReason(touchRecord.validatePan());
                    } else {
                        tripSummary.setHashedPan(hashPAN(touchRecord.getPan()));
                    }
                    continue;
                }
                // handle in case on & off in the different stop
                if (tripSummary.getStarted() != null) {
                    tripSummary.setFinishStopID(touchRecord.getStopID());
                    tripSummary.setFinished(touchRecord.getDateTime());
                    double chargeAmount
                            = calculateChargeAmount(tripSummary.getStartStopID(), tripSummary.getFinishStopID());
                    tripSummary.setChargeAmount(chargeAmount);
                    tripSummary.setDurationInSecond(Duration.between(tripSummary.getStarted().toInstant(), tripSummary.getFinished().toInstant()).getSeconds());
                    tripSummary.setStatus(TripEnums.TripStatus.COMPLETE);
                    // move to next record in case touch on already has invalid data
                    if (!tripSummary.getReason().isEmpty()) {
                        continue;
                    }
                    if (!touchRecord.validatePan().isEmpty()) {
                        tripSummary.setHashedPan(touchRecord.getPan());
                        tripSummary.setReason(touchRecord.validatePan());
                    } else {
                        tripSummary.setHashedPan(hashPAN(touchRecord.getPan()));
                    }

                }
            }
        }

        return tripSummaries;
    }

    @Override
    public Map<String, TripSummary> filterUnprocessedTrips(Map<String, TripSummary> tripSummaries) {
        Map<String, TripSummary> unprocessedTrips = new HashMap<>();
        tripSummaries.forEach((k, v) -> {
            if (!v.getReason().isEmpty()) {
                unprocessedTrips.put(k, v);
            }
        });
        return unprocessedTrips;
    }

    @Override
    public Map<String, TripSummary> filterProcessedTrips(Map<String, TripSummary> tripSummaries) {
        Map<String, TripSummary> processedTrips = new HashMap<>();
        tripSummaries.forEach((k, v) -> {
            if (v.getReason().isEmpty()) {
                processedTrips.put(k, v);
            }
        });
        return processedTrips;
    }

    @Override
    public Double calculateChargeAmount(TripEnums.StopID startStopID, TripEnums.StopID finishStopID) {
        if (startStopID == finishStopID) {
            return 0.0;
        } else if ((startStopID == TripEnums.StopID.StopA && finishStopID == TripEnums.StopID.StopB) ||
                (startStopID == TripEnums.StopID.StopB && finishStopID == TripEnums.StopID.StopA)) {
            return FARE_A_B;
        } else if ((startStopID == TripEnums.StopID.StopB && finishStopID == TripEnums.StopID.StopC) ||
                (startStopID == TripEnums.StopID.StopC && finishStopID == TripEnums.StopID.StopB)) {
            return FARE_B_C;
        } else if ((startStopID == TripEnums.StopID.StopA && finishStopID == TripEnums.StopID.StopC) ||
                (startStopID == TripEnums.StopID.StopC && finishStopID == TripEnums.StopID.StopA)) {
            return FARE_A_C;
        } else {
            if (startStopID == TripEnums.StopID.StopA) {
                return Math.max(FARE_A_B, FARE_A_C);
            } else if (startStopID == TripEnums.StopID.StopB) {
                return Math.max(FARE_A_B, FARE_B_C);
            } else if (startStopID == TripEnums.StopID.StopC) {
                return Math.max(FARE_B_C, FARE_A_C);
            }
        }
        return null;
    }


    @Override
    public String hashPAN(String pan) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = messageDigest.digest(pan.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                hexString.append(Integer.toHexString(0xFF & hashedByte));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
