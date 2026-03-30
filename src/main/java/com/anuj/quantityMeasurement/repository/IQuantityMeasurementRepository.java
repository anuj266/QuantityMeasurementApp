package com.anuj.quantityMeasurement.repository;

import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;

import java.util.List;

public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation);

    List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

    int getTotalCount();

    void deleteAll();

    default String getPoolStatistics() {
        return "Pool statistics not available for this repository";
    }

    default void releaseResources() { }
}