package com.anuj.quantityMeasurement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementCacheRepository;
import com.anuj.quantityMeasurement.service.IQuantityMeasurementService;
import com.anuj.quantityMeasurement.service.QuantityMeasurementServiceImpl;

class QuantityMeasurementServiceTest {

    private IQuantityMeasurementService service;

    @BeforeEach
    void setUp() {
        service = new QuantityMeasurementServiceImpl(new QuantityMeasurementCacheRepository());
    }

    //compare 
    
    @Test
    void testCompare_EqualWeights_ReturnsTrue() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        assertTrue(service.compare(q1, q2));
    }

    @Test
    void testCompare_UnequalWeights_ReturnsFalse() {
        QuantityDTO q1 = new QuantityDTO(500.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        assertFalse(service.compare(q1, q2));
    }

    @Test
    void testCompare_EqualVolumes_ReturnsTrue() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        QuantityDTO q2 = new QuantityDTO(1.0, "LITRE", "VOLUME");
        assertTrue(service.compare(q1, q2));
    }

    @Test
    void testCompare_EqualTemperatures_ReturnsTrue() {
        QuantityDTO q1 = new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO q2 = new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE");
        assertTrue(service.compare(q1, q2));
    }

    @Test
    void testCompare_EqualLengths_ReturnsTrue() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCHES", "LENGTH");
        assertTrue(service.compare(q1, q2));
    }

    //convert

    @Test
    void testConvert_GramToKilogram() {
        QuantityDTO q1     = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO target = new QuantityDTO(0, "KILOGRAM", "WEIGHT");
        QuantityDTO result = service.convert(q1, target);
        assertEquals(1.0, result.getValue(), 1e-6);
        assertEquals("KILOGRAM", result.getUnit());
        assertEquals("WEIGHT", result.getType());
    }

    @Test
    void testConvert_MillilitreToLitre() {
        QuantityDTO q1     = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        QuantityDTO target = new QuantityDTO(0, "LITRE", "VOLUME");
        QuantityDTO result = service.convert(q1, target);
        assertEquals(1.0, result.getValue(), 1e-6);
        assertEquals("LITRE", result.getUnit());
    }

    @Test
    void testConvert_CelsiusToFahrenheit() {
        QuantityDTO q1     = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO target = new QuantityDTO(0, "FAHRENHEIT", "TEMPERATURE");
        QuantityDTO result = service.convert(q1, target);
        assertEquals(212.0, result.getValue(), 1e-4);
        assertEquals("FAHRENHEIT", result.getUnit());
    }

    @Test
    void testConvert_FeetToInches() {
        QuantityDTO q1     = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO target = new QuantityDTO(0, "INCHES", "LENGTH");
        QuantityDTO result = service.convert(q1, target);
        assertEquals(12.0, result.getValue(), 1e-6);
        assertEquals("INCHES", result.getUnit());
    }

    // add

    @Test
    void testAdd_Weights_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO result = service.add(q1, q2);
        assertEquals(2000.0, result.getValue(), 1e-4);
        assertEquals("GRAM", result.getUnit());
        assertEquals("WEIGHT", result.getType());
    }

    @Test
    void testAdd_Volumes_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        QuantityDTO q2 = new QuantityDTO(1.0, "LITRE", "VOLUME");
        QuantityDTO result = service.add(q1, q2);
        assertEquals(2000.0, result.getValue(), 1e-4);
        assertEquals("MILLILITRE", result.getUnit());
    }

    @Test
    void testAdd_Lengths_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCHES", "LENGTH");
        QuantityDTO result = service.add(q1, q2);
        assertEquals(2.0, result.getValue(), 1e-6);
        assertEquals("FEET", result.getUnit());
    }

    @Test
    void testAdd_Temperature_ThrowsUnsupportedOperation() {
        QuantityDTO q1 = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO q2 = new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE");
        assertThrows(UnsupportedOperationException.class, () -> service.add(q1, q2));
    }

    //subtract
    
    @Test
    void testSubtract_Weights_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(2000.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO result = service.subtract(q1, q2);
        assertEquals(1000.0, result.getValue(), 1e-4);
        assertEquals("GRAM", result.getUnit());
        assertEquals("WEIGHT", result.getType());
    }

    @Test
    void testSubtract_Volumes_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(2000.0, "MILLILITRE", "VOLUME");
        QuantityDTO q2 = new QuantityDTO(1.0, "LITRE", "VOLUME");
        QuantityDTO result = service.subtract(q1, q2);
        assertEquals(1000.0, result.getValue(), 1e-4);
        assertEquals("MILLILITRE", result.getUnit());
    }

    @Test
    void testSubtract_Lengths_ResultInFirstUnit() {
        QuantityDTO q1 = new QuantityDTO(2.0, "FEET", "LENGTH");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCHES", "LENGTH");
        QuantityDTO result = service.subtract(q1, q2);
        assertEquals(1.0, result.getValue(), 1e-6);
        assertEquals("FEET", result.getUnit());
    }

    @Test
    void testSubtract_Temperature_ThrowsUnsupportedOperation() {
        QuantityDTO q1 = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO q2 = new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE");
        assertThrows(UnsupportedOperationException.class, () -> service.subtract(q1, q2));
    }

    //divide

    @Test
    void testDivide_Weights() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        assertEquals(1.0, service.divide(q1, q2), 1e-6);
    }

    @Test
    void testDivide_Volumes() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        QuantityDTO q2 = new QuantityDTO(1.0, "LITRE", "VOLUME");
        assertEquals(1.0, service.divide(q1, q2), 1e-6);
    }

    @Test
    void testDivide_Lengths() {
        QuantityDTO q1 = new QuantityDTO(2.0, "FEET", "LENGTH");
        QuantityDTO q2 = new QuantityDTO(12.0, "INCHES", "LENGTH");
        assertEquals(2.0, service.divide(q1, q2), 1e-6);
    }

    @Test
    void testDivide_ZeroDivisor_ThrowsArithmeticException() {
        QuantityDTO q1 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(0.0, "GRAM", "WEIGHT");
        assertThrows(ArithmeticException.class, () -> service.divide(q1, q2));
    }

    //invalid output

    @Test
    void testUnknownType_ThrowsIllegalArgumentException() {
        QuantityDTO q1 = new QuantityDTO(1.0, "GRAM", "INVALID_TYPE");
        QuantityDTO q2 = new QuantityDTO(1.0, "GRAM", "INVALID_TYPE");
        assertThrows(IllegalArgumentException.class, () -> service.compare(q1, q2));
    }

    @Test
    void testUnknownUnit_ThrowsIllegalArgumentException() {
        QuantityDTO q1 = new QuantityDTO(1.0, "UNKNOWN_UNIT", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "GRAM", "WEIGHT");
        assertThrows(IllegalArgumentException.class, () -> service.compare(q1, q2));
    }

    //result dto fields

    @Test
    void testAdd_ResultDTO_HasCorrectType() {
        QuantityDTO q1 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO q2 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO result = service.add(q1, q2);
        assertEquals("WEIGHT", result.getType());
    }

    @Test
    void testConvert_ResultDTO_HasCorrectType() {
        QuantityDTO q1     = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO target = new QuantityDTO(0, "KILOGRAM", "WEIGHT");
        QuantityDTO result = service.convert(q1, target);
        assertEquals("WEIGHT", result.getType());
    }
}