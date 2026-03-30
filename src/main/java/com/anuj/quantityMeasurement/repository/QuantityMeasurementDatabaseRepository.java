package com.anuj.quantityMeasurement.repository;

import com.anuj.quantityMeasurement.entity.QuantityMeasurementEntity;
import com.anuj.quantityMeasurement.model.QuantityModel;
import com.anuj.quantityMeasurement.exception.DatabaseException;
import com.anuj.quantityMeasurement.util.ApplicationConfig;
import com.anuj.quantityMeasurement.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementDatabaseRepository.class);

    private static final String INSERT_SQL =
        "INSERT INTO quantity_measurement_entity " +
        "(operand1_value, operand1_unit, operand2_value, operand2_unit, " +
        " operation, result_value, result_unit, measurement_type) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
        "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";

    private static final String SELECT_BY_OPERATION_SQL =
        "SELECT * FROM quantity_measurement_entity WHERE operation = ? ORDER BY created_at DESC";

    private static final String SELECT_BY_TYPE_SQL =
        "SELECT * FROM quantity_measurement_entity WHERE measurement_type = ? ORDER BY created_at DESC";

    private static final String COUNT_SQL =
        "SELECT COUNT(*) FROM quantity_measurement_entity";

    private static final String DELETE_ALL_SQL =
        "DELETE FROM quantity_measurement_entity";

    private static final String DELETE_HISTORY_SQL =
        "DELETE FROM quantity_measurement_history";

    private final ConnectionPool connectionPool;

    public QuantityMeasurementDatabaseRepository(ApplicationConfig config) {
        this.connectionPool = new ConnectionPool(config);
        logger.info("QuantityMeasurementDatabaseRepository initialized with connection pool");
    }

    // Package-private for testing — inject a pre-built pool
    QuantityMeasurementDatabaseRepository(ConnectionPool pool) {
        this.connectionPool = pool;
        logger.info("QuantityMeasurementDatabaseRepository initialized with provided pool");
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        Connection conn = connectionPool.acquireConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            ps.setDouble(1, entity.getOperand1().getValue());
            ps.setString(2, entity.getOperand1().getUnit());
            ps.setDouble(3, entity.getOperand2().getValue());
            ps.setString(4, entity.getOperand2().getUnit());
            ps.setString(5, entity.getOperation());
            ps.setDouble(6, entity.getResult().getValue());
            ps.setString(7, entity.getResult().getUnit());
            ps.setString(8, entity.getMeasurementType());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                    insertHistory(conn, entity.getId(), "INSERT");
                }
            }
            conn.commit();
            logger.info("Saved to DB: {}", entity);
        } catch (SQLException e) {
            rollback(conn);
            throw new DatabaseException("Failed to save entity: " + e.getMessage(), e);
        } finally {
            resetAutoCommit(conn);
            connectionPool.releaseConnection(conn);
        }
    }

    private void insertHistory(Connection conn, Long entityId, String action) throws SQLException {
        String sql = "INSERT INTO quantity_measurement_history (entity_id, action) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, entityId);
            ps.setString(2, action);
            ps.executeUpdate();
        }
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        Connection conn = connectionPool.acquireConnection();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) results.add(mapRow(rs));
            logger.info("Retrieved {} measurements from DB", results.size());
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all measurements: " + e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return results;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        return queryByParam(SELECT_BY_OPERATION_SQL, operation);
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        return queryByParam(SELECT_BY_TYPE_SQL, measurementType);
    }

    private List<QuantityMeasurementEntity> queryByParam(String sql, String param) {
        Connection conn = connectionPool.acquireConnection();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
            logger.info("Query '{}' returned {} results", param, results.size());
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query by param '" + param + "': " + e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return results;
    }

    @Override
    public int getTotalCount() {
        Connection conn = connectionPool.acquireConnection();
        try (PreparedStatement ps = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count measurements: " + e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    @Override
    public void deleteAll() {
        Connection conn = connectionPool.acquireConnection();
        try {
            conn.setAutoCommit(false);
            // Delete history first (FK constraint)
            try (PreparedStatement ps = conn.prepareStatement(DELETE_HISTORY_SQL)) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_SQL)) {
                ps.executeUpdate();
            }
            conn.commit();
            logger.info("All measurements deleted from DB");
        } catch (SQLException e) {
            rollback(conn);
            throw new DatabaseException("Failed to delete measurements: " + e.getMessage(), e);
        } finally {
            resetAutoCommit(conn);
            connectionPool.releaseConnection(conn);
        }
    }

    @Override
    public String getPoolStatistics() { return connectionPool.getStatistics(); }

    @Override
    public void releaseResources() {
        connectionPool.closeAll();
        logger.info("Database connection pool closed");
    }

    private QuantityMeasurementEntity mapRow(ResultSet rs) throws SQLException {
        QuantityModel op1 = new QuantityModel(rs.getDouble("operand1_value"), rs.getString("operand1_unit"));
        QuantityModel op2 = new QuantityModel(rs.getDouble("operand2_value"), rs.getString("operand2_unit"));
        QuantityModel res = new QuantityModel(rs.getDouble("result_value"),   rs.getString("result_unit"));
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
            op1, op2, rs.getString("operation"), res, rs.getString("measurement_type"));
        entity.setId(rs.getLong("id"));
        return entity;
    }

    private void rollback(Connection conn) {
        try { if (conn != null) conn.rollback(); }
        catch (SQLException e) { logger.error("Rollback failed: {}", e.getMessage()); }
    }

    private void resetAutoCommit(Connection conn) {
        try { if (conn != null) conn.setAutoCommit(true); }
        catch (SQLException e) { logger.warn("Could not reset autoCommit: {}", e.getMessage()); }
    }
}
