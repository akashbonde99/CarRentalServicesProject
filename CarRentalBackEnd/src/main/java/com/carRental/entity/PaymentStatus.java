package com.carRental.entity;

public enum PaymentStatus {
    PENDING,
    CREATED, // Razorpay order created
    SUCCESS, // Payment successful
    FAILED // Payment failed
}
