package com.exercise.travel.controller;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripSummary;
import com.exercise.travel.services.FileService;
import com.exercise.travel.services.TripService;
import com.exercise.travel.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.exercise.travel.config.Constants.TEMP_PATH;

@RestController
@RequestMapping(path = "/api/trip")
public class TripController {

    private static final Logger LOG = LoggerFactory.getLogger(TripController.class);

    @Autowired
    FileService fileService;

    @Autowired
    TripService tripService;

    @GetMapping(value = "/zip-export", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> zipExport(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        List<String> fileNames = Arrays.asList("trips.csv", "unprocessableTouchData.csv", "summary.csv");
        List<TouchRecord> touchRecords = fileService.readTouchRecords(file);
        Map<String, TripSummary> tripSummaries = tripService.createTripsFromTouchRecords(touchRecords);
        Map<String, TripSummary> processedTrips = tripService.filterProcessedTrips(tripSummaries);
        Map<String, TripSummary> unprocessedTrips = tripService.filterUnprocessedTrips(tripSummaries);
        fileService.writeTripsFile(processedTrips, TEMP_PATH + fileNames.get(0));
        fileService.writeUnprocessableTouchDataFile(unprocessedTrips, TEMP_PATH + fileNames.get(1));
        fileService.writeSummaryFile(processedTrips, TEMP_PATH + fileNames.get(2));
        StreamingResponseBody streamResponseBody = out -> {
            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
            for (String fileName : fileNames) {
                Path filePath = Paths.get(fileName);
                LOG.info("exportFile: {}", filePath);
                Resource resource = new FileSystemResource(TEMP_PATH + fileName);
                ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(resource.getFilename()));
                zipEntry.setSize(resource.contentLength());
                zipOut.putNextEntry(zipEntry);
                StreamUtils.copy(resource.getInputStream(), zipOut);
                zipOut.closeEntry();
            }
            zipOut.finish();
            zipOut.close();
        };

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "download.zip" + "\"");
        return ResponseEntity.ok(streamResponseBody);
    }
}
