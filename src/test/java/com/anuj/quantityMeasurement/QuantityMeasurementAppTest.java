package com.anuj.quantityMeasurement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.junit.jupiter.api.Test;

class QuantityMeasurementAppTest {
	
	//inches and feet test

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testEquality_SameValue_ForAllUnits(Length.LengthUnit unit) {
        Length l1 = new Length(10.0, unit);
        Length l2 = new Length(10.0, unit);

        assertTrue(l1.equals(l2));
    }

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testEquality_DifferentValue_ForAllUnits(Length.LengthUnit unit) {
        Length l1 = new Length(10.0, unit);
        Length l2 = new Length(20.0, unit);

        assertFalse(l1.equals(l2));
    }

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testFeetEquality_NullComparison(Length.LengthUnit unit) {
        Length l1 = new Length(68.0, unit);

        assertFalse(l1.equals(null));
    }

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testFeetEquality_NonNumericInput(Length.LengthUnit unit) {
        Length l1 = new Length(68.0, unit);

        assertFalse(l1.equals("68"));
    }

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testFeetEquality_SameReference(Length.LengthUnit unit) {
        Length l1 = new Length(68.0, unit);

        assertTrue(l1.equals(l1));
    }

    @ParameterizedTest
    @EnumSource(Length.LengthUnit.class)
    void testFeetEquality_Consistent(Length.LengthUnit unit) {
        Length l1 = new Length(1.0, unit);
        Length l2 = new Length(1.0, unit);

        assertTrue(l1.equals(l2));
        assertTrue(l1.equals(l2));
        assertTrue(l1.equals(l2));
    }

    //cross unit test

    @Test
    void testFeetAndInchesEquality_SameLength() {
        Length feet = new Length(1.0, Length.LengthUnit.FEET);
        Length inches = new Length(12.0, Length.LengthUnit.INCHES);

        assertTrue(feet.equals(inches));
    }

    @Test
    void testFeetAndInchesEquality_DifferentLength() {
        Length feet = new Length(2.0, Length.LengthUnit.FEET);
        Length inches = new Length(12.0, Length.LengthUnit.INCHES);

        assertFalse(feet.equals(inches));
    }
}