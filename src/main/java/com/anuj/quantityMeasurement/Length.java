package com.anuj.quantityMeasurement;

public class Length {
	
	private double value;
	private LengthUnit unit;
	private static final double EPSILON = 0.0001;
	
	public enum LengthUnit{
		FEET(12.0),
		INCHES(1.0),
		YARDS(36),
		CENTIMETERS(0.393701);
		
		private final double conversionFactor;
		
		LengthUnit(double conversionFactor) {
			this.conversionFactor=conversionFactor;
		}
		
		public double getConversionFactor() {
			return conversionFactor;
		}
	}
	
	public Length(double value, LengthUnit unit) {
		if(value<0) throw new IllegalArgumentException("Length can't be negative");
		if(unit==null) throw new IllegalArgumentException("Unit cannot be null");
		this.value=value;
		this.unit=unit;
	}
	
	private double convertToBaseUnit() {
		return value * unit.getConversionFactor(); 
	}
	
	@Override
	public boolean equals (Object obj) {
		if(this==obj) return true;
		if(obj==null || getClass()!=obj.getClass()) return false;
		Length other= (Length) obj;
		return Math.abs(this.convertToBaseUnit()-other.convertToBaseUnit()) < EPSILON;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(convertToBaseUnit());
	}

}