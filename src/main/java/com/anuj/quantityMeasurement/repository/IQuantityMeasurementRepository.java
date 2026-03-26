package com.anuj.quantityMeasurement.repository;

import java.util.List;
import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;

public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();
}
