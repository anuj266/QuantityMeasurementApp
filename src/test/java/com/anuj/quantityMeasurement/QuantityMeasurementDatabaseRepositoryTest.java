package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;
import com.anuj.quantityMeasurement.model.QuantityModel;
import com.anuj.quantityMeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.anuj.quantityMeasurement.util.ApplicationConfig;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementDatabaseRepositoryTest {

    private static QuantityMeasurementDatabaseRepository repository;

    @BeforeAll
    static void setUpAll() {
        ApplicationConfig config = new ApplicationConfig();
        repository = new QuantityMeasurementDatabaseRepository(config);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        repository.releaseResources();
    }

    private QuantityMeasurementEntity makeEntity(String op, String type) {
        return new QuantityMeasurementEntity(
            new QuantityModel(1000.0, "GRAM"),
            new QuantityModel(1.0,    "KILOGRAM"),
            op,
            new QuantityModel(2000.0, "GRAM"),
            type);
    }

    @Test @Order(1)
    void testSaveEntity_PersistsToDatabase() {
        repository.save(makeEntity("ADD", "WEIGHT"));
        assertEquals(1, repository.getTotalCount());
    }

    @Test @Order(2)
    void testGetAllMeasurements_ReturnsAll() {
        repository.save(makeEntity("ADD",      "WEIGHT"));
        repository.save(makeEntity("SUBTRACT", "WEIGHT"));
        repository.save(makeEntity("CONVERT",  "VOLUME"));
        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(3, all.size());
    }

    @Test @Order(3)
    void testGetMeasurementsByOperation_FiltersCorrectly() {
        repository.save(makeEntity("ADD",      "WEIGHT"));
        repository.save(makeEntity("ADD",      "VOLUME"));
        repository.save(makeEntity("SUBTRACT", "WEIGHT"));
        List<QuantityMeasurementEntity> addOps = repository.getMeasurementsByOperation("ADD");
        assertEquals(2, addOps.size());
        addOps.forEach(e -> assertEquals("ADD", e.getOperation()));
    }

    @Test @Order(4)
    void testGetMeasurementsByType_FiltersCorrectly() {
        repository.save(makeEntity("ADD",     "WEIGHT"));
        repository.save(makeEntity("CONVERT", "VOLUME"));
        repository.save(makeEntity("ADD",     "WEIGHT"));
        List<QuantityMeasurementEntity> weights = repository.getMeasurementsByType("WEIGHT");
        assertEquals(2, weights.size());
        weights.forEach(e -> assertEquals("WEIGHT", e.getMeasurementType()));
    }

    @Test @Order(5)
    void testGetTotalCount_ReturnsCorrectCount() {
        assertEquals(0, repository.getTotalCount());
        repository.save(makeEntity("ADD", "WEIGHT"));
        repository.save(makeEntity("ADD", "WEIGHT"));
        assertEquals(2, repository.getTotalCount());
    }

    @Test @Order(6)
    void testDeleteAll_ClearsDatabase() {
        repository.save(makeEntity("ADD", "WEIGHT"));
        repository.save(makeEntity("ADD", "WEIGHT"));
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    @Test @Order(7)
    void testSQLInjectionPrevention() {
        // Parameterized query treats this as a literal string — no rows match
        List<QuantityMeasurementEntity> result =
            repository.getMeasurementsByOperation("ADD'; DROP TABLE quantity_measurement_entity; --");
        assertTrue(result.isEmpty());
        // Table still intact
        assertDoesNotThrow(() -> repository.getTotalCount());
    }

    @Test @Order(8)
    void testGetPoolStatistics_ReturnsStats() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("ConnectionPool"));
    }

    @Test @Order(9)
    void testSave_EntityGetsId() {
        QuantityMeasurementEntity entity = makeEntity("ADD", "WEIGHT");
        assertNull(entity.getId());
        repository.save(entity);
        assertNotNull(entity.getId());
        assertTrue(entity.getId() > 0);
    }

    @Test @Order(10)
    void testLargeDataSet_Performance() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            repository.save(makeEntity("ADD", "WEIGHT"));
        }
        long elapsed = System.currentTimeMillis() - start;
        assertEquals(100, repository.getTotalCount());
        assertTrue(elapsed < 10000, "100 inserts should complete in < 10s, took: " + elapsed + "ms");
    }

    @Test @Order(11)
    void testDataIsolation_DeleteBetweenTests() {
        // deleteAll() in @BeforeEach ensures each test starts clean
        assertEquals(0, repository.getTotalCount());
    }

    @Test @Order(12)
    void testDatabaseSchema_TablesExist() {
        // If tables didn't exist, save() would throw — this verifies schema loaded correctly
        assertDoesNotThrow(() -> repository.save(makeEntity("ADD", "LENGTH")));
        assertDoesNotThrow(() -> repository.getAllMeasurements());
    }
}