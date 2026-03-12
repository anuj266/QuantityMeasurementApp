package com.anuj.quantityMeasurement;

public class Quantity<U extends IMeasureable> {
	
	private double value;
	private U unit;
	private static final double EPSILON = 1e-6;
	
	public Quantity(double value, U unit) {
		if(!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite!");
		if(unit==null) throw new IllegalArgumentException("Unit cannot be null!");
		this.value=value;
		this.unit=unit;
	}
	
	public double getValue() {
		return value;
	}
	
	public U getUnit() {
		return unit;
	}
	
	@Override 
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj==null || getClass()!=obj.getClass()) return false;
		Quantity<?> other= (Quantity<?>) obj;
		if (!this.unit.getClass().equals(other.getUnit().getClass())) return false;
		return Math.abs(unit.convertToBaseUnit(value)-other.getUnit().convertToBaseUnit(other.getValue())) < EPSILON;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(Math.round(unit.convertToBaseUnit(value)/EPSILON));
	}
	
	@Override
	public String toString() {
		return String.format("%.2f %s", value, unit);
	}
	
	public double convertTo(U targetUnit) {
		if(targetUnit==null) throw new IllegalArgumentException("Unit cannot be null!");
		double baseValue=unit.convertToBaseUnit(value);
		return targetUnit.convertFromBaseUnit(baseValue);
	}
	
	public Quantity<U> add(Quantity<U> other, U targetUnit){
		validateArithmeticOperands(other,targetUnit,ArithmeticOperation.ADD);
		double result=performBaseArithmetic(other,ArithmeticOperation.ADD);
		return new Quantity<>(targetUnit.convertFromBaseUnit(result),targetUnit);
	}
	
	public Quantity<U> add(Quantity<U> other){
		validateArithmeticOperands(other,unit,ArithmeticOperation.ADD);
		return add(other,unit);
	}
	
	public Quantity<U> subtract(Quantity<U> other, U targetUnit){
		validateArithmeticOperands(other,targetUnit,ArithmeticOperation.SUBTRACT);
		double result=performBaseArithmetic(other,ArithmeticOperation.SUBTRACT);
		return new Quantity<>(targetUnit.convertFromBaseUnit(result),targetUnit);
	}
	
	public Quantity<U> subtract(Quantity<U> other){
		validateArithmeticOperands(other,unit,ArithmeticOperation.SUBTRACT);
		return subtract(other,unit);
	}
	
	public double divide(Quantity<U> other){
		validateArithmeticOperands(other,unit,ArithmeticOperation.DIVIDE);
		return performBaseArithmetic(other,ArithmeticOperation.DIVIDE);
	}
	
	private void validateArithmeticOperands(Quantity<U> other, U targetUnit, ArithmeticOperation operation) {
		if(other==null) throw new IllegalArgumentException("Quantity cannot be null");
		if(targetUnit==null) throw new IllegalArgumentException("Unit cannot be null");
		if(!unit.supportsArithmetic()) {
			unit.validateOperationSupport(operation.name());
		}
	}
	
	private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
		double thisBase= unit.convertToBaseUnit(value);
		double otherBase= other.getUnit().convertToBaseUnit(other.getValue());
		return operation.compute(thisBase, otherBase);
	}
	
}