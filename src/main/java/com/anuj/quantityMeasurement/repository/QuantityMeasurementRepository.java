package com.anuj.quantityMeasurement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.anuj.quantityMeasurement.model.QuantityMeasurementEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

	List<QuantityMeasurementEntity> findByOperation(String operation);

	List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

	List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

	List<QuantityMeasurementEntity> findByIsErrorTrue();

	@Query("SELECT q FROM QuantityMeasurementEntity q " + "WHERE q.operation = :op AND q.isError = false")
	List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("op") String operation);

	long countByOperationAndIsErrorFalse(String operation);
}