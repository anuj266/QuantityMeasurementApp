package com.anuj.quantityMeasurement;

public class QuantityMeasurementApp {
	
	public static void demonstrateLengthComparison(Length l1, Length l2) {
		if(l1.equals(l2)) {
			System.out.println("Both lengths are equal (true)");
		}else {
			System.out.println("Both lengths are not equal (true)");
		}
	}
	
	public static boolean demonstrateLengthEquality(Length l1, Length l2) {
		return l1.equals(l2);
	}

	public static void main(String[] args) {
		System.out.print("Comparison btw Feet & Inch: ");
		demonstrateLengthComparison(new Length(1.0,Length.LengthUnit.FEET),
									new Length(12.0,Length.LengthUnit.INCHES));
		
		System.out.print("Comparison btw Yard & Inch: ");
		demonstrateLengthComparison(new Length(1.0,Length.LengthUnit.YARDS),
									new Length(36.0,Length.LengthUnit.INCHES));
		
		System.out.print("Comparison btw Centimeter & Inch: ");
		demonstrateLengthComparison(new Length(100.0,Length.LengthUnit.CENTIMETERS),
									new Length(39.3701,Length.LengthUnit.INCHES));
		
		System.out.print("Comparison btw Feet & Yard: ");
		demonstrateLengthComparison(new Length(3.0,Length.LengthUnit.FEET),
									new Length(1.0,Length.LengthUnit.YARDS));
		
		System.out.print("Comparison btw Centimeter & Feet: ");
		demonstrateLengthComparison(new Length(30.48,Length.LengthUnit.CENTIMETERS),
									new Length(1.0,Length.LengthUnit.FEET));
		
	}

}