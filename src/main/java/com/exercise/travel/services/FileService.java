package com.exercise.travel.services;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    List<TouchRecord> readTouchRecords(MultipartFile inputFile);

    Boolean writeTripsFile(Map<String, TripSummary> tripSummaries, String filePath);

    Boolean writeUnprocessableTouchDataFile( Map<String, TripSummary> unprocessedTrips, String filePath);

    Boolean writeSummaryFile(Map<String, TripSummary> tripSummaries, String filePath);

}
