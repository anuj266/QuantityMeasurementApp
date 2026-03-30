package com.anuj.quantityMeasurement.entity;

import java.time.LocalDateTime;

import com.anuj.quantityMeasurement.model.QuantityModel;

public class QuantityMeasurementEntity {
	
	private Long          id;
    private QuantityModel operand1;
    private QuantityModel operand2;
    private String        operation;
    private QuantityModel result;
    private String        measurementType;
    private LocalDateTime createdAt;

    public QuantityMeasurementEntity(QuantityModel operand1, QuantityModel operand2,
                                     String operation, QuantityModel result,
                                     String measurementType) {
        this.operand1        = operand1;
        this.operand2        = operand2;
        this.operation       = operation;
        this.result          = result;
        this.measurementType = measurementType;
        this.createdAt       = LocalDateTime.now();
    }

    public Long          getId()              { return id; }
    public QuantityModel getOperand1()        { return operand1; }
    public QuantityModel getOperand2()        { return operand2; }
    public String        getOperation()       { return operation; }
    public QuantityModel getResult()          { return result; }
    public String        getMeasurementType() { return measurementType; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void          setId(Long id)       { this.id = id; }

    @Override
    public String toString() {
        return String.format("[%s] %.2f %s %s %.2f %s = %.2f %s",
            measurementType,
            operand1.getValue(), operand1.getUnit(), operation,
            operand2.getValue(), operand2.getUnit(),
            result.getValue(),   result.getUnit());
    }
	
}