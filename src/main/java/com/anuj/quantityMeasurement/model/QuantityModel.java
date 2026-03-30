package com.anuj.quantityMeasurement.model;

public class QuantityModel {
	
	private double value;
	private String unit;
	
	public QuantityModel(double value, String unit) {
		this.value=value;
		this.unit=unit;
	}
	
	public double getValue() { return value; }
	public String getUnit() { return unit; }
}