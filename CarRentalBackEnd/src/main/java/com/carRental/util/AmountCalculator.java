package com.carRental.util;

public class AmountCalculator {
    public static Double calculateTotalAmount(long days, Double pricePerDay) {
        return days * pricePerDay;
    }
}
