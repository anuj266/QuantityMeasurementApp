package com.anuj.quantityMeasurement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(
		name="quantity_measurement_entity",
		indexes= {
			@Index(name="idx_operation", columnList="operation"),
			@Index(name="idx_meas_type", columnList="thisMeasurementType"),
			@Index(name="idx_created_at", columnList="createdAt"),
			@Index(name="idx_is_error", columnList="isError")
		})
public class QuantityMeasurementEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private Double thisValue;
	
	@Column(nullable=false)
	private String thisUnit;
	
	@Column(nullable=false)
	private String thisMeasurementType;
	
	@Column(nullable=false)
	private Double thatValue;
	
	@Column(nullable=false)
	private String thatUnit;
	
	@Column(nullable=false)
	private String thatMeasurementType;
	
	@Column(nullable=false)
	private String operation;
	
	private Double resultValue;
	private String resultUnit;
	private String resultMeasurementType;
	private String resultString;
	
	private String errorMessage;
	private boolean isError=false;
	
	@Column(updatable=false)
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt=LocalDateTime.now();
		updatedAt=LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt=LocalDateTime.now();
	}
}