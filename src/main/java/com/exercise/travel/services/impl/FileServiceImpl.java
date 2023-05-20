package com.exercise.travel.services.impl;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripEnums;
import com.exercise.travel.dto.TripSummary;
import com.exercise.travel.services.FileService;
import com.exercise.travel.services.TripService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

import static com.exercise.travel.config.Constants.DATE_FORMAT;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    @Autowired
    TripService tripService;

    @Override
    public List<TouchRecord> readTouchRecords(MultipartFile inputFile) {
        List<TouchRecord> touchRecords = new ArrayList<>();
        CSVParser parser = null;
        try {
            String[] headers = {"ID", "DateTimeUTC", "TouchType", "StopID", "CompanyID", "BusID", "PAN"};
            CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setHeader(headers).setSkipHeaderRecord(true).build();
            parser = CSVParser.parse(convertToFile(inputFile), StandardCharsets.UTF_8, CSV_FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord record : Objects.requireNonNull(parser)) {
            int id = Integer.parseInt(record.get("ID").trim());
            Date dateTimeUTC = new Date();
            try {
                dateTimeUTC = DATE_FORMAT.parse(record.get("DateTimeUTC").trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TripEnums.TouchType touchType = TripEnums.TouchType.fromString(record.get("TouchType").trim());
            TripEnums.StopID stopID = TripEnums.StopID.fromString(record.get("StopID").trim());
            String companyID = record.get("CompanyID").trim();
            String busID = record.get("BusID").trim();
            String pan = record.get("PAN").trim();

            TouchRecord touchRecord = new TouchRecord(id, dateTimeUTC, touchType, stopID, companyID, busID, pan);
            touchRecords.add(touchRecord);
        }
        try {
            parser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return touchRecords;
    }

    @Override
    public void writeTripsFile(Map<String, TripSummary> tripSummaries, String filePath) {
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

    @Override
    public void writeUnprocessableTouchDataFile(Map<String, TripSummary> unprocessedTrips, String filePath) {
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

    @Override
    public void writeSummaryFile(Map<String, TripSummary> tripSummaries, String filePath) {
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

    private File convertToFile(MultipartFile multipartFile) {
        File file = new File("tmp/touchData.csv");
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
