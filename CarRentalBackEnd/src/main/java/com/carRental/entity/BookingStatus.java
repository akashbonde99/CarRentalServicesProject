package com.carRental.entity;

public enum BookingStatus {
    PENDING, // booking created, payment not done
    PAID, // payment successful
    CONFIRMED, // booking confirmed
    REJECTED, // booking rejected by admin
    CANCELLED // booking cancelled
}
