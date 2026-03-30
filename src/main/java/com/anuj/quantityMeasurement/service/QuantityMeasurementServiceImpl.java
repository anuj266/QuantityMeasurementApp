package com.anuj.quantityMeasurement.service;

import com.anuj.quantityMeasurement.domain.*;
import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;
import com.anuj.quantityMeasurement.model.QuantityModel;
import com.anuj.quantityMeasurement.repository.IQuantityMeasurementRepository;


public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    private <U extends IMeasurable> U resolveUnit(String type, String unit) {
        switch (type.toUpperCase()) {
            case "LENGTH":      return (U) LengthUnit.valueOf(unit.toUpperCase());
            case "WEIGHT":      return (U) WeightUnit.valueOf(unit.toUpperCase());
            case "VOLUME":      return (U) VolumeUnit.valueOf(unit.toUpperCase());
            case "TEMPERATURE": return (U) TemperatureUnit.valueOf(unit.toUpperCase());
            default: throw new IllegalArgumentException("Unknown measurement type: " + type);
        }
    }

    private <U extends IMeasurable> Quantity<U> toQuantity(QuantityDTO dto) {
        U unit = resolveUnit(dto.getType(), dto.getUnit());
        return new Quantity<>(dto.getValue(), unit);
    }

    private <U extends IMeasurable> QuantityDTO saveAndReturn(
            QuantityDTO q1dto, QuantityDTO q2dto, String operation, Quantity<U> result) {
        QuantityModel op1 = new QuantityModel(q1dto.getValue(), q1dto.getUnit());
        QuantityModel op2 = new QuantityModel(q2dto.getValue(), q2dto.getUnit());
        QuantityModel res = new QuantityModel(result.getValue(), result.getUnit().toString());
        repository.save(new QuantityMeasurementEntity(op1, op2, operation, res));
        return new QuantityDTO(result.getValue(), result.getUnit().toString(), q1dto.getType());
    }

    @Override
    public QuantityDTO add(QuantityDTO q1dto, QuantityDTO q2dto) {
        Quantity<IMeasurable> q1 = toQuantity(q1dto);
        Quantity<IMeasurable> q2 = toQuantity(q2dto);
        return saveAndReturn(q1dto, q2dto, "ADD", q1.add(q2));
    }

    @Override
    public QuantityDTO subtract(QuantityDTO q1dto, QuantityDTO q2dto) {
        Quantity<IMeasurable> q1 = toQuantity(q1dto);
        Quantity<IMeasurable> q2 = toQuantity(q2dto);
        return saveAndReturn(q1dto, q2dto, "SUBTRACT", q1.subtract(q2));
    }

    @Override
    public QuantityDTO convert(QuantityDTO q1dto, QuantityDTO q2dto) {
        Quantity<IMeasurable> q1 = toQuantity(q1dto);
        IMeasurable targetUnit   = resolveUnit(q2dto.getType(), q2dto.getUnit());
        double converted         = q1.convertTo(targetUnit);
        return saveAndReturn(q1dto, q2dto, "CONVERT", new Quantity<>(converted, targetUnit));
    }

    @Override
    public boolean compare(QuantityDTO q1dto, QuantityDTO q2dto) {
        return this.<IMeasurable>toQuantity(q1dto).equals(toQuantity(q2dto));
    }

    @Override
    public double divide(QuantityDTO q1dto, QuantityDTO q2dto) {
        Quantity<IMeasurable> q1 = toQuantity(q1dto);
        Quantity<IMeasurable> q2 = toQuantity(q2dto);
        return q1.divide(q2);
    }
}