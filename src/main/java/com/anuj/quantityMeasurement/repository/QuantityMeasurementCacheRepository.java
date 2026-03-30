package com.anuj.quantityMeasurement.repository;

import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementCacheRepository.class);

    private final List<QuantityMeasurementEntity> storage = new ArrayList<>();

    public QuantityMeasurementCacheRepository() {
        logger.info("QuantityMeasurementCacheRepository initialized (in-memory)");
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        storage.add(entity);
        logger.info("Saved to cache: {}", entity);
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        return storage.stream()
            .filter(e -> e.getOperation().equalsIgnoreCase(operation))
            .collect(Collectors.toList());
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        return storage.stream()
            .filter(e -> e.getMeasurementType().equalsIgnoreCase(measurementType))
            .collect(Collectors.toList());
    }

    @Override
    public int getTotalCount() { return storage.size(); }

    @Override
    public void deleteAll() {
        storage.clear();
        logger.info("Cache cleared");
    }
}