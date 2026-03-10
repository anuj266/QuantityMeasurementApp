package com.anuj.quantityMeasurement;

public interface IMeasureable {
	
	public double getConversionFactor();
	
	public double convertToBaseUnit(double value);
	
	public double convertFromBaseUnit(double value);
	
}
