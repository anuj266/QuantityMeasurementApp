package com.anuj.quantityMeasurement.app;

import com.anuj.quantityMeasurement.Controller.QuantityMeasurementController;
import com.anuj.quantityMeasurement.dto.QuantityDTO;

public class QuantityMeasurementApp {

    public static void main(String[] args) {

        QuantityMeasurementController controller = new QuantityMeasurementController();

        QuantityDTO weightInGrams     = new QuantityDTO(1000.0, "GRAM",     "WEIGHT");
        QuantityDTO weightInKilograms = new QuantityDTO(1.0,    "KILOGRAM", "WEIGHT");

        // equality
        System.out.println("Are weights equal: " + controller.compare(weightInGrams, weightInKilograms));

        // conversion
        QuantityDTO convertedWeight = controller.convert(weightInGrams, new QuantityDTO(0, "KILOGRAM", "WEIGHT"));
        System.out.println(convertedWeight.getValue() + " " + convertedWeight.getUnit());

        
        QuantityDTO volumeInML = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        QuantityDTO volumeInL  = new QuantityDTO(1.0,    "LITRE",      "VOLUME");

        // equality
        System.out.println("Are volumes equal: " + controller.compare(volumeInML, volumeInL));

        // addition
        QuantityDTO addedVolume = controller.add(volumeInML, volumeInL);
        System.out.println(addedVolume.getValue() + " " + addedVolume.getUnit());


        QuantityDTO celsius0     = new QuantityDTO(0.0,  "CELSIUS",    "TEMPERATURE");
        QuantityDTO fahrenheit32 = new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE");

        // equality
        System.out.println("0 C equals 32 F: " + controller.compare(celsius0, fahrenheit32));

        // conversion
        QuantityDTO celsius100 = new QuantityDTO(100.0, "CELSIUS",    "TEMPERATURE");
        QuantityDTO fahrenheit = controller.convert(celsius100, new QuantityDTO(0, "FAHRENHEIT", "TEMPERATURE"));
        System.out.println("100 C = " + fahrenheit.getValue() + " " + fahrenheit.getUnit());

        // unsupported operation: addition on temperature
        try {
            controller.add(celsius100, new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE"));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
       }
    }
}
