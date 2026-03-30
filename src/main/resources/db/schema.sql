CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    operand1_value   DOUBLE       NOT NULL,
    operand1_unit    VARCHAR(50)  NOT NULL,
    operand2_value   DOUBLE       NOT NULL,
    operand2_unit    VARCHAR(50)  NOT NULL,
    operation        VARCHAR(20)  NOT NULL,
    result_value     DOUBLE       NOT NULL,
    result_unit      VARCHAR(50)  NOT NULL,
    measurement_type VARCHAR(50)  NOT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id   BIGINT      NOT NULL,
    action      VARCHAR(20) NOT NULL,
    action_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entity_id) REFERENCES quantity_measurement_entity(id)
);

CREATE INDEX IF NOT EXISTS idx_operation ON quantity_measurement_entity(operation);
CREATE INDEX IF NOT EXISTS idx_meas_type ON quantity_measurement_entity(measurement_type);
CREATE INDEX IF NOT EXISTS idx_created_at ON quantity_measurement_entity(created_at);