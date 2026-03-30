package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.Controller.QuantityMeasurementController;
import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.anuj.quantityMeasurement.service.QuantityMeasurementServiceImpl;
import com.anuj.quantityMeasurement.util.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementControllerTest {

    private QuantityMeasurementController controller;

    @BeforeEach
    void setUp() {
        ApplicationConfig config = new ApplicationConfig();
        QuantityMeasurementDatabaseRepository repo = new QuantityMeasurementDatabaseRepository(config);
        repo.deleteAll();
        controller = new QuantityMeasurementController(new QuantityMeasurementServiceImpl(repo));
    }

    //compare

    @Test void testCompare_EqualWeights()   { assertTrue(controller.compare(dto(1000,"GRAM","WEIGHT"),      dto(1,"KILOGRAM","WEIGHT"))); }
    @Test void testCompare_UnequalWeights() { assertFalse(controller.compare(dto(500,"GRAM","WEIGHT"),       dto(1,"KILOGRAM","WEIGHT"))); }
    @Test void testCompare_EqualVolumes()   { assertTrue(controller.compare(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME"))); }
    @Test void testCompare_EqualTemps()     { assertTrue(controller.compare(dto(0,"CELSIUS","TEMPERATURE"),  dto(32,"FAHRENHEIT","TEMPERATURE"))); }
    @Test void testCompare_UnequalTemps()   { assertFalse(controller.compare(dto(100,"CELSIUS","TEMPERATURE"),dto(100,"FAHRENHEIT","TEMPERATURE"))); }
    @Test void testCompare_EqualLengths()   { assertTrue(controller.compare(dto(1,"FEET","LENGTH"),          dto(12,"INCHES","LENGTH"))); }

    //convert

    @Test
    void testConvert_GramToKilogram() {
        QuantityDTO r = controller.convert(dto(1000,"GRAM","WEIGHT"), dto(0,"KILOGRAM","WEIGHT"));
        assertEquals(1.0, r.getValue(), 1e-6);
        assertEquals("KILOGRAM", r.getUnit());
    }

    @Test
    void testConvert_CelsiusToFahrenheit() {
        QuantityDTO r = controller.convert(dto(100,"CELSIUS","TEMPERATURE"), dto(0,"FAHRENHEIT","TEMPERATURE"));
        assertEquals(212.0, r.getValue(), 1e-4);
    }

    @Test
    void testConvert_MillilitreToLitre() {
        QuantityDTO r = controller.convert(dto(1000,"MILLILITRE","VOLUME"), dto(0,"LITRE","VOLUME"));
        assertEquals(1.0, r.getValue(), 1e-6);
    }

    @Test
    void testConvert_FeetToInches() {
        QuantityDTO r = controller.convert(dto(1,"FEET","LENGTH"), dto(0,"INCHES","LENGTH"));
        assertEquals(12.0, r.getValue(), 1e-6);
    }

    //add

    @Test
    void testAdd_Weights() {
        QuantityDTO r = controller.add(dto(1000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(2000.0, r.getValue(), 1e-4);
        assertEquals("GRAM", r.getUnit());
    }

    @Test
    void testAdd_Volumes() {
        QuantityDTO r = controller.add(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME"));
        assertEquals(2000.0, r.getValue(), 1e-4);
    }

    @Test
    void testAdd_Lengths() {
        QuantityDTO r = controller.add(dto(1,"FEET","LENGTH"), dto(12,"INCHES","LENGTH"));
        assertEquals(2.0, r.getValue(), 1e-6);
    }

    @Test
    void testAdd_Temperature_Throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> controller.add(dto(100,"CELSIUS","TEMPERATURE"), dto(50,"CELSIUS","TEMPERATURE")));
    }

    //subtract

    @Test
    void testSubtract_Weights() {
        QuantityDTO r = controller.subtract(dto(2000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(1000.0, r.getValue(), 1e-4);
    }

    @Test
    void testSubtract_Volumes() {
        QuantityDTO r = controller.subtract(dto(2000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME"));
        assertEquals(1000.0, r.getValue(), 1e-4);
    }

    @Test
    void testSubtract_Temperature_Throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> controller.subtract(dto(100,"CELSIUS","TEMPERATURE"), dto(50,"CELSIUS","TEMPERATURE")));
    }

    //divide

    @Test void testDivide_Weights() { assertEquals(1.0, controller.divide(dto(1000,"GRAM","WEIGHT"),      dto(1,"KILOGRAM","WEIGHT")), 1e-6); }
    @Test void testDivide_Volumes() { assertEquals(1.0, controller.divide(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME")),    1e-6); }
    @Test void testDivide_Lengths() { assertEquals(2.0, controller.divide(dto(2,"FEET","LENGTH"),          dto(12,"INCHES","LENGTH")),  1e-6); }

    @Test
    void testDivide_ZeroDivisor_Throws() {
        assertThrows(ArithmeticException.class,
            () -> controller.divide(dto(1000,"GRAM","WEIGHT"), dto(0,"GRAM","WEIGHT")));
    }

    //invalid

    @Test void testUnknownType_Throws() { assertThrows(IllegalArgumentException.class, () -> controller.compare(dto(1,"GRAM","BAD_TYPE"), dto(1,"GRAM","BAD_TYPE"))); }
    @Test void testUnknownUnit_Throws() { assertThrows(IllegalArgumentException.class, () -> controller.compare(dto(1,"BAD_UNIT","WEIGHT"), dto(1,"GRAM","WEIGHT"))); }

    // helper
    private QuantityDTO dto(double v, String u, String t) { return new QuantityDTO(v, u, t); }
}