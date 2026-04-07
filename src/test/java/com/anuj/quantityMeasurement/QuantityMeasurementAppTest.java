package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.model.QuantityDTO;
import com.anuj.quantityMeasurement.model.QuantityInputDTO;
import com.anuj.quantityMeasurement.model.QuantityMeasurementDTO;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuantityMeasurementRepository repository;

    private String base;

    @BeforeEach
    void setUp() {
        base = "http://localhost:" + port + "/api/v1/quantities";
        repository.deleteAll();  // clean DB before each test
    }

    // Helper
    private QuantityInputDTO input(double v1, String u1, String t1,
                                   double v2, String u2, String t2) {
        return new QuantityInputDTO(
            new QuantityDTO(v1, u1, t1),
            new QuantityDTO(v2, u2, t2));
    }

    @Test
    void contextLoads() {}

    @Test
    void testCompare_EqualLengths_ReturnsTrue() {
       
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/compare",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("true", r.getBody().getResultString());
        assertFalse(r.getBody().isError());
    }

    @Test
    void testCompare_UnequalLengths_ReturnsFalse() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/compare",
            input(2.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("false", r.getBody().getResultString());
    }

    @Test
    void testCompare_Temperature_0C_Equals_32F() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/compare",
            input(0.0, "CELSIUS", "TemperatureUnit", 32.0, "FAHRENHEIT", "TemperatureUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("true", r.getBody().getResultString());
    }

    //convert

    @Test
    void testConvert_FeetToInches_Returns12() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/convert",
            input(1.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(12.0, r.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testConvert_GramToKilogram() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/convert",
            input(1000.0, "GRAM", "WeightUnit", 0.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1.0, r.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testConvert_CelsiusToFahrenheit_100C_Is_212F() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/convert",
            input(100.0, "CELSIUS", "TemperatureUnit", 0.0, "FAHRENHEIT", "TemperatureUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(212.0, r.getBody().getResultValue(), 1e-4);
    }

    //add

    @Test
    void testAdd_LengthUnits_Returns2Feet() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/add",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2.0,    r.getBody().getResultValue(), 1e-6);
        assertEquals("FEET", r.getBody().getResultUnit());
    }

    @Test
    void testAdd_WeightUnits() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/add",
            input(1000.0, "GRAM", "WeightUnit", 1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2000.0, r.getBody().getResultValue(), 1e-4);
        assertEquals("GRAM", r.getBody().getResultUnit());
    }

    @Test
    void testAdd_TemperatureUnits_Returns400_Unsupported() {
        
        ResponseEntity<String> r = restTemplate.postForEntity(
            base + "/add",
            input(100.0, "CELSIUS", "TemperatureUnit", 50.0, "CELSIUS", "TemperatureUnit"),
            String.class);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    @Test
    void testAdd_IncompatibleTypes_Returns400() {
        ResponseEntity<String> r = restTemplate.postForEntity(
            base + "/add",
            input(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit"),
            String.class);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertTrue(r.getBody().contains("Cannot perform arithmetic"));
    }

    //subtract

    @Test
    void testSubtract_LengthUnits() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/subtract",
            input(2.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1.0, r.getBody().getResultValue(), 1e-6);
    }

    //divide

    @Test
    void testDivide_WeightUnits() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/divide",
            input(1000.0, "GRAM", "WeightUnit", 1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1.0, r.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testDivide_ByZero_Returns500() {
        ResponseEntity<String> r = restTemplate.postForEntity(
            base + "/divide",
            input(1.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"),
            String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertTrue(r.getBody().contains("Divide by zero"));
    }

    //history and count

    @Test
    void testGetHistoryByOperation_AfterAdd_Returns1Record() {
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        ResponseEntity<QuantityMeasurementDTO[]> r = restTemplate.getForEntity(
            base + "/history/operation/add", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().length);
        assertEquals("add", r.getBody()[0].getOperation());
    }

    @Test
    void testGetHistoryByType_AfterTwoLengthOps_Returns2Records() {
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);
        restTemplate.postForEntity(base + "/compare",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        ResponseEntity<QuantityMeasurementDTO[]> r = restTemplate.getForEntity(
            base + "/history/type/LengthUnit", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2, r.getBody().length);
    }

    @Test
    void testGetCount_AfterTwoCompares_Returns2() {
        restTemplate.postForEntity(base + "/compare",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);
        restTemplate.postForEntity(base + "/compare",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        ResponseEntity<Long> r = restTemplate.getForEntity(
            base + "/count/compare", Long.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2L, r.getBody());
    }

    @Test
    void testGetErrorHistory_AfterFailedAdd_Returns1ErrorRecord() {
        // Trigger an error by adding incompatible types
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit"),
            String.class);

        ResponseEntity<QuantityMeasurementDTO[]> r = restTemplate.getForEntity(
            base + "/history/errored", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody().length >= 1);
        assertTrue(r.getBody()[0].isError());
    }

    @Test
    void testDatabasePersistence_RecordsAccumulateAcrossRequests() {
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);
        restTemplate.postForEntity(base + "/compare",
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);
        restTemplate.postForEntity(base + "/convert",
            input(1.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        // Verify directly in DB using repository
        assertEquals(3, repository.count());
    }
}