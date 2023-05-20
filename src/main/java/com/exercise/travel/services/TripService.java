package com.exercise.travel.services;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripEnums;
import com.exercise.travel.dto.TripSummary;

import java.util.List;
import java.util.Map;

public interface TripService {

    Map<String, TripSummary> createTripsFromTouchRecords(List<TouchRecord> touchRecords);

    Map<String, TripSummary> filterUnprocessedTrips(Map<String, TripSummary> tripSummaries);

    Map<String, TripSummary> filterProcessedTrips(Map<String, TripSummary> tripSummaries);

    Double calculateChargeAmount(TripEnums.StopID startStopID, TripEnums.StopID finishStopID);

    String hashPAN(String pan);
}
