package com.anuj.quantityMeasurement.service;

import com.anuj.quantityMeasurement.dto.QuantityDTO;

public interface IQuantityMeasurementService {
	
	QuantityDTO add(QuantityDTO q1, QuantityDTO q2);
	
	QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2);
	
	QuantityDTO convert(QuantityDTO q1, QuantityDTO q2);
	
	boolean compare(QuantityDTO q1, QuantityDTO q2);
	
	double divide(QuantityDTO q1, QuantityDTO q2);
	
}