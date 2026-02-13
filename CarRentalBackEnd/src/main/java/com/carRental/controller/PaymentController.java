package com.carRental.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.PaymentDTO;
import com.carRental.dto.PaymentOrderDTO;
import com.carRental.service.PaymentService;
import com.carRental.service.RazorpayService;
import com.razorpay.Order;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> data) {
        try {
            int amount = Integer.parseInt(data.get("amount").toString());
            String currency = data.get("currency").toString();

            String order = razorpayService.createOrder(amount, currency, "receipt_100");
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> makePayment(
            @RequestBody PaymentOrderDTO paymentOrderDTO) {

        PaymentDTO createdPayment = paymentService.makePayment(paymentOrderDTO);

        return ResponseEntity.ok(
                new ApiResponse<>("Payment processed successfully", true, createdPayment));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByBookingId(
            @PathVariable Long bookingId) {

        PaymentDTO payment = paymentService.getPaymentByBookingId(bookingId);

        if (payment != null) {
            return ResponseEntity.ok(
                    new ApiResponse<>("Booking payment retrieved", true, payment));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Payment not found for booking", false, null));
    }
}
