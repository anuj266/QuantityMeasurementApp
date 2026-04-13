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

    @Autowired private TestRestTemplate    restTemplate;
    @Autowired private QuantityMeasurementRepository measurementRepository;
    @Autowired private UserRepository      userRepository;

    private String base;
    private String authBase;

    @BeforeEach
    void setUp() {
        base     = "http://localhost:" + port + "/api/v1/quantities";
        authBase = "http://localhost:" + port + "/auth";
        measurementRepository.deleteAll();
        userRepository.deleteAll();
        restTemplate.getRestTemplate().setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory());
    }

    //Utility: register + login and get token

    private String getTokenForUser(String username, String password, String email) {
        // Register
        RegisterRequest reg = new RegisterRequest(username, password, email);
        restTemplate.postForEntity(authBase + "/register", reg, AuthResponse.class);

        // Login
        LoginRequest login = new LoginRequest(username, password);
        ResponseEntity<AuthResponse> loginResponse =
            restTemplate.postForEntity(authBase + "/login", login, AuthResponse.class);

        return loginResponse.getBody().getToken();
    }

   //Creates an HttpEntity with the JWT token in the Authorization header
    
    private HttpEntity<QuantityInputDTO> buildRequest(String token,
                                                       QuantityInputDTO body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

    private QuantityInputDTO input(double v1, String u1, String t1,
                                   double v2, String u2, String t2) {
        return new QuantityInputDTO(
            new QuantityDTO(v1, u1, t1),
            new QuantityDTO(v2, u2, t2));
    }

    //Auth Tests

    @Test
    void contextLoads() { }

    @Test
    void testRegister_NewUser_Returns201() {
        RegisterRequest request =
            new RegisterRequest("vikash", "password123", "vikash@test.com");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            authBase + "/register", request, AuthResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getToken());
        assertEquals("vikash", response.getBody().getUsername());
        assertEquals("USER",   response.getBody().getRole());
    }

    @Test
    void testRegister_DuplicateUsername_Returns409() {
        RegisterRequest request =
            new RegisterRequest("vikash", "password123", "vikash@test.com");

        // First registration — should succeed
        restTemplate.postForEntity(authBase + "/register",
            request, AuthResponse.class);

        // Second registration with same username — should fail
        RegisterRequest duplicate =
            new RegisterRequest("vikash", "different123", "other@test.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
            authBase + "/register", duplicate, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testLogin_ValidCredentials_Returns200WithToken() {
        // Register first
        restTemplate.postForEntity(authBase + "/register",
            new RegisterRequest("vikash", "password123", "vikash@test.com"),
            AuthResponse.class);

        // Then login
        LoginRequest loginRequest = new LoginRequest("vikash", "password123");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            authBase + "/login", loginRequest, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getToken());
        assertFalse(response.getBody().getToken().isEmpty());
    }

    @Test
    void testLogin_WrongPassword_Returns401() {
        // Register first
        restTemplate.postForEntity(authBase + "/register",
            new RegisterRequest("vikash", "password123", "vikash@test.com"),
            AuthResponse.class);

        // USE EXCHANGE INSTEAD OF postForEntity
        LoginRequest wrongLogin = new LoginRequest("vikash", "wrongpassword");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(wrongLogin);
        
        ResponseEntity<String> response = restTemplate.exchange(
            authBase + "/login", HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    //Protected Endpoint Tests 
    
    @Test
    void testProtectedEndpoint_WithoutToken_Returns401() {
        // USE EXCHANGE INSTEAD OF postForEntity
        QuantityInputDTO body = input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit");
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(body);

        ResponseEntity<String> response = restTemplate.exchange(
            base + "/compare", HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testProtectedEndpoint_WithValidToken_Returns200() {
        // Get a valid JWT token
        String token = getTokenForUser("vikash", "password123", "v@test.com");

        // Call the API with the token
        HttpEntity<QuantityInputDTO> request = buildRequest(token,
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.exchange(
                base + "/compare",
                HttpMethod.POST,
                request,
                QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    @Test
    void testProtectedEndpoint_WithExpiredToken_Returns401() {
        HttpHeaders headers = new HttpHeaders();
        // Use a format that looks like a JWT but is invalid
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.invalid.token");
        
        HttpEntity<QuantityInputDTO> request = new HttpEntity<>(
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"),
            headers);

        ResponseEntity<String> response = restTemplate.exchange(
            base + "/compare", HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAddQuantities_WithValidToken() {
        String token = getTokenForUser("vikash", "password123", "v@test.com");

        HttpEntity<QuantityInputDTO> request = buildRequest(token,
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.exchange(
                base + "/add", HttpMethod.POST, request,
                QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 1e-6);
        assertEquals("FEET", response.getBody().getResultUnit());
    }

    @Test
    void testConvertQuantity_WithValidToken() {
        String token = getTokenForUser("vikash", "password123", "v@test.com");

        HttpEntity<QuantityInputDTO> request = buildRequest(token,
            input(1.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.exchange(
                base + "/convert", HttpMethod.POST, request,
                QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12.0, response.getBody().getResultValue(), 1e-6);
    }

    @Test
    void testGetHistory_WithValidToken() {
        String token = getTokenForUser("vikash", "password123", "v@test.com");

        // First add something so there is history
        HttpEntity<QuantityInputDTO> addRequest = buildRequest(token,
            input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));
        restTemplate.exchange(base + "/add",
            HttpMethod.POST, addRequest, QuantityMeasurementDTO.class);

        // Now get history
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);

        ResponseEntity<QuantityMeasurementDTO[]> response =
            restTemplate.exchange(
                base + "/history/operation/add",
                HttpMethod.GET, getRequest,
                QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
    }
}