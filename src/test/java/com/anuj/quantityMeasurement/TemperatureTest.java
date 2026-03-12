package com.anuj.quantityMeasurement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import org.junit.jupiter.api.Test;

class TemperatureTest {

	private static final double EPSILON = 1e-4;

	// equality tests

	@ParameterizedTest
	@EnumSource(TemperatureUnit.class)
	void testEquality_SameValue_ForAllUnits(TemperatureUnit unit) {
		Quantity<TemperatureUnit> t1 = new Quantity<>(10.0, unit);
		Quantity<TemperatureUnit> t2 = new Quantity<>(10.0, unit);
		assertTrue(t1.equals(t2));
	}

	@ParameterizedTest
	@EnumSource(TemperatureUnit.class)
	void testEquality_DifferentValue_ForAllUnits(TemperatureUnit unit) {
		Quantity<TemperatureUnit> t1 = new Quantity<>(10.0, unit);
		Quantity<TemperatureUnit> t2 = new Quantity<>(20.0, unit);
		assertFalse(t1.equals(t2));
	}

	@ParameterizedTest
	@EnumSource(TemperatureUnit.class)
	void testEquality_NullComparison(TemperatureUnit unit) {
		assertFalse(new Quantity<>(10.0, unit).equals(null));
	}

	@ParameterizedTest
	@EnumSource(TemperatureUnit.class)
	void testEquality_SameReference(TemperatureUnit unit) {
		Quantity<TemperatureUnit> t1 = new Quantity<>(10.0, unit);
		assertTrue(t1.equals(t1));
	}

	// cross unit equality

	@ParameterizedTest
	@CsvSource({
		"0.0,    CELSIUS,    273.15, KELVIN",
		"100.0,  CELSIUS,    212.0,  FAHRENHEIT",
		"0.0,    CELSIUS,    32.0,   FAHRENHEIT",
		"-40.0,  CELSIUS,    -40.0,  FAHRENHEIT",
		"0.0,    KELVIN,     -273.15,CELSIUS"
	})
	void testCrossUnitEquality_SameTemperature(double v1, TemperatureUnit u1,
			double v2, TemperatureUnit u2) {
		assertTrue(new Quantity<>(v1, u1).equals(new Quantity<>(v2, u2)));
	}

	@ParameterizedTest
	@CsvSource({
		"100.0, CELSIUS,    100.0,  FAHRENHEIT",
		"0.0,   CELSIUS,    0.0,    KELVIN",
		"100.0, CELSIUS,    100.0,  KELVIN"
	})
	void testCrossUnitEquality_DifferentTemperature(double v1, TemperatureUnit u1,
			double v2, TemperatureUnit u2) {
		assertFalse(new Quantity<>(v1, u1).equals(new Quantity<>(v2, u2)));
	}

	// conversion tests

	@ParameterizedTest
	@CsvSource({
		"0.0,    CELSIUS,     FAHRENHEIT, 32.0",
		"100.0,  CELSIUS,     FAHRENHEIT, 212.0",
		"-40.0,  CELSIUS,     FAHRENHEIT, -40.0",
		"32.0,   FAHRENHEIT,  CELSIUS,    0.0",
		"212.0,  FAHRENHEIT,  CELSIUS,    100.0",
		"0.0,    CELSIUS,     KELVIN,     273.15",
		"100.0,  CELSIUS,     KELVIN,     373.15",
		"273.15, KELVIN,      CELSIUS,    0.0",
		"0.0,    KELVIN,      CELSIUS,    -273.15",
		"5.0,    CELSIUS,     CELSIUS,    5.0"
	})
	void testConversion(double value, TemperatureUnit source,
			TemperatureUnit target, double expected) {
		assertEquals(expected, new Quantity<>(value, source).convertTo(target), EPSILON);
	}

	@ParameterizedTest
	@EnumSource(TemperatureUnit.class)
	void testRoundTrip_AllUnitsToCelsiusAndBack(TemperatureUnit unit) {
		double original  = 100.0;
		double converted = new Quantity<>(original, unit).convertTo(TemperatureUnit.CELSIUS);
		double back      = new Quantity<>(converted, TemperatureUnit.CELSIUS).convertTo(unit);
		assertEquals(original, back, EPSILON);
	}

	// unsupported operations tests

	@Test
	void testAdd_ThrowsUnsupportedOperation() {
		Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<TemperatureUnit> t2 = new Quantity<>(50.0,  TemperatureUnit.CELSIUS);
		assertThrows(UnsupportedOperationException.class, () -> t1.add(t2));
	}

	@Test
	void testSubtract_ThrowsUnsupportedOperation() {
		Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<TemperatureUnit> t2 = new Quantity<>(50.0,  TemperatureUnit.CELSIUS);
		assertThrows(UnsupportedOperationException.class, () -> t1.subtract(t2));
	}

	@Test
	void testDivide_ThrowsUnsupportedOperation() {
		Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<TemperatureUnit> t2 = new Quantity<>(50.0,  TemperatureUnit.CELSIUS);
		assertThrows(UnsupportedOperationException.class, () -> t1.divide(t2));
	}

	// cross category type safety

	@Test
	void testTemperatureAndLengthIncompatibility() {
		Quantity<TemperatureUnit> temp   = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<LengthUnit>      length = new Quantity<>(100.0, LengthUnit.FEET);
		assertFalse(temp.equals(length));
	}

	@Test
	void testTemperatureAndWeightIncompatibility() {
		Quantity<TemperatureUnit> temp   = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<WeightUnit>      weight = new Quantity<>(100.0, WeightUnit.KILOGRAM);
		assertFalse(temp.equals(weight));
	}

	@Test
	void testTemperatureAndVolumeIncompatibility() {
		Quantity<TemperatureUnit> temp   = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
		Quantity<VolumeUnit>      volume = new Quantity<>(100.0, VolumeUnit.LITRE);
		assertFalse(temp.equals(volume));
	}

	// constructor validation

	@Test
	void testConstructor_NaN_throws() {
		assertThrows(IllegalArgumentException.class,
				() -> new Quantity<>(Double.NaN, TemperatureUnit.CELSIUS));
	}

	@Test
	void testConstructor_Infinite_throws() {
		assertThrows(IllegalArgumentException.class,
				() -> new Quantity<>(Double.POSITIVE_INFINITY, TemperatureUnit.CELSIUS));
	}

	@Test
	void testConstructor_NullUnit_throws() {
		assertThrows(IllegalArgumentException.class,
				() -> new Quantity<>(1.0, null));
	}
}
