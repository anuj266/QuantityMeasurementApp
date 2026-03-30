package com.anuj.quantityMeasurement.entity;

import com.anuj.quantityMeasurement.model.QuantityModel;

public class QuantityMeasurementEntity {
	
	private  QuantityModel operand1;
	private QuantityModel operand2;
	private String operation;
	private QuantityModel result;
	
	public QuantityMeasurementEntity(QuantityModel operand1, QuantityModel operand2, String operation, QuantityModel result) {
		this.operand1=operand1;
		this.operand2=operand2;
		this.operation=operation;
		this.result=result;
	}
	
	@Override
    public String toString() {
        return operand1.getValue() + " " + operand1.getUnit() + " "+ operation + " "+ operand2.getValue() 
               + " " + operand2.getUnit() + " = " + result.getValue() + " " + result.getUnit();
    }
	
}