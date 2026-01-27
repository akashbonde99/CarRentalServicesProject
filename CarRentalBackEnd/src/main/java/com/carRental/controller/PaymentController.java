package com.carRental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.PaymentDTO;
import com.carRental.dto.PaymentOrderDTO;
import com.carRental.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> makePayment(
            @RequestBody PaymentOrderDTO paymentOrderDTO) {
        PaymentDTO createdPayment = paymentService.makePayment(paymentOrderDTO);
        return ResponseEntity
                .ok(new ApiResponse<>("Payment processed successfully", true, createdPayment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(
            @PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return ResponseEntity.ok(new ApiResponse<>("Payment details retrieved", true, payment));
        }
        return ResponseEntity.status(404).body(new ApiResponse<>("Payment not found", false, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(new ApiResponse<>("All payments retrieved", true, payments));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByBookingId(
            @PathVariable Long bookingId) {
        PaymentDTO payment = paymentService.getPaymentByBookingId(bookingId);
        if (payment != null) {
            return ResponseEntity.ok(new ApiResponse<>("Booking payment retrieved", true, payment));
        }
        return ResponseEntity.status(404)
                .body(new ApiResponse<>("Payment not found for booking", false, null));
    }
}
