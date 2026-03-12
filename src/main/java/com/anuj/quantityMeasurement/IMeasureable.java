package com.anuj.quantityMeasurement;

public interface IMeasureable {
	
	default double getConversionFactor() {
		return 1.0;
	}
	
	public double convertToBaseUnit(double value);
	
	public double convertFromBaseUnit(double value);
	
	@FunctionalInterface
	interface SupportsArithmetic{
		boolean isSupported();
	}
	
	SupportsArithmetic supportsArithmetic = () -> true;
	
	default boolean supportsArithmetic() {
		return supportsArithmetic.isSupported();
	}
	
	default void validateOperationSupport(String operation) { } 
	
}