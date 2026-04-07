package com.anuj.quantityMeasurement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementDTO {
	
	private Double thisValue;
	private String thisUnit;
	private String thisMeasurementType;
	
	private Double thatValue;
	private String thatUnit;
	private String thatMeasurementType;
	
	private String operation;
	
	private String resultString;
	private Double resultValue;
	private String resultUnit;
	private String resultMeasurementType;
	
	private String errorMessage;
	private boolean error;
	
	public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity e) {
		return new QuantityMeasurementDTO(
	            e.getThisValue(),
	            e.getThisUnit(),
	            e.getThisMeasurementType(),
	            e.getThatValue(),
	            e.getThatUnit(),
	            e.getThatMeasurementType(),
	            e.getOperation(),
	            e.getResultString(),
	            e.getResultValue(),
	            e.getResultUnit(),
	            e.getResultMeasurementType(),
	            e.getErrorMessage(),
	            e.isError()
	        );
	}
	
	public QuantityMeasurementEntity toEntity() {
		QuantityMeasurementEntity entity=new QuantityMeasurementEntity();
		entity.setThisValue(this.thisValue);
        entity.setThisUnit(this.thisUnit);
        entity.setThisMeasurementType(this.thisMeasurementType);
        entity.setThatValue(this.thatValue);
        entity.setThatUnit(this.thatUnit);
        entity.setThatMeasurementType(this.thatMeasurementType);
        entity.setOperation(this.operation);
        entity.setResultString(this.resultString);
        entity.setResultValue(this.resultValue);
        entity.setResultUnit(this.resultUnit);
        entity.setResultMeasurementType(this.resultMeasurementType);
        entity.setErrorMessage(this.errorMessage);
        entity.setError(this.error);
        return entity;
	}
	
	public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities){
		return entities.stream().map(QuantityMeasurementDTO::fromEntity).collect(Collectors.toList());
	}
	
}