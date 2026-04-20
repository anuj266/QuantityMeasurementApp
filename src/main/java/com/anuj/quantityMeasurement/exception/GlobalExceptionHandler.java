package com.anuj.quantityMeasurement.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Handles @Valid validation failures.
     * @NotNull, @NotEmpty, @Email, @Size etc.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST,
            "Validation Error", errors, request);
    }

    /*
     * Handles our custom business logic exceptions.
     * Incompatible measurement types, unknown unit name etc.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
            "Quantity Measurement Error", ex.getMessage(), request);
    }

    /*
     * Handles wrong username or password during login.
     * Spring Security throws BadCredentialsException.
     * Returns 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
            "Authentication Failed",
            "Invalid username or password", request);
    }

    /*
     * Handles user not found in database.
     * Returns 404 Not Found.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(
            UsernameNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND,
            "User Not Found", ex.getMessage(), request);
    }

    /*
     * Handles invalid or tampered JWT tokens.
     * JJWT throws JwtException when token is bad.
     * Returns 401 Unauthorized.
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(
            JwtException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
            "Invalid Token", ex.getMessage(), request);
    }

    /*
     * Handles duplicate username or email during registration.
     * AuthService throws IllegalArgumentException for these cases.
     *
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT,
            "Registration Error", ex.getMessage(), request);
    }

    
    //Catch-all for everything else not handled above.
     
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error", ex.getMessage(), request);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String error,
            String message, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",    status.value());
        body.put("error",     error);
        body.put("message",   message);
        body.put("path",
            request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(body);
    }
}