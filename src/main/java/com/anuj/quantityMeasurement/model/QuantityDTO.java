package com.anuj.quantityMeasurement.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {
	
	@NotNull(message="Value must not be null")
	private Double value;
	
	@NotEmpty(message="Unit must not be empty")
	private String unit;
	
	@NotEmpty(message="Measurement type must not be empty")
	private String measurementType;
	
}