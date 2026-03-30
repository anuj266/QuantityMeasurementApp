package com.anuj.quantityMeasurement.repository;

import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;

public interface IQuantityMeasurementRepository {
	
	void save(QuantityMeasurementEntity entity);
	
}