package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.dto.QuantityDTO;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementCacheRepository;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.anuj.quantityMeasurement.service.*;
import com.anuj.quantityMeasurement.util.ApplicationConfig;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementServiceTest {

    private IQuantityMeasurementService service;

    @BeforeEach
    void setUp() {
        // Use DB repository wired to H2 test database
        ApplicationConfig config = new ApplicationConfig();
        QuantityMeasurementDatabaseRepository repo = new QuantityMeasurementDatabaseRepository(config);
        repo.deleteAll();
        service = new QuantityMeasurementServiceImpl(repo);
    }

    //compare

    @Test void testCompare_EqualWeights()     { assertTrue(service.compare(dto(1000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"))); }
    @Test void testCompare_UnequalWeights()   { assertFalse(service.compare(dto(500,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"))); }
    @Test void testCompare_EqualVolumes()     { assertTrue(service.compare(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME"))); }
    @Test void testCompare_EqualTemp()        { assertTrue(service.compare(dto(0,"CELSIUS","TEMPERATURE"), dto(32,"FAHRENHEIT","TEMPERATURE"))); }
    @Test void testCompare_EqualLengths()     { assertTrue(service.compare(dto(1,"FEET","LENGTH"), dto(12,"INCHES","LENGTH"))); }

    //convert

    @Test
    void testConvert_GramToKilogram() {
        QuantityDTO r = service.convert(dto(1000,"GRAM","WEIGHT"), dto(0,"KILOGRAM","WEIGHT"));
        assertEquals(1.0,  r.getValue(), 1e-6);
        assertEquals("KILOGRAM", r.getUnit());
        assertEquals("WEIGHT",   r.getType());
    }

    @Test
    void testConvert_CelsiusToFahrenheit() {
        QuantityDTO r = service.convert(dto(100,"CELSIUS","TEMPERATURE"), dto(0,"FAHRENHEIT","TEMPERATURE"));
        assertEquals(212.0, r.getValue(), 1e-4);
    }

    @Test
    void testConvert_FeetToInches() {
        QuantityDTO r = service.convert(dto(1,"FEET","LENGTH"), dto(0,"INCHES","LENGTH"));
        assertEquals(12.0, r.getValue(), 1e-6);
    }

    //add

    @Test
    void testAdd_Weights() {
        QuantityDTO r = service.add(dto(1000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(2000.0, r.getValue(), 1e-4);
        assertEquals("GRAM", r.getUnit());
    }

    @Test
    void testAdd_Volumes() {
        QuantityDTO r = service.add(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME"));
        assertEquals(2000.0, r.getValue(), 1e-4);
    }

    @Test
    void testAdd_Temperature_Throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> service.add(dto(100,"CELSIUS","TEMPERATURE"), dto(50,"CELSIUS","TEMPERATURE")));
    }

    //subtract

    @Test
    void testSubtract_Weights() {
        QuantityDTO r = service.subtract(dto(2000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(1000.0, r.getValue(), 1e-4);
        assertEquals("GRAM", r.getUnit());
    }

    @Test
    void testSubtract_Temperature_Throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> service.subtract(dto(100,"CELSIUS","TEMPERATURE"), dto(50,"CELSIUS","TEMPERATURE")));
    }

    //divide

    @Test void testDivide_Weights()  { assertEquals(1.0, service.divide(dto(1000,"GRAM","WEIGHT"),      dto(1,"KILOGRAM","WEIGHT")), 1e-6); }
    @Test void testDivide_Volumes()  { assertEquals(1.0, service.divide(dto(1000,"MILLILITRE","VOLUME"), dto(1,"LITRE","VOLUME")),    1e-6); }
    @Test void testDivide_ZeroDivisor() {
        assertThrows(ArithmeticException.class,
            () -> service.divide(dto(1000,"GRAM","WEIGHT"), dto(0,"GRAM","WEIGHT")));
    }

    //persistence

    @Test
    void testAdd_PersistsToRepository() {
        ApplicationConfig config = new ApplicationConfig();
        QuantityMeasurementDatabaseRepository repo = new QuantityMeasurementDatabaseRepository(config);
        repo.deleteAll();
        IQuantityMeasurementService svc = new QuantityMeasurementServiceImpl(repo);
        svc.add(dto(1000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(1, repo.getTotalCount());
        repo.releaseResources();
    }

    //invalid input

    @Test void testUnknownType_Throws()  { assertThrows(IllegalArgumentException.class, () -> service.compare(dto(1,"GRAM","INVALID"), dto(1,"GRAM","INVALID"))); }
    @Test void testUnknownUnit_Throws()  { assertThrows(IllegalArgumentException.class, () -> service.compare(dto(1,"BAD_UNIT","WEIGHT"), dto(1,"GRAM","WEIGHT"))); }

    //service with cache repository

    @Test
    void testServiceWithCacheRepository() {
        QuantityMeasurementCacheRepository cache = new QuantityMeasurementCacheRepository();
        IQuantityMeasurementService cacheSvc = new QuantityMeasurementServiceImpl(cache);
        cacheSvc.add(dto(1000,"GRAM","WEIGHT"), dto(1,"KILOGRAM","WEIGHT"));
        assertEquals(1, cache.getTotalCount());
    }

    // helper
    private QuantityDTO dto(double v, String u, String t) { return new QuantityDTO(v, u, t); }
}