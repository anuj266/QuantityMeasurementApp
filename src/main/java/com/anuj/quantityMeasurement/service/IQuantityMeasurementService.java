package com.anuj.quantityMeasurement.service;

import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.dto.QuantityDTO.IMeasurableUnit;

public interface IQuantityMeasurementService {

    boolean compare(QuantityDTO q1, QuantityDTO q2);

    QuantityDTO convert(QuantityDTO input, IMeasurableUnit targetUnit);

    QuantityDTO add(QuantityDTO q1, QuantityDTO q2);

    QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2);

    double divide(QuantityDTO q1, QuantityDTO q2);
}
