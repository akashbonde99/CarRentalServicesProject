package com.carRental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.carRental.dto.PaymentDTO;
import com.carRental.dto.PaymentOrderDTO;
import com.carRental.entity.Booking;
import com.carRental.entity.Payment;
import com.carRental.entity.PaymentStatus;
import com.carRental.repository.BookingRepository;
import com.carRental.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final RazorpayService razorpayService;

    @Override
    public PaymentDTO makePayment(PaymentOrderDTO paymentOrderDTO) {
        Payment payment = modelMapper.map(paymentOrderDTO, Payment.class);
        if (paymentOrderDTO.getBookingId() != null) {
            Booking booking = bookingRepository.findById(Long.valueOf(paymentOrderDTO.getBookingId()))
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Strict payment check
            if (booking.getBookingStatus() != com.carRental.entity.BookingStatus.CONFIRMED) {
                throw new RuntimeException("Booking must be confirmed before payment.");
            }
            payment.setBooking(booking);
            // Update booking payment status on success (done below)
        }

        if (paymentOrderDTO.getRazorpayPaymentId() != null) {
            boolean isVerified = razorpayService.verifySignature(
                    paymentOrderDTO.getRazorpayOrderId(),
                    paymentOrderDTO.getRazorpayPaymentId(),
                    paymentOrderDTO.getRazorpaySignature());

            if (!isVerified) {
                payment.setPaymentStatus(PaymentStatus.FAILED); // Set status to failed
                throw new RuntimeException("Payment verification failed");
            }
            payment.setRazorpayPaymentId(paymentOrderDTO.getRazorpayPaymentId());
            payment.setRazorpayOrderId(paymentOrderDTO.getRazorpayOrderId());
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            // Sync with Booking
            if (payment.getBooking() != null) {
                payment.getBooking().setPaymentStatus(PaymentStatus.SUCCESS);
                bookingRepository.save(payment.getBooking());
            }

        } else if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            // Sync with Booking
            if (payment.getBooking() != null) {
                payment.getBooking().setPaymentStatus(PaymentStatus.SUCCESS);
                bookingRepository.save(payment.getBooking());
            }
        }
        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDTO.class);
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null)
            return null;
        return modelMapper.map(payment, PaymentDTO.class);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_BookingId(bookingId).orElse(null);
        if (payment == null)
            return null;
        return modelMapper.map(payment, PaymentDTO.class);
    }
}
