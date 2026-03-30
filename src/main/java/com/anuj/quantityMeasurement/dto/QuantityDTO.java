package com.anuj.quantityMeasurement.dto;

public class QuantityDTO {
	
	private double value;
	private String unit;
	private String type;
	
	public QuantityDTO(double value, String unit, String type) {
		this.value=value;
		this.unit=unit;
		this.type=type;
	}
	
	public double getValue() { return value; }
	public String getUnit() { return unit; }
	public String getType() { return type; }
	
}