package com.anuj.quantityMeasurement.Controller;

import com.anuj.quantityMeasurement.model.QuantityInputDTO;
import com.anuj.quantityMeasurement.model.QuantityMeasurementDTO;
import com.anuj.quantityMeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements",
     description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    @Autowired
    private IQuantityMeasurementService service;

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities for equality")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        logger.info("POST /compare - {} {} vs {} {}",
            input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
            input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());

        QuantityMeasurementDTO result = service.compareQuantities(
            input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a different unit")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @Valid @RequestBody QuantityInputDTO input) {

        logger.info("POST /convert - {} {} → {}",
            input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
            input.getThatQuantityDTO().getUnit());

        QuantityMeasurementDTO result = service.convertQuantity(
            input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities — result in unit of first quantity")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        logger.info("POST /add - {} {} + {} {}",
            input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
            input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());

        QuantityMeasurementDTO result = service.addQuantities(
            input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract one quantity from another")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        logger.info("POST /subtract - {} {} - {} {}",
            input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
            input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());

        QuantityMeasurementDTO result = service.subtractQuantities(
            input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide one quantity by another")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        logger.info("POST /divide - {} {} ÷ {} {}",
            input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
            input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());

        QuantityMeasurementDTO result = service.divideQuantities(
            input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get measurement history filtered by operation type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByOperation(
            @PathVariable String operation) {

        logger.info("GET /history/operation/{}", operation);
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @GetMapping("/history/type/{type}")
    @Operation(summary = "Get measurement history filtered by measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByType(
            @PathVariable String type) {

        logger.info("GET /history/type/{}", type);
        return ResponseEntity.ok(service.getHistoryByType(type));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get all measurements that resulted in errors")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {

        logger.info("GET /history/errored");
        return ResponseEntity.ok(service.getErrorHistory());
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get count of successful operations by type")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {

        logger.info("GET /count/{}", operation);
        return ResponseEntity.ok(service.getOperationCount(operation));
    }
}