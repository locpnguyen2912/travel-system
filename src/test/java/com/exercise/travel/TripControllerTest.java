package com.exercise.travel;


import com.exercise.travel.controller.TripController;
import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripSummary;
import com.exercise.travel.services.FileService;
import com.exercise.travel.services.TripService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exercise.travel.config.Constants.TEMP_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TripControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    @Test
    public void testExportFile() throws ParseException, IOException {
        // Mock touchRecords and tripSummaries
        List<TouchRecord> touchRecords = Arrays.asList(
                new TouchRecord(),
                new TouchRecord(),
                new TouchRecord(),
                new TouchRecord()
        );
        Map<String, TripSummary> tripSummaries = new HashMap<>();
        tripSummaries.put("16-05-2023,Company1,Bus1", new TripSummary());
        tripSummaries.put("16-05-2023,Company2,Bus2", new TripSummary());

        // Mock filterProcessedTrips and filterUnprocessedTrips
        Map<String, TripSummary> processedTrips = new HashMap<>();
        processedTrips.put("16-05-2023,Company1,Bus1", new TripSummary());
        Map<String, TripSummary> unprocessedTrips = new HashMap<>();
        unprocessedTrips.put("16-05-2023,Company2,Bus2", new TripSummary());

        // Mock fileService methods
        when(fileService.readTouchRecords(any())).thenReturn(touchRecords);
        when(tripService.createTripsFromTouchRecords(touchRecords)).thenReturn(tripSummaries);
        when(tripService.filterProcessedTrips(tripSummaries)).thenReturn(processedTrips);
        when(tripService.filterUnprocessedTrips(tripSummaries)).thenReturn(unprocessedTrips);

        // Mock fileService.writeTripsFile
        Path tripsFilePath = Paths.get(TEMP_PATH + "trips.csv");
        when(fileService.writeTripsFile(processedTrips, tripsFilePath.toString())).thenReturn(true);

        // Mock fileService.writeUnprocessableTouchDataFile
        Path unprocessableFilePath = Paths.get(TEMP_PATH + "unprocessableTouchData.csv");
        when(fileService.writeUnprocessableTouchDataFile(unprocessedTrips, unprocessableFilePath.toString())).thenReturn(true);

        // Mock fileService.writeSummaryFile
        Path summaryFilePath = Paths.get(TEMP_PATH + "summary.csv");
        when(fileService.writeSummaryFile(processedTrips, summaryFilePath.toString())).thenReturn(true);

        // Mock response
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Invoke zipExport method
        ResponseEntity<StreamingResponseBody> responseEntity = tripController.zipExport(createMultipartFile(), response);

        // Verify the response status and headers
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("attachment; filename=\"download.zip\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION));

    }

    private MockMultipartFile createMultipartFile() throws IOException {
        return new MockMultipartFile("file", "test.csv", "text/plain",
                new ByteArrayInputStream("test content".getBytes()));
    }
}
