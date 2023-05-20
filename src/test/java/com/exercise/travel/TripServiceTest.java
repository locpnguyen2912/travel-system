package com.exercise.travel;

import com.exercise.travel.dto.TouchRecord;
import com.exercise.travel.dto.TripEnums;
import com.exercise.travel.dto.TripSummary;
import com.exercise.travel.services.TripService;
import com.exercise.travel.services.impl.TripServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.exercise.travel.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TripServiceTest {

    private TripService tripService;

    @BeforeEach
    void setUp() {
        tripService = new TripServiceImpl();
    }

    @Test
    void createTripsFromTouchRecords_shouldCreateSingleCompleteTrip() throws ParseException {
        // Given
        List<TouchRecord> touchRecords = Arrays.asList(
                new TouchRecord(1, DATE_FORMAT.parse("16-05-2023 12:15:00"), TripEnums.TouchType.ON, TripEnums.StopID.StopA, "Company1", "Bus1", "2255550000666662"),
                new TouchRecord(2, DATE_FORMAT.parse("16-05-2023 12:25:00"), TripEnums.TouchType.OFF, TripEnums.StopID.StopB, "Company1", "Bus1", "2255550000666667")
        );

        // When
        Map<String, TripSummary> tripSummaries = tripService.createTripsFromTouchRecords(touchRecords);

        // Then
        assertNotNull(tripSummaries);
        assertEquals(1, tripSummaries.size());

        TripSummary tripSummary = tripSummaries.values().iterator().next();
        assertEquals("Company1", tripSummary.getCompanyID());
        assertEquals("Bus1", tripSummary.getBusID());
        assertEquals(TripEnums.StopID.StopA, tripSummary.getStartStopID());
        assertEquals(TripEnums.StopID.StopB, tripSummary.getFinishStopID());
        assertEquals(TripEnums.TripStatus.COMPLETE, tripSummary.getStatus());
        assertEquals(FARE_A_B, tripSummary.getChargeAmount());
    }


    @Test
    void createTripsFromTouchRecords_shouldCreateSingleInCompleteTrip() throws ParseException {
        // Given
        List<TouchRecord> touchRecords = Collections.singletonList(
                new TouchRecord(1, DATE_FORMAT.parse("16-05-2023 12:15:00"), TripEnums.TouchType.ON, TripEnums.StopID.StopB, "Company1", "Bus1", "2255550000666662")
        );

        // When
        Map<String, TripSummary> tripSummaries = tripService.createTripsFromTouchRecords(touchRecords);

        // Then
        assertNotNull(tripSummaries);
        assertEquals(1, tripSummaries.size());

        TripSummary tripSummary = tripSummaries.values().iterator().next();
        assertEquals("Company1", tripSummary.getCompanyID());
        assertEquals("Bus1", tripSummary.getBusID());
        assertEquals(TripEnums.StopID.StopB, tripSummary.getStartStopID());
        assertNull(tripSummary.getFinishStopID());
        assertEquals(TripEnums.TripStatus.INCOMPLETE, tripSummary.getStatus());
        assertEquals(FARE_B_C, tripSummary.getChargeAmount());
    }

    @Test
    void createTripsFromTouchRecords_shouldCreateSingleCancelTrip() throws ParseException {
        // Given
        List<TouchRecord> touchRecords = Arrays.asList(
                new TouchRecord(1, DATE_FORMAT.parse("16-05-2023 12:15:00"), TripEnums.TouchType.ON, TripEnums.StopID.StopA, "Company1", "Bus1", "2255550000666662"),
                new TouchRecord(2, DATE_FORMAT.parse("16-05-2023 12:15:30"), TripEnums.TouchType.OFF, TripEnums.StopID.StopA, "Company1", "Bus1", "2255550000666667")
        );

        // When
        Map<String, TripSummary> tripSummaries = tripService.createTripsFromTouchRecords(touchRecords);

        // Then
        assertNotNull(tripSummaries);
        assertEquals(1, tripSummaries.size());

        TripSummary tripSummary = tripSummaries.values().iterator().next();
        assertEquals("Company1", tripSummary.getCompanyID());
        assertEquals("Bus1", tripSummary.getBusID());
        assertEquals(TripEnums.StopID.StopA, tripSummary.getStartStopID());
        assertEquals(TripEnums.StopID.StopA, tripSummary.getFinishStopID());
        assertEquals(TripEnums.TripStatus.CANCELLED, tripSummary.getStatus());
        assertEquals(0.0, tripSummary.getChargeAmount());
    }

    @Test
    void createTripsFromTouchRecords_shouldCreateSingleTripAndMarkAsUnProcessTripWithReason() throws ParseException {
        // Given
        List<TouchRecord> touchRecords = Arrays.asList(
                new TouchRecord(1, DATE_FORMAT.parse("16-05-2023 12:15:00"), TripEnums.TouchType.ON, TripEnums.StopID.StopA, "Company1", "Bus1", "2255550000666662"),
                new TouchRecord(2, DATE_FORMAT.parse("16-05-2023 12:25:30"), TripEnums.TouchType.OFF, TripEnums.StopID.StopB, "Company1", "Bus1", "")
        );

        // When
        Map<String, TripSummary> tripSummaries = tripService.createTripsFromTouchRecords(touchRecords);

        // Then
        assertNotNull(tripSummaries);
        assertEquals(1, tripSummaries.size());

        TripSummary tripSummary = tripSummaries.values().iterator().next();
        assertEquals("Company1", tripSummary.getCompanyID());
        assertEquals("Bus1", tripSummary.getBusID());
        assertEquals(TripEnums.StopID.StopA, tripSummary.getStartStopID());
        assertEquals(TripEnums.StopID.StopB, tripSummary.getFinishStopID());
        assertEquals(MISSING_PAN, tripSummary.getReason());

    }
}

