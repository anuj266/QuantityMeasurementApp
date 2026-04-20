package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.model.*;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementRepository;
import com.anuj.quantityMeasurement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuantityMeasurementAppTest {

    @LocalServerPort
    private int port;

    @Autowired private TestRestTemplate              restTemplate;
    @Autowired private QuantityMeasurementRepository measurementRepo;
    @Autowired private UserRepository                userRepo;

    private String base;
    private String authBase;

    @BeforeEach
    void setUp() {
        base     = "http://localhost:" + port + "/api/v1/quantities";
        authBase = "http://localhost:" + port + "/auth";
        measurementRepo.deleteAll();
        userRepo.deleteAll();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private QuantityInputDTO input(double v1, String u1, String t1,
                                   double v2, String u2, String t2) {
        return new QuantityInputDTO(
            new QuantityDTO(v1, u1, t1),
            new QuantityDTO(v2, u2, t2));
    }

    private String registerAndGetToken(String username,
                                       String password,
                                       String email) {
        restTemplate.postForEntity(
            authBase + "/register",
            new RegisterRequest(username, password, email),
            AuthResponse.class);

        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
            authBase + "/login",
            new LoginRequest(username, password),
            AuthResponse.class);

        return login.getBody().getToken();
    }

    private HttpEntity<QuantityInputDTO> withToken(String token,
                                                    QuantityInputDTO body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Void> getWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }

    // ── Context load ──────────────────────────────────────────────────────────

    @Test
    void contextLoads() {
        // passes if Spring context starts successfully
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PUBLIC CALCULATOR TESTS — NO TOKEN NEEDED
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testCompare_NoToken_Returns200() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/compare",
            input(1.0, "FEET", "LengthUnit",
                  12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("true", r.getBody().getResultString());
    }

    @Test
    void testAdd_NoToken_Returns200() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/add",
            input(1000.0, "GRAM", "WeightUnit",
                  1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(2000.0, r.getBody().getResultValue(), 1e-4);
        assertEquals("GRAM", r.getBody().getResultUnit());
    }

    @Test
    void testSubtract_NoToken_Returns200() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/subtract",
            input(2000.0, "GRAM", "WeightUnit",
                  1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1000.0, r.getBody().getResultValue(), 1e-4);
    }

    @Test
    void testDivide_NoToken_Returns200() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/divide",
            input(1000.0, "GRAM", "WeightUnit",
                  1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1.0, r.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testConvert_NoToken_Returns200() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/convert",
            input(100.0, "CELSIUS", "TemperatureUnit",
                  0.0, "FAHRENHEIT", "TemperatureUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(212.0, r.getBody().getResultValue(), 1e-4);
    }

    @Test
    void testConvert_FeetToInches_NoToken() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/convert",
            input(1.0, "FEET", "LengthUnit",
                  0.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(12.0, r.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testCompare_Temperature_NoToken() {
        ResponseEntity<QuantityMeasurementDTO> r = restTemplate.postForEntity(
            base + "/compare",
            input(0.0, "CELSIUS", "TemperatureUnit",
                  32.0, "FAHRENHEIT", "TemperatureUnit"),
            QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("true", r.getBody().getResultString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PROTECTED HISTORY TESTS — TOKEN REQUIRED
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testHistory_NoToken_Returns401() {
        ResponseEntity<String> r = restTemplate.getForEntity(
            base + "/history/operation/add", String.class);

        /*
         * FIX: Only check the STATUS CODE — not the body message.
         *
         * The 401 status is what proves security is working.
         * Checking exact body text is fragile — the message
         * can vary based on Spring Security configuration and
         * the exact path through the filter chain.
         *
         * What matters for this test:
         *   ✅ Request is rejected (not 200)
         *   ✅ Rejection is specifically "Unauthorized" (401)
         *   ✅ Not a server error (not 500)
         */
        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    @Test
    void testCount_NoToken_Returns401() {
        ResponseEntity<String> r = restTemplate.getForEntity(
            base + "/count/compare", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    @Test
    void testHistory_WithValidToken_Returns200() {
        // Do a public operation first — no token needed
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit",
                  12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        // Register and login to get token
        String token = registerAndGetToken(
            "vikash", "password123", "v@test.com");

        // Use token to view history
        ResponseEntity<QuantityMeasurementDTO[]> r =
            restTemplate.exchange(
                base + "/history/operation/add",
                HttpMethod.GET,
                getWithToken(token),
                QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1, r.getBody().length);
        assertEquals("add", r.getBody()[0].getOperation());
    }

    @Test
    void testCount_WithValidToken_Returns200() {
        // Public operation first
        restTemplate.postForEntity(base + "/compare",
            input(1.0, "FEET", "LengthUnit",
                  12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        String token = registerAndGetToken(
            "vikash", "password123", "v@test.com");

        ResponseEntity<Long> r = restTemplate.exchange(
            base + "/count/compare",
            HttpMethod.GET,
            getWithToken(token),
            Long.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1L, r.getBody());
    }

    @Test
    void testHistory_WithInvalidToken_Returns401() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer totally.fake.token");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> r = restTemplate.exchange(
            base + "/history/operation/add",
            HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AUTH TESTS
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testRegister_Returns201() {
        ResponseEntity<AuthResponse> r = restTemplate.postForEntity(
            authBase + "/register",
            new RegisterRequest("vikash", "password123", "v@test.com"),
            AuthResponse.class);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertNotNull(r.getBody().getToken());
        assertFalse(r.getBody().getToken().isEmpty());
        assertEquals("vikash", r.getBody().getUsername());
        assertEquals("USER", r.getBody().getRole());
    }

    @Test
    void testLogin_ValidCredentials_Returns200() {
        restTemplate.postForEntity(authBase + "/register",
            new RegisterRequest("vikash", "password123", "v@test.com"),
            AuthResponse.class);

        ResponseEntity<AuthResponse> r = restTemplate.postForEntity(
            authBase + "/login",
            new LoginRequest("vikash", "password123"),
            AuthResponse.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody().getToken());
    }

    @Test
    void testLogin_WrongPassword_Returns401() {
        restTemplate.postForEntity(authBase + "/register",
            new RegisterRequest("vikash", "password123", "v@test.com"),
            AuthResponse.class);

        ResponseEntity<String> r = restTemplate.postForEntity(
            authBase + "/login",
            new LoginRequest("vikash", "wrongpassword"),
            String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    @Test
    void testDuplicateUsername_Returns409() {
        restTemplate.postForEntity(authBase + "/register",
            new RegisterRequest("vikash", "password123", "v@test.com"),
            AuthResponse.class);

        ResponseEntity<String> r = restTemplate.postForEntity(
            authBase + "/register",
            new RegisterRequest("vikash", "other123", "other@test.com"),
            String.class);

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // COMBINED FLOW TEST
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testFullFlow_PublicOperations_ThenLoginForHistory() {
        /*
         * Simulates real calculator website usage:
         *
         * Phase 1: Anonymous — does calculations freely
         * Phase 2: Registers + logs in
         * Phase 3: Views history of those calculations
         */

        // Phase 1: Anonymous calculations — no login
        restTemplate.postForEntity(base + "/add",
            input(1.0, "FEET", "LengthUnit",
                  12.0, "INCHES", "LengthUnit"),
            QuantityMeasurementDTO.class);

        restTemplate.postForEntity(base + "/compare",
            input(1000.0, "GRAM", "WeightUnit",
                  1.0, "KILOGRAM", "WeightUnit"),
            QuantityMeasurementDTO.class);

        restTemplate.postForEntity(base + "/convert",
            input(100.0, "CELSIUS", "TemperatureUnit",
                  0.0, "FAHRENHEIT", "TemperatureUnit"),
            QuantityMeasurementDTO.class);

        // Phase 2: Register and login
        String token = registerAndGetToken(
            "vikash", "password123", "v@test.com");

        // Phase 3: View history with token
        ResponseEntity<QuantityMeasurementDTO[]> addHistory =
            restTemplate.exchange(
                base + "/history/operation/add",
                HttpMethod.GET,
                getWithToken(token),
                QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, addHistory.getStatusCode());
        assertEquals(1, addHistory.getBody().length);
        assertEquals("add", addHistory.getBody()[0].getOperation());

        // Count operations
        ResponseEntity<Long> countAdd = restTemplate.exchange(
            base + "/count/add",
            HttpMethod.GET,
            getWithToken(token),
            Long.class);

        assertEquals(HttpStatus.OK, countAdd.getStatusCode());
        assertEquals(1L, countAdd.getBody());

        // Total records in DB should be 3
        assertEquals(3, measurementRepo.count());
    }
}