package com.anuj.quantityMeasurement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final String PROPERTIES_FILE = "application.properties";

    // Defaults
    private static final String DEFAULT_DB_URL      = "jdbc:h2:mem:quantitydb;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_DB_USER     = "sa";
    private static final String DEFAULT_DB_PASSWORD = "";
    private static final String DEFAULT_DB_DRIVER   = "org.h2.Driver";
    private static final int    DEFAULT_POOL_SIZE    = 10;
    private static final long   DEFAULT_POOL_TIMEOUT = 30000L;
    private static final String DEFAULT_REPO_TYPE   = "database";

    private final Properties properties = new Properties();

    public ApplicationConfig() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded configuration from {}", PROPERTIES_FILE);
            } else {
                logger.warn("'{}' not found — using defaults", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            logger.error("Failed to load properties: {}", e.getMessage());
        }
    }

    // System properties override file properties
    private String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null) return sys;
        return properties.getProperty(key, defaultValue);
    }

    public String getDbUrl()          { return get("db.url",            DEFAULT_DB_URL); }
    public String getDbUsername()     { return get("db.username",        DEFAULT_DB_USER); }
    public String getDbPassword()     { return get("db.password",        DEFAULT_DB_PASSWORD); }
    public String getDbDriver()       { return get("db.driver",          DEFAULT_DB_DRIVER); }
    public String getRepositoryType() { return get("repository.type",    DEFAULT_REPO_TYPE); }

    public int getPoolSize() {
        try { return Integer.parseInt(get("db.pool.size", String.valueOf(DEFAULT_POOL_SIZE))); }
        catch (NumberFormatException e) { return DEFAULT_POOL_SIZE; }
    }

    public long getPoolTimeout() {
        try { return Long.parseLong(get("db.pool.timeout", String.valueOf(DEFAULT_POOL_TIMEOUT))); }
        catch (NumberFormatException e) { return DEFAULT_POOL_TIMEOUT; }
    }
}
