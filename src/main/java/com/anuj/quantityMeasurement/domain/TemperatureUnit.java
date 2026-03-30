package com.anuj.quantityMeasurement.domain;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {

	CELSIUS(celsius -> celsius, celsius -> celsius),
	FAHRENHEIT(fahrenheit -> (fahrenheit - 32) * 5.0 / 9.0, celsius -> (celsius * 9.0 / 5.0) + 32),
	KELVIN(kelvin -> kelvin - 273.15, celsius -> celsius + 273.15);

	private final Function<Double, Double> toBase;
	private final Function<Double, Double> fromBase;

	SupportsArithmetic supportsArithmetic = () -> false;

	TemperatureUnit(Function<Double, Double> toBase, Function<Double, Double> fromBase) {
		this.toBase   = toBase;
		this.fromBase = fromBase;
	}

	@Override
	public double convertToBaseUnit(double value) {
		return toBase.apply(value);
	}

	@Override
	public double convertFromBaseUnit(double value) {
		return fromBase.apply(value);
	}

	@Override
	public boolean supportsArithmetic() {
		return supportsArithmetic.isSupported(); 
	}

	@Override
	public void validateOperationSupport(String operation) {
		throw new UnsupportedOperationException("Operation '" + operation + "' is not supported for TemperatureUnit");
	}
}