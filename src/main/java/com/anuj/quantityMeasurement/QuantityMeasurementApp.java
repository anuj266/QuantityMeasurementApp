package com.anuj.quantityMeasurement;

public class QuantityMeasurementApp {
	
	private static void printComparison(String label, Length l1, Length l2) {
        System.out.print(label + ": ");
        System.out.println(l1.equals(l2)
                ? "Both lengths are equal (True)"
                : "Both lengths are not equal (False)");
    }
	
	public static void demonstrateFeetEquality() {
		Length feet1=new Length(88.0,Length.LengthUnit.FEET);
		Length feet2=new Length(88.0,Length.LengthUnit.FEET);
		printComparison("Feet", feet1, feet2);
	}
	
	public static void demonstrateInchesEquality() {
		Length inches1=new Length(88.0,Length.LengthUnit.INCHES);
		Length inches2=new Length(88.0,Length.LengthUnit.INCHES);
		printComparison("Inches", inches1, inches2);
	}
	
	public static void demonstrateFeetInchesComparison() {
		Length inches=new Length(12.0,Length.LengthUnit.INCHES);
		Length feet=new Length(1.0,Length.LengthUnit.FEET);
		printComparison("Inches & Feet", inches, feet);
	}
	
	public static boolean demonstrateLengthEquality(Length l1, Length l2) {
		return l1.equals(l2);
	}

	public static void main(String[] args) {
		demonstrateFeetEquality();
		demonstrateInchesEquality();
		demonstrateFeetInchesComparison();
	}

}