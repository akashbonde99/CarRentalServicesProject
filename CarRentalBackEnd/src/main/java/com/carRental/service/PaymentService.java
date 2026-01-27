package com.carRental.service;

import java.util.List;
import com.carRental.dto.PaymentDTO;
import com.carRental.dto.PaymentOrderDTO;

public interface PaymentService {
    PaymentDTO makePayment(PaymentOrderDTO paymentOrderDTO);

    PaymentDTO getPaymentById(Long id);

    List<PaymentDTO> getAllPayments();

    PaymentDTO getPaymentByBookingId(Long bookingId);
}
