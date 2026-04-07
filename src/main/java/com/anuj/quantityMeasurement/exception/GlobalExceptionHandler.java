package com.anuj.quantityMeasurement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",    HttpStatus.BAD_REQUEST.value());  // 400
        body.put("error",     "Quantity Measurement Error");
        body.put("message",   errors);
        
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException ex, WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Quantity Measurement Error");
        body.put("message",   ex.getMessage());
        body.put("path",      request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",    HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        body.put("error",     "Internal Server Error");
        body.put("message",   ex.getMessage());
        body.put("path",      request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(body);
    }
}