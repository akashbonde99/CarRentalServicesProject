package com.carRental.dto;

import java.time.LocalDate;

import com.carRental.entity.PaymentMode;
import com.carRental.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private Double amount;
    private LocalDate paymentDate;
    private PaymentStatus paymentStatus;
    private PaymentMode paymentMode;
    private String transactionId;
}
