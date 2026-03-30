package com.anuj.quantityMeasurement.app;

import com.anuj.quantityMeasurement.Controller.QuantityMeasurementController;
import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;
import com.anuj.quantityMeasurement.repository.*;
import com.anuj.quantityMeasurement.service.QuantityMeasurementServiceImpl;
import com.anuj.quantityMeasurement.util.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QuantityMeasurementApp {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementApp.class);

    private final IQuantityMeasurementRepository repository;
    private final QuantityMeasurementController  controller;

    public QuantityMeasurementApp() {
        ApplicationConfig config = new ApplicationConfig();
        String repoType = config.getRepositoryType();

        if ("database".equalsIgnoreCase(repoType)) {
            this.repository = new QuantityMeasurementDatabaseRepository(config);
            logger.info("Using DATABASE repository");
        } else {
            this.repository = new QuantityMeasurementCacheRepository();
            logger.info("Using CACHE repository");
        }

        this.controller = new QuantityMeasurementController(
            new QuantityMeasurementServiceImpl(repository));
        logger.info("QuantityMeasurementApp initialized");
    }

    public void closeResources() {
        repository.releaseResources();
        logger.info("Resources released");
    }

    public void deleteAllMeasurements() {
        repository.deleteAll();
        logger.info("All measurements deleted");
    }

    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return repository.getAllMeasurements();
    }

    public static void main(String[] args) {
        QuantityMeasurementApp app = new QuantityMeasurementApp();

        try {
            //weight
            QuantityDTO weightInGrams     = new QuantityDTO(1000.0, "GRAM",     "WEIGHT");
            QuantityDTO weightInKilograms = new QuantityDTO(1.0,    "KILOGRAM", "WEIGHT");

            logger.info("Are weights equal: {}",
                app.controller.compare(weightInGrams, weightInKilograms));

            QuantityDTO convertedWeight = app.controller.convert(
                weightInGrams, new QuantityDTO(0, "KILOGRAM", "WEIGHT"));
            logger.info("Converted weight: {} {}", convertedWeight.getValue(), convertedWeight.getUnit());

            QuantityDTO addedWeight = app.controller.add(weightInGrams, weightInKilograms);
            logger.info("Added weight: {} {}", addedWeight.getValue(), addedWeight.getUnit());

            QuantityDTO subtractedWeight = app.controller.subtract(weightInGrams, weightInKilograms);
            logger.info("Subtracted weight: {} {}", subtractedWeight.getValue(), subtractedWeight.getUnit());

            logger.info("Divided weight: {}", app.controller.divide(weightInGrams, weightInKilograms));

            //volume
            QuantityDTO volumeInML = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
            QuantityDTO volumeInL  = new QuantityDTO(1.0,    "LITRE",      "VOLUME");

            logger.info("Are volumes equal: {}",
                app.controller.compare(volumeInML, volumeInL));

            QuantityDTO addedVolume = app.controller.add(volumeInML, volumeInL);
            logger.info("Added volume: {} {}", addedVolume.getValue(), addedVolume.getUnit());

            //temperature
            QuantityDTO celsius0     = new QuantityDTO(0.0,   "CELSIUS",    "TEMPERATURE");
            QuantityDTO fahrenheit32 = new QuantityDTO(32.0,  "FAHRENHEIT", "TEMPERATURE");
            QuantityDTO celsius100   = new QuantityDTO(100.0, "CELSIUS",    "TEMPERATURE");

            logger.info("0 C equals 32 F: {}",
                app.controller.compare(celsius0, fahrenheit32));

            QuantityDTO fahrenheit = app.controller.convert(
                celsius100, new QuantityDTO(0, "FAHRENHEIT", "TEMPERATURE"));
            logger.info("100 C = {} {}", fahrenheit.getValue(), fahrenheit.getUnit());

            try {
                app.controller.add(celsius100, new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE"));
            } catch (UnsupportedOperationException e) {
                logger.info("Expected error: {}", e.getMessage());
            }

            //Report all stored measurements
            List<QuantityMeasurementEntity> all = app.getAllMeasurements();
            logger.info("Total measurements stored: {}", all.size());
            all.forEach(e -> logger.info("  {}", e));

            logger.info("Pool stats: {}", app.repository.getPoolStatistics());

            //Cleanup
            app.deleteAllMeasurements();
            logger.info("Measurements after delete: {}", app.repository.getTotalCount());

        } finally {
            app.closeResources();
        }
    }
}