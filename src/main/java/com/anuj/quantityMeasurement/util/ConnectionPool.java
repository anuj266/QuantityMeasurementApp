package com.anuj.quantityMeasurement.util;

import com.anuj.quantityMeasurement.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private final BlockingQueue<Connection> pool;
    private final ApplicationConfig         config;
    private final AtomicInteger             activeConnections = new AtomicInteger(0);
    private final int                       poolSize;

    public ConnectionPool(ApplicationConfig config) {
        this.config   = config;
        this.poolSize = config.getPoolSize();
        this.pool     = new ArrayBlockingQueue<>(poolSize);
        initializePool();
    }

    private void initializePool() {
        try {
            Class.forName(config.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("JDBC driver not found: " + config.getDbDriver(), e);
        }

        for (int i = 0; i < poolSize; i++) {
            pool.offer(createConnection());
        }
        logger.info("Connection pool initialized with {} connections", poolSize);
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(
                config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create database connection: " + e.getMessage(), e);
        }
    }

    public Connection acquireConnection() {
        try {
            Connection conn = pool.poll(config.getPoolTimeout(), TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new DatabaseException("Connection pool exhausted — timeout waiting for connection");
            }
            // Replace closed connections
            if (conn.isClosed()) {
                conn = createConnection();
            }
            activeConnections.incrementAndGet();
            logger.debug("Connection acquired. Active: {}", activeConnections.get());
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for connection", e);
        } catch (SQLException e) {
            throw new DatabaseException("Error checking connection status", e);
        }
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            pool.offer(conn);
            activeConnections.decrementAndGet();
            logger.debug("Connection released. Active: {}", activeConnections.get());
        }
    }

    public void closeAll() {
        logger.info("Closing all connections in pool");
        pool.forEach(conn -> {
            try { conn.close(); } catch (SQLException e) {
                logger.warn("Error closing connection: {}", e.getMessage());
            }
        });
        pool.clear();
    }

    public String getStatistics() {
        return String.format("ConnectionPool[total=%d, active=%d, idle=%d]",
            poolSize, activeConnections.get(), pool.size());
    }

    public int getActiveConnections() { return activeConnections.get(); }
    public int getIdleConnections()   { return pool.size(); }
    public int getTotalConnections()  { return poolSize; }
}