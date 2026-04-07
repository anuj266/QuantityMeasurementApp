package com.anuj.quantityMeasurement.service;

import com.anuj.quantityMeasurement.exception.QuantityMeasurementException;
import com.anuj.quantityMeasurement.model.*;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementRepository;
import com.anuj.quantityMeasurement.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

	private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);
	
	@Autowired
    private QuantityMeasurementRepository repository;
	
	@SuppressWarnings("unchecked")
    private <U extends IMeasurable> U resolveUnit(String measurementType, String unit) {
        switch (measurementType) {
            case "LengthUnit":
                return (U) LengthUnit.valueOf(unit.toUpperCase());
            case "WeightUnit":
                return (U) WeightUnit.valueOf(unit.toUpperCase());
            case "VolumeUnit":
                return (U) VolumeUnit.valueOf(unit.toUpperCase());
            case "TemperatureUnit":
                return (U) TemperatureUnit.valueOf(unit.toUpperCase());
            default:
                throw new QuantityMeasurementException(
                    "Unknown measurement type: " + measurementType +
                    ". Valid types: LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit");
        }
    }
	
	private <U extends IMeasurable> Quantity<U> convertDtoToModel(QuantityDTO dto) {
        U unit = resolveUnit(dto.getMeasurementType(), dto.getUnit());
        return new Quantity<>(dto.getValue(), unit);
    }

	private QuantityMeasurementDTO buildSaveAndReturn(
            QuantityDTO thisDto, QuantityDTO thatDto,
            String operation,
            Double resultValue, String resultUnit, String resultMeasurementType,
            String resultString,
            String errorMessage, boolean isError) {

        QuantityMeasurementDTO dto = new QuantityMeasurementDTO(
            thisDto.getValue(),   thisDto.getUnit(),   thisDto.getMeasurementType(),
            thatDto.getValue(),   thatDto.getUnit(),   thatDto.getMeasurementType(),
            operation,
            resultString, resultValue, resultUnit, resultMeasurementType,
            errorMessage, isError
        );
        repository.save(dto.toEntity());
        logger.info("Saved operation '{}' to database. Error: {}", operation, isError);

        return dto;
    }
	
	@Override
    public QuantityMeasurementDTO compareQuantities(QuantityDTO thisDto, QuantityDTO thatDto) {
        logger.info("compareQuantities: {} {} vs {} {}",
            thisDto.getValue(), thisDto.getUnit(),
            thatDto.getValue(), thatDto.getUnit());
        try {
            Quantity<IMeasurable> q1 = convertDtoToModel(thisDto);
            Quantity<IMeasurable> q2 = convertDtoToModel(thatDto);
            boolean result = q1.equals(q2);
            
            return buildSaveAndReturn(thisDto, thatDto, "compare",
                0.0, null, null, String.valueOf(result), null, false);
        } catch (Exception e) {
            logger.error("compareQuantities error: {}", e.getMessage());
            // Save the error record, then return DTO with error info
            return buildSaveAndReturn(thisDto, thatDto, "compare",
                0.0, null, null, null, e.getMessage(), true);
        }
    }

    @Override
    public QuantityMeasurementDTO convertQuantity(QuantityDTO thisDto, QuantityDTO thatDto) {
        logger.info("convertQuantity: {} {} → {}",
            thisDto.getValue(), thisDto.getUnit(), thatDto.getUnit());
        try {
            Quantity<IMeasurable> q1 = convertDtoToModel(thisDto);
            
            IMeasurable targetUnit = resolveUnit(thatDto.getMeasurementType(), thatDto.getUnit());
            double converted = q1.convertTo(targetUnit);
            return buildSaveAndReturn(thisDto, thatDto, "convert",
                converted, null, null, null, null, false);
        } catch (Exception e) {
            logger.error("convertQuantity error: {}", e.getMessage());
            return buildSaveAndReturn(thisDto, thatDto, "convert",
                0.0, null, null, null, e.getMessage(), true);
        }
    }

    @Override
    public QuantityMeasurementDTO addQuantities(QuantityDTO thisDto, QuantityDTO thatDto) {
        logger.info("addQuantities: {} {} + {} {}",
            thisDto.getValue(), thisDto.getUnit(),
            thatDto.getValue(), thatDto.getUnit());
        try {
            validateSameMeasurementType(thisDto, thatDto, "add");
            Quantity<IMeasurable> q1 = convertDtoToModel(thisDto);
            Quantity<IMeasurable> q2 = convertDtoToModel(thatDto);
            // add(q2) returns result in the unit of q1 (first operand's unit)
            Quantity<IMeasurable> result = q1.add(q2);
            return buildSaveAndReturn(thisDto, thatDto, "add",
                result.getValue(), result.getUnit().toString(),
                thisDto.getMeasurementType(), null, null, false);
        } catch (QuantityMeasurementException e) {
            buildSaveAndReturn(thisDto, thatDto, "add",
                0.0, null, null, null, e.getMessage(), true);
            throw e;
        } catch (Exception e) {
            logger.error("addQuantities error: {}", e.getMessage());
            buildSaveAndReturn(thisDto, thatDto, "add",
                0.0, null, null, null, e.getMessage(), true);
            throw new QuantityMeasurementException("add Error: " + e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO subtractQuantities(QuantityDTO thisDto, QuantityDTO thatDto) {
        logger.info("subtractQuantities: {} {} - {} {}",
            thisDto.getValue(), thisDto.getUnit(),
            thatDto.getValue(), thatDto.getUnit());
        try {
            validateSameMeasurementType(thisDto, thatDto, "subtract");
            Quantity<IMeasurable> q1 = convertDtoToModel(thisDto);
            Quantity<IMeasurable> q2 = convertDtoToModel(thatDto);
            Quantity<IMeasurable> result = q1.subtract(q2);
            return buildSaveAndReturn(thisDto, thatDto, "subtract",
                result.getValue(), result.getUnit().toString(),
                thisDto.getMeasurementType(), null, null, false);
        } catch (QuantityMeasurementException e) {
            buildSaveAndReturn(thisDto, thatDto, "subtract",
                0.0, null, null, null, e.getMessage(), true);
            throw e;
        } catch (Exception e) {
            logger.error("subtractQuantities error: {}", e.getMessage());
            buildSaveAndReturn(thisDto, thatDto, "subtract",
                0.0, null, null, null, e.getMessage(), true);
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO divideQuantities(QuantityDTO thisDto, QuantityDTO thatDto) {
        logger.info("divideQuantities: {} {} ÷ {} {}",
            thisDto.getValue(), thisDto.getUnit(),
            thatDto.getValue(), thatDto.getUnit());
        try {
            validateSameMeasurementType(thisDto, thatDto, "divide");
            Quantity<IMeasurable> q1 = convertDtoToModel(thisDto);
            Quantity<IMeasurable> q2 = convertDtoToModel(thatDto);
            double result = q1.divide(q2);
            return buildSaveAndReturn(thisDto, thatDto, "divide",
                result, null, null, null, null, false);
        } catch (QuantityMeasurementException e) {
            buildSaveAndReturn(thisDto, thatDto, "divide",
                0.0, null, null, null, e.getMessage(), true);
            throw e;
        } catch (Exception e) {
            logger.error("divideQuantities error: {}", e.getMessage());
            buildSaveAndReturn(thisDto, thatDto, "divide",
                0.0, null, null, null, e.getMessage(), true);
            throw e;
        }
    }

    
    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
       
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByOperation(operation.toLowerCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByIsErrorTrue());
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation.toLowerCase());
    }

    private void validateSameMeasurementType(
            QuantityDTO a, QuantityDTO b, String op) {
        if (!a.getMeasurementType().equals(b.getMeasurementType())) {
            throw new QuantityMeasurementException(
                "Cannot perform arithmetic between different measurement categories: "
                + a.getMeasurementType() + " and " + b.getMeasurementType());
        }
    }
}