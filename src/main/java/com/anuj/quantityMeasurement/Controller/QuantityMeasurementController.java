package com.anuj.quantityMeasurement.Controller;

import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
        logger.info("QuantityMeasurementController initialized");
    }

    public QuantityDTO add(QuantityDTO q1, QuantityDTO q2) {
        logger.info("add() called");
        return service.add(q1, q2);
    }

    public QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2) {
        logger.info("subtract() called");
        return service.subtract(q1, q2);
    }

    public QuantityDTO convert(QuantityDTO q1, QuantityDTO q2) {
        logger.info("convert() called");
        return service.convert(q1, q2);
    }

    public boolean compare(QuantityDTO q1, QuantityDTO q2) {
        logger.info("compare() called");
        return service.compare(q1, q2);
    }

    public double divide(QuantityDTO q1, QuantityDTO q2) {
        logger.info("divide() called");
        return service.divide(q1, q2);
    }
}