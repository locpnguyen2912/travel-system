package com.exercise.travel.util;

import com.exercise.travel.dto.TripEnums;
import com.exercise.travel.dto.TripSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class FileUtils {
    public static void writeTripsFile(Map<String, TripSummary> tripSummaries, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write("started, finished, DurationSec, fromStopId, toStopId, ChargeAmount, CompanyId, BusId, HashedPan, Status\n");
            for (TripSummary tripSummary : tripSummaries.values()) {
                writer.write(tripSummary.toCSVString());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeUnprocessableTouchDataFile( Map<String, TripSummary> unprocessedTrips, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write("started, finished, DurationSec, fromStopId, toStopId, ChargeAmount, CompanyId, BusId, HashedPan, Status\n");
            for (TripSummary tripSummary : unprocessedTrips.values()) {
                writer.write(tripSummary.toUnprocessedCSVString());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSummaryFile(Map<String, TripSummary> tripSummaries, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write("date, CompanyId, BusId, CompleteTripCount, IncompleteTripCount, CancelledTripCount, TotalCharges\n");

            SortedMap<String, TripSummary> sortedSummaries = new TreeMap<>(tripSummaries);
            for (Map.Entry<String, TripSummary> entry : sortedSummaries.entrySet()) {
                String key = entry.getKey();
                TripSummary tripSummary = entry.getValue();

                int completeTripCount = tripSummary.getStatus() == TripEnums.TripStatus.COMPLETE ? 1 : 0;
                int incompleteTripCount = tripSummary.getStatus()
                        == TripEnums.TripStatus.INCOMPLETE ? 1 : 0;
                int cancelledTripCount = tripSummary.getStatus() == TripEnums.TripStatus.CANCELLED ? 1 : 0;
                Double totalCharges = tripSummary.getChargeAmount();

                writer.write(key + "," + completeTripCount + "," + incompleteTripCount + ","
                        + cancelledTripCount + "," + totalCharges + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File convertToFile(MultipartFile multipartFile) {
        File file = new File("tmp/touchData.csv");
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
